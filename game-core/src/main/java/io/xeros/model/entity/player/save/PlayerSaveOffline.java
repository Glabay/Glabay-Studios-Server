package io.xeros.model.entity.player.save;

import com.google.common.base.Preconditions;
import io.xeros.util.Misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Helper class to retrieve password from character files.
 */
public class PlayerSaveOffline {

    public static File getCharacterFile(String name) {
        return getCharacterFile(new File(PlayerSave.getSaveDirectory()), name);
    }

    public static File getCharacterFile(File directory, String name) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().equalsIgnoreCase(name + ".txt")) {
                return file;
            }
        }

        return null;
    }

    public static String getPassword(File characterFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(characterFile))) {
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.contains("character-password")) {
                    return line.split(" = ")[1];
                }
            }

            throw new IllegalStateException("No password found in file: " + characterFile);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static PlayerAddresses getAddresses(File characterFile) {
        List<String> lines = getCharacterFileLines(characterFile);
        Preconditions.checkState(lines != null, "Lines are null.");

        String ip = null;
        String mac = null;
        String uuid = null;
        for (String it : lines) {
            if (it.contains("character-ip-address"))
                ip = extract(it);
            if (it.contains("character-mac-address"))
                mac = extract(it);
            if (it.contains("character-uuid"))
                uuid = extract(it);
            if (ip != null && mac != null && uuid != null)
                break;
        }

        Preconditions.checkState(ip != null && mac != null && uuid != null, "Couldn't find all addresses " + characterFile);
        return new PlayerAddresses(ip, mac, uuid);
    }

    private static String extract(String line) {
        int indexOfEquals = line.indexOf("=");
        Preconditions.checkState(indexOfEquals != -1);
        return line.substring(indexOfEquals + 2);
    }
    public static List<String> getCharacterFileLines(File characterFile) {
        Preconditions.checkArgument(characterFile != null);
        try (BufferedReader reader = new BufferedReader(new FileReader(characterFile))) {
            return reader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static boolean passwordMatches(String entered, String actual) {
        return entered.equals(actual)
                || Misc.basicEncrypt(entered).equals(actual)
                || Objects.equals(Misc.md5Hash(entered), actual);
    }
}
