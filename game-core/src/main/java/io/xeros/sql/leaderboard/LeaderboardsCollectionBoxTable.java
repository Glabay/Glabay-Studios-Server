package io.xeros.sql.leaderboard;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

public class LeaderboardsCollectionBoxTable implements DatabaseTable {

    public static final String ITEM_ID = "item_id";
    public static final String AMOUNT = "amount";
    public static final String USERNAME = "username";
    public static final String CLAIMED = "claimed";

    @Override
    public String getName() {
        return "leaderboards_collection";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner columns = new StringJoiner(", ");
        columns.add(USERNAME + " VARCHAR (255)");
        columns.add(ITEM_ID + " INT NOT NULL");
        columns.add(AMOUNT + " INT NOT NULL");
        columns.add(CLAIMED + " INT NOT NULL default 0");

        connection.createStatement().execute("CREATE TABLE " + getName() + "(" + columns + ")");
        connection.createStatement().execute("CREATE INDEX idx_username ON " + getName() + " (" + USERNAME + ")");
        connection.createStatement().execute("CREATE INDEX idx_item_id ON " + getName() + " (" + ITEM_ID + ")");
        connection.createStatement().execute("CREATE INDEX idx_amount ON " + getName() + " (" + AMOUNT + ")");
        connection.createStatement().execute("CREATE INDEX idx_claimed ON " + getName() + " (" + CLAIMED + ")");
    }
}
