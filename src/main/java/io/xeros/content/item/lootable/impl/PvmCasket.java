package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * Revamped a simple means of receiving a random item based on chance.
 * 
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class PvmCasket implements Lootable {

	/**
	 * The item id of the PvM Casket required to trigger the event
	 */
	public static final int PVM_CASKET = 405; //Casket

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(LootRarity.COMMON,
			Arrays.asList(
					new GameItem(21547, 1),//small foe bone
					new GameItem(21547, 1),//small foe bone
					new GameItem(21547, 1),//small foe bone
					new GameItem(21547, 1),//small foe bone
					new GameItem(21549, 1),//medium foe bone
					new GameItem(21549, 1),//medium foe bone
					new GameItem(21549, 1),//medium foe bone
					new GameItem(21551, 1),//large foe bone
					new GameItem(21551, 1),//large foe bone
					new GameItem(21553, 1),//rare foe bone
					new GameItem(454, 150), 
					new GameItem(1079),
					new GameItem(1077),
					new GameItem(1075), 
					new GameItem(1071), 
					new GameItem(1073), 
					new GameItem(6106),
					new GameItem(6107),
					new GameItem(6108),
					new GameItem(6109),
					new GameItem(6110),
					new GameItem(6111),
					new GameItem(2550),
					new GameItem(1127), 
					new GameItem(995, 10000),
					new GameItem(1125), 
					new GameItem(1123), 
					new GameItem(1121), 
					new GameItem(987),
					new GameItem(1333),
					new GameItem(7947, 100), 
					new GameItem(1331), 
					new GameItem(1381), 
					new GameItem(1383), 
					new GameItem(1385), 
					new GameItem(1387), 
					new GameItem(1329), 
					new GameItem(1327), 
					new GameItem(1325),
					new GameItem(1323),
					new GameItem(6128),
					new GameItem(6129),
					new GameItem(6130),
					new GameItem(6131),
					new GameItem(6133),
					new GameItem(6135),
					new GameItem(6137),
					new GameItem(6143),
					new GameItem(6149),
					new GameItem(1321),
					new GameItem(1319), 
					new GameItem(1317), 
					new GameItem(1315),
					new GameItem(6185), 
					new GameItem(6322),
					new GameItem(6324),
					new GameItem(6326),
					new GameItem(6328),
					new GameItem(6330),
					new GameItem(5667),
					new GameItem(5668),
					new GameItem(537, 10), 
					new GameItem(6181), 
					new GameItem(6179), 
					new GameItem(1725),
					new GameItem(1727),
					new GameItem(1729),
					new GameItem(1731),
					new GameItem(1704),
					new GameItem(527, 50), 
					new GameItem(114, 20), 
					new GameItem(128, 20), 
					new GameItem(4129),
					new GameItem(4127),
					new GameItem(4125),
					new GameItem(4123),
					new GameItem(4121),
					new GameItem(1215, 1), 
					new GameItem(134, 20), 
					new GameItem(3145, 100), 
					new GameItem(6177), 
					new GameItem(985, 1))
					
			);
			
		items.put(LootRarity.UNCOMMON,
				Arrays.asList(
						new GameItem(4566),
						new GameItem(4587), 
						new GameItem(861),
						new GameItem(158, 25),
						new GameItem(164, 25),
						new GameItem(146, 25),
						new GameItem(140, 25),
						new GameItem(537, 50),
						new GameItem(859),
						new GameItem(4131),
						new GameItem(5698, 1),
						new GameItem(6568),
						new GameItem(4089),
						new GameItem(4091),
						new GameItem(4093),
						new GameItem(4095),
						new GameItem(4097),
						new GameItem(4099),
						new GameItem(4100),
						new GameItem(4102),
						new GameItem(4675),
						new GameItem(4104),
						new GameItem(4106),
						new GameItem(2368),
						new GameItem(4108),
						new GameItem(6536),
						new GameItem(6524),
						new GameItem(6525),
						new GameItem(6526),
						new GameItem(6522, 200),
						new GameItem(6527),			
						new GameItem(11937, 100), 
						new GameItem(386, 100), 
						new GameItem(1409),
						new GameItem(140, 20), 
						new GameItem(11876, 100),
						new GameItem(995, 100000))
						
		);
			
			items.put(LootRarity.RARE,
					Arrays.asList(
							new GameItem(TheUnbearable.KEY, 1),//unbearable key
							new GameItem(FragmentOfSeren.KEY, 1),//serens key
							new GameItem(22374, 1),//hespori key
							new GameItem(4566), //rubber chicken
							new GameItem(990, 10),//crystal key
							new GameItem(4224),//crystal shield
							new GameItem(3751),//berserker helm
							new GameItem(3753),//warrior helm
							new GameItem(3755),//farseer helm
							new GameItem(9242, 50),//ruby bolts e
							new GameItem(9244, 50),//dragonstone bolts e
							new GameItem(9245, 50),//onyx bolts e
							new GameItem(11212, 50),//dragon arrow
							new GameItem(4212), //crystal bow
							new GameItem(4153, 1),//granite maul
							new GameItem(11840),//dragon boots
							new GameItem(2366),//left shielf half
							new GameItem(4151, 1),//whip
							new GameItem(995, 2500000)//coins
					));//godsword shard 1
							
							
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	/**
	 * Opens a PvM Casket if possible, and ultimately triggers and event, if possible.
	 *
	 */
	@Override
	public void roll(Player player) {
		if (System.currentTimeMillis() - player.lastMysteryBox < 150 * 4) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a PvM Casket.");
			return;
		}
		if (!player.getItems().playerHasItem(PVM_CASKET)) {
			player.sendMessage("You need PvM Casket to do this.");
			return;
		}
		player.getItems().deleteItem(PVM_CASKET, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		open(player);
	}

	public static void open(final Player player) {
		int coins = 50000 + Misc.random(15000);
		int coinsDouble = 100000 + Misc.random(50000);
		int random = Misc.random(100);
		List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON)
				: random <= 80 ? items.get(LootRarity.UNCOMMON)
				: items.get(LootRarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		GameItem itemDouble = Misc.getRandomItem(itemList);

		if (Misc.random(10) == 0) {
			player.getItems().addItem(995, coins + coinsDouble);
			player.getItems().addItem(item.id(), item.amount());
			player.getItems().addItem(itemDouble.id(), itemDouble.amount());
			player.sendMessage("You receive <col=255>" + item.amount() + " x " + ItemAssistant.getItemName(item.id()) + "</col>, and <col=255>"
					+ Misc.insertCommas(Integer.toString(coins)) + "</col>GP.");
			player.sendMessage("You receive <col=255>" + itemDouble.amount() + " x " + ItemAssistant.getItemName(itemDouble.id()) + "</col>, and <col=255>"
					+ Misc.insertCommas(Integer.toString(coins)) + "</col>GP.");
		} else {
			player.getItems().addItem(995, coins);
			player.getItems().addItem(item.id(), item.amount());
			player.sendMessage("You receive <col=255>" + item.amount() + " x " + ItemAssistant.getItemName(item.id()) + "</col>, and <col=255>"
					+ Misc.insertCommas(Integer.toString(coins)) + "</col>GP.");
		}
	}

}