package io.xeros.net.login;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public class LoginAttempt {
    private final long time = System.currentTimeMillis();

    public boolean inLastXMinutes(int minutes) {
        return System.currentTimeMillis() - time <= TimeUnit.MINUTES.toMillis(minutes);
    }
}
