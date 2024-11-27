package io.xeros.content.leaderboards;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaderboardEntry {

    private final String loginName;
    private final String displayName;
    private final LeaderboardType type;
    private final long amount;
    private final LocalDateTime timestamp;

    public LeaderboardEntry(LeaderboardType type, String loginName, long amount, LocalDateTime timestamp) {
        this(type, loginName, loginName, amount, timestamp);
    }

    public LeaderboardEntry(LeaderboardType type, String loginName, String displayName, long amount, LocalDateTime timestamp) {
        this.type = type;
        this.loginName = loginName;
        this.displayName = displayName;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "username='" + loginName + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                '}';
    }

    public String getLoginName() {
        return loginName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LeaderboardType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public LocalDate getLocalDate() {
        return timestamp.toLocalDate();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
