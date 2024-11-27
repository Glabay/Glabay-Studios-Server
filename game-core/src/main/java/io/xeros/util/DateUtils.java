package io.xeros.util;

import java.time.LocalDateTime;

public class DateUtils {

    public static boolean isFirstWeekOfMonth() {
        return LocalDateTime.now().getDayOfMonth() < 7;
    }

}
