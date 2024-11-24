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
import io.xeros.model.FishingSpotTypes;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import java.util.Objects;

import static io.xeros.model.definition.Items.*;

/**
 * Class Fishing Handles: Fishing
 * 
 * @author PapaDoc START: 22:07 23/12/2010 FINISH: 22:28 23/12/2010
 * @author Zeighe
 */

public class Fishing extends SkillHandler {
	
	public static int[] anglerOutfit = { ANGLER_HAT, ANGLER_TOP, ANGLER_WADERS, ANGLER_BOOTS };

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
		if (Hespori.activeGolparSeed) rewardAmount = 2;
		int bottleRoll = Misc.random(chance);
		if (Misc.random(chance) == 1) {
			player.sendMessage("You catch a clue bottle!");
			if (bottleRoll < easyChance)
				player.getItems().addItemUnderAnyCircumstance(CLUE_BOTTLE_EASY, rewardAmount);
            else if (bottleRoll < medChance)
                player.getItems().addItemUnderAnyCircumstance(CLUE_BOTTLE_MEDIUM, rewardAmount);
            else
				player.getItems().addItemUnderAnyCircumstance(CLUE_BOTTLE_HARD, rewardAmount);
		}
	}

	private static void foeArtefact(Player player, int petChance) {
		int chance = petChance/24;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll < 65) {//1/300
				player.getItems().addItemUnderAnyCircumstance(ANCIENT_COIN, 1);//ancient coin foe for 200
				player.sendMessage("You found a coin that can be used in the Fire of Exchange!");
			}
			else if (artefactRoll < 99) {//1/600
				player.getItems().addItemUnderAnyCircumstance(SCRAP_PAPER, 1);//anicent talisman foe for 300
				player.sendMessage("You found a talisman that can be used in the Fire of Exchange!");
			}
			else {//1/1000
				player.getItems().addItemUnderAnyCircumstance(CRUNCHY_TRAY_2, 1);//golden statuette foe for 500
				PlayerHandler.executeGlobalMessage("@bla@[@red@Fishing@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette while fishing!");
			}
		}
	}

	public static void attemptdata(final Player player, int fishingSpotId) {
		double multiplier = 1;
        for (int j : anglerOutfit)
            if (player.getItems().isWearingItem(j))
                multiplier += 0.625;

		if (!noInventorySpace(player, "fishing")) {
			player.sendMessage("You must have space in your inventory to start fishing.");
			return;
		}
		// resetFishing(c);
		for (var fishingSpot : FishingSpotTypes.values())
            if (Objects.equals(fishingSpot.getFishingSpotId(), fishingSpotId)) {
                if (player.playerLevel[Player.playerFishing] < fishingSpot.getLevelReq()) {
                    player.sendMessage("You haven't got high enough fishing level to fish here!");
                    player.sendMessage("You at least need the fishing level of %d.".formatted(fishingSpot.getLevelReq()));
                    player.getPA().sendStatement("You need the fishing level of %d to fish here.".formatted(fishingSpot.getLevelReq()));
                    return;
                }
                if (fishingSpot.getBaitId() > 0) if (!player.getItems().playerHasItem(fishingSpot.getBaitId())) {
                    player.sendMessage("You haven't got any " + ItemAssistant.getItemName(fishingSpot.getBaitId()) + "!");
                    player.sendMessage("You need " + ItemAssistant.getItemName(fishingSpot.getBaitId()) + " to fish here.");
                    return;
                }
                if (player.playerSkilling[10]) return;

                player.playerSkillProp[10][0] = fishingSpot.getAnimationID(); // ANIM
                player.playerSkillProp[10][1] = fishingSpot.getRawFishId(); // FISH
                double experience = fishingSpot.getExperience() * multiplier; // XP
                player.playerSkillProp[10][3] = fishingSpot.getBaitId(); // BAIT
                player.playerSkillProp[10][4] = fishingSpot.getToolId(); // EQUIP
                player.playerSkillProp[10][5] = fishingSpot.getSecondaryFishId(); // sFish
                player.playerSkillProp[10][6] = fishingSpot.getSecondaryFishLevelReq(); // sLvl
                player.playerSkillProp[10][7] = fishingSpot.getRawFishId(); // FISH
                player.playerSkillProp[10][9] = Misc.random(1) == 0 ? 7 : 5;
                player.playerSkillProp[10][10] = fishingSpot.getPetChance(); // petChance

                if (!hasFishingEquipment(player, player.playerSkillProp[10][4])) return;

                player.sendMessage("You start fishing.");
                player.startAnimation(player.playerSkillProp[10][0]);
                player.stopPlayerSkill = true;

                player.playerSkilling[10] = true;
                Server.getEventHandler().stop(player, "skilling");

                Server.getEventHandler().submit(new Event<>("skilling", player, getTimer(fishingSpotId) + 5 + playerFishingLevel(player)) {
                    @Override
                    public void execute() {
                        if (player.getItems().freeSlots() == 0) {
                            player.sendMessage("Your inventory is full.");
                            player.fishing = false;
                            return;
                        }
                        if (player.playerSkillProp[10][5] > 0)
                            if (player.playerLevel[Player.playerFishing] >= player.playerSkillProp[10][6])
                                player.playerSkillProp[10][1] = player.playerSkillProp[10][Misc.random(1) == 0 ? 7 : 5];
                        if (Misc.random(250) == 0 && player.getInterfaceEvent().isExecutable()) {
                            player.getInterfaceEvent().execute();
                            stop();
                            resetFishing(player);
                            return;
                        }
                        if ((SkillcapePerks.FISHING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player)) && player.getItems().freeSlots() < 2) {
                            stop();
                            player.sendMessage("Your inventory is full.");
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

                            if (Misc.random(player.playerSkillProp[10][10] / 6) == 1) {
                                player.getItems().addItemUnderAnyCircumstance(anglerOutfit[Misc.random(anglerOutfit.length - 1)], 1);
                                player.sendMessage("You notice a angler piece floating in the water and pick it up.");
                            }
                            int petRate = attachment.skillingPetRateScroll ? (int) (player.playerSkillProp[10][10] * .75) : player.playerSkillProp[10][10];
                            if (Misc.random(petRate) == 2 && player.getItems().getItemCount(HERON, true) == 0 && player.petSummonId != 13320) {
                                PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + "</col> caught a fish and a <col=CC0000>Heron</col> pet!");
                                player.getItems().addItemUnderAnyCircumstance(HERON, 1);
                                player.getCollectionLog().handleDrop(player, 5, HERON, 1);
                            }
                        }
                        switch (player.playerSkillProp[10][1]) {
                            case 389:
                                if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY))
                                    player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.FISH_MANTA);
                                break;
                            case 371:
                                if (Boundary.isIn(player, Boundary.CATHERBY_BOUNDARY))
                                    player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.FISH_SWORD);
                                break;

                            case 377:
                                if (Boundary.isIn(player, Boundary.KARAMJA_BOUNDARY))
                                    player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.FISH_LOBSTER_KAR);
                                break;

                            case 3142:
                                if (Boundary.isIn(player, Boundary.RESOURCE_AREA_BOUNDARY))
                                    player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KARAMBWAN);
                                break;
                        }
                        if (player.playerSkillProp[10][7] == 389) if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY))
                            player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.FISH_MANTA);

                        switch (player.playerSkillProp[10][4]) {
                            case 389 -> {
                                if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY))
                                    player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.FISH_MANTA);
                            }
                            case 377 -> {
                                if (Boundary.isIn(player, Boundary.KARAMJA_BOUNDARY))
                                    player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.FISH_LOBSTER_KAR);
                            }
                            case 3142 -> {
                                if (Boundary.isIn(player, Boundary.RESOURCE_AREA_BOUNDARY))
                                    player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KARAMBWAN);
                            }
                        }

                        if (experience > 0)
                            player.getPA().addSkillXPMultiplied((int) (experience), Player.playerFishing, true);
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

	private static boolean hasFishingEquipment(Player c, int equipment) {
		if (!c.getItems().playerHasItem(equipment)) {

			if (equipment == HARPOON || equipment == DRAGON_HARPOON) {
				if (c.getItems().playerHasItem(DRAGON_HARPOON) || c.playerEquipment[3] == DRAGON_HARPOON) return true;
				if (c.getItems().playerHasItem(INFERNAL_HARPOON) || c.playerEquipment[3] == INFERNAL_HARPOON) return true;
				if (c.getItems().playerHasItem(BARB_TAIL_HARPOON) || c.playerEquipment[3] == BARB_TAIL_HARPOON) return true;
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
		for (int i = 0; i < 11; i++) c.playerSkillProp[10][i] = -1;
	}

	private static int playerFishingLevel(Player c) {
		return (10 - (int) (double) (c.playerLevel[Player.playerFishing] / 10));
	}

	private static int getTimer(int npcId) {
        return switch (npcId) {
            case 1 -> 2;
            case 2 -> 3;
            case 3, 4, 5 -> 4;
            case 6, 10, 9, 8, 7 -> 5;
            case 11, 12 -> 9;
            default -> -1;
        };
	}

}