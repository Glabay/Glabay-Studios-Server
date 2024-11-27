package io.xeros.util.logging.global;

import io.xeros.util.logging.GlobalLog;

import java.util.Set;

public class SqlLog extends GlobalLog {

    private final Class<?> clazz;

    public SqlLog(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getLoggedMessage() {
        return clazz.getName();
    }

    @Override
    public Set<String> getFileNames() {
        return Set.of("sql");
    }
}
