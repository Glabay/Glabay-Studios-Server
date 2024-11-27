package io.xeros.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class Captcha {

    private static final Random random = new Random();

    public static byte[] toByteArray(BufferedImage bi) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", baos);
        return baos.toByteArray();
    }

    /**
     *  Generate a CAPTCHA String consisting of random lowercase & uppercase letters, and numbers.
     */
    public static String generateCaptchaString() {
        int length = 5 + (Math.abs(random.nextInt()) % 3);
        StringBuilder captchaStringBuffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char character;
            do {
                int baseCharNumber = Math.abs(random.nextInt()) % 62;
                int charNumber;
                if (baseCharNumber < 26) {
                    charNumber = 65 + baseCharNumber;
                } else if (baseCharNumber < 52) {
                    charNumber = 97 + (baseCharNumber - 26);
                } else {
                    charNumber = 48 + (baseCharNumber - 52);
                }
                character = (char) charNumber;
            } while (character == 'J' || character == 'j' || character == '0'
                    || character == 'o' || character == 'O' || character == 'Q' || character == 'q');
            captchaStringBuffer.append(character);
        }

        return captchaStringBuffer.toString();
    }
}
