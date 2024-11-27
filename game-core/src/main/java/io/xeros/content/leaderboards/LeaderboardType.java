package io.xeros.content.leaderboards;

public enum LeaderboardType {
    ROGUE_XP("Rogue XP"),
    STANDARD_XP("Standard XP"),
    OUTLAST_WINS("Outlast Wins"),
    COX("CoX"),
    TOB("ToB"),
    NIGHTMARE("Nightmare"),
    HESPORI("Hespori"),
    WILDY_EVENTS("Wildy Event"),
    MOST_BURNED("Most Burned"),
    BOSS_POINTS("Boss Points");

    private final String name;

    LeaderboardType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
