package io.xeros.content.events.eventcalendar;

import java.time.LocalDate;
import java.time.Month;

/**
 * An interface that provides methods to get the current date.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public interface DateProvider {

    /**
     * Gets the month.
     * @return the {@link Month}
     */
    Month getMonth();

    /**
     * Gets the day of the month
     * @return the day of the money
     */
    int getDay();

    /**
     * Gets the current year
     * @return the current year
     */
    int getYear();

    /**
     * Check if a date is before the date provided by this {@link DateProvider}
     * @param month the month
     * @param year the year
     * @return <code>true</code> if the date is before the date provided by this {@link DateProvider}
     */
    boolean isBefore(Month month, int year);

    /**
     * Gets the local date.
     */
    LocalDate getLocalDate();
}
