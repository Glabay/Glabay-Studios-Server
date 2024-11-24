package io.xeros.content.items;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.model.CombatType;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Nov 7, 2013
 */
public class Degrade {

	public static final int PVP_DEGRADE_TIME_HOURS = 10;

	public static int getPvPHitsToDegrade() {
		int ticks = (int) TimeUnit.HOURS.toMillis(PVP_DEGRADE_TIME_HOURS) / 600;
		return ticks / 4;
	}

	public static int getMaximumItems() {
		return DegradableItem.values().length;
	}

	/**
	 * README (Warning)
	 * You cannot change the position of any elements in this enum because it uses
	 * {@link Enum#ordinal()} to store the values!
	 */
	public enum DegradableItem {
		SARADOMIN_SWORD_BLESSED(Items.SARADOMINS_BLESSED_SWORD, true, CombatType.MELEE, 12804, -1, 10_000),
		AMULET_OF_THE_DAMNED_FULL(true, Items.AMULET_OF_THE_DAMNED_FULL, true, null, 12853, -1, 1),
		AMULET_OF_THE_DAMNED(true, Items.AMULET_OF_THE_DAMNED, true, null, -1, 2_500_000, 2_000),
		VESTAS_CHAINBODY(Items.VESTAS_CHAINBODY, -1, 12_500_000, getPvPHitsToDegrade()),
		VESTAS_PLATESKIRT(Items.VESTAS_PLATESKIRT, -1, 12_500_000, getPvPHitsToDegrade()),
		ZURIELS_ROBETOP(Items.ZURIELS_ROBE_TOP, -1, 12_500_000, getPvPHitsToDegrade()),
		ZURIELS_ROBE_BOTTOM(Items.ZURIELS_ROBE_BOTTOM, -1, 12_500_000, getPvPHitsToDegrade()),
		ZURIELS_STAFF(Items.ZURIELS_STAFF, true, -1, 12_500_000, getPvPHitsToDegrade()),
		ZURIELS_HOOD(Items.ZURIELS_HOOD, -1, 12_500_000, getPvPHitsToDegrade()),
		MORRIGANS_LEATHER_BODY(Items.MORRIGANS_LEATHER_BODY, -1, 10_500_000, getPvPHitsToDegrade()),
		MORRIGANS_LEATHER_CHAPS(Items.MORRIGANS_LEATHER_CHAPS, -1, 10_500_000, getPvPHitsToDegrade()),
		MORRIGANS_LEATHER_COIF(Items.MORRIGANS_COIF, -1, 10_500_000, getPvPHitsToDegrade()),
		STATIUS_PLATEBODY(Items.STATIUSS_PLATEBODY, -1, 10_500_000, getPvPHitsToDegrade()),
		STATIUS_PLATELEGS(Items.STATIUSS_PLATELEGS, -1, 10_500_000, getPvPHitsToDegrade()),
		STATIUS_FULL_HELM(Items.STATIUSS_FULL_HELM, -1, 10_500_000, getPvPHitsToDegrade()),
		STATIUS_WARHAMMER(Items.STATIUSS_WARHAMMER, true, CombatType.MELEE, -1, 10_500_000, getPvPHitsToDegrade()),
		VESTAS_LONGSWORD(Items.VESTAS_LONGSWORD, true, CombatType.MELEE, -1, 10_500_000, getPvPHitsToDegrade()),
		VESTAS_SPEAR(Items.VESTAS_SPEAR, true, CombatType.MELEE, -1, 10_500_000, getPvPHitsToDegrade()),
		TENTACLE_WHIP(Items.ABYSSAL_TENTACLE, true, CombatType.MELEE, 12004, 2_000_000, 10_000);

		private final int itemId;
		private final int brokenId;
		private final int cost;
		private final int hits;
		private final CombatType combatType;
		private final boolean weapon;
		private final boolean special;

		public static DegradableItem[] AMULETS_OF_THE_DAMNED = { DegradableItem.AMULET_OF_THE_DAMNED, DegradableItem.AMULET_OF_THE_DAMNED_FULL};

		DegradableItem(int itemId, int brokenId, int cost, int hits) {
			this(itemId, false, null, brokenId, cost, hits);
		}

		DegradableItem(int itemId, boolean weapon, int brokenId, int cost, int hits) {
			this(itemId, weapon, null, brokenId, cost, hits);
		}

		DegradableItem(int itemId, boolean weapon, CombatType combatType, int brokenId, int cost, int hits) {
			this(false, itemId, weapon, combatType, brokenId, cost, hits);
		}

