package io.xeros;

import javax.swing.*;
import java.io.File;
import java.util.Properties;

public class AssetLoader {

    private static final Properties cacheProperties = ServerProperties.getCacheProperties();

    public static void initCache() {
        ServerProperties.loadProperties(ServerProperties.PropType.SERVER);
        ServerProperties.loadProperties(ServerProperties.PropType.CACHE);
    }

    private static void openFileSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./"));
        fileChooser.setDialogTitle("Select Assets Root");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            cacheProperties.setProperty("cache", fileChooser.getSelectedFile().getAbsolutePath());
            ServerProperties.saveProperties(ServerProperties.PropType.CACHE, cacheProperties);
        }
    }

    private static boolean shouldOpenChooser() {
        return cacheProperties.getProperty("cache") == null;
    }

    public static File getFolder(String dir) {
        return new File(cacheProperties.getProperty("cache") + File.separator + dir);
    }

    public static File getFile(String dir, String name) {
        return new File(getFolder(dir), name);
    }
}
