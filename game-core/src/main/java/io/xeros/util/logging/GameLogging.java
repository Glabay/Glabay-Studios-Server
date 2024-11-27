package io.xeros.util.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.xeros.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogging {

    private static final Logger logger = LoggerFactory.getLogger(GameLogging.class);

    /**
     * Executor for I/O.
     */
    private static final ScheduledExecutorService IO_EXECUTOR = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("game-logging-%d").build());

    private static final ConcurrentLinkedQueue<Log> BATCHED = new ConcurrentLinkedQueue<>();

    public void schedule() {
        IO_EXECUTOR.scheduleAtFixedRate(() -> {
            Log log;
            while ((log = BATCHED.poll()) != null)
                writeToDisk(log);
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void write(final Log... logs) {
        IO_EXECUTOR.submit(() -> Arrays.stream(logs).forEach(this::writeToDisk));
    }

    public void batchWrite(Log... logs) {
        BATCHED.addAll(Arrays.asList(logs));
    }

    private void writeToDisk(Log log) {
        try {
            for (String name : log.getFileNames()) {
                Path path = buildPath(log, name, "txt");
                createPath(path);

                try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                    if (log.isIncludeMetadata()) {
                        writer.write(String.format("[%s] [%s] %s", log.getClass().getSimpleName(), DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(log.getDate()), log.getLoggedMessage()));
                    } else {
                        writer.write(log.getLoggedMessage());
                    }

                    writer.newLine();
                } catch (IOException e) {
                    logger.error("Error while writing log {}", log, e);
                    e.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            logger.error("Error while writing log {}", log, e);
            e.printStackTrace(System.err);
        }
    }

    private void createPath(Path path) {
        try {
            if (!Files.exists(path)) {
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }

                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private Path buildPath(Log log, String fileName, String fileExtension) {
        return Path.of(Server.getGameLogDirectory(), log.getDirectory(), fileExtension, fileName + "." + fileExtension);
    }

}