		/**
		 * @param itemId The item.
		 * @param weapon If <code>true</code> it will be degraded when attacking, otherwise on defending.
		 * @param combatType The combat type that will degrade this item, null means all types will degrade it.
		 * @param brokenId The broken item id.
		 * @param cost The cost to defend.
		 * @param hits The attacks/defends to fully degrade the item.
		 * @param special Special items must be called to degrade explicitly through {@link Degrade#degrade(Player, DegradableItem...)}.
		 */
		DegradableItem(boolean special, int itemId, boolean weapon, CombatType combatType, int brokenId, int cost, int hits) {
			Preconditions.checkState(hits > 0, "Hits is zero: " + this);
			this.special = special;
			this.combatType = combatType;
			this.weapon = weapon;
			this.itemId = itemId;
			this.brokenId = brokenId;
			this.cost = cost;
			this.hits = hits;
		}

		public int getItemId() {
			return this.itemId;
		}

		public int getBrokenId() {
			return this.brokenId;
		}

		public int getCost() {
			return this.cost;
		}

		public int getHits() {
			return this.hits;
		}

		static Set<DegradableItem> DEGRADABLES = Collections.unmodifiableSet(EnumSet.allOf(DegradableItem.class));

		public static Optional<DegradableItem> forId(int itemId) {
			return DEGRADABLES.stream().filter(d -> d.itemId == itemId).findFirst();
		}
	}

	public static void degradeDefending(Player player) {
		degrade(player, null, false);
	}

