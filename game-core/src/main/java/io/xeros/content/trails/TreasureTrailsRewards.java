package io.xeros.content.trails;

import java.util.*;

import io.xeros.model.definition.Items;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class TreasureTrailsRewards {

	public static final int MAX_RARE_ROLLS = 2;

	/**
	 * After first roll the drop rate decreased (gets lower) by this amount (e.g. 6 = 6 times more rare).
	 */
	public static final int RARE_ROLL_CURVE = 6;

	private static final Map<RewardLevel, Map<RewardRarity, List<TreasureTrailsRewardItem>>> rewards = new HashMap<>();
	public static final Map<RewardLevel, List<TreasureTrailsRewardItem>> possibleDrops = new HashMap<>();

	public static Map<RewardLevel, Map<RewardRarity, List<TreasureTrailsRewardItem>>> getRewards() {
		return rewards;
	}

	public static List<GameItem> getRandomRewardItems(RewardLevel difficulty, int rolls) {
		return TreasureTrailsRewardItem.toGameItems(getRandomRewards(difficulty, rolls));
	}

	public static List<TreasureTrailsRewardItem> getRandomRewards(RewardLevel difficulty, int rolls) {
		List<TreasureTrailsRewardItem> result =new ArrayList<>();

		if (difficulty == RewardLevel.MEDIUM) {
			rolls += 1;
		} else if (difficulty == RewardLevel.HARD) {
			rolls += 2;
		} else if (difficulty == RewardLevel.MASTER) {
			rolls += 2;
		}

		double random = Math.random();
		while (random > 0.05) {
			random = Math.random();
		}

		result.add(getReward(random, 0, difficulty));

		for (int i = 0; i < rolls; i++) {
			result.add(getReward(Math.random(), i + 1, difficulty));
		}
		Collections.shuffle(result);
		return result;
	}

	/**
	 * Get a random reward when completing Treasure Trails.
	 */
	private static TreasureTrailsRewardItem getReward(double roll, int currentRoll, RewardLevel level) {
		boolean rollRares = currentRoll < MAX_RARE_ROLLS;
		int increase = currentRoll == 0 ? 1 : RARE_ROLL_CURVE * currentRoll;
		List<TreasureTrailsRewardItem> rewardList;
		if (rollRares && roll <= 0.001 / increase && containsReward(level, RewardRarity.VERY_RARE)) {
			rewardList = rewards.get(level).get(RewardRarity.VERY_RARE);
		} else if (rollRares && roll <= 0.009 / increase && containsReward(level, RewardRarity.RARE)) {
			rewardList = rewards.get(level).get(RewardRarity.RARE);
		} else if (roll <= 0.07 / increase && containsReward(level, RewardRarity.UNCOMMON)) {
			rewardList = rewards.get(level).get(RewardRarity.UNCOMMON);
		} else {
			rewardList = rewards.get(level).get(RewardRarity.COMMON);
		}

		return rewardList.get(Misc.trueRand(rewardList.size())).copy();
	}

	private static boolean containsReward(RewardLevel level, RewardRarity rarity) {
		List<TreasureTrailsRewardItem> rewardList = rewards.get(level).get(rarity);
		return rewardList != null && rewardList.size() > 0;
	}

	/**
	 * Add item to reward table.
	 *
	 * Items that have a rarity of common {@link TreasureTrailsRewardItem#getRarity()} or
	 * when you add an item to multiple tables <code>{@param levels} > 1</code>, this item
	 * will not be added to {@link TreasureTrailsRewards#possibleDrops}, which is used to
	 * generate the item list for the Collection Log.
	 */
	private static void add(TreasureTrailsRewardItem item, RewardLevel...levels) {
		for (RewardLevel level : levels) {
			if (levels.length <= 1) { // We add the collection log map in this scope
				if (item.getRarity() != RewardRarity.COMMON) {
					List<TreasureTrailsRewardItem> items;
					if (possibleDrops.containsKey(level)) {
						items = possibleDrops.get(level);
					} else {
						items = new ArrayList<>();
					}
					items.add(item);
					possibleDrops.put(level, items);
				}
			}

			add(item, level, item.getRarity());
		}
	}

	private static void add(TreasureTrailsRewardItem item, RewardLevel level, RewardRarity rarity) {
		if (rewards.containsKey(level)) {
			if (rewards.get(level).containsKey(rarity)) {
				rewards.get(level).get(rarity).add(item);
			} else {
				List<TreasureTrailsRewardItem> list =new ArrayList<>();
				list.add(item);
				rewards.get(level).put(rarity, list);
			}
		} else {
			List<TreasureTrailsRewardItem> list =new ArrayList<>();
			list.add(item);
			Map<RewardRarity, List<TreasureTrailsRewardItem>> map =new HashMap<>();
			map.put(rarity, list);
			rewards.put(level, map);
		}
	}

	public static List<TreasureTrailsRewardItem> getRewardsForType(int type) {
		RewardLevel level = null;
		switch(type) {
		case 1:
			level = RewardLevel.EASY;
			break;
		case 2:
			level = RewardLevel.MEDIUM;
			break;
		case 3:
			level = RewardLevel.HARD;
			break;
		case 4:
			level = RewardLevel.MASTER;
			break;
		}
		return possibleDrops.get(level);
	}

	public static void load() {
		// common
//		add(new TreasureTrailsRewardItem(Items.FIRE_RUNE, 20, 150, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.WATER_RUNE, 20, 150, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.AIR_RUNE, 20, 150, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.EARTH_RUNE, 20, 150, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.MIND_RUNE, 20, 150, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.BODY_RUNE, 20, 150, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.NATURE_RUNE, 5, 20, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.LAW_RUNE, 5, 20, RewardRarity.COMMON), RewardLevel.SHARED);
//		add(new TreasureTrailsRewardItem(Items.COINS, 2500, 50000, RewardRarity.COMMON), RewardLevel.SHARED);

		// ALL Uncommon
		add(new TreasureTrailsRewardItem(Items.RED_BERET, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_ROBE_TOP, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_ROBE_LEGS, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_STOLE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_MITRE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_CLOAK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_CROZIER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.BANDOS_ROBE_TOP, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.BANDOS_ROBE_LEGS, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.BANDOS_STOLE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.BANDOS_MITRE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.BANDOS_CLOAK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.BANDOS_CROZIER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_PAGE_1, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_PAGE_2, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_PAGE_3, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_PAGE_4, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_PAGE_1, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_PAGE_2, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_PAGE_3, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_PAGE_4, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_PAGE_1, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_PAGE_2, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_PAGE_3, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_PAGE_4, 1, 1, RewardRarity.UNCOMMON), RewardLevel.ALL);
		add(new TreasureTrailsRewardItem(Items.PURPLE_SWEETS_2, 2, 27, RewardRarity.UNCOMMON), RewardLevel.ALL);

		// Easy Common
		add(new TreasureTrailsRewardItem(Items.TROUT_NOTED, 5, 20, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.LOBSTER_NOTED, 5, 20, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.STEEL_ARROW, 50, 150, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_PLATELEGS, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_PLATESKIRT, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_CHAINBODY, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_FULL_HELM, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.LEATHER_COWL, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SQ_SHIELD, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_KITESHIELD, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SWORD, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_LONGSWORD, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_2H_SWORD, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SCIMITAR, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_WARHAMMER, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_AXE, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_BATTLEAXE, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_MACE, 1, 1, RewardRarity.COMMON), RewardLevel.EASY);

		// Easy Uncommon
		add(new TreasureTrailsRewardItem(Items.BLACK_PLATEBODY_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_PLATELEGS_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_FULL_HELM_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_KITESHIELD_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_PLATEBODY_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_PLATELEGS_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_FULL_HELM_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_KITESHIELD_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.HIGHWAYMAN_MASK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SHIELD_H1, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SHIELD_H2, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SHIELD_H4, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_SHIELD_H5, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.STUDDED_BODY_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.STUDDED_BODY_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.STUDDED_CHAPS_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.STUDDED_CHAPS_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLUE_SKIRT_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLUE_SKIRT_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLUE_WIZARD_ROBE_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLUE_WIZARD_ROBE_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLUE_WIZARD_HAT_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLUE_WIZARD_HAT_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.EASY);

		// Easy Rare
		add(new TreasureTrailsRewardItem(Items.BLUE_BERET, 1, 1, RewardRarity.RARE), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.BLACK_BERET, 1, 1, RewardRarity.RARE), RewardLevel.EASY);
		add(new TreasureTrailsRewardItem(Items.WHITE_BERET, 1, 1, RewardRarity.RARE), RewardLevel.EASY);

		// Medium Common
		add(new TreasureTrailsRewardItem(Items.SWORDFISH_NOTED, 5, 20, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_ARROW, 50, 150, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATELEGS, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATESKIRT, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_DHIDE_CHAPS, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_CHAINBODY, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATEBODY, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_DHIDE_BODY, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_MED_HELM, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_FULL_HELM, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.COIF, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SQ_SHIELD, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_KITESHIELD, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_DAGGER, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SPEAR, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PICKAXE, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SWORD, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_LONGSWORD, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_2H_SWORD, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SCIMITAR, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_AXE, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_BATTLEAXE, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_MACE, 1, 1, RewardRarity.COMMON), RewardLevel.MEDIUM);

		// Medium Uncommon
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATEBODY_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATELEGS_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_KITESHIELD_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_FULL_HELM_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATEBODY_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_PLATELEGS_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_KITESHIELD_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_FULL_HELM_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.RED_HEADBAND, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.BLACK_HEADBAND, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.BROWN_HEADBAND, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.RED_BOATER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ORANGE_BOATER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_BOATER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.BLUE_BOATER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.BLACK_BOATER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SHIELD_H1, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.HOLY_BLESSING, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.UNHOLY_BLESSING, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.PEACEFUL_BLESSING, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.HONOURABLE_BLESSING, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.WAR_BLESSING, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ANCIENT_BLESSING, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SHIELD_H2, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SHIELD_H3, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SHIELD_H4, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ADAMANT_SHIELD_H5, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_DHIDE_BODY_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_DHIDE_BODY_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_DHIDE_CHAPS_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GREEN_DHIDE_CHAPS_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.BLACK_ELEGANT_SHIRT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.BLACK_ELEGANT_LEGS, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.PURPLE_ELEGANT_SHIRT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.PURPLE_ELEGANT_LEGS, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.WHITE_ELEGANT_BLOUSE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.WHITE_ELEGANT_SKIRT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.PURPLE_ELEGANT_BLOUSE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.PURPLE_ELEGANT_SKIRT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_CLOAK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_CLOAK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_CLOAK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MEDIUM);

		// Medium Rare
		add(new TreasureTrailsRewardItem(Items.HOLY_SANDALS, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.MUSKETEER_TABARD, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.MUSKETEER_PANTS, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.EXPLORER_BACKPACK, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.PITH_HELMET, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.RANGER_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.WIZARD_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.MAGES_BOOK, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.INFINITY_TOP, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.INFINITY_HAT, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.INFINITY_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.INFINITY_GLOVES, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.INFINITY_BOTTOMS, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_MITRE, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_MITRE, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_MITRE, 1, 1, RewardRarity.RARE), RewardLevel.MEDIUM);

		// Hard Common
		add(new TreasureTrailsRewardItem(Items.SHARK_NOTED, 5, 20, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.MAGIC_LONGBOW, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.MAGIC_SHORTBOW, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_ARROW, 50, 150, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATELEGS, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATESKIRT, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_CHAINBODY, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATEBODY, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_MED_HELM, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_FULL_HELM, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_SQ_SHIELD, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_KITESHIELD, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_DAGGER, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_SPEAR, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PICKAXE, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_SWORD, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_LONGSWORD, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_2H_SWORD, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_SCIMITAR, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_WARHAMMER, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_AXE, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_BATTLEAXE, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_MACE, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLACK_DHIDE_VAMB, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLACK_DHIDE_CHAPS, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLACK_DHIDE_BODY, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.HOLY_BLESSING, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.UNHOLY_BLESSING, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.PEACEFUL_BLESSING, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.HONOURABLE_BLESSING, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.WAR_BLESSING, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ANCIENT_BLESSING, 1, 1, RewardRarity.COMMON), RewardLevel.HARD);

		// Hard Uncommon
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATEBODY_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATELEGS_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_FULL_HELM_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_KITESHIELD_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATEBODY_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_PLATELEGS_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_FULL_HELM_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_KITESHIELD_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.TAN_CAVALIER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.DARK_CAVALIER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLACK_CAVALIER, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_SHIELD_H2, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RUNE_SHIELD_H3, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLUE_DHIDE_BODY_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLUE_DHIDE_BODY_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLUE_DHIDE_CHAPS_G, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BLUE_DHIDE_CHAPS_T, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ENCHANTED_ROBE, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ENCHANTED_TOP, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ENCHANTED_HAT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.HARD);

		// Hard Rare
		add(new TreasureTrailsRewardItem(Items.RANGER_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.PIRATES_HAT, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_PLATEBODY, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_PLATELEGS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_FULL_HELM, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_KITESHIELD, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_PLATEBODY, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_PLATELEGS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_FULL_HELM, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_KITESHIELD, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_PLATEBODY, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_PLATELEGS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_FULL_HELM, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_KITESHIELD, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GILDED_PLATEBODY, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GILDED_PLATELEGS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GILDED_PLATESKIRT, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GILDED_FULL_HELM, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GILDED_KITESHIELD, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_BRACERS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_DHIDE, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_CHAPS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_COIF, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_BRACERS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_DHIDE, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_CHAPS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_COIF, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_BRACERS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_DHIDE, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_CHAPS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_COIF, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_CROZIER, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_CROZIER, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_CROZIER, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_STOLE, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_STOLE, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_STOLE, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.RANGERS_TUNIC, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.SARADOMIN_DHIDE_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ZAMORAK_DHIDE_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.GUTHIX_DHIDE_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.BANDOS_DHIDE_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ARMADYL_DHIDE_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.ANCIENT_DHIDE_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.HARD);

		// Hard Very Rare
		add(new TreasureTrailsRewardItem(Items.ROBIN_HOOD_HAT, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_RANGE_TOP, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_RANGE_LEGS, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_RANGE_COIF, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_VAMBRACES, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_ROBE_TOP, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_ROBE, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_MAGE_HAT, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_AMULET, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_PLATELEGS, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_PLATEBODY, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_FULL_HELMET, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_KITESHIELD, 1, 1, RewardRarity.VERY_RARE), RewardLevel.HARD);

		// Master Common
		add(new TreasureTrailsRewardItem(Items.COINS, 1_000_000, 2_000_000, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_DAGGER, 1, 2, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_MACE, 1, 2, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_LONGSWORD, 1, 2, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_SCIMITAR, 1, 2, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_BATTLEAXE, 1, 2, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_HALBERD, 1, 2, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.NATURE_RUNE, 150, 300, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DEATH_RUNE, 150, 300, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BLOOD_RUNE, 150, 300, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.SOUL_RUNE, 150, 300, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.ONYX_DRAGON_BOLTS_E, 20, 30, RewardRarity.COMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.MANTA_RAY_NOTED, 20, 30, RewardRarity.COMMON), RewardLevel.MASTER);

		// Master Uncommon
		add(new TreasureTrailsRewardItem(Items.LESSER_DEMON_MASK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.GREATER_DEMON_MASK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BLACK_DEMON_MASK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.OLD_DEMON_MASK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.JUNGLE_DEMON_MASK, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.SAMURAI_KASA, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.SAMURAI_SHIRT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.SAMURAI_GLOVES, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.SAMURAI_GREAVES, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.SAMURAI_BOOTS, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BUCKET_HELM, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);

		// Master Rare
		add(new TreasureTrailsRewardItem(Items.CABBAGE_ROUND_SHIELD, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BLACK_UNICORN_MASK, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.WHITE_UNICORN_MASK, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CRIER_COAT, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CRIER_BELL, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.GOLDEN_APRON, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.TEAM_CAPE_ZERO, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.TEAM_CAPE_X, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.TEAM_CAPE_I, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DARK_TUXEDO_JACKET, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DARK_TUXEDO_CUFFS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DARK_TROUSERS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DARK_TUXEDO_SHOES, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.HELM_OF_RAEDWALD, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CLUE_HUNTER_GLOVES, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CLUE_HUNTER_GARB, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CLUE_HUNTER_TROUSERS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CLUE_HUNTER_BOOTS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CLUE_HUNTER_CLOAK, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.LIGHT_TUXEDO_JACKET, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.LIGHT_TUXEDO_CUFFS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.LIGHT_TROUSERS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.LIGHT_TUXEDO_SHOES, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BLACKSMITHS_HELM, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.RING_OF_NATURE, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.RING_OF_COINS, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);

		// Master Very Rare
		add(new TreasureTrailsRewardItem(Items.GOLDEN_CHEFS_HAT, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.CLUELESS_SCROLL, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.MONKS_ROBE_TOP_G, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.MONKS_ROBE_G, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.DRAGON_DEFENDER_ORNAMENT_KIT, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.LARGE_SPADE, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BLACK_LEPRECHAUN_HAT, 1, 1, RewardRarity.UNCOMMON), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BOWL_WIG, 1, 1, RewardRarity.RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_AXE, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_PICKAXE, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.BUCKET_HELM_G, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.HEAVY_CASKET, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_DRUIDIC_ROBE_TOP, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_DRUIDIC_STAFF, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_DRUIDIC_CLOAK, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_PLATESKIRT, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_BOW, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_LONGSWORD, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_WAND, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
		add(new TreasureTrailsRewardItem(Items.THIRD_AGE_CLOAK, 1, 1, RewardRarity.VERY_RARE), RewardLevel.MASTER);
	}
}
