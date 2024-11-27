package io.xeros.util.logging;

import java.time.LocalDateTime;
import java.util.Set;

public abstract class Log {

    private final LocalDateTime date = LocalDateTime.now();

    public abstract String getLoggedMessage();

    public abstract String getDirectory();

    public abstract Set<String> getFileNames();

    private final boolean includeMetadata;

    public Log() {
        this(true);
    }

    public Log(boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isIncludeMetadata() {
        return includeMetadata;
    }
}
