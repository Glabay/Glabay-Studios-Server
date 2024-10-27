package io.xeros.net.login.captcha;

import com.github.cage.Cage;
import io.xeros.Configuration;
import io.xeros.util.Captcha;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CaptchaRequirement {

    @Getter
    private final String captcha;
    @Getter
    private final byte[] image;
    private final long time;
    @Getter
    @Setter
    private int attempts;

    public CaptchaRequirement() throws IOException {
        String captcha = Captcha.generateCaptchaString();
        if (Configuration.LOWERCASE_CAPTCHA)
            captcha = captcha.toLowerCase();
        this.captcha = captcha;
        this.image = Captcha.toByteArray(new Cage().drawImage(this.captcha));
        time = System.currentTimeMillis();
    }

    /**
     * @return true if passes captcha or none required
     */
    public boolean isIncorrect(String captchaInput) {
        addAttempt();
        return !match(captchaInput);
    }

    public boolean match(String captchaInput) {
        return captchaInput.equals(captcha);
    }

    public void addAttempt() {
        attempts++;
    }

    public boolean expired() {
        return System.currentTimeMillis() - time > TimeUnit.MINUTES.toMillis(5);
    }

}
