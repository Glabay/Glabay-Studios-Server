package io.xeros.net.login.captcha;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginCaptcha {

    private static final Map<String, CaptchaRequirement> CAPTCHA = new HashMap<>();

    public static CaptchaRequirement create(String loginName) throws IOException {
        loginName = loginName.toLowerCase();
        CaptchaRequirement requirement = get(loginName);
        if (requirement == null || requirement.expired())
            CAPTCHA.put(loginName, new CaptchaRequirement());
        return CAPTCHA.get(loginName);
    }

    public static CaptchaRequirement refresh(String loginName) throws IOException {
        loginName = loginName.toLowerCase();
        CaptchaRequirement newCaptcha = new CaptchaRequirement();
        CaptchaRequirement requirement = get(loginName);
        if (requirement != null)
            newCaptcha.setAttempts(newCaptcha.getAttempts());
        CAPTCHA.put(loginName, newCaptcha);
        return CAPTCHA.get(loginName);
    }

    public static CaptchaRequirement get(String loginName) {
        return CAPTCHA.get(loginName.toLowerCase());
    }

    public static void remove(String loginName) {
        CAPTCHA.remove(loginName.toLowerCase());
    }
}
