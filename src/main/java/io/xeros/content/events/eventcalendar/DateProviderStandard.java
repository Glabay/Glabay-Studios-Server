package io.xeros.content.events.eventcalendar;

import java.time.LocalDate;
import java.time.Month;

import io.xeros.util.Misc;

public class DateProviderStandard implements DateProvider {

    @Override
    public Month getMonth() {
        return Misc.getMonth();
    }

    @Override
    public int getDay() {
        return Misc.getDayOfTheMonth();
    }

    @Override
    public int getYear() {
        return Misc.getYear();
    }

    @Override
    public boolean isBefore(Month month, int year) {
        return Misc.isBefore(month, year);
    }

    @Override
    public LocalDate getLocalDate() {
        return LocalDate.of(getYear(), getMonth(), getDay());
    }
}
