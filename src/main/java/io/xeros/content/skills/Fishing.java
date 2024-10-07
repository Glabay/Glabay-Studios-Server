package io.xeros.content.skills;

import io.xeros.Server;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * Class Fishing Handles: Fishing
 * 
 * @author: PapaDoc START: 22:07 23/12/2010 FINISH: 22:28 23/12/2010
 */

public class Fishing extends SkillHandler {
	
	public static int[] anglerOutfit = { ANGLER_HAT, ANGLER_TOP, ANGLER_WADERS, ANGLER_BOOTS };

	/**
	 * Fishing data
	 * Id, Level, Equipment, Bait, Raw material, XP, Animation,  
	 */
	public static int[][] data = { 
			{ 1, 1, 303, -1, 317, 10, 621, 321, 15, 30, 12000 }, // SHRIMP
			{ 2, 5, 307, 313, 327, 20, 622, 345, 10, 30, 12000 }, // SARDINE + HERRING
			{ 3, 16, 305, -1, 353, 20, 620, -1, -1, -1, 12000 }, // MACKEREL
			{ 4, 20, 309, 314, 335, 50, 622, 331, 30, 70, 12000 }, // TROUT
			{ 5, 23, 305, -1, 341, 45, 619, 363, 46, 100, 12000 }, // BASS + COD
			{ 6, 25, 309, 314, 349, 60, 622, -1, -1, -1, 12000 }, // PIKE
			{ 7, 35, 311, -1, 359, 80, 618, 371, 50, 100, 12000 }, // TUNA + SWORDIE
			{ 7, 35, 21028, -1, 359, 80, 7401, 371, 50, 100, 12000 }, // TUNA + SWORDIE
			{ 7, 35, 21031, -1, 359, 80, 7402, 371, 50, 100, 12000 }, // TUNA + SWORDIE
			{ 8, 40, 301, -1, 377, 90, 619, -1, -1, -1, 10000 }, // LOBSTER
			{ 9, 62, 303, -1, 7944, 100, 620, -1, -1, -1, 8000 }, // MONKFISH
			{ 10, 76, 311, -1, 383, 110, 618, -1, -1, -1, 6000 }, // SHARK
			{ 10, 76, 21028, -1, 383, 110, 7401, -1, -1, -1, 6000 }, // SHARK
			{ 10, 76, 21031, -1, 383, 110, 7402, -1, -1, -1, 6000 }, // SHARK
			{ 11, 79, 305, -1, 395, 100, 620, 389, 81, 130, 6000 }, // SEA TURTLE
			{ 12, 81, 305, -1, 389, 130, 620, -1, -1, -1, 6000 }, // MANTA RAY
			{ 13, 85, 301, 11940, 11934, 132, 619, -1, -1, -1, 6000 }, // DARK CRAB
			{ 14, 1, -1, -1, 3150, 1, 620, -1, -1, -1, 15000 }, // Karambwanji AFK FISH
			{ 15, 65, 3159, 3150, 3142, 105, 620, -1, -1, -1, 8000 }, // Karambwan
			{ 16, 82, 307, 13431, 13439, 118, 622, -1, -1, -1, 6000 } // Anglerfish
	};
	
