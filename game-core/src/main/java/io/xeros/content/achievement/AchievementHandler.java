package io.xeros.content.achievement;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.xeros.Server;
import io.xeros.content.achievement.Achievements.Achievement;
import io.xeros.content.achievement.inter.TasksInterface;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ClaimAchievementLog;

/**
 * @author Jason MacKeigan (http://www.rune-server.org/members/Jason)
 */
public class AchievementHandler {

    public static final String COLOR = "074091";

    private static final int START_BUTTON_ID = 24_901;
    private static final int BUTTON_SEPARATION = 13;

    private static final int MAXIMUM_TIER_ACHIEVEMENTS = 100;
    private static final int MAXIMUM_TIERS = AchievementTier.values().length;

    private final int[][] amountRemaining = new int[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private final boolean[][] completed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private final boolean[][] claimed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];

    private final Player player;
    public int points;
    private boolean firstAchievementLoginJune2021;

    public AchievementHandler(Player player) {
        this.player = player;
    }

    public void onLogin() {
        fixKc(AchievementType.COX, player.totalRaidsFinished);
        fixKc(AchievementType.TOB, player.tobCompletions);
        fixKc(AchievementType.GROTESQUES, player.getNpcDeathTracker().getKc("grotesque guardians"));
        fixKc(AchievementType.NIGHTMARE, player.getNpcDeathTracker().getKc("the nightmare"));
        fixKc(AchievementType.HYDRA, player.getNpcDeathTracker().getKc("alchemical hydra"));
        fixKc(AchievementType.HUNLLEF, player.getNpcDeathTracker().getKc("crystalline hunllef"));
        fixKc(AchievementType.MIMIC, player.getNpcDeathTracker().getKc("the mimic"));

        // Ultimate ironman achievements to autocomplete (but no rewards)
        if (player.getMode().getType() == ModeType.ULTIMATE_IRON_MAN) {
            AchievementType[] autocomplete = { AchievementType.PRESETS, AchievementType.TOURNAMENT };
            for (Achievement achievement : Achievement.values()) {
                if (Arrays.stream(autocomplete).anyMatch(it -> achievement.getType() == it)) {
                    setComplete(achievement, true);
                    setClaimed(achievement, true);
                    setAmountRemaining(achievement, achievement.getAmount());
                }
            }
        }

        // This is a fix for someone having a complete achievement marked as incomplete.
        for (Achievement achievement : Achievement.values()) {
            if (!isComplete(achievement)) {
                int remaining = getAmountRemaining(achievement);
                int total = achievement.getAmount();
                if (remaining == total) {
                    setComplete(achievement, true);
                }
            }
        }

        if (!firstAchievementLoginJune2021) {
            firstAchievementLoginJune2021 = true;

            // Reset because amount required to complete changed
            Achievement[] unset = {
                Achievement.Farming_Task_I, Achievement.INTERMEDIATE_FARMER, Achievement.EXPERT_FARMER
            };

            Arrays.stream(unset).forEach(it -> {
                if (isComplete(it.getTier().getId(), it.getId()) && getAmountRemaining(it.getTier().getId(), it.getId()) < it.getAmount()) {
                    setComplete(it.getTier().getId(), it.getId(), false);
                    setClaimed(it.getTier().getId(), it.getId(), false);
                    player.sendMessage(Misc.colorWrap(COLOR, "Set achievement '" + it.getFormattedName() + "' to incomplete because amount required has changed"));
                }
            });

            // Determine highest 'amount' in each achievementthat shares a type,
            // set all of achievement type to that amount (they stop ticking when complete, so they will have different values)
            List<List<Achievement>> unsetGrouped = Arrays.stream(AchievementType.values())
                    .map(it -> Arrays.stream(unset).filter(group -> group.getType() == it)
                    .collect(Collectors.toList()))
                    .filter(it -> !it.isEmpty())
                    .collect(Collectors.toList());

            unsetGrouped.forEach(group -> {
                group.sort((a, b) -> Integer.compare(getAmountRemaining(b), getAmountRemaining(a))); // Reversed on purpose
                int highest = getAmountRemaining(group.get(0));
                group.forEach(it -> {
                    setAmountRemaining(it, highest);
                    if (getAmountRemaining(it) >= it.getAmount()) {
                        setAmountRemaining(it, it.getAmount());
                        setComplete(it, true);
                    }
                });
            });

            // Add rewards again because they changed
            Achievement[] reward = {
                    Achievement.Farming_Task_I, Achievement.INTERMEDIATE_FARMER, Achievement.EXPERT_FARMER
            };

            Arrays.stream(reward).forEach(achievement -> {
                if (isComplete(achievement.getTier().getId(), achievement.getId())) {
                    Achievements.addReward(player, achievement);
                    setClaimed(achievement, true);
                    player.sendMessage(Misc.colorWrap(COLOR, "Reclaimed achievement '" + achievement.getFormattedName() + "' because rewards have changed."));
                }
            });
        }
    }

