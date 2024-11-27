package io.xeros.model.entity.player.save.backup;

import io.xeros.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PlayerSaveBackupFile {

    private final LocalDateTime localDateTime;

    public PlayerSaveBackupFile(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public PlayerSaveBackupFile(String fileName) {
        this.localDateTime = LocalDateTime.parse(fileName.replace("_", ":").replace(".zip", ""));
    }

    public String getFileName() {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "_") + ".zip";
    }

    public boolean expired(int expiryDays) {
        return LocalDateTime.now().isAfter(localDateTime.plus(expiryDays, ChronoUnit.DAYS));
    }
}
