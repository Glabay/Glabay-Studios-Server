package io.xeros.content.dailyrewards;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;
import io.xeros.util.logging.global.DailyRewardsCompletedLog;
import io.xeros.util.logging.player.DailyRewardLog;

public class DailyRewards {

    public static final int CLAIM_BUTTON = 23_721;
    public static final String COLOR = "<col=7a2100>";
    private static final int INTERFACE_ID = 23_680;
    private static final int DAILY_REWARD_STREAK_CONFIG = 1371;
    private static final int CLAIM_REWARD_BUTTON_TEXT_INTERFACE_ID = 23_722;
    private static final int MINUTES_BETWEEN_CLAIMS = 24 * 60;
    private static final int CURRENT_REWARD_ITEM_CONTAINER_INTERFACE_ID = 23_719;
    private static final int NEXT_REWARD_ITEM_CONTAINER_INTERFACE_ID = 23_720;
    private static final int REWARD_LIST_ITEM_CONTAINER_INTERFACE_ID = 23_718;
    private static final int ITEM_BOXES_START_INTERFACE_ID = 23686;
    private static final int ITEM_BOXES_END_INTERFACE_ID = 23717;

    private final Player player;
    private LocalDateTime lastClaimed = null;
    private String lastRewardIdentifier = null;
    private int streak = 0;
    private boolean notify = false;

    public DailyRewards(Player player) {
        this.player = player;
    }

    public void onLogin() {
        notifyWhenReady(true);
    }

    public void notifyWhenReady(boolean login) {
        if (getMinutesUntilClaim() > 0) {
            notify = true;
        } else if ((notify || login) && getMinutesUntilClaim() == 0) {
            notify = false;
            player.sendMessage(COLOR + "Your daily reward is ready to be claimed.");
        }
    }

