package io.xeros.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String entered, String actual) {
        return BCrypt.checkpw(entered, actual);
    }
}
