package io.xeros.net.login;

import io.xeros.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class LoginThrottler {

    private static final Logger logger = LoggerFactory.getLogger(LoginThrottler.class);
    private static final Map<String, CopyOnWriteArrayList<LoginAttempt>> loginAttempts = new ConcurrentHashMap<>();
    private static final Map<String, SuccessfulLogin> successfulLogins = new ConcurrentHashMap<>();

    private static final Map<String, Long> timeouts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 10;
    private static final int TIMEOUT_MINUTES = 5;

    public static void addIncorrectLoginAttempt(String username, String ipAddress, String macAddress, String uuid) {
        if (Configuration.DISABLE_LOGIN_THROTTLE)
            return;
        add(username(username));
        add(ipAddress);
        if (macAddress != null && !macAddress.equals(ipAddress) && !macAddress.isEmpty())
            add(macAddress);
        if (uuid != null && !uuid.isEmpty())
            add(uuid);
    }

    public static void addSuccessfulLogin(String username, String ipAddress, String macAddress, String uuid) {
        if (!successfulLogins.containsKey(username)) {
            successfulLogins.put(username, new SuccessfulLogin(ipAddress, macAddress, uuid));
        }
    }

    private static void add(String address) {
        if (loginAttempts.containsKey(address)) {
            loginAttempts.get(address).add(new LoginAttempt());
            logger.debug("Add login attempt for address {}", address);
        } else {
            CopyOnWriteArrayList<LoginAttempt> list = new CopyOnWriteArrayList<>();
            list.add(new LoginAttempt());
            LoginThrottler.loginAttempts.put(address, list);
            logger.debug("Add login attempt for address {}", address);
        }
    }

    public static boolean canLoginAttempt(String username, String ipAddress, String macAddress, String uuid) {
        if (Configuration.DISABLE_LOGIN_THROTTLE)
            return true;
        if (successfulLogins.containsKey(username)) {
            var successfulLogin = successfulLogins.get(username);
            if (!successfulLogin.ip().equals(ipAddress)
                    || !successfulLogin.mac().equals(macAddress)
                    || !successfulLogin.uuid().equals(uuid)) {
                if (Configuration.DISABLE_NEW_MAC) {
                    return false;
                }
            }
        }
        if (isTimedOut(username(username)) || denyLoginAttempt(username(username)))
            return false;
        if (isTimedOut(ipAddress) || denyLoginAttempt(ipAddress))
            return false;
        if (macAddress != null && !macAddress.equals(ipAddress) && !macAddress.isEmpty())
            if (isTimedOut(macAddress) || denyLoginAttempt(macAddress))
                return false;
        if (uuid != null && !uuid.isEmpty())
            return !isTimedOut(uuid) && !denyLoginAttempt(uuid);
        return true;
    }

    private static boolean denyLoginAttempt(String address) {
        if (loginAttempts.containsKey(address)) {
            loginAttempts.get(address).removeIf(it -> !it.inLastXMinutes(TIMEOUT_MINUTES));
            if (loginAttempts.get(address).stream().filter(it -> it.inLastXMinutes(TIMEOUT_MINUTES)).count() >= MAX_ATTEMPTS) {
                timeouts.put(address, System.currentTimeMillis());
                logger.info("Denying login attempt for too many attempts, address={}", address);
                return true;
            }
        }

        return false;
    }

    private static boolean isTimedOut(String address) {
        if (address == null || address.isEmpty())
            return false;
        if (timeouts.containsKey(address)) {
            long time = timeouts.get(address);
            if (System.currentTimeMillis() - time < TimeUnit.MINUTES.toMillis(TIMEOUT_MINUTES)) {
                return true;
            }

            timeouts.remove(address);
            return false;
        }

        return false;
    }

    private static String username(String username) {
        return "user=" + username;
    }

    public static void clear() {
        timeouts.clear();
        loginAttempts.clear();
    }
}
