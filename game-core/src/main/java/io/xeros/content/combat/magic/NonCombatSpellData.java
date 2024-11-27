package io.xeros.content.combat.magic;

import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.model.Staff;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;

public class NonCombatSpellData extends MagicRequirements {

	public static void attemptDate(Player c, int action) {
		switch (action) {
		case 4135:
			bonesToBase(c, 15, new int[] { 557, 555, 561 }, new int[] { 2, 2, 1 }, new int[] { 526, 1963 });
			break;
		case 62005:
			bonesToBase(c, 60, new int[] { 557, 555, 561 }, new int[] { 4, 4, 2 }, new int[] { !c.getItems().playerHasItem(532) ? 526 : 532, 6883 });
			break;
		case 8014:
			bonesToBase(c, 15, new int[] { 8014, -1, -1 }, new int[] { 1, -1, -1 }, new int[] { !c.getItems().playerHasItem(532) ? 526 : 532, 1963 });
			break;
		case 8015:
			bonesToBase(c, 60, new int[] { 8015, -1, -1 }, new int[] { 1, -1, -1 }, new int[] { !c.getItems().playerHasItem(532) ? 526 : 532, 6883 });
			break;
		}
	}

	public static boolean consumeRunesOnCast(Player player, int[] runes, int[] amount) {
		for (int index = 0; index < runes.length; index++) {
			if (!player.getRunePouch().hasRunes(runes[index], amount[index]) && !player.getItems().playerHasItem(runes[index], amount[index])
					&& !Staff.getRunesProvidedBy(player.playerEquipment[3]).contains(runes[index])) {
				player.sendMessage("You don't have the runes required to cast this spell.");
				return false;
			}
		}

		for (int index = 0; index < runes.length; index++) {
			if (Staff.getRunesProvidedBy(player.playerEquipment[3]).contains(runes[index])) {
				continue;
			}

			if (player.getRunePouch().hasRunes(runes[index], amount[index])) {
				player.getRunePouch().deleteRunesOnCast(runes[index], amount[index]);
			} else if (player.getItems().playerHasItem(runes[index], amount[index])) {
				player.getItems().deleteItem(runes[index], amount[index]);
			}
		}

		return true;
	}

	/**
	 * @param c player.
	 * @param levelReq requirement for the spell.
	 * @param runes required to cast the spell.
	 * @param amount of @param runes required to cast the spell.
	 * @param item Item required/given upon casted spell.
	 */
	public static void bonesToBase(Player c, int levelReq, int[] runes, int[] amount, int[] item) {
		if (!hasRequiredLevel(c, levelReq)) {
			c.sendMessage("You need to have a magic level of " + levelReq + " to cast this spell.");
			return;
		}
		if ((!c.getItems().playerHasItem(item[0], 1))) {
			c.sendMessage("You need some bones to cast this spell!");
			return;
		}

		if (!consumeRunesOnCast(c, runes, amount)) {
			return;
		}

		c.getItems().replaceItem(c, item[0], item[1]);
		c.gfx100(141);
		c.startAnimation(722);
		c.getPA().addSkillXPMultiplied(35, 6, true);
		c.sendMessage("You use your magic power to convert bones into " + ItemAssistant.getItemName(item[1]).toLowerCase() + (item[1] != 526 ? ("e") : ("")) + "s!");
		c.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.BONES_TO_PEACHES);
		c.attacking.reset();
	}

	public static void superHeatItem(Player c, int itemID) {
		if (System.currentTimeMillis() - c.alchDelay > 700) {
			if (c.playerLevel[6] < 43) {
				c.sendMessage("You need to have a magic level of 43 to cast this spell.");
				return;
			}
			if (!MagicRequirements.checkMagicReqs(c, 54, true)) {
				return;
			}

			int[][] data = { { 436, 1, 438, 1, 2349, 6, 1 }, // TIN
					{ 438, 1, 436, 1, 2349, 6, 1 }, // COPPER
					{ 440, 1, -1, -1, 2351, 13, 15 }, // IRON ORE
					{ 453, 1, 440, 1, 2353, 15, 20 }, // STEEL BAR
					{ 442, 1, -1, -1, 2355, 18, 30 }, // SILVER ORE
					{ 444, 1, -1, -1, 2357, 23, 40 }, // GOLD BAR
					{ 447, 1, 453, 1, 2359, 30, 50 }, // MITHRIL ORE
					{ 449, 1, 453, 1, 2361, 38, 70 }, // ADDY ORE
					{ 451, 1, 453, 1, 2363, 50, 85 }, // RUNE ORE
			};
			for (int i = 0; i < data.length; i++) {
				if (itemID == data[i][0]) {
					if (!c.getItems().playerHasItem(data[i][2], data[i][3])) {
						c.sendMessage("You haven't got enough " + ItemAssistant.getItemName(data[i][2]).toLowerCase() + " to cast this spell!");
						return;
					}
					if (c.playerLevel[Player.playerSmithing] < data[i][6]) {
						c.sendMessage("You need a smithing level of " + data[i][6] + " to heat this ore.");
						return;
					}
					c.getItems().deleteItem(itemID, c.getItems().getInventoryItemSlot(itemID), 1);
					for (int lol = 0; lol < data[i][3]; lol++) {
						c.getItems().deleteItem(data[i][2], c.getItems().getInventoryItemSlot(data[i][2]), 1);
					}
					c.getItems().addItem(data[i][4], 1);
					c.alchDelay = System.currentTimeMillis();
					c.getPA().addSkillXPMultiplied(data[i][5], Player.playerSmithing, true);
					c.getPA().addSkillXPMultiplied(53, 6, true);
					c.startAnimation(CombatSpellData.MAGIC_SPELLS[54][2]);
					c.gfx100(CombatSpellData.MAGIC_SPELLS[54][3]);
					c.getPA().sendFrame106(6);
					return;
				}
			}
			c.sendMessage("You can only cast superheat item on ores!");
			return;
		}
	}
}