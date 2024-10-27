package io.xeros.model.entity.player.save.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.sql.DatabaseCredentials;
import io.xeros.util.Misc;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="http://www.devx.com/tips/Tip/14049">...</a>
 * @author Michael Sasse (<a href="https://github.com/mikeysasse/">...</a>)
 */
public class PlayerSaveBackup {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSaveBackup.class);
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("player-save-backup-%d").build());
    private static long ticks = 0;

    public static void start(long millisBetweenMassSave, int ticksBetweenSaveBackup) {
        service.scheduleAtFixedRate(() -> {
            try {
                ticks++;
                PlayerSave.saveAll();

                if (ticks % ticksBetweenSaveBackup == 0)
                    backup(LocalDateTime.now());
            } catch (Exception e) {
                logger.error("Error occurred while creating player save backup.", e);
                e.printStackTrace(System.err);
            }
        }, 0, millisBetweenMassSave, TimeUnit.MILLISECONDS);
    }

    public static File backup(LocalDateTime localDateTime) throws IOException {
        File backupFileDirectory = new File(Server.getBackupDirectory());
        Files.createDirectories(backupFileDirectory.toPath());
        File saveFileDirectory = new File(Server.getSaveDirectory());
        Preconditions.checkState(saveFileDirectory.exists(), "No save directory [" + saveFileDirectory + "] exists.");

        // dump sql database
        try {
            dumpMysqlDatabase();
        } catch (Exception e) {
            logger.error("Error while dumping mysql database", e);
        }

        // Create new backup and delete expired ones
        File created = createBackupFile(localDateTime, backupFileDirectory, saveFileDirectory);
        deleteOldDiskBackups(backupFileDirectory);

        // Upload new remote backup and deleted expired ones
        deleteExpiredRemoteBackups();
        uploadRemoteBackup(created);
        return created;
    }

    private static void dumpMysqlDatabase() throws IOException {
        if (!Server.getConfiguration().isLocalDatabaseEnabled())
            return;

        String os = System.getProperty("os.name").toLowerCase();
        DatabaseCredentials database = Server.getConfiguration().getLocalDatabase();
        String command = Misc.replaceBracketsWithArguments("mysqldump -u{} -p{} {} > {}",
                database.getUsername(), database.getPassword(), database.getName(),
                Path.of(new File(Server.getSaveDirectory()).getAbsolutePath(), "database.sql"));

        logger.info("Dumping database {}.", database);
        logger.debug(command);

        Process exec;
        if (os.contains("linux"))
            exec = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", command});
        else if (os.contains("windows")) {
            String mysqlDumpPath = Server.getConfiguration().getMysqlDumpPath();
            String path = mysqlDumpPath == null ? "" : mysqlDumpPath;
            exec = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", path + command});
        } else {
            logger.warn("Unsupported operating system for mysqldump: {}", os);
            return;
        }

        try {
            if (exec.waitFor() != 0)
                throw new IllegalStateException("Something went wrong with mysqldump command, code: " + exec.exitValue());
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }

    private static File createBackupFile(LocalDateTime timeForFile, File backupFileDirectory, File saveFileDirectory) throws IOException {
        PlayerSaveBackupFile backup = new PlayerSaveBackupFile(timeForFile);
        File backupZipFile = backupFileDirectory.toPath().resolve(backup.getFileName()).toFile();
        File zipped = create(saveFileDirectory, backupZipFile);
        Preconditions.checkState(zipped != null && zipped.exists(), "Backup file not created " + zipped);
        logger.info("Backed up save files to file {} ", zipped);
        return zipped;
    }

    private static void deleteOldDiskBackups(File backupFileDirectory) {
        Arrays.stream(Objects.requireNonNull(backupFileDirectory.listFiles())).forEach(file -> {
            try {
                PlayerSaveBackupFile backup = new PlayerSaveBackupFile(file.getName());
                if (backup.expired(Configuration.PLAYER_SAVE_BACKUPS_DELETE_AFTER_DAYS)) {
                    if (!file.delete()) {
                        logger.warn("Could not delete player save backup file: " + file);
                    }
                }
            } catch (Exception e) {
                logger.error("Error for file {}", file, e);
                e.printStackTrace(System.err);
            }
        });
    }

    private static File create(File directoryToZip, File outputFile) {
        Preconditions.checkState(directoryToZip.exists(), "Directory to zip doesn't exist.");
        Preconditions.checkState(!outputFile.exists(), "Output zip file already exists.");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
            zipDir(directoryToZip.getPath(), zos);
            return outputFile;
        } catch (IOException e) {
            logger.error("Error while creating zip directoryToZip={}, outputFile={}", directoryToZip, outputFile, e);
            e.printStackTrace(System.err);
            return null;
        }
    }

    private static void zipDir(String dir2zip, ZipOutputStream zos) throws IOException {
        File zipDir = new File(dir2zip);
        String[] dirList = zipDir.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;
        for (String s : dirList) {
            File f = new File(zipDir, s);
            if (f.isDirectory()) {
                String filePath = f.getPath();
                zipDir(filePath, zos);
                continue;
            }
            FileInputStream fis = new FileInputStream(f);
            ZipEntry anEntry = new ZipEntry(f.getPath());
            zos.putNextEntry(anEntry);
            while ((bytesIn = fis.read(readBuffer)) != -1) {
                zos.write(readBuffer, 0, bytesIn);
            }
            fis.close();
        }
    }

    private static void deleteExpiredRemoteBackups() throws IOException {
        connectFTP(client -> {
            try {
                client.enterLocalPassiveMode();
                FTPFile[] files = client.listFiles();
                List<PlayerSaveBackupFile> backups = Arrays.stream(files).filter(it -> it.getName().endsWith(".zip")).map(it -> new PlayerSaveBackupFile(it.getName()))
                        .toList();

                for (PlayerSaveBackupFile backup : backups) {
                    if (backup.expired(Configuration.PLAYER_SAVE_BACKUPS_EXTERNAL_DELETE_AFTER_DAYS)) {
                        if (!client.deleteFile(backup.getFileName()))
                            throw new IllegalStateException("Could not delete remote backup " + backup.getFileName());
                        logger.debug("Deleted old remote backup {}", backup.getFileName());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        });
    }

    private static void uploadRemoteBackup(File file) throws IOException {
        connectFTP(client -> {
            try {
                client.setFileType(FTP.BINARY_FILE_TYPE);
                client.enterLocalPassiveMode();

                try (FileInputStream fis = new FileInputStream(file)) {
                    client.storeFile(file.getName(), fis);
                    logger.info("Uploaded backup file to webserver.");
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        });
    }

    private static void connectFTP(Consumer<FTPClient> callback) throws IOException {
        FTPClient client = new FTPClient();
        try {
            DatabaseCredentials credentials = Server.getConfiguration().getBackupFtpCredentials();

            if (credentials == null) {
                logger.warn("No FTP credentials to upload remote backups.");
                return;
            }

            client.connect(credentials.getUrl());
            int reply = client.getReplyCode();

            if (reply != FTPReply.SERVICE_READY) {
                client.disconnect();
                throw new IllegalStateException("Could not connect to ftp backup server, response: " + reply);
            }

            if (!client.login(credentials.getUsername(), credentials.getPassword())) {
                client.logout();
                client.disconnect();
                throw new IllegalStateException("Could not login to ftp backup server.");
            }

            callback.accept(client);
        } finally {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        }
    }
}
