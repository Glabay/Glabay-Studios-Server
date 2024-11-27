package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.Server;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class LarransChest implements Lootable {

	private static final int KEY = Items.LARRANS_KEY;
	private static final int ANIMATION = 881;

	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(LootRarity.COMMON, Arrays.asList(
				new GameItem(995, 5_000_000),
				new GameItem(21046, 5),//15% chest rate tomb
				new GameItem(Items.STEEL_BAR_NOTED, 1000),
				new GameItem(Items.MAGIC_LOGS_NOTED, 400),
				new GameItem(Items.RAW_ANGLERFISH_NOTED, 300),
				new GameItem(Items.IRON_ORE_NOTED, 1000),
				new GameItem(Items.COAL_NOTED, 1000),
				new GameItem(Items.PURE_ESSENCE_NOTED, 15000),
				new GameItem(Items.BLOOD_RUNE, 1500),
				new GameItem(Items.DEATH_RUNE, 1500),
				new GameItem(Items.GRIMY_TORSTOL_NOTED, 75),
				new GameItem(Items.GRIMY_TOADFLAX_NOTED, 75),
				new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 75),
				new GameItem(FragmentOfSeren.KEY, 1),//seren key
				new GameItem(TheUnbearable.KEY,1),//sotetseg key
				new GameItem(Items.OVERLOAD_4, 5)
		));

		items.put(LootRarity.RARE, Arrays.asList(
				new GameItem(Items.VESTAS_LONGSWORD),
				new GameItem(Items.VESTAS_SPEAR),
				new GameItem(Items.VESTAS_CHAINBODY),
				new GameItem(Items.VESTAS_PLATESKIRT),
				new GameItem(Items.STATIUSS_WARHAMMER),
				new GameItem(Items.STATIUSS_FULL_HELM),
				new GameItem(Items.STATIUSS_PLATEBODY),
				new GameItem(Items.STATIUSS_PLATELEGS),
				new GameItem(Items.ZURIELS_HOOD),
				new GameItem(Items.ZURIELS_ROBE_BOTTOM),
				new GameItem(Items.ZURIELS_ROBE_TOP),
				new GameItem(Items.ZURIELS_STAFF),
				new GameItem(Items.MORRIGANS_COIF),
				new GameItem(Items.MORRIGANS_LEATHER_BODY),
				new GameItem(Items.MORRIGANS_LEATHER_CHAPS)
		));
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	@Override
	public void roll(Player c) {
		if (c.getItems().playerHasItem(KEY)) {
			c.getItems().deleteItem(KEY, 1);
			c.startAnimation(ANIMATION);
			int random = Misc.random(500);
			if (c.getItems().playerHasItem(21046)) {
				random = Misc.random(493);
				c.getItems().deleteItem(21046, 1);
				c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
				c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
			}
			List<GameItem> itemList = random == 0 ? items.get(LootRarity.RARE) : items.get(LootRarity.COMMON);
			GameItem reward = Misc.getRandomItem(itemList);
			if (random == 0) {
				if (!c.getDisplayName().equalsIgnoreCase("thimble") && !c.getDisplayName().equalsIgnoreCase("top hat")) {

					PlayerHandler.executeGlobalMessage("@pur@" + c.getDisplayNameFormatted() + " received a drop: " +
							"" + ItemDef.forId(reward.id()).getName() + " x " + reward.amount() + " from Larran's chest.");
				}
			}
			if (!c.getItems().addItem(reward.id(), reward.amount())) {
				Server.itemHandler.createGroundItem(c, reward.id(), c.getX(), c.getY(), c.heightLevel, reward.amount());
			}
		} else {
			c.sendMessage("The chest is locked, it won't budge!");
		}
	}

}