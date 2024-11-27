package io.xeros.util.dateandtime;

import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

public class TimeSpan {

    private final TimeUnit unit;
    private final long duration;

    public TimeSpan(TimeUnit unit, long duration) {
        this.unit = unit;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return Misc.secondsToFormattedCountdown(unit.toSeconds(duration));
    }

    /**
     * Add {@link System#currentTimeMillis()} to {@link TimeSpan#toMillis()}.
     */
    public long offsetCurrentTimeMillis() {
        return System.currentTimeMillis() + toMillis();
    }

    public long toMillis() {
        return unit.toMillis(duration);
    }

    public int toTicks() {
        return Misc.toCycles(duration, unit);
    }
}
