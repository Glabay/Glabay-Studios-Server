package io.xeros.content.achievement.inter;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.DifficultyAchievementDiary;
import io.xeros.content.achievement_diary.StatefulAchievementDiary;
import io.xeros.content.achievement_diary.impl.VarrockAchievementDiary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TasksInterface {

    /**
     * Generates achievements json to /temp/achievements.json.
     */
    public static void main(String...args) throws Exception {
        Server.startServerless();
        Files.createDirectories(Path.of("temp"));
        JsonUtil.toJacksonJson(TasksInterface.getAchievements(null), "./temp/achievements.json");
        System.exit(0);
    }

    private static List<String> getAchievementProgress(Player player, Achievements.Achievement achievement) {
        int maxProgress = achievement.getAmount();
        int currentProgress = player.getAchievements().getAmountRemaining(achievement.getTier().getId(), achievement.getId());
        if (currentProgress > maxProgress)
            currentProgress = maxProgress;
        return Lists.newArrayList(currentProgress + "/" + maxProgress);
    }

    public static void updateProgress(Player player, String type, Achievements.Achievement achievement) {
        player.getPA().runClientScript(2, type,
                achievement.getFormattedName(),
                JsonUtil.toJson(getAchievementProgress(player, achievement)),
                player.getAchievements().isClaimed(achievement.getTier().getId(), achievement.getId()) ? 1 : 0
        );
    }

    public static void sendAchievementsEntries(Player player) {
        for (Achievements.Achievement achievement : Achievements.Achievement.values()) {
            TasksInterface.updateProgress(player, "achievements", achievement);
        }
    }

    public static void sendDiaryEntries(Player player) {
        player.getPA().runClientScript(1, "diaries", "reset");

        List<TaskEntry> entries = player.getDiaryManager().getAll().stream().map(diary -> {
            List<String> progress = Lists.newArrayList(
                    diary.getProgressText(DifficultyAchievementDiary.EntryDifficulty.EASY),
                    diary.getProgressText(DifficultyAchievementDiary.EntryDifficulty.MEDIUM),
                    diary.getProgressText(DifficultyAchievementDiary.EntryDifficulty.HARD),
                    diary.getProgressText(DifficultyAchievementDiary.EntryDifficulty.ELITE)
            );

            return new TaskEntry(diary.getName().replace(" area", ""),
                    "",
                    null,
                    "",
                    progress
            );
        }).collect(Collectors.toList());

        player.getPA().runClientScript(1, "diaries", JsonUtil.toJson(entries));

        player.getPA().runClientScript(1, "diaries", "draw");
    }

    public static List<TaskEntry> getAchievements(Player player) {
        return Arrays.stream(Achievements.Achievement.values()).map(it -> {
            TaskDifficulty difficulty = it.getTier() == AchievementTier.TIER_1 ? TaskDifficulty.BEGINNER
                    : it.getTier() == AchievementTier.TIER_2 ? TaskDifficulty.INTERMEDIATE
                    : it.getTier() == AchievementTier.TIER_3 ? TaskDifficulty.EXPERT
                    : it.getTier() == AchievementTier.STARTER ? TaskDifficulty.STARTER
                    : TaskDifficulty.LEGENDARY;

            String extraRewards = Arrays.stream(it.getRewards()).map(reward -> reward.getFormattedName(false))
                    .map(reward -> {
                        if (reward.length() > 22)
                            return reward.substring(0, 21) + "..";
                        else return reward;
                    })
                    .collect(Collectors.joining("\\n"));

            boolean claimed = player != null && (player.getAchievements().isComplete(it.getTier().getId(), it.getId())
                    && player.getAchievements().isClaimed(it.getTier().getId(), it.getId()));

            return new TaskEntry(
                    it.getFormattedName(),
                    it.getDescription(),
                    claimed,
                    difficulty,
                    Lists.newArrayList(it.getRewards()),
                    extraRewards,
                    player == null ? Lists.newArrayList("0/1") : getAchievementProgress(player, it)
            );
        }).collect(Collectors.toList());
    }
}
