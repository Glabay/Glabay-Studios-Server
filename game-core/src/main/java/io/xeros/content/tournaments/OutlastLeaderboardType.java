package io.xeros.content.tournaments;

public enum OutlastLeaderboardType {
    WINS("Most Wins"),
    KILLS("Most Kills"),
    KDR("K/D"),
    ;

    private final String displayName;

    OutlastLeaderboardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
