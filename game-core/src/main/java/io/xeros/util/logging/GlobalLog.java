package io.xeros.util.logging;

public abstract class GlobalLog extends Log {

    public GlobalLog() {
        this(true);
    }

    public GlobalLog(boolean includeMetadata) {
        super(includeMetadata);
    }

    @Override
    public String getDirectory() {
        return "global_logs/";
    }

}
