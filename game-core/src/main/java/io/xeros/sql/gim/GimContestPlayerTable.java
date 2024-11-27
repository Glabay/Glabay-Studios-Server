package io.xeros.sql.gim;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

public class GimContestPlayerTable implements DatabaseTable {

    public static final String GROUP_NAME = "group_name";
    public static final String PLAYER_NAME = "username";
    public static final String TYPE = "type";
    public static final String VALUE = "value";

    @Override
    public String getName() {
        return "gim_contest_players";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner columns = new StringJoiner(",");
        columns.add(GROUP_NAME + " VARCHAR (255)");
        columns.add(PLAYER_NAME + " VARCHAR (255)");
        columns.add(TYPE + " INT NOT NULL");
        columns.add(VALUE + " BIGINT NOT NULL");
        connection.createStatement().execute("CREATE TABLE " + getName() + "(" + columns.toString() + ")");
        connection.createStatement().execute("CREATE INDEX idx_type ON " + getName() + " (" + TYPE + ")");
        connection.createStatement().execute("CREATE INDEX idx_group_name ON " + getName() + " (" + GROUP_NAME + ")");
        connection.createStatement().execute("CREATE INDEX idx_player_name ON " + getName() + " (" + PLAYER_NAME + ")");
    }
}
