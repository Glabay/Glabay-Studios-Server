package io.xeros.sql.outlast;

import io.xeros.content.tournaments.OutlastLeaderboardEntry;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OutlastLeaderboardAdd implements SqlQuery<Boolean> {

    private final OutlastLeaderboardEntry entry;

    public OutlastLeaderboardAdd(OutlastLeaderboardEntry entry) {
        this.entry = entry;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement select = connection.prepareStatement("SELECT * FROM outlast_leaderboard WHERE username = ?");
        select.setString(1, entry.getUsername().toLowerCase());
        ResultSet rs = select.executeQuery();

        if (rs.next()) {
            PreparedStatement delete = connection.prepareStatement("DELETE FROM outlast_leaderboard WHERE username = ?");
            delete.setString(1, entry.getUsername().toLowerCase());
            delete.execute();
        }

        PreparedStatement insert = connection.prepareStatement("INSERT INTO outlast_leaderboard VALUES(?, ?, ?, ?, ?, ?)");
        insert.setString(1, entry.getUsername().toLowerCase());
        insert.setInt(2, entry.getKills());
        insert.setInt(3, entry.getDeaths());
        insert.setDouble(4, entry.getKdr());
        insert.setInt(5, entry.getTotalGames());
        insert.setInt(6, entry.getWins());
        return insert.execute();
    }
}
