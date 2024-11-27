package io.xeros.sql.outlast;

import io.xeros.content.tournaments.OutlastRecentWinner;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OutlastRecentWinnersAdd implements SqlQuery<Boolean> {

    private final OutlastRecentWinner winner;

    public OutlastRecentWinnersAdd(OutlastRecentWinner winner) {
        this.winner = winner;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement insert = connection.prepareStatement("INSERT INTO outlast_recent_winners VALUES(?, ?)");
        insert.setString(1, winner.getUsername().toLowerCase());
        insert.setTimestamp(2, Timestamp.valueOf(winner.getDate()));
        return insert.execute();
    }
}
