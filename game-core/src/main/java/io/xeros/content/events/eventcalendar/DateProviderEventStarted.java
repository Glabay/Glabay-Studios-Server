package io.xeros.content.events.eventcalendar;

import java.time.LocalDate;
import java.time.Month;

import com.google.common.base.Preconditions;
import io.xeros.util.Misc;

public class DateProviderEventStarted implements DateProvider {

    private final int day;

    public DateProviderEventStarted(int day) {
        Preconditions.checkArgument(EventCalendarDay.forDayOfTheMonth(day) != null, "Invalid day.");
        this.day = day;
    }

    @Override
    public Month getMonth() {
        return EventCalendar.MONTH;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getYear() {
        return EventCalendar.YEAR;
    }

    @Override
    public boolean isBefore(Month month, int year) {
        return Misc.isBefore(month, year, getMonth(), getYear());
    }

    @Override
    public LocalDate getLocalDate() {
        return LocalDate.of(getYear(), getMonth(), getDay());
    }
}
