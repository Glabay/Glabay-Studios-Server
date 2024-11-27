package io.xeros.content.tournaments;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.StringJoiner;

public class OutlastLeaderboardEntry {

    private static final DecimalFormat KDR_FORMAT = new DecimalFormat("#.##");

    private static double calculateKDR(int kills, int deaths) {
        return (double) (kills) / (double) (deaths == 0 ? 1 : deaths);
    }

    private final String username;
    private final String displayName;
    private final int kills;
    private final int deaths;
    private final double kdr;
    private final int wins;
    private final int totalGames;

    public OutlastLeaderboardEntry(Player player) {
        this(player.getLoginName(), player.getDisplayName(), player.outlastKills, player.outlastDeaths, calculateKDR(player.outlastKills, player.outlastDeaths), player.tournamentWins, player.tournamentTotalGames);
    }

    public OutlastLeaderboardEntry(String username, String displayName, int kills, int deaths, double kdr, int wins, int totalGames) {
        this.username = username;
        this.displayName = displayName;
        this.kills = kills;
        this.deaths = deaths;
        this.kdr = kdr;
        this.wins = wins;
        this.totalGames = totalGames;
    }

    public String format(OutlastLeaderboardType type) {
        StringJoiner s = new StringJoiner(" - ");
        switch (type) {
            case KDR:
                return s.add(displayName())
                        .add(kdr())
                        .add(kills())
                        .add(deaths())
                        .toString();
            case KILLS:
                return s.add(displayName())
                        .add(kills())
                        .add(deaths())
                        .add(kdr())
                        .toString();
            case WINS:
                return s.add(displayName())
                        .add(wins())
                        .add(games())
                        .toString();
            default: throw new IllegalStateException("No format for " + type);
        }
    }

    @Override
    public String toString() {
        return "TournamentLeaderboardEntry{" +
                "username='" + username + '\'' +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", kdr=" + kdr +
                ", wins=" + wins +
                ", totalGames=" + totalGames +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutlastLeaderboardEntry that = (OutlastLeaderboardEntry) o;
        return kills == that.kills &&
                deaths == that.deaths &&
                Double.compare(that.kdr, kdr) == 0 &&
                wins == that.wins &&
                totalGames == that.totalGames &&
                Objects.equals(username.toLowerCase(), that.username.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase(), kills, deaths, kdr, wins, totalGames);
    }

    private String displayName() {
        return displayName;
    }

    private String kills() {
        return "Kills: " + Misc.insertCommas(kills);
    }

    private String deaths() {
        return "Deaths: " + Misc.insertCommas(deaths);
    }

    private String kdr() {
        return "K/D: " + KDR_FORMAT.format(kdr);
    }

    private String wins() {
        return "Wins: " + Misc.insertCommas(wins);
    }

    private String games() {
        return "Games: " + Misc.insertCommas(totalGames);
    }

    public String getUsername() {
        return username;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getKdr() {
        return kdr;
    }

    public int getWins() {
        return wins;
    }

    public int getTotalGames() {
        return totalGames;
    }
}
