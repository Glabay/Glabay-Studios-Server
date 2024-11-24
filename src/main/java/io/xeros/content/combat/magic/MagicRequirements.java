package io.xeros.content.combat.magic;

import io.xeros.Configuration;
import io.xeros.content.skills.crafting.BryophytaStaff;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import org.apache.commons.lang3.RandomUtils;

public class MagicRequirements extends MagicConfig {

	public static boolean hasRunes(Player c, int[] runes, int[] amount) {
		if (c.getRunePouch().hasRunes(runes, amount)) {
			return true;
		}
		for (int i = 0; i < runes.length; i++) {
			if (c.getItems().playerHasItem(runes[i], amount[i])) {
				return true;
			}
		}
		c.sendMessage("You don't have enough required runes to cast this spell!");
		return false;
	}

	public static void deleteRunes(Player c, int[] runes, int[] amount) {
		if (c.getRunePouch().hasRunes(runes, amount)) {
			c.getRunePouch().deleteRunesOnCast(runes, amount);
			return;
		}
		for (int i = 0; i < runes.length; i++) {
			c.getItems().deleteItem(runes[i], c.getItems().getInventoryItemSlot(runes[i]), amount[i]);
		}
	}

	public static boolean hasRequiredLevel(Player c, int i) {
		return c.playerLevel[6] >= i;
	}

	public static boolean wearingTomeOfFire(Player player) {
		return player.playerEquipment[Player.playerShield] == Items.TOME_OF_FIRE;
	}

	public static boolean wearingStaff(Player c, int runeId) {
		int wep = c.playerEquipment[Player.playerWeapon];
		switch (runeId) {
		case Items.FIRE_RUNE:
			if (wearingTomeOfFire(c) || wep == 12000 || wep == 1387 || wep == 1393 || wep == 1401 || wep == 12796 || wep == 11789 || wep == 12000 || wep == 12795 || wep == 22335)
				return true;
			break;
		case Items.WATER_RUNE:
			if (wep == 1383 || wep == 1395 || wep == 12796 || wep == 11789 || wep == 6563 || wep == 1403 || wep == 21006 || wep == 20730  || wep == 12795)
				return true;
			break;
		case Items.AIR_RUNE:
			if (wep == 12000 ||wep == 1381 || wep == 1397 || wep == 1405 || wep == 12000 || wep == 20736 || wep == 20736 || wep == 20730 || wep == 22335)
				return true;
			break;

		case Items.EARTH_RUNE:
			if (wep == 1385 || wep == 1399 || wep == 1407 || wep == 6563 || wep == 20736 | wep == 20736)
				return true;
			break;
		case Items.NATURE_RUNE:
			if (BryophytaStaff.isWearingStaffWithCharge(c))
				return true;
			break;
		}
		return false;
	}

