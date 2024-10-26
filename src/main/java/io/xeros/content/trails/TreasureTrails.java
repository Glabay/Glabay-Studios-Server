package io.xeros.content.trails;

import java.util.List;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class TreasureTrails {

	public static final int EASY_CASKET = 2714;
	public static final int MEDIUM_CASKET = 2802;
	public static final int HARD_CASKET = 2775;
	public static final int MASTER_CASKET = 19841;

	public static final int EASY_CLUE_SCROLL = 2677;
	public static final int MEDIUM_CLUE_SCROLL = 2801;
	public static final int HARD_CLUE_SCROLL = 2722;
	public static final int MASTER_CLUE_SCROLL = 19835;

	private final Player player;

	public TreasureTrails(Player player) {
		this.player = player;
	}

	private static final int[] CLUE_HUNTER = {Items.CLUE_HUNTER_BOOTS, Items.CLUE_HUNTER_CLOAK,
			Items.CLUE_HUNTER_GARB, Items.CLUE_HUNTER_GLOVES, Items.CLUE_HUNTER_TROUSERS};

	private static int getCluesCompleted(Player player, RewardLevel difficulty) {
		switch (difficulty) {
			case EASY:
				return player.getEasyClueCounter();
			case MEDIUM:
				return player.getMediumClueCounter();
			case HARD:
				return player.getHardClueCounter();
			case MASTER:
				return player.getMasterClueCounter();
		}
		return 0;
	}

	private static void announceRare(Player player, GameItem item, RewardLevel difficulty) {
		PlayerHandler.executeGlobalMessage("[<col=CC0000>Treasure</col>] <col=255>" + player.getDisplayNameFormatted() + "</col> " +
				"<col=CC0000>received " +
				"<col=255>" + ItemDef.forId(item.id()).getName() + "</col> " +
				"<col=CC0000>from <col=255>" + Misc.formatPlayerName(difficulty.name().toLowerCase()) + " clue #" + getCluesCompleted(player, difficulty) + ".");
	}

	public static boolean rollMimicCasket(RewardLevel rewardLevel) {
		int roll = rewardLevel == RewardLevel.MASTER ? 10
				: rewardLevel == RewardLevel.HARD ? 25 : 0;
		return roll != 0 && Misc.trueRand(roll) == 0;
	}

	/**
	 * This method is used by clues.
	 * Rolls=1+rand(1, 2)
	 */
	public List<GameItem> generateRewardList(RewardLevel rewardLevel) {
		return generateRewardList(rewardLevel, 1 + Misc.random(2));
	}

	/**
	 * This will generate the drop list, globally announce rares and update collection log. It's assumed
	 * that you will be dropped/adding these items immediately after calling!
	 * @param rewardLevel The {@link RewardLevel}
	 * @return List of random items.
	 */
	public List<GameItem> generateRewardList(RewardLevel rewardLevel, int rolls) {
		player.getNpcDeathTracker().add(Misc.optimizeText(rewardLevel.name().toLowerCase()), -1, 0);
		List<GameItem> rewards = TreasureTrailsRewards.getRandomRewardItems(rewardLevel, rolls);

		for (GameItem item : rewards) {
			if (ItemDef.forId(item.id()).getName().contains("3rd") || item.id() == 2577 || ItemDef.forId(item.id()).getName().contains("mage's")) {
				announceRare(player, item, rewardLevel);
			}

			if (TreasureTrailsRewards.possibleDrops.get(rewardLevel).stream().anyMatch(it -> it.getItemId() == item.id())) {
				player.getCollectionLog().handleDrop(player, rewardLevel.ordinal(), item.id(), item.amount());
			}
		}

		if (rewardLevel == RewardLevel.MASTER || rewardLevel == RewardLevel.HARD) {
			int rand = Misc.trueRand(rewardLevel == RewardLevel.MASTER ? 512 : 1024);
			int item = CLUE_HUNTER[Misc.trueRand(CLUE_HUNTER.length)];
			if (rand == 0) {
				announceRare(player, new GameItem(item, 1), rewardLevel);
				rewards.add(new GameItem(item, 1));
			}
		}

		return rewards;
	}

	public void addRewards(RewardLevel rewardLevel) {
		List<GameItem> rewards = generateRewardList(rewardLevel);
		rewards.forEach(it -> player.getItems().addItemUnderAnyCircumstance(it.id(), it.amount()));
		displayRewards(rewards);
	}

	public void displayRewards(List<GameItem> rewards) {
		player.outStream.createFrameVarSizeWord(53);
		player.outStream.writeUShort(6963);
		player.outStream.writeUShort(rewards.size());
		for (int i = 0; i < rewards.size(); i++) {
			if (player.playerItemsN[i] > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(rewards.get(i).amount());
			} else {
				player.outStream.writeByte(rewards.get(i).amount());
			}
			if (rewards.size() > 0) {
				player.outStream.writeWordBigEndianA(rewards.get(i).id() + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.endFrameVarSizeWord();
		player.flushOutStream();
		player.getPA().showInterface(6960);
	}

	private static void openCasket(Player player, RewardLevel rewardLevel) {
		if (player.getItems().freeSlots() < 3) {
			player.sendMessage("You need at least 3 free slots to open this.");
			return;
		}

		player.getItems().deleteItem(rewardLevel.getCasketId(), 1);
		player.getTrails().addRewards(rewardLevel);

		switch (rewardLevel) {
			case EASY:
				player.setEasyClueCounter(player.getEasyClueCounter() + 1);
				player.sendMessage("<col=2d256d>You have completed " + player.getEasyClueCounter() + " Easy Treasure Trails.");
				break;
			case MEDIUM:
				player.setMediumClueCounter(player.getMediumClueCounter() + 1);
				player.sendMessage("<col=2d256d>You have completed " + player.getMediumClueCounter() + " medium Treasure Trails.");
				break;
			case HARD:
				player.setHardClueCounter(player.getHardClueCounter() + 1);
				player.sendMessage("<col=2d256d>You have completed " + player.getHardClueCounter() + " hard Treasure Trails.");
				break;
			case MASTER:
				player.setMasterClueCounter(player.getMasterClueCounter() + 1);
				player.sendMessage("<col=2d256d>You have completed " + player.getMasterClueCounter() + " master Treasure Trails.");
				PetHandler.roll(player, PetHandler.Pets.BLOODHOUND);
				break;
		}
	}

	private static void openClueScroll(Player c, RewardLevel rewardLevel) {
		if (rewardLevel == RewardLevel.MASTER && !MasterClue.checkRequirementOnOpen(c)) {
			return;
		}

		Achievements.increase(c, AchievementType.CLUES, 1);
		c.getItems().deleteItem(rewardLevel.getClueScrollId(), 1);

		if (rollMimicCasket(rewardLevel)) {
			c.getItems().addItem(Items.MIMIC, 1);
			c.sendMessage("You've received a Mimic casket!");
		} else {
			c.getItems().addItem(rewardLevel.getCasketId(), 1);
			c.sendMessage("You've received a " + rewardLevel.getFormattedName() + " clue scroll casket.");
		}
	}

	public static boolean firstClickItem(Player c, int itemId) {
		for (RewardLevel rewardLevel : RewardLevel.values()) {
			if (rewardLevel.getCasketId() == itemId) {
				openCasket(c, rewardLevel);
				return true;
			}

			if (rewardLevel.getClueScrollId() == itemId) {
				openClueScroll(c, rewardLevel);
				return true;
			}
		}

		return false;
	}
}
