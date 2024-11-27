package io.xeros.sql.outlast;

import io.xeros.content.tournaments.OutlastRecentWinner;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class OutlastRecentWinnersGetRecent implements SqlQuery<List<OutlastRecentWinner>> {

    private final int limit;

    public OutlastRecentWinnersGetRecent(int limit) {
        this.limit = limit;
    }

    @Override
    public List<OutlastRecentWinner> execute(DatabaseManager context, Connection connection) throws SQLException {
        StringJoiner selects = new StringJoiner(", ");
        selects.add("outlast_recent_winners.username");
        selects.add("outlast_recent_winners.date");
        selects.add("outlast_leaderboard.wins");
        selects.add("display_names.display_name");

        PreparedStatement select = connection.prepareStatement("SELECT " + selects.toString()
                + " FROM outlast_recent_winners"
                + " INNER JOIN outlast_leaderboard ON outlast_leaderboard.username = outlast_recent_winners.username"
                + " INNER JOIN display_names ON outlast_leaderboard.username = display_names.login_name"
                + " ORDER BY outlast_recent_winners.date DESC"
        );

        //PreparedStatement select = connection.prepareStatement("SELECT * FROM outlast_recent_winners, outlast_leaderboard"
         //       + " ORDER BY date DESC GROUP BY username");
        select.setMaxRows(limit);
        ResultSet rs = select.executeQuery();

        ArrayList<OutlastRecentWinner> recentWinners = new ArrayList<>();
        while (rs.next()) {
            String username = rs.getString("username");
            String displayName = rs.getString("display_name");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            int wins = rs.getInt("wins");
            recentWinners.add(new OutlastRecentWinner(username, displayName, wins, date));
        }

        return recentWinners;
    }
}
