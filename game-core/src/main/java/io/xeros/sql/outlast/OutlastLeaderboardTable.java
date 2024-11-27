package io.xeros.sql.outlast;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

public class OutlastLeaderboardTable implements DatabaseTable {

    public static final String USERNAME = "username";
    public static final String KILLS = "kills";
    public static final String DEATHS = "deaths";
    public static final String KDR = "kdr";
    public static final String TOTAL_GAMES = "games";
    public static final String WINS = "wins";


    @Override
    public String getName() {
        return "outlast_leaderboard";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner columns = new StringJoiner(", ");
        columns.add(USERNAME + " VARCHAR (255)");
        columns.add(KILLS + " INT NOT NULL");
        columns.add(DEATHS + " INT NOT NULL");
        columns.add(KDR + " DOUBLE NOT NULL");
        columns.add(TOTAL_GAMES + " INT NOT NULL");
        columns.add(WINS + " INT NOT NULL");

        connection.createStatement().execute("CREATE TABLE " + getName() + "(" + columns.toString() + ")");
        connection.createStatement().execute("CREATE INDEX idx_username ON " + getName() + " (" + USERNAME + ")");
        connection.createStatement().execute("CREATE INDEX idx_kills ON " + getName() + " (" + KILLS + ")");
        connection.createStatement().execute("CREATE INDEX idx_kdr ON " + getName() + " (" + KDR + ")");
        connection.createStatement().execute("CREATE INDEX idx_wins ON " + getName() + " (" + WINS + ")");
    }
}
