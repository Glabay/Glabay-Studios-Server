package io.xeros.sql.leaderboard;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

public class LeaderboardsRewardsTable implements DatabaseTable {

    public static final String ITEM_ID = "item_id";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String PLACE = "place";
    public static final String PERIOD = "period";

    @Override
    public String getName() {
        return "leaderboards_rewards";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner columns = new StringJoiner(", ");
        columns.add(ITEM_ID + " INT NOT NULL");
        columns.add(AMOUNT + " INT NOT NULL");
        columns.add(TYPE + " INT NOT NULL");
        columns.add(PLACE + " INT NOT NULL");
        columns.add(PERIOD + " INT NOT NULL");

        connection.createStatement().execute("CREATE TABLE " + getName() + "(" + columns + ")");
        connection.createStatement().execute("CREATE INDEX idx_item_id ON " + getName() + " (" + ITEM_ID + ")");
        connection.createStatement().execute("CREATE INDEX idx_amount ON " + getName() + " (" + AMOUNT + ")");
        connection.createStatement().execute("CREATE INDEX idx_type ON " + getName() + " (" + TYPE + ")");
        connection.createStatement().execute("CREATE INDEX idx_place ON " + getName() + " (" + PLACE + ")");
        connection.createStatement().execute("CREATE INDEX idx_period ON " + getName() + " (" + PERIOD + ")");
    }
}
