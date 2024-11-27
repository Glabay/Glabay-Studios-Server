package io.xeros.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Simple class that is serialized and then used to determine if a sunday has passed since instantiation.
 */
public class SundayReset {

    private LocalDateTime reset = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).withHour(23).withMinute(59);

    public SundayReset() { }

    public SundayReset(LocalDateTime reset) {
        this.reset = reset;
    }

    public boolean isReset() {
        return LocalDateTime.now().isAfter(reset);
    }

    public String getTimeUntilReset() {
        return Misc.secondsToFormattedCountdown(LocalDateTime.now().until(reset, ChronoUnit.SECONDS));
    }

    public LocalDateTime getReset() {
        return reset;
    }
}
