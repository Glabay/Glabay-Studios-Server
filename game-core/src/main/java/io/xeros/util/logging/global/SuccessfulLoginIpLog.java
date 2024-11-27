package io.xeros.util.logging.global;

import io.xeros.util.logging.GlobalLog;

import java.util.Set;

public class SuccessfulLoginIpLog extends GlobalLog {

    private final String ipAddress;

    public SuccessfulLoginIpLog(String ipAddress) {
        super(false);
        this.ipAddress = ipAddress;
    }

    @Override
    public String getLoggedMessage() {
        return ipAddress;
    }

    @Override
    public Set<String> getFileNames() {
        return Set.of("ips_successful_login");
    }
}
