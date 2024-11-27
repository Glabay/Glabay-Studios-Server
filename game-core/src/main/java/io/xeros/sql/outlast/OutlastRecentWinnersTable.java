package io.xeros.sql.outlast;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

public class OutlastRecentWinnersTable implements DatabaseTable {

    public static final String USERNAME = "username";
    public static final String DATE = "date";

    @Override
    public String getName() {
        return "outlast_recent_winners";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner columns = new StringJoiner(",");
        columns.add(USERNAME + " VARCHAR(255)");
        columns.add(DATE + " TIMESTAMP");

        connection.createStatement().execute("CREATE TABLE " + getName() + " (" + columns.toString() + ")");
        connection.createStatement().execute("CREATE INDEX idx_date ON " + getName() + " (" + DATE + ")");
    }
}
