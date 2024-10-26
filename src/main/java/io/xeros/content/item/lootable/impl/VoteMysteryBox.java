package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
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
public class VoteMysteryBox implements Lootable {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int VOTE_MYSTERY_BOX = 11739;

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
						new GameItem(20199),//monk g top
						new GameItem(20202),//monk g bottom
						new GameItem(20205),//gold chef hat
						new GameItem(20208),//gold apron
						new GameItem(20169),//steel g
						new GameItem(20172),
						new GameItem(20175),
						new GameItem(20178),
						new GameItem(20181),//steel g
						new GameItem(20184),//steel t
						new GameItem(20187),
						new GameItem(20190),
						new GameItem(20193),
						new GameItem(20196),//steel t
						new GameItem(12956),//cow fit
						new GameItem(12957),
						new GameItem(12958),
						new GameItem(12959),//cow fit
						new GameItem(12892),//antisanta fit
						new GameItem(12893),
						new GameItem(12894),
						new GameItem(12895),
						new GameItem(12896),//anti santa fit
						new GameItem(12887),//santa fit
						new GameItem(12888),
						new GameItem(12889),
						new GameItem(12890),
						new GameItem(12891),//santa fit
						new GameItem(12412),//pirate hat
						new GameItem(12379),//rune cane
						new GameItem(12351),//musk hat
						new GameItem(12323),//red cav
						new GameItem(12321),//white cav
						new GameItem(12319),//crier hat
						new GameItem(12315),//pink ele top
						new GameItem(12317),//pink elegant bottom
						new GameItem(12313),//boater
						new GameItem(12311),
						new GameItem(12309),//boater
						new GameItem(12307),//headbands
						new GameItem(12305),
						new GameItem(12303),
						new GameItem(12301),
						new GameItem(12299),//headbands
						new GameItem(12297),//black pick
						new GameItem(12205),//bronze g
						new GameItem(12207),
						new GameItem(12209),
						new GameItem(12211),
						new GameItem(12213),//bronze g
						new GameItem(12235),//iron g
						new GameItem(12237),
						new GameItem(12239),
						new GameItem(12241),
						new GameItem(12243),//iron g
						new GameItem(12325, 1))//blue cav
		);

		items.put(LootRarity.UNCOMMON,
				Arrays.asList(
						new GameItem(20199),//monk top g
						new GameItem(20110),//bowl wig
						new GameItem(20059),//bucket helm g
						new GameItem(20053),//spectacles
						new GameItem(20050),//obby cape(r)
						new GameItem(20035),//samurai
						new GameItem(20038),
						new GameItem(20041),
						new GameItem(20044),
						new GameItem(20047),//samurai
						new GameItem(19973),//tux stuff
						new GameItem(19976),
						new GameItem(19979),
						new GameItem(19982),
						new GameItem(19985),
						new GameItem(19958),
						new GameItem(19961),
						new GameItem(19964),
						new GameItem(19967),
						new GameItem(19970),//tuxedo stuff
						new GameItem(13640),//farmers legs
						new GameItem(13642),//farmers top
						new GameItem(13644),//farmers boots
						new GameItem(13646),//farmers hat
						new GameItem(13258),//angler fit
						new GameItem(13259),
						new GameItem(13260),
						new GameItem(13261),//angler fit
						new GameItem(12349),//gold elegant bottom
						new GameItem(12347),//gold elegant top
						new GameItem(12345),//gold elegant female
						new GameItem(12343),//gold elegant
						new GameItem(12339),//pink elegant
						new GameItem(12341),//pink elegant female
						new GameItem(12335),//briefcase
						new GameItem(12337),//spectacles
						new GameItem(12277),//mith g
						new GameItem(12279),
						new GameItem(12281),
						new GameItem(12283),
						new GameItem(12285),//mith g
						new GameItem(12251),//goblin mask
						new GameItem(12013),//prospector
						new GameItem(12014),
						new GameItem(12015),
						new GameItem(12016),//prospector
						new GameItem(11919),//cow mask
						new GameItem(2615),//rune g
						new GameItem(2617),
						new GameItem(2619),
						new GameItem(2621),//rune g
						new GameItem(2631),//highwayman mask
						new GameItem(2633),//blue beret
						new GameItem(2653),//rune zammy
						new GameItem(2655),
						new GameItem(2657),
						new GameItem(2659),//rune zammy
						new GameItem(2661),//full sara
						new GameItem(2663),
						new GameItem(2665),
						new GameItem(2667),//full sara
						new GameItem(2669),//full guthix
						new GameItem(2671),
						new GameItem(2673),
						new GameItem(2675),//full guthix
						new GameItem(7390),//wiz g top
						new GameItem(7386),//wiz g bottom
						new GameItem(7388),//wiz t bottom
						new GameItem(7392),//wiz t top
						new GameItem(7394),//wiz g hat
						new GameItem(7396),//wiz t hat
						new GameItem(7398),//enchanted leg
						new GameItem(7399),//enchanted top
						new GameItem(7400),//enchanted helm
						new GameItem(7332),//black h1 kite
						new GameItem(7334),//addy h1
						new GameItem(7336),//rune h1
						new GameItem(7338),//h2
						new GameItem(7340),
						new GameItem(7342),//h2
						new GameItem(7344),//h3
						new GameItem(7346),
						new GameItem(7348),//h3
						new GameItem(7350),//h4
						new GameItem(7352),
						new GameItem(7354),//h4
						new GameItem(7356),//h5
						new GameItem(7358),
						new GameItem(7360),//h5
						new GameItem(7362),//studded body g
						new GameItem(7364),//stud body t
						new GameItem(7366),//studed chap g
						new GameItem(7368),//studded chaps t
						new GameItem(7370),//green dhide body g
						new GameItem(7372),//green d hide body t
						new GameItem(7374),//blue body g
						new GameItem(7376),//blue body t
						new GameItem(7378),//green chap g
						new GameItem(7380),//t
						new GameItem(7382),//blue chap g
						new GameItem(7384),//t
						new GameItem(12247))//red beret
		);
		items.put(LootRarity.RARE,
				Arrays.asList(
						new GameItem(20020, 1), //lesser mask
						new GameItem(20023, 1),//greater demon mask
						new GameItem(20029),//old demon mask
						new GameItem(20032),//jungle demon mask
						new GameItem(20000),//d scim g
						new GameItem(19994),//ranger gloves
						new GameItem(19991),//bucket helm
						new GameItem(19988),//blacksmith helm
						new GameItem(12518),//green dragon mask
						new GameItem(12520),//blue drag
						new GameItem(12522),//red drag
						new GameItem(12524),//black drag
						new GameItem(12516),//pith helmet
						new GameItem(12514),//explorer jack
						new GameItem(12441),//muskateer top
						new GameItem(12443),//muskateer bottom
						new GameItem(12439),//royal sceptre
						new GameItem(12434),//top hat and monocle
						new GameItem(12432),//top hat
						new GameItem(12428),//penguin mask
						new GameItem(12245),//beanie
						new GameItem(12414),//d chain g
						new GameItem(12415),//d leg g
						new GameItem(12416),//dragon skirt g
						new GameItem(12417),//dragon full helm g
						new GameItem(12418),//dragon sq g
						new GameItem(12397),//royal crown
						new GameItem(12371),//lava dragon mask
						new GameItem(12367),//steel drag
						new GameItem(12365),//iron drag mask
						new GameItem(12363),//bronze dragon mask
						new GameItem(12361),//cat mask
						new GameItem(3481),//gold plate
						new GameItem(3483),//gold legs
						new GameItem(3485),//gold skirt
						new GameItem(3486),//gold full helm
						new GameItem(3488),//gold kite
						new GameItem(12389),//gold scimi
						new GameItem(12391),//gold boots
						new GameItem(20146),//gold med
						new GameItem(20149),//gold chain
						new GameItem(20152),//gold sq
						new GameItem(20155),//gold 2h
						new GameItem(20161),//gilded hasta
						new GameItem(12359),//leprachon hat
						new GameItem(20026, 1),
						new GameItem(24315),//spooky costume start
						new GameItem(24317),
						new GameItem(24319),
						new GameItem(24321),
						new GameItem(24323)//spooky costume end

				));
	}

	public static CycleEvent getCycleEvent(final Player player) {
		return new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.isDisconnected() || Objects.isNull(player)) {
					container.stop();
					return;
				}

				int random = Misc.random(100);
				List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
				GameItem item = Misc.getRandomItem(itemList);
				GameItem itemDouble = Misc.getRandomItem(itemList);

				if (Misc.random(10) == 0) {
					player.getItems().addItem(item.id(), item.amount());
					player.getItems().addItem(itemDouble.id(), itemDouble.amount());
					player.sendMessage("You receive <col=255>" + item.amount() + " x " + ItemAssistant.getItemName(item.id()) + "</col>.");
					player.sendMessage("You receive <col=255>" + itemDouble.amount() + " x " + ItemAssistant.getItemName(itemDouble.id()) + "</col>.");
				} else {
					player.getItems().addItem(item.id(), item.amount());
					player.sendMessage("You receive <col=255>" + item.amount() + " x " + ItemAssistant.getItemName(item.id()) + "</col>.");
				}
				container.stop();
			}
		};
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 */
	public void roll(Player player) {
		if (System.currentTimeMillis() - player.lastMysteryBox < 600 * 4) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a hourly box.");
			return;
		}
		if (!player.getItems().playerHasItem(VOTE_MYSTERY_BOX)) {
			player.sendMessage("You need a hourly box to do this.");
			return;
		}
		player.getItems().deleteItem(VOTE_MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		CycleEventHandler.getSingleton().stopEvents(this);
		CycleEventHandler.getSingleton().addEvent(this, getCycleEvent(player), 2);
	}

}