package io.xeros;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;

public class ServerProperties {
    private static final Logger logger = LoggerFactory.getLogger(ServerProperties.class);

    public enum PropType {
        SERVER,
        CACHE
    }

    @Getter
    private static final Properties serverProperties = new Properties();
    @Getter
    private static final Properties cacheProperties = new Properties();

    public static void loadProperties(PropType type) {
        var propFile = new File("./" + type.name().toLowerCase(Locale.getDefault()) + ".properties");
        try (var fis = new FileInputStream(propFile)) {
            if (!propFile.exists())
                if (!propFile.createNewFile())
                    logger.error("Error creating missing properties file: {}", propFile.getAbsolutePath());

            switch (type) {
                case CACHE:
                    cacheProperties.load(fis);
                    var assetsLink = new File("./assetslink.txt");
                    if (assetsLink.exists()) {
                        var cachePath = new String(Files.readAllBytes(assetsLink.toPath())).replace("cache\\", "");
                        cacheProperties.setProperty("cache", cachePath);
                        saveProperties(type, cacheProperties);
                        if (!assetsLink.delete())
                            logger.error("Error deleting Cache Prop File: {}", assetsLink.getAbsolutePath());
                    }
                    break;
                case SERVER:
                    serverProperties.load(fis);
                    break;
            }
        }
        catch (IOException e) {
            e.printStackTrace(System.err); // Consider a more robust error handling strategy
        }
    }

    public static void saveProperties(PropType type, Properties p) {
        var path = "./%s.properties".formatted(type.name().toLowerCase(Locale.getDefault()));
        try (var fr = new FileOutputStream(path)) {
            p.store(fr, "Properties");
        }
        catch (IOException e) {
            e.printStackTrace(System.err); // Consider a more robust error handling strategy
        }
    }
}
