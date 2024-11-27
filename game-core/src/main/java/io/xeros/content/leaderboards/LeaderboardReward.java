package io.xeros.content.leaderboards;

import io.xeros.model.items.GameItem;

public class LeaderboardReward {
    private final LeaderboardType type;
    private final LeaderboardPeriodicity period;
    private final GameItem item;
    private final int place;

    public LeaderboardReward(LeaderboardType type, LeaderboardPeriodicity period, int place, GameItem item) {
        this.type = type;
        this.period = period;
        this.place = place;
        this.item = item;
    }

    // For Jackson
    private LeaderboardReward() {
        type = null;
        period = null;
        item = null;
        place = 0;
    }

    public LeaderboardType getType() {
        return type;
    }

    public LeaderboardPeriodicity getPeriod() {
        return period;
    }

    public GameItem getItem() {
        return item;
    }

    public int getPlace() {
        return place;
    }

    @Override
    public String toString() {
        return "LeaderboardReward{" +
                "type=" + type +
                ", period=" + period +
                ", place=" + place +
                ", item=" + item +
                '}';
    }
}
