package io.xeros.content.item.lootable.impl;

import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import QuickUltra.Rarity;

/**
 * Revamped a simple means of receiving a random item based on chance.
 * 
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class SlayerMysteryBox extends MysteryBoxLootable {

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(LootRarity.COMMON, //50% chance
				Arrays.asList(
						new GameItem(995, 3_000_000 + Misc.random(3_000_000)), //coins
						new GameItem(537, 95 + Misc.random(30)),//dragon bones
						new GameItem(11937, 200),//dark crabs
						new GameItem(811, 500),//rune dart
						new GameItem(11230, 150),//dragon dart
						new GameItem(8901),//black mask
						new GameItem(4088, 5),//dragon platelegs
						new GameItem(4588, 5),//dragon plateskirt
						new GameItem(4153),//g maul
						new GameItem(13116),//bone crusher
						new GameItem(13226),//herb sack
						new GameItem(23686, 15),// divine combat
						new GameItem(23746, 15),// divine magic
						new GameItem(23734, 15),//divine range
						new GameItem(2435, 50),//prayer pots
						new GameItem(990, 5),//crystal key
						new GameItem(1632, 50)//uncut d stone

				));
			items.put(LootRarity.UNCOMMON, //50% chance
					Arrays.asList(
							new GameItem(6121),//break vial scroll
							new GameItem(2572),//row
							new GameItem(4151),//whip
							new GameItem(6585, 1),//fury
							new GameItem(23943),//elven signet
							new GameItem(406, 5)//pvm casket
					));

			items.put(LootRarity.RARE,//8% chance
					Arrays.asList(
							new GameItem(19835, 2),//master clue
							new GameItem(23951, 2),//crystalline key
							new GameItem(FragmentOfSeren.KEY, 2),//serens key
							new GameItem(TheUnbearable.KEY, 2),//sotetsegs key
							new GameItem(23804),//imbue dust
							new GameItem(12018),//salve ei
							new GameItem(19835, 2),//master clue
							new GameItem(23951, 2),//crystalline key
							new GameItem(FragmentOfSeren.KEY, 2),//serens key
							new GameItem(TheUnbearable.KEY, 2),//sotetsegs key
							new GameItem(23804),//imbue dust
							new GameItem(12018),//salve ei
							new GameItem(20724, 1)//imbued heart


							));
		}

    /**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public SlayerMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 13438;
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}
}