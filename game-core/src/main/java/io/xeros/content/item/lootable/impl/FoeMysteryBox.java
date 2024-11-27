package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

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
public class FoeMysteryBox extends MysteryBoxLootable {

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
					new GameItem(692),//25k foe
					new GameItem(693),//50k foe
					new GameItem(692),//25k foe
					new GameItem(693),//50k foe
					new GameItem(12783),//row i scroll
					new GameItem(13346)//ultra m box
				));
			items.put(LootRarity.UNCOMMON, //50% chance
					Arrays.asList(
							new GameItem(692),//25k foe
							new GameItem(693),//50k foe
							new GameItem(692),//25k foe
							new GameItem(693),//50k foe
							new GameItem(12783),//row i scroll
							new GameItem(13346)//ultra m box
					));

			items.put(LootRarity.RARE,//8% chance
					Arrays.asList(
							new GameItem(23939),//seren
							new GameItem(30010),//postie pet
							new GameItem(30011),//imp
							new GameItem(30012),//bird
							new GameItem(30013),//penguin
							new GameItem(30014),//klik
							new GameItem(30015),//shadow warrior
							new GameItem(30016),//shadow archer
							new GameItem(30017),//shadow wizard
							new GameItem(30018),//healer death spawn
							new GameItem(30019),//holy death spawn
							new GameItem(30020),//corrupt beast
							new GameItem(30021),//roc
							new GameItem(23939),//seren
							new GameItem(30010),//postie pet
							new GameItem(30011),//imp
							new GameItem(30012),//bird
							new GameItem(30013),//penguin
							new GameItem(30014),//klik
							new GameItem(30015),//shadow warrior
							new GameItem(30016),//shadow archer
							new GameItem(30017),//shadow wizard
							new GameItem(30018),//healer death spawn
							new GameItem(30019),//holy death spawn
							new GameItem(30020),//corrupt beast
							new GameItem(30021),//roc
							new GameItem(23939),//seren
							new GameItem(30010),//postie pet
							new GameItem(30011),//imp
							new GameItem(30012),//bird
							new GameItem(30013),//penguin
							new GameItem(30014),//klik
							new GameItem(30015),//shadow warrior
							new GameItem(30016),//shadow archer
							new GameItem(30017),//shadow wizard
							new GameItem(30018),//healer death spawn
							new GameItem(30019),//holy death spawn
							new GameItem(30020),//corrupt beast
							new GameItem(30021),//roc
							new GameItem(23939),//seren
							new GameItem(30010),//postie pet
							new GameItem(30011),//imp
							new GameItem(30012),//bird
							new GameItem(30013),//penguin
							new GameItem(30014),//klik
							new GameItem(30015),//shadow warrior
							new GameItem(30016),//shadow archer
							new GameItem(30017),//shadow wizard
							new GameItem(30018),//healer death spawn
							new GameItem(30019),//holy death spawn
							new GameItem(30020),//corrupt beast
							new GameItem(30021),//roc
							new GameItem(23939),//seren
							new GameItem(30022)//yama

							));
		}

    /**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public FoeMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 8167;
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}
}