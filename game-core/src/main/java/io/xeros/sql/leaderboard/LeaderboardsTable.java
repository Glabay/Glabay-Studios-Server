package io.xeros.sql.leaderboard;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

public class LeaderboardsTable implements DatabaseTable {

    public static final String USERNAME = "username";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String DATE = "date";


    @Override
    public String getName() {
        return "leaderboards";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner columns = new StringJoiner(", ");
        columns.add(USERNAME + " VARCHAR (255)");
        columns.add(AMOUNT + " INT NOT NULL");
        columns.add(TYPE + " INT NOT NULL");
        columns.add(DATE + " DATE DEFAULT (CURRENT_DATE)");

        connection.createStatement().execute("CREATE TABLE " + getName() + "(" + columns.toString() + ")");
        connection.createStatement().execute("CREATE INDEX idx_username ON " + getName() + " (" + USERNAME + ")");
        connection.createStatement().execute("CREATE INDEX idx_amount ON " + getName() + " (" + AMOUNT + ")");
        connection.createStatement().execute("CREATE INDEX idx_type ON " + getName() + " (" + TYPE + ")");
        connection.createStatement().execute("CREATE INDEX idx_date ON " + getName() + " (" + DATE + ")");
    }
}
