package io.xeros.util.logging.global;

import io.xeros.util.logging.GlobalLog;

import java.util.Set;
import java.util.StringJoiner;

public class LoginRequestLog extends GlobalLog {

    private final String ip;
    private final String mac;
    private final String uuid;
    private final String message;

    public LoginRequestLog(String ip, String message) {
        this(ip, null, null, message);
    }

    public LoginRequestLog(String ip, String mac, String uuid, String message) {
        this.ip = ip;
        this.mac = mac;
        this.uuid = uuid;
        this.message = message;
    }

    @Override
    public String getLoggedMessage() {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add("ip=" + ip);
        if (mac != null)
            joiner.add("mac=" + mac);
        if (uuid != null)
            joiner.add("mac=" + uuid);
        return "[" + joiner.toString() + "] " + message;
    }

    @Override
    public Set<String> getFileNames() {
        return Set.of("login_request");
    }
}