	/**
	 * Degrade items that are worn.
	 * @param player The player.
	 * @param attacking Are we attacking or are we defending? Will degrade weapons or armour.
	 */
	public static void degrade(Player player, CombatType combatType, boolean attacking) {
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
			return;
		}
		for (DegradableItem degradable : DegradableItem.DEGRADABLES) {
			if (degradable.special)
				continue;

			// Don't degrade these this way
			if (degradable == DegradableItem.AMULET_OF_THE_DAMNED || degradable == DegradableItem.AMULET_OF_THE_DAMNED_FULL)
				continue;

			// Don't degrade weapons when being hit and vice-versa
			if (attacking && !degradable.weapon || !attacking && degradable.weapon) {
				continue;
			}

			// Don't degrade when attacking with magic while using a melee weapon
			if (degradable.combatType != null && attacking && degradable.combatType != combatType) {
				continue;
			}

			degrade(player, degradable);
		}
	}

	/**
	 * You should only call this if your item has a special condition where it
	 * will degrade. Otherwise it will be degraded from attacking/defending like
	 * everything else.
	 */
	public static void degrade(Player player, DegradableItem...items) {
		for (DegradableItem degradable : items) {
			if (player.getItems().isWearingItem(degradable.getItemId())) {
				String name = degradable.name().toLowerCase().replaceAll("_", " ");
				player.degradableItem[degradable.ordinal()]++;
				if (player.degradableItem[degradable.ordinal()] >= degradable.getHits()) {
					int slot = player.getItems().getWornItemSlot(degradable.getItemId());
					player.getItems().equipItem(player.getItems().isWearingItem(12006) ? -1 : degradable.getBrokenId(), 1, slot);
					player.sendMessage("Your " + name + " has degraded.", 0xFFCC00);
					if (degradable.cost > -1) {
						player.claimDegradableItem[degradable.ordinal()] = true;
						player.sendMessage("Talk to Perdu outside the Edgeville bank to get it back for a price.", 0xFFCC00);
					}
					player.degradableItem[degradable.ordinal()] = 0;
				}
			}
		}
	}

	public static void reset(Player player, DegradableItem item) {
		player.degradableItem[item.ordinal()] = 0;
	}

	public static void checkPercentage(Player player, int clickedItem) {
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
			return;
		}
		for (DegradableItem degradable : DegradableItem.values()) {
			if (degradable.getItemId() == clickedItem) {
				int remaining = (degradable.hits - player.degradableItem[degradable.ordinal()]);
				int percent = 100 - (player.degradableItem[degradable.ordinal()] / (degradable.getHits() / 100));
				if (remaining != degradable.hits && percent == 100)
					percent = 99;
				player.sendMessage("Your " + ItemDef.forId(clickedItem).getName() + " has " + Misc.insertCommas(remaining) + " hits remaining " +
						"(" + percent + "%).");
				break;
			}
		}
	}

	private static final int[][] CRYSTAL_BOW_DEGRADE = { { 4215, 100_000 }, { 4216, 120_000 }, { 4217, 150_000 }, { 4218, 180_000 }, { 4219, 210_000 }, { 4220, 240_000 },
			{ 4221, 270_000 }, { 4222, 300_000 }, { 4223, 320_000 }, { 4207, 2000000 }, };

	private static final int FULL_CRYSTAL_BOW = 4212;

	public static boolean repairCrystalBow(final Player player, final int itemId) {
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
			return false;
		}
		if (!player.getItems().playerHasItem(itemId)) {
			player.sendMessage("You do not have the item required to repair this.");
			return false;
		}
		ItemDef definition = ItemDef.forId(itemId);

		for (int[] degraded : CRYSTAL_BOW_DEGRADE) {
			if (degraded[0] == itemId) {
				int cost = degraded[1];
				if (player.getItems().getItemAmount(995) < cost) {
					player.sendMessage("You need " + cost + " coins at least to charge this.");
					return false;
				}
				player.getItems().deleteItem2(itemId, 1);
				player.getItems().deleteItem2(995, cost);
				player.getItems().addItem(FULL_CRYSTAL_BOW, 1);
				player.sendMessage("You charged your " + (definition == null ? "bow" : definition.getName()) + " for " + cost + " coins.");
				player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.CRYSTAL);
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if an item is repairable under some standards.
	 * 
	 * @param player the player repairing the item
	 * @param item the item to be repaired
	 * @return will return a DegradableItem object if the item is repairable
	 */
	public static boolean repair(Player player, int item) {
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
			return false;
		}
		Optional<DegradableItem> degradable = DegradableItem.forId(item);
		if (!degradable.isPresent()) {
			return false;
		}
		DegradableItem degraded = degradable.get();
		if (player.degradableItem[degraded.ordinal()] <= 0) {
			player.sendMessage("This item has not degraded at all, therefor it does not need to be fixed.");
			return false;
		}
		if (player.claimDegradableItem[degraded.ordinal()]) {
			player.sendMessage("This item has degrdaded and needs to be claimed. Talk to the old wise man.");
			return false;
		}
		double percent = (100 - (player.degradableItem[degraded.ordinal()] / (degraded.hits / 100))) / 100.0D;
		int cost = (int) (degraded.cost * percent);
		
		if (player.getRechargeItems().hasItem(13141)) {
			cost = (int) (cost * 0.90);
		}
		if (player.getRechargeItems().hasItem(13142)) {
			cost = (int) (cost * 0.80);
		}
		if (player.getRechargeItems().hasItem(13143)) {
			cost = (int) (cost * 0.70);
		}
		if (player.getRechargeItems().hasItem(13144)) {
			cost = (int) (cost * 0.50);
		}
		if (player.getItems().getItemAmount(995) < cost) {
			player.sendMessage("You do not have the coins to repair this item.");
			return false;
		}
		player.getItems().deleteItem2(995, cost);
		player.degradableItem[degraded.ordinal()] = 0;
		player.sendMessage("The item has been repaired for " + cost + " coins.");
		return true;
	}

	public static boolean claim(Player player, int item) {
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
			return false;
		}
		Optional<DegradableItem> degradable = DegradableItem.forId(item);
		if (!degradable.isPresent()) {
			return false;
		}
		DegradableItem degraded = degradable.get();
		if (!player.claimDegradableItem[degraded.ordinal()] || degraded.cost < 0) {
			player.sendMessage("This item does not need to be claimed.");
			return false;
		}
		int cost = degraded.cost;
		if (player.getItems().getItemAmount(995) < cost) {
			player.sendMessage("You do not have the coins to claim this item.");
			return false;
		}
		if (player.getItems().freeSlots() < 1) {
			player.sendMessage("You need at least one free slot to do this.");
			return false;
		}
		player.getItems().deleteItem2(995, cost);
		player.degradableItem[degraded.ordinal()] = 0;
		player.claimDegradableItem[degraded.ordinal()] = false;
		player.getItems().addItem(degraded.getItemId(), 1);
		player.sendMessage("You have claimed the " + ItemAssistant.getItemName(item) + " for " + cost + " coins.");
		return true;
	}

	public static DegradableItem[] getClaimedItems(Player player) {
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
			return null;
		}
		DegradableItem[] options = new DegradableItem[4];
		int i = 0;
		for (int j = 0; j < player.claimDegradableItem.length; j++) {
			if (player.claimDegradableItem[j]) {
				options[i] = DegradableItem.values()[j];
				i++;
			}
			if (i == options.length) {
				break;
			}
		}
		DegradableItem[] backingArray = new DegradableItem[i];
		System.arraycopy(options, 0, backingArray, 0, i);
		return backingArray;
	}

}
