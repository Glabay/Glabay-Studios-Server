package io.xeros.content.achievement.inter;

import io.xeros.model.items.GameItem;

import java.util.List;

public class TaskEntry {

    private final String title;
    private final String description;
    private final boolean claimed;
    private final TaskDifficulty taskDifficulty;
    private final List<GameItem> rewards;
    private final String extraRewards;
    private final List<String> progress;

    public TaskEntry(String title, String description, List<GameItem> rewards, String extraRewards, List<String> progress) {
        this.title = title;
        this.description = description;
        this.claimed = false;
        this.taskDifficulty = TaskDifficulty.NONE;
        this.rewards = rewards;
        this.extraRewards = extraRewards;
        this.progress = progress;
    }

    public TaskEntry(String title, String description, boolean claimed, TaskDifficulty taskDifficulty, List<GameItem> rewards, String extraRewards, List<String> progress) {
        this.title = title;
        this.description = description;
        this.claimed = claimed;
        this.taskDifficulty = taskDifficulty;
        this.rewards = rewards;
        this.extraRewards = extraRewards;
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public TaskDifficulty getTaskDifficulty() {
        return taskDifficulty;
    }

    public List<GameItem> getRewards() {
        return rewards;
    }

    public String getExtraRewards() {
        return extraRewards;
    }

    public List<String> getProgress() {
        return progress;
    }
}
