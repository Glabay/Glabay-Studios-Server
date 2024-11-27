package io.xeros.content.achievement_diary;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import io.xeros.content.achievement.inter.TasksInterface;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.model.entity.player.Player;

public abstract class StatefulAchievementDiary<T extends Enum<T>> extends
		DifficultyAchievementDiary<T> {

	private final Map<T, Integer> partialAchievements = new HashMap<>();
	public StatefulAchievementDiary(String name, Player player) {
		super(name, player);
	}

	public void progress(T achievement) {
		progress(achievement, true);
		TasksInterface.sendDiaryEntries(player);
	}

	public String getProgressText(EntryDifficulty difficulty) {
		Set<T> set = difficulty == EntryDifficulty.EASY ? getEasy()
				: difficulty == EntryDifficulty.MEDIUM ? getMedium()
				: difficulty == EntryDifficulty.HARD ? getHard()
				: getElite();
		List<Boolean> collect = set.stream().map(it -> {
			int current = getAbsoluteAchievementStage(it);
			int maximum = getMaximum(it);
			return current == maximum;
		}).collect(Collectors.toList());
		int total = collect.size();
		int complete = (int) collect.stream().filter(it -> it).count();
		return complete + "/" + total;
	}

	public void progress(T achievement, boolean notify) {
		if (hasDone(achievement)) {
			return;
		}
		OptionalInt current = getAchievementStage(achievement);
		if (!current.isPresent()) {
			setAchievementStage(achievement, 1, notify); 
		} else {
			int currentStage = current.getAsInt();
			setAchievementStage(achievement, currentStage + 1, notify); 
		}
	}

	public void progress(T achievement, boolean notify, int amount) {
		if (hasDone(achievement)) {
			return;
		}
		OptionalInt current = getAchievementStage(achievement);
		if (!current.isPresent()) {
			setAchievementStage(achievement, amount, notify);
		} else {
			int currentStage = current.getAsInt();
			setAchievementStage(achievement, currentStage + amount, notify);
		}
	}

	public abstract int getMaximum(T achievement);
	
	public final boolean complete(T achievement) {
		boolean success = achievements.add(achievement);
		if (success) {
			uponCompletion(achievement);
			partialAchievements.remove(achievement);
		}
		return success;
	}

	public OptionalInt getAchievementStage(T achievement) {
		Integer result = partialAchievements.get(achievement);
		if (result == null) {
			if (hasDone(achievement)) {
				return OptionalInt.of(getMaximum(achievement));
			}
			return OptionalInt.empty();
		}
		return OptionalInt.of(result);
	}
	
	public int getAbsoluteAchievementStage(T achievement) {
		OptionalInt result = getAchievementStage(achievement);
		if (!result.isPresent()) {
			return 0;
		}
		return result.getAsInt();
	}

	public void setAchievementStage(T achievement, int stage, boolean notify) {
		int maximum = getMaximum(achievement);
		
		if (maximum == -1 || maximum == 0) {
			if (notify) {
				complete(achievement);
			} else {
				nonNotifyComplete(achievement);
			}
			return;
		}
		int wantedStage = stage;
		if (wantedStage >= maximum) {
			if (notify) {
				complete(achievement);
			} else {
				nonNotifyComplete(achievement);
			}
			return;
		}
		partialAchievements.put(achievement, wantedStage);
	}

	public void forEachPartial(BiConsumer<T, Integer> action) {
		partialAchievements.forEach((entry, stage) -> action.accept(entry, stage));
	}
	
	public Map<T, Integer> getPartialAchievements() {
		return partialAchievements;
	}
}