	private static void clueBottles(Player player, int petChance) {
		int chance =  petChance/40;
		int easyChance = 50;
		int medChance = 100;
		int rewardAmount = 1;
		if (player.fasterCluesScroll) {
			chance = petChance/80;
			easyChance = 25;
			medChance = 50;
		}
		if (Hespori.activeGolparSeed) {
			rewardAmount = 2;
		}
		int bottleRoll = Misc.random(chance);
		if (Misc.random(chance) == 1) {
			player.sendMessage("You catch a clue bottle!");
			if (bottleRoll < easyChance) {
				player.getItems().addItemUnderAnyCircumstance(13648, rewardAmount);
			} else if (bottleRoll >= easyChance && bottleRoll < medChance) {
				player.getItems().addItemUnderAnyCircumstance(13649, rewardAmount);
			} else {
				player.getItems().addItemUnderAnyCircumstance(13650, rewardAmount);
			}
		}
	}
	private static void foeArtefact(Player player, int petChance) {
		int chance = petChance/24;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll <65) {//1/300
				player.getItems().addItemUnderAnyCircumstance(11180, 1);//ancient coin foe for 200
				player.sendMessage("You found a coin that can be used in the Fire of Exchange!");
			} else if (artefactRoll >= 65 && artefactRoll < 99) {//1/600
				player.getItems().addItemUnderAnyCircumstance(681, 1);//anicent talisman foe for 300
				player.sendMessage("You found a talisman that can be used in the Fire of Exchange!");
			} else if (artefactRoll > 99){//1/1000
				player.getItems().addItemUnderAnyCircumstance(9034, 1);//golden statuette foe for 500
				PlayerHandler.executeGlobalMessage("@bla@[@red@Fishing@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette while fishing!");
			}
		}
	}
	public static void attemptdata(final Player player, int npcId) {
		double multiplier = 1;
		for (int i = 0; i < anglerOuftit.length; i++) {
			if (player.getItems().isWearingItem(anglerOuftit[i])) {
				multiplier += 0.625;
			}
		}
		if (!noInventorySpace(player, "fishing")) {
			player.sendMessage("You must have space in your inventory to start fishing.");
			return;
		}
		// resetFishing(c);
		for (int i = 0; i < data.length; i++) {
			if (npcId == data[i][0]) {
				if (player.playerLevel[Player.playerFishing] < data[i][1]) {
					player.sendMessage("You haven't got high enough fishing level to fish here!");
					player.sendMessage("You at least need the fishing level of " + data[i][1] + ".");
					player.getPA().sendStatement("You need the fishing level of " + data[i][1] + " to fish here.");
					return;
				}
				if (data[i][3] > 0) {
					if (!player.getItems().playerHasItem(data[i][3])) {
						player.sendMessage("You haven't got any " + ItemAssistant.getItemName(data[i][3]) + "!");
						player.sendMessage("You need " + ItemAssistant.getItemName(data[i][3]) + " to fish here.");
						return;
					}
				}
				if (player.playerSkilling[10]) {
					return;
				}
				//double percentOfXp = player.getMode().getType().equals(ModeType.OSRS) ? data[i][5] * multiplier : (data[i][5] * Config.FISHING_EXPERIENCE / 100) * multiplier;
				
				player.playerSkillProp[10][0] = data[i][6]; // ANIM
				player.playerSkillProp[10][1] = data[i][4]; // FISH
				double experience = data[i][5] * multiplier; // XP
				player.playerSkillProp[10][3] = data[i][3]; // BAIT
				player.playerSkillProp[10][4] = data[i][2]; // EQUIP
				player.playerSkillProp[10][5] = data[i][7]; // sFish
				player.playerSkillProp[10][6] = data[i][8]; // sLvl
				player.playerSkillProp[10][7] = data[i][4]; // FISH
				player.playerSkillProp[10][9] = Misc.random(1) == 0 ? 7 : 5;
				player.playerSkillProp[10][10] = data[i][10]; // petChance

				if (!hasFishingEquipment(player, player.playerSkillProp[10][4])) {
					return;
				}
				player.sendMessage("You start fishing.");
				player.startAnimation(player.playerSkillProp[10][0]);
				player.stopPlayerSkill = true;

				player.playerSkilling[10] = true;
				Server.getEventHandler().stop(player, "skilling");

				//getTimer(player, npcId) + 5 + playerFishingLevel(player)

				Server.getEventHandler().submit(new Event<Player>("skilling", player, getTimer(player, npcId) + 5 + playerFishingLevel(player)) {
					@Override
					public void execute() {
						if (player.getItems().freeSlots() == 0) {
							player. sendMessage("Your inventory is full.");
							player.fishing = false;
		                        return;
		                    }
						if (player.playerSkillProp[10][5] > 0) {
							if (player.playerLevel[Player.playerFishing] >= player.playerSkillProp[10][6]) {
								player.playerSkillProp[10][1] = player.playerSkillProp[10][Misc.random(1) == 0 ? 7 : 5];
							}
						}
						if (Misc.random(250) == 0 && player.getInterfaceEvent().isExecutable()) {
							player.getInterfaceEvent().execute();
							stop();
							resetFishing(player);
							return;
						}
						if ((SkillcapePerks.FISHING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player)) && player.getItems().freeSlots() < 2) {
							stop();
							player. sendMessage("Your inventory is full.");
							player.fishing = false;
							return;
						}

						if (player.playerSkillProp[10][1] > 0) {
							//player.sendMessage("You catch a " + ItemAssistant.getItemName(player.playerSkillProp[10][1]) + ".");
							Achievements.increase(player, AchievementType.FISH, 1);
							player.getItems().addItem(player.playerSkillProp[10][1], SkillcapePerks.FISHING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player) ? 2 : 1);
							player.startAnimation(player.playerSkillProp[10][0]);
							clueBottles(player, player.playerSkillProp[10][10]);
							foeArtefact(player, player.playerSkillProp[10][10]);

							if (Misc.random(player.playerSkillProp[10][10] / 6 ) == 1) {
								player.getItems().addItemUnderAnyCircumstance(anglerOuftit[Misc.random(anglerOuftit.length - 1)], 1);
								player.sendMessage("You notice a angler piece floating in the water and pick it up.");
							}
							int petRate = attachment.skillingPetRateScroll ? (int) (player.playerSkillProp[10][10] * .75) : player.playerSkillProp[10][10];
							if (Misc.random(petRate) == 2 && player.getItems().getItemCount(13320, true) == 0 && player.petSummonId != 13320) {
								 PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + "</col> caught a fish and a <col=CC0000>Heron</col> pet!");
								 player.getItems().addItemUnderAnyCircumstance(13320, 1);
								 player.getCollectionLog().handleDrop(player, 5, 13320, 1);
							 }
						}
						switch (player.playerSkillProp[10][1]) {
						case 389:
							if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
								player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.FISH_MANTA);
							}
							break;
						case 371:
							if (Boundary.isIn(player, Boundary.CATHERBY_BOUNDARY)) {
								player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.FISH_SWORD);
							}
							break;
							
						case 377:
							if (Boundary.isIn(player, Boundary.KARAMJA_BOUNDARY)) {
								player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.FISH_LOBSTER_KAR);
							}
							break;
							
						case 3142:
							if (Boundary.isIn(player, Boundary.RESOURCE_AREA_BOUNDARY)) {
								player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KARAMBWAN);
							}
							break;
						}
						switch (player.playerSkillProp[10][7]) {
						case 389:
							if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
								player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.FISH_MANTA);
							}
							break;
						}
						
						switch (player.playerSkillProp[10][4]) {
						case 389:
							if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
								player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.FISH_MANTA);
							}
							break;
							
						case 377:
							if (Boundary.isIn(player, Boundary.KARAMJA_BOUNDARY)) {
								player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.FISH_LOBSTER_KAR);
							}
							break;
							
						case 3142:
							if (Boundary.isIn(player, Boundary.RESOURCE_AREA_BOUNDARY)) {
								player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KARAMBWAN);
							}
							break;
						}
						
						if (experience > 0) {
							player.getPA().addSkillXPMultiplied((int)(experience), Player.playerFishing, true);
						}
						if (player.playerSkillProp[10][3] > 0) {
							player.getItems().deleteItem(player.playerSkillProp[10][3], player.getItems().getInventoryItemSlot(player.playerSkillProp[10][3]), 1);

							if (!player.getItems().playerHasItem(player.playerSkillProp[10][3])) {
								player.sendMessage("You haven't got any " + ItemAssistant.getItemName(player.playerSkillProp[10][3]) + " left!");
								player.sendMessage("You need " + ItemAssistant.getItemName(player.playerSkillProp[10][3]) + " to fish here.");
								stop();
								resetFishing(player);
							}
						}
						if (!hasFishingEquipment(player, player.playerSkillProp[10][4])) {
							stop();
							resetFishing(player);
						}
						if (!noInventorySpace(player, "fishing")) {
							stop();
							resetFishing(player);
						}
						if (!player.stopPlayerSkill) {
							stop();
							resetFishing(player);
						}
						if (!player.playerSkilling[10]) {
							stop();
							resetFishing(player);
						}
					}
				});
			}
		}
	}

	private static boolean hasFishingEquipment(Player c, int equipment) {
		if (!c.getItems().playerHasItem(equipment)) {

			if(equipment == 311 || equipment == 21028) {
				if(c.getItems().playerHasItem(21028) || c.playerEquipment[3] == 21028) {
					return true;
				}
				if(c.getItems().playerHasItem(21031) || c.playerEquipment[3] == 21031) {
					return true;
				}
				if(c.getItems().playerHasItem(10129) || c.playerEquipment[3] == 10129) {
					return true;
				}
			}

			c.sendMessage("You need a " + ItemAssistant.getItemName(equipment) + " to fish here.");
			return false;
		}
		return true;
	}

	private static void resetFishing(Player c) {
		c.startAnimation(65535);
		c.getPA().removeAllWindows();
		c.playerSkilling[10] = false;
		for (int i = 0; i < 11; i++) {
			c.playerSkillProp[10][i] = -1;
		}
	}

	private static int playerFishingLevel(Player c) {
		return (10 - (int) Math.floor(c.playerLevel[Player.playerFishing] / 10));
	}

	private final static int getTimer(Player c, int npcId) {
		switch (npcId) {
		case 1:
			return 2;
		case 2:
			return 3;
		case 3:
			return 4;
		case 4:
			return 4;
		case 5:
			return 4;
		case 6:
			return 5;
		case 7:
			return 5;
		case 8:
			return 5;
		case 9:
			return 5;
		case 10:
			return 5;
		case 11:
			return 9;
		case 12:
			return 9;
		default:
			return -1;
		}
	}

}