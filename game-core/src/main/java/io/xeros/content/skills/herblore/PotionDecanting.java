package io.xeros.content.skills.herblore;

import java.util.Arrays;
import java.util.List;

import io.xeros.Server;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

/**
 * @author Jason MacKeigan
 * @date Jan 5, 2015, 11:25:35 AM
 */
public class PotionDecanting {
	/**
	 * A single instance of the PotionMixing class
	 */
	private static final PotionDecanting POTION_MIXING = new PotionDecanting();

	/**
	 * When an item is clicked we want to establish between items that are and are not potions
	 * 
	 * @param item the item we're determining this for
	 * @return true if the item is a potion based on its item id, otherwise it will return false
	 */
	public boolean isPotion(GameItem item) {
		Potion potion = Potion.get(item.id());
		return potion != null;
	}

	/**
	 * Determines if two potions have the same name. This allows us to ensure two potions are the same.
	 * 
	 * @param potion1 the first potion
	 * @param potion2 the second potion
	 * @return true if they both match.
	 */
	public boolean matches(GameItem potion1, GameItem potion2) {
		Potion p1 = Potion.get(potion1.id());
		Potion p2 = Potion.get(potion2.id());
		if (p1 == null || p2 == null) {
			return false;
		}
		return p1.equals(p2);
	}

	/**
	 * Mixes two single potions together to combine as one
	 * 
	 * @param player the player combining the potions
	 */
	public void mix(Player player, GameItem item1, GameItem item2) {
		if (!player.getItems().playerHasItem(item1.id(), item1.amount(), item1.getSlot())) {
			return;
		}
		if (!player.getItems().playerHasItem(item2.id(), item2.amount(), item2.getSlot())) {
			return;
		}
		Potion potion1 = Potion.get(item1.id());
		Potion potion2 = Potion.get(item2.id());
		if (potion1 == null || potion2 == null) {
			return;
		}
		if (potion1.isFull(item1.id()) || potion2.isFull(item2.id())) {
			return;
		}
		player.getItems().deleteItemNoSave(item1.id(), item1.getSlot(), item1.amount());
		player.getItems().deleteItemNoSave(item2.id(), item2.getSlot(), item2.amount());
		int dose1 = potion1.getDosage(item1.id());
		int dose2 = potion2.getDosage(item2.id());
		int sum = dose1 + dose2;
		if (sum >= 4) {
			item1 = new GameItem(potion1.full);
			if (sum - 4 > 0) {
				item2 = new GameItem(potion2.getItemId(sum - 4));
			} else {
				item2 = new GameItem(229);
			}
		} else {
			item1 = new GameItem(potion1.getItemId(sum));
			item2 = new GameItem(229);
		}
		player.getItems().addItem(item1.id(), item1.amount());
		player.getItems().addItem(item2.id(), item2.amount());
	}

	/**
	 * Decant all unnoted and noted potions in a player's inventory.
	 * 
	 * @param player The player of which we want to decant the inventory.
	 */
	public static void decantInventory(Player player) {
		for (int index = 0; index < player.playerItems.length; index++) {
			if (player.playerItems[index] <= 0) {
				continue;
			}
			GameItem item = new GameItem(player.playerItems[index] - 1, player.playerItemsN[index], index);
			if (!item.getDef().isNoted()) {
				decantUnnotedItem(player, item);
			} else {
				decantNotedItem(player, item);
			}
		}
	}

	private static void decantUnnotedItem(Player player, GameItem item) {
		Potion potion = Potion.get(item.id());
		if (potion == null || potion.isFull(item.id())) {
			return;
		}
		int slot = item.getSlot();
		boolean hasChanged = true;

		// Potions can change inventory slots while adding/deleting them to create other dosages.
		while (hasChanged) {
			slot = Potion.getFirstIncomplete(player, potion);
			if (slot < 0 || player.playerItems[slot] <= 0) {
				return;
			}
			hasChanged = false;
			item = new GameItem(player.playerItems[slot] - 1, player.playerItemsN[slot], slot);
			for (int index = player.playerItems.length - 1; index > slot; index--) {
				if (player.playerItems[index] - 1 <= 0) {
					continue;
				}
				GameItem item2 = new GameItem(player.playerItems[index] - 1, player.playerItemsN[index], index);
				if (POTION_MIXING.matches(item, item2) && !Potion.get(item2.id()).isFull(item2.id())) {
					POTION_MIXING.mix(player, item, item2);
					if (item.id() != player.playerItems[item.getSlot()] - 1) {
						hasChanged = true;
					}
					break;
				}
			}
		}
	}