    public void print(BufferedWriter writer, int tier) {
        try {
            for (Achievement achievement : Achievement.ACHIEVEMENTS) {
                if (achievement.getTier().getId() == tier) {
                    if (amountRemaining[tier][achievement.getId()] > 0) {
                        writer.write(achievement.name().toLowerCase() + " = "
                                + amountRemaining[tier][achievement.getId()]
                                + "\t" + completed[tier][achievement.getId()]
                                + "\t" + claimed[tier][achievement.getId()]
                        );
                        writer.newLine();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * If achievement is less than kc, tick achievement by kc.
     */
    private void fixKc(AchievementType type, int kc) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getType() == type) {
                int amount = kc - getAmountRemaining(achievement);
                if (amount > 0) {
                    Achievements.increase(player, achievement.getType(), amount);
                }
            }
        }
    }

    public void readFromSave(String name, String[] data, AchievementTier tier) {
        int amount = Integer.parseInt(data[0]);
        boolean complete = Boolean.parseBoolean(data[1]);
        boolean claimed = data.length >= 3 ? Boolean.parseBoolean(data[2]) : complete; // Set to complete because it was auto claimed
        read(name, tier.getId(), amount, complete, claimed);
    }

    private void read(String name, int tier, int amount, boolean complete, boolean claimed) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getTier().getId() == tier) {
                if (achievement.name().toLowerCase().equals(name)) {
                    this.setComplete(tier, achievement.getId(), complete);
                    this.setAmountRemaining(tier, achievement.getId(), amount);
                    this.setClaimed(tier, achievement.getId(), claimed);
                    break;
                }
            }
        }
    }

    public void kill(NPC npc) {
        String name = npc.getNpcStats().getName();
        if (name == null || name.length() <= 0) {
            return;
        } else {
            name = name.toLowerCase().replaceAll("_", " ");
        }
        Achievements.increase(player, AchievementType.SLAY_ANY_NPCS, 1);
        if ((name.contains("dragon") || name.contains("vorkath")) && !name.contains("baby"))
            Achievements.increase(player, AchievementType.SLAY_DRAGONS, 1);
        List<String> checked = new ArrayList<>();
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (!achievement.getType().name().toLowerCase().contains("kill"))
                continue;
            if (achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "").equalsIgnoreCase(name)) {
                if (checked.contains(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "")))
                    continue;
                Achievements.increase(player, achievement.getType(), 1);
                checked.add(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", ""));
            }
        }
    }

    public boolean hasCompletedAll() {
        int amount = 0;
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getTier().getId(), achievement.getId()))
                amount++;
        }
        return amount == Achievements.getMaximumAchievements();
    }

    public boolean clickButton(int buttonId) {
        Achievement[] achievements = Achievement.values();
        for (int index = 0; index < achievements.length; index++) {
            int achievementButton = START_BUTTON_ID + (index * BUTTON_SEPARATION);

            if (buttonId != achievementButton) {
                continue;
            }

            Achievement achievement = achievements[index];
            if (!isComplete(achievement.getTier().getId(), achievement.getId())) {
                // We had an issue where achievements would not be set to complete when they were actually completed.
                // This will force them to completed when you claim them if the amount required has been achieved.
                int amountRequired = achievement.getAmount();
                if (getAmountRemaining(achievement) >= amountRequired) {
                    setComplete(achievement, true);
                } else {
                    player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                    return true;
                }
            }

            if (isClaimed(achievement.getTier().getId(), achievement.getId())) {
                player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                return true;
            }

            Achievements.addReward(player, achievement);
            setClaimed(achievement.getTier().getId(), achievement.getId(), true);
            TasksInterface.updateProgress(player, "achievements", achievement);
            player.sendMessage("<col=" + COLOR + ">Claimed the " + achievement.getTier().getName().toLowerCase()
                    + " achievement '" + achievement.getFormattedName() + "'.</col>");
            Server.getLogging().write(new ClaimAchievementLog(player, achievement));
            return true;
        }

        return false;
    }

    public boolean isComplete(Achievement achievement) {
        return isComplete(achievement.getTier().getId(), achievement.getId());
    }

    public boolean isComplete(int tier, int index) {
        return completed[tier][index];
    }

    public boolean setComplete(Achievement achievement, boolean state) {
        return setComplete(achievement.getTier().getId(), achievement.getId(), state);
    }

    public boolean setComplete(int tier, int index, boolean state) {
        return this.completed[tier][index] = state;
    }

    public int getAmountRemaining(Achievement achievement) {
        return getAmountRemaining(achievement.getTier().getId(), achievement.getId());
    }

    public int getAmountRemaining(int tier, int index) {
        return amountRemaining[tier][index];
    }

    public void setAmountRemaining(Achievement achievement, int amountRemaining) {
        setAmountRemaining(achievement.getTier().getId(), achievement.getId(), amountRemaining);
    }

    public void setAmountRemaining(int tier, int index, int amountRemaining) {
        this.amountRemaining[tier][index] = amountRemaining;
    }

    public boolean isClaimed(int tier, int index) {
        return claimed[tier][index];
    }

    public boolean setClaimed(Achievement achievement, boolean state) {
        return setClaimed(achievement.getTier().getId(), achievement.getId(), state);
    }

    public boolean setClaimed(int tier, int index, boolean state) {
        return this.claimed[tier][index] = state;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isFirstAchievementLoginJune2021() {
        return firstAchievementLoginJune2021;
    }

    public void setFirstAchievementLoginJune2021(boolean firstAchievementLoginJune2021) {
        this.firstAchievementLoginJune2021 = firstAchievementLoginJune2021;
    }
}
