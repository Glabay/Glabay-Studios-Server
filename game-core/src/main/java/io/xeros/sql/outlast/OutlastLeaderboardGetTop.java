package io.xeros.sql.outlast;

import io.xeros.content.tournaments.OutlastLeaderboardType;
import io.xeros.content.tournaments.OutlastLeaderboardEntry;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.util.Misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OutlastLeaderboardGetTop implements SqlQuery<List<OutlastLeaderboardEntry>> {

    private final OutlastLeaderboardType type;
    private final int limit;

    public OutlastLeaderboardGetTop(OutlastLeaderboardType type, int limit) {
        this.type = type;
        this.limit = limit;
    }

    @Override
    public List<OutlastLeaderboardEntry> execute(DatabaseManager context, Connection connection) throws SQLException {
        ArrayList<OutlastLeaderboardEntry> leadersList = new ArrayList<>();

        PreparedStatement leaders = connection.prepareStatement("SELECT * FROM outlast_leaderboard "
                + " INNER JOIN display_names ON outlast_leaderboard.username = display_names.login_name"
                + " ORDER BY " + getOrderBy()
        );

        leaders.setMaxRows(limit);
        ResultSet rs = leaders.executeQuery();
        while (rs.next()) {
            String username = rs.getString("username");
            String displayName = rs.getString("display_name");
            double kdr = rs.getDouble("kdr");
            int kills = rs.getInt("kills");
            int deaths = rs.getInt("deaths");
            int wins = rs.getInt("wins");
            int totalGames = rs.getInt("games");
            leadersList.add(new OutlastLeaderboardEntry(Misc.formatPlayerName(username), displayName, kills, deaths, kdr, wins, totalGames));
        }

        return leadersList;
    }

    private String getOrderBy() {
        switch (type) {
            case KILLS:
                return "kills DESC";
            case WINS:
                return "wins DESC";
            case KDR:
                return "kdr DESC";
            default: throw new IllegalStateException();
        }
    }

}