	private static void decantNotedItem(Player player, GameItem item) {
		Potion potion = Potion.get(item.id() - 1);
		if (potion == null || potion.isFull(item.id() - 1)) {
			return;
		}
		List<Integer> idList = Arrays.asList(potion.getItemId(4), potion.getItemId(3), potion.getItemId(2), potion.getItemId(1));
		long totalDosage = 0;
		long totalVials = 0;
		for (int index = 0; index < player.playerItems.length; index++) {
			if (player.playerItems[index] > 0) {
				if (idList.contains(player.playerItems[index] - 2)) {
					totalDosage += (long) potion.getDosage(player.playerItems[index] - 2) * player.playerItemsN[index];
					totalVials += player.playerItemsN[index];
					player.getItems().deleteItemNoSave(player.playerItems[index] - 1, index, Integer.MAX_VALUE);
				}
			}
		}
		long potionsAdded = 0;
		int dosage = 4;
		while (totalDosage > 0 && dosage > 0) {
			int canAdd = Integer.MAX_VALUE - player.getItems().getItemAmount(potion.getItemId(dosage) + 1);
			int toAdd = (int) Long.min(Integer.MAX_VALUE, totalDosage / dosage);
			int added = Integer.min(canAdd, toAdd);
			if (added > 0 && !player.getItems().addItem(potion.getItemId(dosage) + 1, added)) {
				Server.itemHandler.createGroundItem(player, potion.getItemId(dosage) + 1, player.getX(), player.getY(), player.heightLevel, added);
			}
			totalDosage -= (long) added * dosage;
			dosage--;
			potionsAdded += added;
		}
		int vialsToAdd = (int) Long.min(Integer.MAX_VALUE, totalVials - potionsAdded);
		if (vialsToAdd <= 0) {
			return;
		}
		if (player.getItems().freeSlots() <= 0 && !player.getItems().playerHasItem(230)) {
			Server.itemHandler.createGroundItem(player, 230, player.getX(), player.getY(), player.heightLevel, vialsToAdd);
		} else {
			player.getItems().addItem(230, vialsToAdd);
		}
	}

	/**
	 * Retrieves the single instance of the PotionMixing class
	 * 
	 * @return
	 */
	public static PotionDecanting get() {
		return POTION_MIXING;
	}

	public enum Potion {
		AGILITY(3032, 3034, 3036, 3038),
		ANTI_VENOM(12905, 12907, 12909, 12911),
		ANTI_VENOM_PLUS(12913, 12915, 12917, 12919),
		ANTIDOTE_PLUS(5943, 5945, 5947, 5949),
		ANTIDOTE_PLUS_PLUS(5952, 5954, 5956, 5958),
		ANTIFIRE(2452, 2454, 2456, 2458),
		SUPER_ANTIFIRE(21978, 21981, 21984, 21987),
		EXTENDED_ANTIFIRE(11951, 11953, 11955, 11957),
		EXTENDED_SUPER_ANTIFIRE(22209, 22212, 22215, 22218),
		ANTIPOISON(2446, 175, 177, 179),
		ATTACK(2428, 121, 123, 125),
		COMBAT(9739, 9741, 9743, 9745),
		DEFENCE(2432, 133, 135, 137),
		ENERGY(3008, 3010, 3012, 3014),
		FISHING(2438, 151, 153, 155),
		MAGIC(3040, 3042, 3044, 3046),
		OVERLOAD(11730, 11731, 11732, 11733),
		PRAYER(2434, 139, 141, 143),
		RANGING(2444, 169, 171, 173),
		RESTORE(2430, 127, 129, 131),
		SARADOMIN_BREW(6685, 6687, 6689, 6691),
		STRENGTH(113, 115, 117, 119),
		SUPER_ANTIPOISON(2448, 181, 183, 185),
		SUPER_ATTACK(2436, 145, 147, 149),
		SUPER_COMBAT(12695, 12697, 12699, 12701),
		SUPER_DEFENCE(2442, 163, 165, 167),
		SUPER_ENERGY(3016, 3018, 3020, 3022),
		SUPER_RESTORE(3024, 3026, 3028, 3030),
		SUPER_STRENGTH(2440, 157, 159, 161),
		DIVINE_SUPER_COMBAT_POTION(Items.DIVINE_SUPER_COMBAT_POTION4,
				Items.DIVINE_SUPER_COMBAT_POTION3,
				Items.DIVINE_SUPER_COMBAT_POTION2,
				Items.DIVINE_SUPER_COMBAT_POTION1),
		DIVINE_RANGING_POTION(23733, 23736, 23739, 23742),
		DIVINE_MAGIC_POTION(23745, 23748, 23751,23754),
		SANFEW(10925, 10927, 10929,10931),
		ZAMORAK_BREW(2450, 189, 191, 193);

		Potion(int full, int threeQuarters, int half, int quarter) {
			this.quarter = quarter;
			this.half = half;
			this.threeQuarters = threeQuarters;
			this.full = full;
		}

		private final int quarter;
        private final int half;
        private final int threeQuarters;
        private final int full;

		public boolean isFull(int id) {
			return full == id;
		}

		public int getDosage(int id) {
			return id == full ? 4 : id == threeQuarters ? 3 : id == half ? 2 : id == quarter ? 1 : 0;
		}

		public int getItemId(int dosage) {
			return dosage == 4 ? full : dosage == 3 ? threeQuarters : dosage == 2 ? half : dosage == 1 ? quarter : 0;
		}

		static Potion get(int id) {
			for (Potion p : values()) {
				if (p.full == id || p.half == id || p.quarter == id || p.threeQuarters == id) {
					return p;
				}
			}
			return null;
		}

		/**
		 * Find the first slot of a given potion in a player's inventory which does not have a full dosage.
		 * 
		 * @param player The players of we want to look at the invenotry.
		 * @param potion The potion which we want to find.
		 * @return The first inventory slot containing the potion of any dosage. Returns -1 if the player or potion is null or if the potion could not be found.
		 */
		public static int getFirstIncomplete(Player player, Potion potion) {
			if (player == null || potion == null) {
				return -1;
			}
			for (int slot = 0; slot < player.playerItems.length; slot++) {
				int item = player.playerItems[slot] - 1;
				for (int dosage = 1; dosage <= 3; dosage++) {
					if (item == potion.getItemId(dosage)) {
						return slot;
					}
				}
			}
			return -1;
		}
	}

}