    public void openInterface() {
        if (DailyRewardContainer.get().getRewards().isEmpty()) {
            player.sendMessage("There are no daily rewards for now, check back later!");
            return;
        }

        // Reset rewards progress if the identifier changed for the rewards container (identifier differentiates between the month, event, etc)
        if (!DailyRewardContainer.get().getIdentifier().equals(lastRewardIdentifier)) {
            lastRewardIdentifier = DailyRewardContainer.get().getIdentifier();
            lastClaimed = null;
            streak = 0;
        }

        if (streak == 0) {
            player.sendMessage(COLOR + "Warning: you can only have one account that claims daily rewards.");
        }

        List<ImmutableItem> rewards = DailyRewardContainer.get().getRewards();
        boolean completed = streak == rewards.size();

        for (int interfaceId = ITEM_BOXES_START_INTERFACE_ID; interfaceId <= ITEM_BOXES_END_INTERFACE_ID; interfaceId++) {
            int index = interfaceId - ITEM_BOXES_START_INTERFACE_ID;
            player.getPA().sendInterfaceHidden(interfaceId, index >= rewards.size());
        }

        // Update 'claim' button based on whether we can claim again
        if (completed) {
            player.getPA().sendString("@gre@Completed", CLAIM_REWARD_BUTTON_TEXT_INTERFACE_ID);
        } else {
            long minutesUntilClaim = getMinutesUntilClaim();
            if (minutesUntilClaim == 0) {
                player.getPA().sendString("@gre@Claim Reward", CLAIM_REWARD_BUTTON_TEXT_INTERFACE_ID);
            } else {
                player.getPA().sendString(getFormattedTimeLeft(), CLAIM_REWARD_BUTTON_TEXT_INTERFACE_ID);
            }
        }

        // Update reward displays
        player.getItems().sendImmutableItemContainer(REWARD_LIST_ITEM_CONTAINER_INTERFACE_ID, rewards); // Full list
        if (streak >= rewards.size()) {
            player.getItems().sendImmutableItemContainer(CURRENT_REWARD_ITEM_CONTAINER_INTERFACE_ID, Lists.newArrayList(new ImmutableItem(-1, 0)));
            player.getItems().sendImmutableItemContainer(NEXT_REWARD_ITEM_CONTAINER_INTERFACE_ID, Lists.newArrayList(new ImmutableItem(-1, 0)));
        } else {
            player.getItems().sendImmutableItemContainer(CURRENT_REWARD_ITEM_CONTAINER_INTERFACE_ID, Lists.newArrayList(rewards.get(streak)));

            if (streak + 1 >= rewards.size()) {
                player.getItems().sendImmutableItemContainer(NEXT_REWARD_ITEM_CONTAINER_INTERFACE_ID, Lists.newArrayList(new ImmutableItem(-1, 0)));
            } else {
                player.getItems().sendImmutableItemContainer(NEXT_REWARD_ITEM_CONTAINER_INTERFACE_ID, Lists.newArrayList(rewards.get(streak + 1)));
            }
        }

        player.getPA().sendConfig(DAILY_REWARD_STREAK_CONFIG, completed ? 50 : streak);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public void claim() {
        boolean developer = Server.isDebug() && player.getRights().contains(Right.OWNER);
        long minutesUntilClaim = getMinutesUntilClaim();
        if (minutesUntilClaim == 0 || developer) {
            if (streak == DailyRewardContainer.get().getRewards().size()) {
                player.sendMessage(COLOR + "You've already claimed all the rewards, try again next month!");
            } else {
                if (DailyRewardsRecords.canClaim(player) || developer) {
                    if (developer) {
                        player.sendMessage("You are a developer and have no restriction on claiming rewards.");
                    }
                    DailyRewardsRecords.add(player, streak);
                    ImmutableItem item = DailyRewardContainer.get().getRewards().get(streak);
                    Server.getLogging().write(new DailyRewardLog(player, streak, item));
                    player.sendMessage(COLOR + "You receive x" + Misc.formatCoins(item.getAmount()) + " " + ItemDef.forId(item.getId()).getName() + ", be sure to check back tomorrow!");
                    Achievements.increase(player, AchievementType.DAILY, 1);
                    player.getInventory().addAnywhere(item);
                    lastClaimed = LocalDateTime.now();
                    lastRewardIdentifier = DailyRewardContainer.get().getIdentifier();
                    streak++;
                    openInterface();
                    if (streak == DailyRewardContainer.get().getRewards().size()) {
                        Server.getLogging().write(new DailyRewardsCompletedLog(player.getLoginName()));
                        player.sendMessage(COLOR + "You've finished the daily rewards for this month, thanks for playing!");
                    }
                } else {
                    player.sendMessage(COLOR + "You have already claimed a reward in the last 24 hours.");
                }
            }
        } else {
            player.sendMessage(COLOR + "You need to wait " + getFormattedTimeLeft() + COLOR + " to claim your next prize!");
        }
    }

    private String getFormattedTimeLeft() {
        long minutesUntilClaim = getMinutesUntilClaim();
        long hours = minutesUntilClaim / 60; //since both are ints, you get an int
        long minutes = minutesUntilClaim % 60;
        return String.format("@red@%d:%02d", hours, minutes);
    }

    private long getMinutesUntilClaim() {
        if (lastClaimed != null) {
            long minutesElapsed = ChronoUnit.MINUTES.between(lastClaimed, LocalDateTime.now());
            if (minutesElapsed > MINUTES_BETWEEN_CLAIMS) {
                return 0;
            } else {
                return MINUTES_BETWEEN_CLAIMS - minutesElapsed;
            }
        }

        return 0;
    }

    LocalDateTime getLastClaimed() {
        return lastClaimed;
    }

    void setLastClaimed(LocalDateTime lastClaimed) {
        this.lastClaimed = lastClaimed;
    }

    String getLastRewardIdentifier() {
        return lastRewardIdentifier;
    }

    void setLastRewardIdentifier(String lastRewardIdentifier) {
        this.lastRewardIdentifier = lastRewardIdentifier;
    }

    int getStreak() {
        return streak;
    }

    void setStreak(int streak) {
        this.streak = streak;
    }
}
