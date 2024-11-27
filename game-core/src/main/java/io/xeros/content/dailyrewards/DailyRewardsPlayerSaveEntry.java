package io.xeros.content.dailyrewards;

import java.time.LocalDateTime;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

public class DailyRewardsPlayerSaveEntry implements PlayerSaveEntry {

    private static final String LAST_REWARD_DATE = "daily_rewards_claim_date";
    private static final String LAST_REWARD_IDENTIFIER = "daily_rewards_identifier";
    private static final String REWARD_STREAK = "daily_rewards_streak";

    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList(LAST_REWARD_DATE, LAST_REWARD_IDENTIFIER, REWARD_STREAK);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        switch (key) {
            case LAST_REWARD_DATE:
                String[] args = value.split("_");
                player.getDailyRewards().setLastClaimed(LocalDateTime.of(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]), Integer.parseInt(args[4])));
                return true;
            case LAST_REWARD_IDENTIFIER:
                player.getDailyRewards().setLastRewardIdentifier(value);
                return true;
            case REWARD_STREAK:
                player.getDailyRewards().setStreak(Integer.parseInt(value));
                return true;
        }
        return false;
    }

    @Override
    public String encode(Player player, String key) {
        switch (key) {
            case LAST_REWARD_DATE:
                LocalDateTime last = player.getDailyRewards().getLastClaimed();
                if (last == null)
                    return null;
                return last.getYear() + "_" + last.getMonth().getValue() + "_" + last.getDayOfMonth() + "_" + last.getHour() + "_" + last.getMinute();
            case LAST_REWARD_IDENTIFIER:
                return player.getDailyRewards().getLastRewardIdentifier();
            case REWARD_STREAK:
                return String.valueOf(player.getDailyRewards().getStreak());
        }
        return null;
    }

    @Override
    public void login(Player player) { }
}
