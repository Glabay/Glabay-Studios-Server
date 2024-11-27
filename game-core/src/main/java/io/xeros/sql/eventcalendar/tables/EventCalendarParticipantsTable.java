package io.xeros.sql.eventcalendar.tables;

import java.sql.Connection;
import java.sql.SQLException;

import io.xeros.sql.DatabaseTable;

public class EventCalendarParticipantsTable implements DatabaseTable {

    public static final String USERNAME = "username";
    public static final String IP_ADDRESS = "ip";
    public static final String MAC_ADDRESS = "mac";
    public static final String ENTRY_DAY = "day";

    @Override
    public String getName() {
        return "calendar_participants";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE " + getName()
                + "(" + USERNAME + " VARCHAR (255),"
                + IP_ADDRESS + " VARCHAR (255),"
                + MAC_ADDRESS + " VARCHAR (255),"
                + ENTRY_DAY + " INT NOT NULL"

                + ", INDEX idx_username (username)"
                + ", INDEX idx_mac (mac)"
                + ", INDEX idx_ip (ip)"
                + ", INDEX idx_day (day)"
                + ")"
        );
    }
}
