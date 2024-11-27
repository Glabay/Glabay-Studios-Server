package io.xeros.content.vote_panel;

import io.xeros.Configuration;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VotePanelInterface {
    /*
        Interface Variables
         */
    public static final int INTERFACE_ID = 24127;
    public static final int ITEM_WIDGET_ID = 24202;
    public static final int TOP_VOTERS_WIDGET_ID = 24191;
    public static final int PIPE_WIDGET_ID = 24138;
    public static final int PENTAGON_WIDGET_START_ID = 24142;
    public static final int DAY_STREAK_TEXT_WIDGET_ID = 24137;

    public static final int BLUE_RED_POINTS_TEXT_WIDGET_ID = 24133;
    public static final int TIME_LEFT_TEXT_WIDGET_ID = 24201;
    public static final int CLAIM_TEXT_WIDGET_ID = 24200;
    public static final int VOTE_KEY_WIDGET_ID = 24136;
    public static final int TEN_PERCENT_WIDGET_TEXT_ID = 24173;
    public static final int THIRTY_BONUS_XP_WIDGET_TEXT_ID = 24167;
    public static final int COST_WIDGET_ID = 24169;
    public static final int BONUS_XP_COST = 2;
    public static final int DR_BOOST_COST = 2;
    public static final int VOTE_CRYSTAL_COST = 1;
    public static final int ULTRA_MYSTERY_BOX_COST = 6;
    private static final String ORANGE_COLOUR = "<col=0xFF9933>";
    private static final Map<Integer, Integer> DAY_TEXT_ID_MAPPINGS = Map.of(
            0, 24154,
            1, 24148,
            2, 24157,
            3, 24151,
            4, 24159
    );
    /**
     * Open interface to the player
     * @param player
     */
    public static void openInterface(Player player, boolean coreUpdateRequired) {
        if (player == null) {
            return;
        }

        if (!Configuration.VOTE_PANEL_ACTIVE) {
            player.sendMessage("Vote panel is disabled.");
            return;
        }

        List<String> topVoters = VotePanelManager.generateTopThree();

        for(int i = 0; i < 3; i++) {
            if (i < topVoters.size()) {
                player.getPA().sendFrame34a(ITEM_WIDGET_ID, VotePanelManager.REWARD_IDS[i], i, 1);
                player.getPA().sendFrame126((i + 1) + ". " + topVoters.get(i), TOP_VOTERS_WIDGET_ID + (i * 2));
            } else {
                player.getPA().sendFrame34a(ITEM_WIDGET_ID, VotePanelManager.REWARD_IDS[i], i, 1);
                player.getPA().sendFrame126("-----", TOP_VOTERS_WIDGET_ID + (i * 2));
            }
        }

        if (coreUpdateRequired) {
            resetInterface(player);
        }

        VoteUser user = VotePanelManager.getUser(player);
        if (user != null) {
            for (int day = 0; day < VoteUser.MAX_DAY_STREAK; day++) {
                if (day < user.getDayStreak()) {
                    if (!DAY_TEXT_ID_MAPPINGS.containsKey(day)) {
                        continue;
                    }
                    var textWidgetId = DAY_TEXT_ID_MAPPINGS.get(day);
                    player.getPA().sendChangeSprite(PENTAGON_WIDGET_START_ID + day, (byte) 1);
                    player.getPA().sendChangeSprite(PIPE_WIDGET_ID + day, (byte) 2);
                    player.getPA().sendFrame126("@gre@Day " + (day + 1), textWidgetId);
                }
            }

            for(int i = 0; i < 2; i++) {
                player.getPA().sendFrame126("" + (i == 0 ? user.getBluePoints() : user.getRedPoints()), BLUE_RED_POINTS_TEXT_WIDGET_ID + i);
            }

            if (user.getPrizeSlot() != -1) {
                player.sendMessage("@gre@You have a vote prize to claim in ::vpanel!");
            }

            player.getPA().sendFrame126((user.getPrizeSlot() != -1 ? "@gre@" : "@or4@") + "Claim Prize", CLAIM_TEXT_WIDGET_ID);
            player.getPA().sendFrame126("Current Streak: @whi@" + user.getDayStreak(), DAY_STREAK_TEXT_WIDGET_ID);
        }

        if (VotePanelManager.hasDropBoost(player)) {
            player.getPA().sendFrame126("+10% DR @gre@" + (60 - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - player.dropBoostStart)) + "m", TEN_PERCENT_WIDGET_TEXT_ID);
        } else {
            player.getPA().sendFrame126("+10% Droprate 1h", TEN_PERCENT_WIDGET_TEXT_ID);
        }

        for(int i = 0; i < 4; i++) {
            player.getPA().sendFrame126("" + (i == 0 ? BONUS_XP_COST : i == 1 ? DR_BOOST_COST : i == 2 ? VOTE_CRYSTAL_COST : ULTRA_MYSTERY_BOX_COST), COST_WIDGET_ID + (i * 6));
        }

        player.getPA().sendFrame126(player.bonusXpTime > 0 ? "+50% XP @gre@" + VotePanelManager.getBonusXPTimeInMinutes(player) + "m"
                : "+50% XP 30m", THIRTY_BONUS_XP_WIDGET_TEXT_ID);


        player.getPA().sendFrame126("Top 3 Voters reset in:\\n" + VotePanelManager.getTimeRemaining(), TIME_LEFT_TEXT_WIDGET_ID);
        //player.getPA().sendFrame126("Vote Key: " + (player.voteKeyPoints >= 30 ? "@gre@" : "@red@") + player.voteKeyPoints + "@gre@/30" + "Votes", VOTE_KEY_WIDGET_ID);
        player.getPA().showInterface(24127);
    }

    /**
     * Resets the interface variables
     * @param player
     */
    private static void resetInterface(Player player) {
        for (int day = 0; day < VoteUser.MAX_DAY_STREAK; day++) {
            if (!DAY_TEXT_ID_MAPPINGS.containsKey(day)) {
                continue;
            }
            var textWidgetId = DAY_TEXT_ID_MAPPINGS.get(day);
            player.getPA().sendFrame126("@red@Day " + (day + 1), textWidgetId);
            player.getPA().sendChangeSprite(PENTAGON_WIDGET_START_ID + day, (byte) 0);
            player.getPA().sendChangeSprite(PIPE_WIDGET_ID + day, (byte) 0);

        }
        for(int i = 0; i < 2; i++) {
            player.getPA().sendFrame126("0", BLUE_RED_POINTS_TEXT_WIDGET_ID + i);
        }
        player.getPA().sendFrame126("Current Streak: @whi@0", DAY_STREAK_TEXT_WIDGET_ID);
        player.getPA().sendFrame126("Claim Prize", CLAIM_TEXT_WIDGET_ID);
    }

    /**
     * Handles all buttons on the interface
     * @param player
     * @param button
     * @return
     */
    public static boolean handleActionButton(Player player, int button) {
        VoteUser user = VotePanelManager.getUser(player);
        boolean bonusWeekend = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
        switch(button) {
            case 94065:
                player.getPA().closeAllWindows();
                return true;
            case 94133:
                claim(player);
                return true;
            case 94100: //30 min xp boost
                if (user == null || user.getBluePoints() < BONUS_XP_COST) {
                    return true;
                }

                if (player.bonusXpTime > 0 || player.xpScrollTicks > 0) {
                    player.sendMessage("You still have an active XP bonus going.");
                    return true;
                }
                if (bonusWeekend) {
                    player.sendMessage("Bonus Weekend is currently active! You can save this for another time.");
                    return true;
                }
                player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, (int) ((player.bonusXpTime/100) + 30));
                user.setBluePoints(user.getBluePoints() - BONUS_XP_COST);
                player.bonusXpTime = Misc.toCycles(30, TimeUnit.MINUTES);
                openInterface(player, false);
                VotePanelManager.saveToJSON();
                return true;
            case 94112: //Vote crystal
                if (user == null || user.getBluePoints() < VOTE_CRYSTAL_COST) {
                    return true;
                }

                if (player.getItems().freeSlots() > 0) {
                    user.setBluePoints(user.getBluePoints() - VOTE_CRYSTAL_COST);
                    openInterface(player, false);
                    VotePanelManager.saveToJSON();
                    player.getItems().addItem(23933, 1);
                } else {
                    player.sendMessage("Please free up an inventory space before doing this.");
                }
                return true;
            case 94106: //10% Dr 1 hr
                if (user == null || user.getBluePoints() < DR_BOOST_COST) {
                    return true;
                }

                if (VotePanelManager.hasDropBoost(player)) {
                    player.sendMessage("You still have an active drop boost going.");
                    return true;
                }

                user.setBluePoints(user.getBluePoints() - DR_BOOST_COST);
                player.dropBoostStart = System.currentTimeMillis();
                openInterface(player, false);
                VotePanelManager.saveToJSON();
                return true;
            case 94118: //Ultra mbox
                if (user == null || user.getRedPoints() < ULTRA_MYSTERY_BOX_COST) {
                    return true;
                }

                if (player.getItems().freeSlots() > 0) {
                    user.setRedPoints(user.getRedPoints() - ULTRA_MYSTERY_BOX_COST);
                    openInterface(player, false);
                    VotePanelManager.saveToJSON();
                    player.getItems().addItem(13346, 1);
                } else {
                    player.sendMessage("Please free up an inventory space before doing this.");
                }
                return true;
        }
        return false;
    }

    /**
     * Handles claiming a reward
     * @param player
     */
    public static void claim(Player player) {
        if (!Configuration.VOTE_PANEL_ACTIVE) {
            player.sendMessage("Vote panel is disabled.");
            return;
        }
        VoteUser user = VotePanelManager.getUser(player);
        if (user != null && user.getPrizeSlot() != -1) {
            if (player.getItems().freeSlots() > 0) {
                player.getItems().addItem(VotePanelManager.REWARD_IDS[user.getPrizeSlot()], 1);
                user.setPrizeSlot(-1);
                player.sendMessage("@gre@You've claimed your prize!");
                openInterface(player, true);
                VotePanelManager.saveToJSON();
            } else {
                player.sendMessage("Please free up an inventory space before doing this.");
            }
        } else {
            player.sendMessage("Last weeks top voters were...");
            if (VotePanelManager.wrapper.getLastWeeksTopVoters().size() == 0) {
                player.sendMessage("There were no voters last week.");
                return;
            }

            for(int i = 0; i < VotePanelManager.wrapper.getLastWeeksTopVoters().size(); i++) {
                player.sendMessage((i + 1) + ") " + VotePanelManager.wrapper.getLastWeeksTopVoters().get(i) + "   ");
            }
        }
    }
}
