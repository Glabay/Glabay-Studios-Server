package io.xeros.content.skills.herblore;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import io.xeros.content.achievement_diary.DifficultyAchievementDiary;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

/**
 * Items which can be crushed with a Pestle and Mortar
 * 
 * @author Emiel
 *
 */
public enum Crushable {
	LAVA_SCALE(new GameItem(Items.LAVA_SCALE), new GameItem(Items.LAVA_SCALE_SHARD)),
	UNICORN_HORN(new GameItem(237), new GameItem(235)),
	BLUE_DRAGON_SCALE(new GameItem(243), new GameItem(241)),
	CRYSTAL_DUST(new GameItem(Items.CRYSTAL_SHARD, 100), new GameItem(Items.CRYSTAL_DUST, 4)),
	;

	private final GameItem original;
	private final GameItem result;

	public static final int PESTLE = 233;

	Crushable(GameItem original, GameItem result) {
		this.original = original;
		this.result = result;
	}

	private static final Set<Crushable> VALUES = Collections.unmodifiableSet(EnumSet.allOf(Crushable.class));

	public static Optional<GameItem> getResult(int original) {
		return VALUES.stream().filter(c -> c.original.id() == original).map(c -> c.result).findAny();
	}

	public static Optional<GameItem> getOriginal(int original) {
		return VALUES.stream().filter(c -> c.original.id() == original).map(c -> c.original).findAny();
	}

	public static boolean crushIngredient(Player c, int item1, int item2) {
		if (item1 != PESTLE && item2 != PESTLE) {
			return false;
		}
		int ingredient;
		try {
			ingredient = getOther(PESTLE, item1, item2);
		} catch (IllegalArgumentException e) {
			return false;
		}
		Optional<GameItem> result = getResult(ingredient);
		Optional<GameItem> original = getOriginal(ingredient);
		if (result.isPresent() && original.isPresent()) {
			GameItem crushedItem = original.get();
			GameItem resultItem = result.get();

			if (!c.getItems().playerHasItem(crushedItem.id(), crushedItem.amount())) {
				c.sendMessage("You need x" + crushedItem.amount() + " " + ItemDef.forId(crushedItem.id()).getName() + " to grind.");
				return true;
			}

			int resultAmount = resultItem.amount();
			if (resultItem.id() == Items.LAVA_SCALE_SHARD) {
				if (c.getDiaryManager().getWildernessDiary().hasDone(DifficultyAchievementDiary.EntryDifficulty.HARD)) {
					resultAmount = Misc.random(6, 9);
				} else {
					resultAmount = Misc.random(3, 6);
				}
			}

			c.getItems().deleteItem(ingredient, crushedItem.amount());
			c.getItems().addItem(resultItem.id(), resultAmount);
			c.sendMessage("You grind down the " + ItemDef.forId(ingredient).getName().toLowerCase() + " to " + ItemDef.forId(resultItem.id()).getName().toLowerCase() + ".");
			return true;
		}
		return false;
	}

	/**
	 * Find the integer which does not match the notThis integer.
	 * 
	 * @param notThis The integer which is not to be returned.
	 * @param i1 Integer which may or may not equal notThis.
	 * @param i2 Integer which may or may not equal notThis.
	 * @return The integer which does
	 * @throws IllegalArgumentException Thrown in case neither of the 2 integers matches the notThis integer.
	 */
	private static int getOther(int notThis, int i1, int i2) throws IllegalArgumentException {
		if (notThis == i1) {
			return i2;
		} else if (notThis == i2) {
			return i1;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
