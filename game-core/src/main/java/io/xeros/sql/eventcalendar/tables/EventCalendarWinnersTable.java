package io.xeros.sql.eventcalendar.tables;

import java.sql.Connection;
import java.sql.SQLException;

import io.xeros.sql.DatabaseTable;

public class EventCalendarWinnersTable implements DatabaseTable {

    public static final String USERNAME = "username";
    public static final String DAY = "day";

    @Override
    public String getName() {
        return "calendar_winners";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE " + getName()
                + "(" + USERNAME + " VARCHAR (255),"
                + DAY + " INT NOT NULL"
                + ")"
        );
    }

}
