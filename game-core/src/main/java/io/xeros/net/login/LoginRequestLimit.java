package io.xeros.net.login;

import io.xeros.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LoginRequestLimit {

    private static final Logger logger = LoggerFactory.getLogger(LoginRequestLimit.class);
    public static int MAX_LOGINS_PER_TICK = 8;
    public static final int INVALID_LOGIN_REQUEST_TIMEOUT = 30_000;
    private static final Map<String, Long> INVALID_LOGINS = new ConcurrentHashMap<>();

    private static final AtomicLong loginRequestTick = new AtomicLong();
    private static final AtomicLong invalidLoginUpdateTick = new AtomicLong();
    private static final AtomicInteger loginRequestsThisTick = new AtomicInteger();

    public static void addRequest() {
        reset();
        loginRequestsThisTick.getAndIncrement();
    }

    public static boolean rejectConnectionRequest() {
        reset();
        return loginRequestsThisTick.get() >= MAX_LOGINS_PER_TICK;
    }

    public static void addInvalidLogin(String ip) {
        INVALID_LOGINS.put(ip, System.currentTimeMillis());
    }

    public static boolean timedOutInvalidLoginRequest(String ip) {
        reset();
        long time = INVALID_LOGINS.getOrDefault(ip, 0L);
        if (time == 0)
            return false;
        if (System.currentTimeMillis() - time < INVALID_LOGIN_REQUEST_TIMEOUT)
            return true;
        INVALID_LOGINS.remove(ip);
        return false;
    }

    private static void reset() {
        long tick = Server.getTickCount();
        if (loginRequestTick.get() != tick) {
            loginRequestTick.set(tick);
            invalidLoginUpdateTick.set(tick);
            loginRequestsThisTick.set(0);

            long l = System.currentTimeMillis();
            INVALID_LOGINS.entrySet().removeIf(it -> l - it.getValue() > INVALID_LOGIN_REQUEST_TIMEOUT);
        }
    }
}
