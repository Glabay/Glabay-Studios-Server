package io.xeros.content.events.eventcalendar;

import java.time.LocalDate;
import java.time.Month;

import io.xeros.util.Misc;

public class DateProviderEventEnded implements DateProvider {


    @Override
    public Month getMonth() {
        if (EventCalendar.MONTH == Month.DECEMBER) {
            return Month.JANUARY;
        } else {
            return Month.of(EventCalendar.MONTH.ordinal() + 2);
        }
    }

    @Override
    public int getDay() {
        return 1;
    }

    @Override
    public int getYear() {
        if (EventCalendar.MONTH == Month.DECEMBER) {
            return EventCalendar.YEAR + 1;
        } else {
            return EventCalendar.YEAR;
        }
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