	public static boolean checkMagicReqs(Player c, int spell, boolean deleteRunes) {
		if (spell > -1) {
			int rune1 = CombatSpellData.MAGIC_SPELLS[spell][8];
			int rune2 = CombatSpellData.MAGIC_SPELLS[spell][10];
			int rune3 = CombatSpellData.MAGIC_SPELLS[spell][12];
			int rune4 = CombatSpellData.MAGIC_SPELLS[spell][14];
			if (!Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
				boolean hasRunesInPouch1 = c.getRunePouch().hasRunes(rune1, CombatSpellData.MAGIC_SPELLS[spell][9]);
				boolean hasRunesInPouch2 = c.getRunePouch().hasRunes(rune2, CombatSpellData.MAGIC_SPELLS[spell][11]);
				boolean hasRunesInPouch3 = c.getRunePouch().hasRunes(rune3, CombatSpellData.MAGIC_SPELLS[spell][13]);
				boolean hasRunesInPouch4 = c.getRunePouch().hasRunes(rune4, CombatSpellData.MAGIC_SPELLS[spell][15]);

				boolean hasRunesInInventory1 = c.getItems().playerHasItem(rune1, CombatSpellData.MAGIC_SPELLS[spell][9]);
				boolean hasRunesInInventory2 = c.getItems().playerHasItem(rune2, CombatSpellData.MAGIC_SPELLS[spell][11]);
				boolean hasRunesInInventory3 = c.getItems().playerHasItem(rune3, CombatSpellData.MAGIC_SPELLS[spell][13]);
				boolean hasRunesInInventory4 = c.getItems().playerHasItem(rune4, CombatSpellData.MAGIC_SPELLS[spell][15]);

				boolean hasStaff1 = wearingStaff(c, rune1);
				boolean hasStaff2 = wearingStaff(c, rune2);
				boolean hasStaff3 = wearingStaff(c, rune3);
				boolean hasStaff4 = wearingStaff(c, rune4);

				if (!(hasRunesInPouch1 || hasRunesInInventory1 || hasStaff1)) {
					c.sendMessage("You don't have the required runes to cast this spell.");
					return false;
				}

				if (!(hasRunesInPouch2 || hasRunesInInventory2 || hasStaff2)) {
					c.sendMessage("You don't have the required runes to cast this spell.");
					return false;
				}

				if (!(hasRunesInPouch3 || hasRunesInInventory3 || hasStaff3)) {
					c.sendMessage("You don't have the required runes to cast this spell.");
					return false;
				}

				if (!(hasRunesInPouch4 || hasRunesInInventory4 || hasStaff4)) {
					c.sendMessage("You don't have the required runes to cast this spell.");
					return false;
				}
			}


			if (c.playerAttackingIndex > 0) {
				if (PlayerHandler.players[c.playerAttackingIndex] != null) {
					for (int r = 0; r < CombatSpellData.REDUCE_SPELLS.length; r++) { //Reducing/weaken spells
						if (CombatSpellData.REDUCE_SPELLS[r] == CombatSpellData.MAGIC_SPELLS[spell][0]) {
							c.reduceSpellId = r;
							Player.canUseReducingSpell[c.reduceSpellId] = (System.currentTimeMillis() - PlayerHandler.players[c.playerAttackingIndex].reduceSpellDelay[c.reduceSpellId]) > CombatSpellData.REDUCE_SPELL_TIME[c.reduceSpellId];
							break;
						}
					}
					if (!Player.canUseReducingSpell[c.reduceSpellId]) {
						c.sendMessage("That player is currently immune to this spell.");
						c.usingMagic = false;
						c.stopMovement();
						c.attacking.reset();
						return false;
					}
				}
			}

			int staffRequired = CombatSpellData.getStaffNeeded(c, spell);
			if (staffRequired > 0 && Configuration.RUNES_REQUIRED) { // staff required
				if (c.playerEquipment[Player.playerWeapon] != staffRequired) {
					c.sendMessage("You need a " + ItemAssistant.getItemName(staffRequired).toLowerCase() + " to cast this spell.");
					return false;
				}
			}

			if (Configuration.MAGIC_LEVEL_REQUIRED) { // check magic level
				if (c.playerLevel[6] < CombatSpellData.MAGIC_SPELLS[spell][1]) {
					c.sendMessage("You need to have a magic level of " + CombatSpellData.MAGIC_SPELLS[spell][1] + " to cast this spell.");
					return false;
				}
			}
			boolean runesNecessary = true;
			if ((c.playerEquipment[Player.playerWeapon] == 11791 || c.playerEquipment[Player.playerWeapon] == 12904) && RandomUtils.nextInt(0, 100) < 13) {
				runesNecessary = false;
			}

			if (deleteRunes) {
				if (!Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
					if (Configuration.RUNES_REQUIRED && runesNecessary) {
						if (rune1 > 0) { // deleting runes
							if (!wearingStaff(c, rune1)) {
								c.getRunePouch().deleteRunesOnCast(rune1, CombatSpellData.MAGIC_SPELLS[spell][9]);
								c.getItems().deleteItem(rune1, CombatSpellData.MAGIC_SPELLS[spell][9]);
							} else {
								BryophytaStaff.depleteIfUsed(c, rune1);
							}
						}
						if (rune2 > 0) {
							if (!wearingStaff(c, rune2)) {
								c.getRunePouch().deleteRunesOnCast(rune2, CombatSpellData.MAGIC_SPELLS[spell][11]);
								c.getItems().deleteItem(rune2, CombatSpellData.MAGIC_SPELLS[spell][11]);
							} else {
								BryophytaStaff.depleteIfUsed(c, rune2);
							}
						}

						if (rune3 > 0) {
							if (!wearingStaff(c, rune3)) {
								c.getRunePouch().deleteRunesOnCast(rune3, CombatSpellData.MAGIC_SPELLS[spell][13]);
								c.getItems().deleteItem(rune3, CombatSpellData.MAGIC_SPELLS[spell][13]);
							} else {
								BryophytaStaff.depleteIfUsed(c, rune3);
							}
						}
						if (rune4 > 0) {
							if (!wearingStaff(c, rune4)) {
								c.getRunePouch().deleteRunesOnCast(rune4, CombatSpellData.MAGIC_SPELLS[spell][15]);
								c.getItems().deleteItem(rune4, CombatSpellData.MAGIC_SPELLS[spell][15]);
							} else {
								BryophytaStaff.depleteIfUsed(c, rune4);
							}
						}
					}
				}
			}
		}
		return true;
	}
}