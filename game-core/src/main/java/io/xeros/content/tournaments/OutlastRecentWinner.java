package io.xeros.content.tournaments;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.time.LocalDateTime;

public class OutlastRecentWinner {

    private final String username;
    private final String displayName;
    private final int wins;
    private final LocalDateTime date;

    public OutlastRecentWinner(Player player) {
        this(player.getLoginName(), player.getDisplayName(), 0, LocalDateTime.now()); // wins is ignored here
    }

    public OutlastRecentWinner(String username, String displayName, int wins, LocalDateTime date) {
        this.username = username;
        this.displayName = displayName;
        this.wins = wins;
        this.date = date;
    }

    @Override
    public String toString() {
        return "OutlastRecentWinner{" +
                "username='" + username + '\'' +
                ", wins=" + wins +
                ", date=" + date +
                '}';
    }

    public String format() {
        return Misc.formatPlayerName(displayName) + " - " + "Wins: " + Misc.insertCommas(wins);
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getWins() {
        return wins;
    }
}
