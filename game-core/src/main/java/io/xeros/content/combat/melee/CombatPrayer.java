package io.xeros.content.combat.melee;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.xeros.Server;
import io.xeros.content.itemskeptondeath.ItemsKeptOnDeathInterface;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.model.entity.Bonus;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;

/**
 * Handles prayers drain and switching
 * 
 * @author 2012
 * @author Organic
 */

public class CombatPrayer {
	
	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1,
			CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4,
			ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7,
			RAPID_RESTORE = 8, RAPID_HEAL = 9, PROTECT_ITEM = 10,
			HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13,
			ULTIMATE_STRENGTH = 14, INCREDIBLE_REFLEXES = 15,
			PROTECT_FROM_MAGIC = 16, PROTECT_FROM_RANGED = 17,
			PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20,
			RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, PRESERVE = 24,
			CHIVALRY = 25, PIETY = 26, RIGOUR = 27, AUGURY = 28;
    public static final int[] PRAYER_LEVEL_REQUIRED = { 1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43,
            44, 45, 46, 49, 52, 55, 60, 70, 74, 77 };
    public static final int[] PRAYER = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
            24, 25, 26, 27, 28 };
    public static final String[] PRAYER_NAME = { "Thick Skin", "Burst of Strength", "Clarity of Thought", "Sharp Eye",
            "Mystic Will", "Rock Skin", "Superhuman Strength", "Improved Reflexes", "Rapid Restore", "Rapid Heal",
            "Protect Item", "Hawk Eye", "Mystic Lore", "Steel Skin", "Ultimate Strength", "Incredible Reflexes",
            "Protect from Magic", "Protect from Missiles", "Protect from Melee", "Eagle Eye", "Mystic Might",
            "Retribution", "Redemption", "Smite", "Preserve", "Chivalry", "Piety", "Rigour", "Augury" };
    public static final int[] PRAYER_GLOW = { 83, 84, 85, 700, 701, 86, 87, 88, 89, 90, 91, 702, 703, 92, 93, 94, 95, 96, 97,
            704, 705, 98, 99, 100, 708, 706, 707, 710, 712 };
    public static final int[] PRAYER_HEAD_ICONS = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0,
            -1, -1, 3, 5, 4, -1, -1, -1, -1, -1 };

    public static double[] prayerData = { 1, // Thick Skin.
			1, // Burst of Strength.
			1, // Clarity of Thought.
			1, // Sharp Eye.
			1, // Mystic Will.
			2, // Rock Skin.
			2, // SuperHuman Strength.
			2, // Improved Reflexes.
			0.4, // Rapid restore.
			0.6, // Rapid Heal.
			0.6, // Protect Items.
			1.5, // Hawk eye.
			2, // Mystic Lore.
			4, // Steel Skin.
			4, // Ultimate Strength.
			4, // Incredible Reflexes.
			4, // Protect from Magic.
			4, // Protect from Missiles.
			4, // Protect from Melee.
			4, // Eagle Eye.
			4, // Mystic Might.
			1, // Retribution.
			2, // Redemption.
			6, // Smite.
			1.5, // Preserve.
			8, // Chivalry.
			8, // Piety.
			8, // Rigour.
			8, // Augury.
	};

	public static void handlePrayerDrain(Player c) {
		c.usingPrayer = false;
		double toRemove = 0.0;
		if (c.isDead || c.getHealth().getCurrentHealth() <= 0)
			return;
		for (int j = 0; j < prayerData.length; j++) {
			if (c.prayerActive[j]) {
				toRemove += prayerData[j] / 20;
				c.usingPrayer = true;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1.5 + (0.035 * c.getItems().getBonus(Bonus.PRAYER)));
		}
		c.prayerPoint -= toRemove;
		if (c.prayerPoint <= 0) {
			c.prayerPoint = 1.0 + c.prayerPoint;
			reducePrayerLevel(c);
		}
	}

	public static void shiftProtectionPrayersRight(Player player, boolean set) {
		player.setProtectionPrayersShiftRight(set);
		if (player.isProtectionPrayersShiftRight()) {
			shiftPrayersRight(player);
		} else {
			shiftPrayersLeft(player);
		}
	}

	private static void shiftPrayersLeft(Player player) {
		if (player.protectingMelee()) {
			activatePrayer(player, PROTECT_FROM_RANGED, false);
		} else if (player.protectingRange()) {
			activatePrayer(player, PROTECT_FROM_MAGIC, false);
		} else if (player.protectingMagic()) {
			activatePrayer(player, PROTECT_FROM_MELEE, false);
		}
	}

	private static void shiftPrayersRight(Player player) {
		if (player.protectingMelee()) {
			activatePrayer(player, PROTECT_FROM_MAGIC, false);
		} else if (player.protectingRange()) {
			activatePrayer(player, PROTECT_FROM_MELEE, false);
		} else if (player.protectingMagic()) {
			activatePrayer(player, PROTECT_FROM_RANGED, false);
		}
	}

	public static void reducePrayerLevel(Player c) {
		if (c.playerLevel[5] - 1 > 0) {
			c.playerLevel[5] -= 1;
		} else {
			c.sendMessage("You have run out of prayer points!");
			c.playerLevel[5] = 0;
			resetPrayers(c);
			c.prayerId = -1;
		}
		c.getPA().refreshSkill(5);
	}

	public static void resetPrayers(Player c) {
		for (int i = 0; i < c.prayerActive.length; i++) {
			c.prayerActive[i] = false;
			c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
		}
		c.headIcon = -1;
		c.getPA().requestUpdates();
	}

	public static void resetPrayer(int id, Player p) {
		p.prayerActive[id] = false;
		p.getPA().sendFrame36(PRAYER_GLOW[id], 0);
		if (isOverheadPrayer(id))
			p.headIcon = -1;
		p.getPA().requestUpdates();
	}
	
	public static void resetOverHeads(Player c) {
		c.prayerActive[16] = false;
		c.getPA().sendFrame36(PRAYER_GLOW[17], 0);
		c.prayerActive[17] = false;
		c.getPA().sendFrame36(PRAYER_GLOW[17], 0);
		c.prayerActive[18] = false;
		c.getPA().sendFrame36(PRAYER_GLOW[18], 0);
		c.prayerActive[23] = false;
		c.getPA().sendFrame36(PRAYER_GLOW[23], 0);
		c.prayerActive[16] = false;
		c.prayerActive[17] = false;
		c.prayerActive[18] = false;
		c.prayerActive[23] = false;
		c.headIcon = -1;
		c.getPA().requestUpdates();
	}

	public static boolean isPrayerOn(Player player, int id) {
		return player.prayerActive[id];
	}

	public static boolean isPrayerOn(Player player, int... ids) {
		for (int prayerId: ids) {
			if (player.prayerActive[prayerId]) return true;
		}

		return false;
	}

	public static boolean isOverheadPrayer(int id) {
		List<Integer> overheads = Arrays.asList(16, 17, 18, 21, 22, 23);
		return overheads.stream().anyMatch(i -> id == i);
	}
	
	public static int[] getTurnOff(int id) {
		int[] turnOff = new int[0];
		switch (id) {
		case THICK_SKIN:
			turnOff = new int[] { ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case ROCK_SKIN:
			turnOff = new int[] { THICK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case STEEL_SKIN:
			turnOff = new int[] { THICK_SKIN, ROCK_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case CLARITY_OF_THOUGHT:
			turnOff = new int[] { IMPROVED_REFLEXES, INCREDIBLE_REFLEXES,
					CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case IMPROVED_REFLEXES:
			turnOff = new int[] { CLARITY_OF_THOUGHT, INCREDIBLE_REFLEXES,
					CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case INCREDIBLE_REFLEXES:
			turnOff = new int[] { IMPROVED_REFLEXES, CLARITY_OF_THOUGHT,
					CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case BURST_OF_STRENGTH:
			turnOff = new int[] { SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH,
					SHARP_EYE, MYSTIC_WILL, HAWK_EYE, MYSTIC_LORE, EAGLE_EYE,
					MYSTIC_MIGHT, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case SUPERHUMAN_STRENGTH:
			turnOff = new int[] { BURST_OF_STRENGTH, ULTIMATE_STRENGTH,
					SHARP_EYE, MYSTIC_WILL, HAWK_EYE, MYSTIC_LORE, EAGLE_EYE,
					MYSTIC_MIGHT, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case ULTIMATE_STRENGTH:
			turnOff = new int[] { SUPERHUMAN_STRENGTH, BURST_OF_STRENGTH,
					SHARP_EYE, MYSTIC_WILL, HAWK_EYE, MYSTIC_LORE, EAGLE_EYE,
					MYSTIC_MIGHT, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case SHARP_EYE:
			turnOff = new int[] { MYSTIC_WILL, HAWK_EYE, MYSTIC_LORE,
					EAGLE_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case HAWK_EYE:
			turnOff = new int[] { MYSTIC_WILL, SHARP_EYE, MYSTIC_LORE,
					EAGLE_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case EAGLE_EYE:
			turnOff = new int[] { MYSTIC_WILL, HAWK_EYE, MYSTIC_LORE,
					SHARP_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case MYSTIC_WILL:
			turnOff = new int[] { SHARP_EYE, HAWK_EYE, MYSTIC_LORE, EAGLE_EYE,
					MYSTIC_MIGHT, BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH,
					ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT, IMPROVED_REFLEXES,
					INCREDIBLE_REFLEXES, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case MYSTIC_LORE:
			turnOff = new int[] { MYSTIC_WILL, HAWK_EYE, SHARP_EYE, EAGLE_EYE,
					MYSTIC_MIGHT, BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH,
					ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT, IMPROVED_REFLEXES,
					INCREDIBLE_REFLEXES, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case MYSTIC_MIGHT:
			turnOff = new int[] { MYSTIC_WILL, HAWK_EYE, MYSTIC_LORE,
					EAGLE_EYE, SHARP_EYE, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY, RIGOUR, AUGURY };
			break;
		case PROTECT_FROM_MAGIC:
			turnOff = new int[] { REDEMPTION, SMITE, RETRIBUTION,
					PROTECT_FROM_RANGED, PROTECT_FROM_MELEE };
			break;
		case PROTECT_FROM_RANGED:
			turnOff = new int[] { REDEMPTION, SMITE, RETRIBUTION,
					PROTECT_FROM_MAGIC, PROTECT_FROM_MELEE };
			break;
		case PROTECT_FROM_MELEE:
			turnOff = new int[] { REDEMPTION, SMITE, RETRIBUTION,
					PROTECT_FROM_RANGED, PROTECT_FROM_MAGIC };
			break;
		case RETRIBUTION:
			turnOff = new int[] { REDEMPTION, SMITE, PROTECT_FROM_MELEE,
					PROTECT_FROM_RANGED, PROTECT_FROM_MAGIC };
			break;
		case REDEMPTION:
			turnOff = new int[] { RETRIBUTION, SMITE, PROTECT_FROM_MELEE,
					PROTECT_FROM_RANGED, PROTECT_FROM_MAGIC };
			break;
		case SMITE:
			turnOff = new int[] { REDEMPTION, RETRIBUTION, PROTECT_FROM_MELEE,
					PROTECT_FROM_RANGED, PROTECT_FROM_MAGIC };
			break;
		case CHIVALRY:
			turnOff = new int[] { SHARP_EYE, MYSTIC_WILL, HAWK_EYE,
					MYSTIC_LORE, EAGLE_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, PIETY, RIGOUR, AUGURY, THICK_SKIN,
					ROCK_SKIN, STEEL_SKIN };
			break;
		case PIETY:
			turnOff = new int[] { SHARP_EYE, MYSTIC_WILL, HAWK_EYE,
					MYSTIC_LORE, EAGLE_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY,
					THICK_SKIN, ROCK_SKIN, STEEL_SKIN, RIGOUR, AUGURY };
			break;
		case RIGOUR:
			turnOff = new int[] { SHARP_EYE, MYSTIC_WILL, HAWK_EYE,
					MYSTIC_LORE, EAGLE_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY,
					THICK_SKIN, ROCK_SKIN, STEEL_SKIN, PIETY, AUGURY };
			break;
		case AUGURY:
			turnOff = new int[] { SHARP_EYE, MYSTIC_WILL, HAWK_EYE,
					MYSTIC_LORE, EAGLE_EYE, MYSTIC_MIGHT, BURST_OF_STRENGTH,
					SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CLARITY_OF_THOUGHT,
					IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY,
					THICK_SKIN, ROCK_SKIN, STEEL_SKIN, RIGOUR, PIETY };
			break;
		}
		return turnOff;
	}

	public static void activatePrayer(Player c, int i) {
		activatePrayer(c, i, true);
	}

	private static void activatePrayer(Player c, int i, boolean shift) {
		// Shift prayers right
		if (shift && c.isProtectionPrayersShiftRight()) {
			if (i == 18) {
				i = 16;
			} else if (i == 17) {
				i = 18;
			} else if (i == 16) {
				i = 17;
			}
		}
//		//def - 13,0,5
//		if (i == 13 || i == 0 || i == 5 && c.prayerActive[27]) {
//			c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
//			c.sendMessage("You cannot activate this prayer while rigour is activated.");
//			return;
//		}

		if (i == RETRIBUTION) {
			//c.sendMessage("@red@rETRIBUTION is disabled until it can be fixed.");
			//c.getPA().sendFrame36(PRAYER_GLOW[i], 0);

		}

		if (i == REDEMPTION) {
			//c.sendMessage("@red@Redemption is disabled until it can be fixed.");
			//c.getPA().sendFrame36(PRAYER_GLOW[i], 0);

		}

		if (i == 18) { // Protect melee
            if (((TourneyManager.getSingleton().isInArena(c) || TourneyManager.getSingleton().isInLobbyBounds(c)))
					&& (TourneyManager.getSingleton().getCurrentPrayerBlock().equals("PROTECT_MELEE"))
			) {
                c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
                c.sendMessage("You cannot activate this prayer while in the arena");
                return;
            }
        }
		if (i == 16) { // Protect mage
			if (((TourneyManager.getSingleton().isInArena(c) || TourneyManager.getSingleton().isInLobbyBounds(c))) &&
					((TourneyManager.getSingleton().getCurrentPrayerBlock().equals("PROTECT_MAGIC")
							|| TourneyManager.getSingleton().getCurrentPrayerBlock().equals("PROTECT_MELEE")))
			) {
                c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
                c.sendMessage("You cannot activate this prayer while in the arena");
                return;
            }

		}
		if (i == 17) { // Protect range
			if (((TourneyManager.getSingleton().isInArena(c) || TourneyManager.getSingleton().isInLobbyBounds(c)))
					&& ((TourneyManager.getSingleton().getCurrentPrayerBlock().equals("PROTECT_RANGE")
					|| TourneyManager.getSingleton().getCurrentPrayerBlock().equals("PROTECT_MELEE"))
			)
			) {
                c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
                c.sendMessage("You cannot activate this prayer while in the arena");
                return;
            }
		}
		if (i == 25) {
			if (c.playerLevel[1] < 65) {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				if (c.playerLevel[5] < 60) {
					c.getPA().sendFrame126("You need a @bl2@Prayer @bla@level of " + PRAYER_LEVEL_REQUIRED[i] + " and 65 defence to use @bl2@" + PRAYER_NAME[i] + "@bla@.", 357);
					c.getPA().sendFrame126("Click here to continue", 358);
					c.getPA().sendChatboxInterface(356);
				}
				c.nextChat = -1;
				c.sendMessage("You need a defence @bla@level of at least 65 to use this prayer.");
				return;
			}
		}
		if (i == 26) {
			if (c.playerLevel[1] < 70) {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				if (c.playerLevel[5] < 70) {
					c.getPA().sendFrame126("You need a @bl2@Prayer @bla@level of " + PRAYER_LEVEL_REQUIRED[i] + " and 70 defence to use @bl2@" + PRAYER_NAME[i] + "@bla@.", 357);
					c.getPA().sendFrame126("Click here to continue", 358);
					c.getPA().sendChatboxInterface(356);
				}
				c.nextChat = -1;
				c.sendMessage("You need a defence @bla@level of at least 70 to use this prayer.");
				return;
			}
		}
		if (i == 27) {
			if (!c.rigour && !Server.isDebug() && !Boundary.isIn(c, Boundary.OUTLAST) && !Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA) && !Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)) {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				c.getDH().sendStatement("You have not unlocked the Dextorious prayer scroll");
				return;
			}

			if (c.playerLevel[1] < 70) {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				if (c.playerLevel[5] < 74) {
					c.getPA().sendFrame126("You need a @bl2@Prayer @bla@level of " + PRAYER_LEVEL_REQUIRED[i] + " to use @bl2@" + PRAYER_NAME[i] + "@bla@.", 357);
					c.getPA().sendFrame126("Click here to continue", 358);
					c.getPA().sendChatboxInterface(356);
				}
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				c.nextChat = -1;
				c.sendMessage("You need a defence @bla@level of at least 70 to use this prayer.");
				return;
			}
		}
		if (i == 28) {
			if (!c.augury && !Server.isDebug() && !Boundary.isIn(c, Boundary.OUTLAST) && !Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA) && !Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)) {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				c.getDH().sendStatement("You have not unlocked the Arcane prayer scroll");
				return;
			}
			if (c.playerLevel[1] < 70) {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				if (c.playerLevel[5] < 77) {
					c.getPA().sendFrame126("You need a @bl2@Prayer @bla@level of " + PRAYER_LEVEL_REQUIRED[i] + " to use @bl2@" + PRAYER_NAME[i] + "@bla@.", 357);
					c.getPA().sendFrame126("Click here to continue", 358);
					c.getPA().sendChatboxInterface(356);
				}
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				c.nextChat = -1;
				c.sendMessage("You need a defence @bla@level of at least 70 to use this prayer.");
				return;
			}
		}
		if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			c.sendMessage("You cannot activate prayers whilst trading!");
			return;
		}
		if (Boundary.isIn(c, Boundary.RFD)) {
			resetPrayers(c);
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (session.getRules().contains(DuelSessionRules.Rule.NO_PRAYER)) {
					c.sendMessage("Prayer has been disabled for this duel.");
					resetPrayers(c);
					return;
				}
			}
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("You have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if (c.isDead || c.getHealth().getCurrentHealth() <= 0) {
			return;
		}
		if (c.clanWarRule[3]) {
			c.sendMessage("You are not allowed to use prayer during this war!");
			resetPrayers(c);
			return;
		}
		int[] defPray = { 0, 5, 13, 25, 26, 27, 28 };
		int[] strPray = { 1, 6, 14, 25, 26 };
		int[] atkPray = { 2, 7, 15, 25, 26 };
		int[] rangePray = { 3, 11, 19, 27};
		int[] magePray = { 4, 12, 20, 28 };

		if (c.playerLevel[5] > 0) {
			if (c.getPA().getLevelForXP(c.playerXP[5]) >= PRAYER_LEVEL_REQUIRED[i]) {
				boolean headIcon = false;
				switch (i) {

				case 0:
				case 5:
				case 13:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < defPray.length; j++) {
							if (defPray[j] != i) {
								c.prayerActive[defPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[defPray[j]], 0);
							}
						}
					}
					break;
				case 1:
				case 6:
				case 14:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;

				case 2:
				case 7:
				case 15:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;

				case 3:// range prays
				case 11:
				case 19:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;
				case 4:
				case 12:
				case 20:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;
				case 10:
					c.lastProtItem = System.currentTimeMillis();
					c.protectItem = !c.protectItem;
					break;

				case 16:
				case 17:
				case 18:
					if (System.currentTimeMillis() - c.stopPrayerDelay < 5000) {
						c.sendMessage("You have been injured and can't use this prayer!");
						c.getPA().sendFrame36(PRAYER_GLOW[16], 0);
						c.getPA().sendFrame36(PRAYER_GLOW[17], 0);
						c.getPA().sendFrame36(PRAYER_GLOW[18], 0);
						return;
					}
					if (i == 16)
						c.protMageDelay = System.currentTimeMillis();
					else if (i == 17)
						c.protRangeDelay = System.currentTimeMillis();
					else if (i == 18)
						c.protMeleeDelay = System.currentTimeMillis();
				case 21:
				case 22:
				case 23:
					headIcon = true;
					for (int p = 16; p < 24; p++) {
						if (i != p && p != 19 && p != 20) {
							c.prayerActive[p] = false;
							c.getPA().sendFrame36(PRAYER_GLOW[p], 0);
						}
					}
					break;
				case 25:
				case 26:
				case 27:
				case 28:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[magePray[j]], 0);
							}
						}
						for (int j = 0; j < defPray.length; j++) {
							if (defPray[j] != i) {
								c.prayerActive[defPray[j]] = false;
								c.getPA().sendFrame36(PRAYER_GLOW[defPray[j]], 0);
							}
						}
					}
					break;
				}
				if (!headIcon) {
					if (c.prayerActive[i] == false) {
						c.prayerActive[i] = true;
						c.getPA().sendFrame36(PRAYER_GLOW[i], 1);
					} else {
						c.prayerActive[i] = false;
						c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
					}
				} else {
					if (c.prayerActive[i] == false) {
						c.prayerActive[i] = true;
						c.getPA().sendFrame36(PRAYER_GLOW[i], 1);
						c.headIcon = PRAYER_HEAD_ICONS[i];
						c.getPA().requestUpdates();
					} else {
						c.prayerActive[i] = false;
						c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
						c.headIcon = -1;
						c.getPA().requestUpdates();
					}
				}

				if (i == PROTECT_ITEM) {
					ItemsKeptOnDeathInterface.refreshIfOpen(c);
				}
			} else {
				c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
				c.getPA().sendFrame126("You need a @bl2@Prayer @bla@level of " + PRAYER_LEVEL_REQUIRED[i] + " to use @bl2@" + PRAYER_NAME[i] + "@bla@.", 357);
				c.getPA().sendFrame126("Click here to continue", 358);
				c.getPA().sendChatboxInterface(356);
				c.nextChat = -1;
			}
		} else {
			c.getPA().sendFrame36(PRAYER_GLOW[i], 0);
			c.sendMessage("You have run out of prayer points!");
		}

	}
}
