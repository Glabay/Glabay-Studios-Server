package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.definition.Items;
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
public class YoutubeMysteryBox extends MysteryBoxLootable {

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
						new GameItem(21046, 5),//chest rate bonus tablets
						new GameItem(10835 , 3),//buldging bags
						new GameItem(2528),//exp lamp
						new GameItem(10835, 5),//buldging bag
						new GameItem(23933, 5),//vote crystals
						new GameItem(Items.DRAGON_BONES_NOTED, 100),//d bones
						new GameItem(Items.ABYSSAL_WHIP),//whip
						new GameItem(Items.DRAGON_DEFENDER),//dragon defender
						new GameItem(Items.DRAGON_AXE),//
						new GameItem(Items.FIGHTER_TORSO),//
						new GameItem(Items.AMULET_OF_FURY),//
						new GameItem(Items.DRAGON_CHAINBODY),//
						new GameItem(Items.DRAGON_PLATELEGS),//
						new GameItem(Items.BANDOS_BOOTS),//
						new GameItem(Items.DARK_BOW),//
						new GameItem(Items.DRAGON_BOOTS))//

				);
			items.put(LootRarity.UNCOMMON, //50% chance
					Arrays.asList(
							new GameItem(21046, 5),//chest rate bonus tablets
							new GameItem(10835 , 3),//buldging bags
							new GameItem(2528),//exp lamp
							new GameItem(10835, 5),//buldging bag
							new GameItem(23933, 5),//vote crystals
							new GameItem(Items.DRAGON_BONES_NOTED, 100),//d bones
							new GameItem(Items.ABYSSAL_WHIP),//whip
							new GameItem(Items.DRAGON_DEFENDER),//dragon defender
							new GameItem(Items.DHAROKS_ARMOUR_SET),//
							new GameItem(Items.AHRIMS_ARMOUR_SET),//
							new GameItem(Items.DRAGON_PLATEBODY),//
							new GameItem(Items.DRAGON_AXE),//
							new GameItem(Items.FIGHTER_TORSO),//
							new GameItem(Items.SARADOMIN_SWORD),//
							new GameItem(Items.AMULET_OF_FURY),//
							new GameItem(Items.DRAGON_CHAINBODY),//
							new GameItem(Items.DRAGON_PLATELEGS),//
							new GameItem(Items.BANDOS_BOOTS),//
							new GameItem(Items.DARK_BOW),//
							new GameItem(Items.DRAGON_BOOTS))//
					);

			items.put(LootRarity.RARE,//8% chance
					Arrays.asList(
							new GameItem(11785),//ACB
							new GameItem(Items.NEITIZNOT_FACEGUARD),
							new GameItem(Items.BERSERKER_RING_I),
							new GameItem(Items.ARCHERS_RING_I),
							new GameItem(Items.WARRIOR_RING_I),
							new GameItem(Items.SEERS_RING_I),
							new GameItem(Items.BANDOS_GODSWORD),
							new GameItem(Items.ZAMORAK_GODSWORD),
							new GameItem(Items.SARADOMIN_GODSWORD),
							new GameItem(995, 10_000_000),

							new GameItem(11785),//ACB
							new GameItem(Items.NEITIZNOT_FACEGUARD),
							new GameItem(Items.BERSERKER_RING_I),
							new GameItem(Items.ARCHERS_RING_I),
							new GameItem(Items.WARRIOR_RING_I),
							new GameItem(Items.SEERS_RING_I),
							new GameItem(Items.BANDOS_GODSWORD),
							new GameItem(Items.ZAMORAK_GODSWORD),
							new GameItem(Items.SARADOMIN_GODSWORD),
							new GameItem(995, 10_000_000),

							new GameItem(Items.ARMADYL_GODSWORD),
							new GameItem(Items.DRAGONFIRE_SHIELD),
							new GameItem(Items.HEAVY_BALLISTA),//
							new GameItem(Items.DRAGON_CROSSBOW),//
							new GameItem(21015),//dihn bulwark
							new GameItem(21006),//kodai wand
							new GameItem(20784)//dragon claws
							));
		}

    /**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public YoutubeMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 12789;
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}
}