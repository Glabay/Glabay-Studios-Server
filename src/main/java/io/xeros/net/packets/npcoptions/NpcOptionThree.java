package io.xeros.net.packets.npcoptions;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.*;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.herblore.PotionDecanting;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/*
 * @author Matt
 * Handles all 3rd options on non playable characters.
 */

public class NpcOptionThree {

	public static final Logger logger = LoggerFactory.getLogger(NpcOptionThree.class);
	public static void handleOption(Player player, int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) return;
		player.clickNpcType = 0;
		player.clickedNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;

		if (PetHandler.isPet(npcType))
			if (Objects.equals(PetHandler.getOptionForNpcId(npcType), "three"))
				if (PetHandler.pickupPet(player, npcType, true)) return;

		NPC npc = NPCHandler.npcs[player.clickedNpcIndex];
		var npcActionManager = Server.getNpcOptionActionManager();
		if (npcActionManager.findHandlerById(npcType).isPresent()) {
			var npcAction = npcActionManager.findHandlerById(npcType).get();
			if (npcAction.performedAction(player, npc, 3))
				return;
			else
				logger.error("Unhandled NPC Action 3: {} ", npcAction.getClass().getSimpleName());
		}
		switch (npcType) {
//		case 6637:
//            if (player.getItems().freeSlots() < 1) {
//                player.sendMessage("Your inventory is full.");
//                return;
//            }
//            NPCHandler.npcs[player.clickedNpcIndex].absX = 0;
//            NPCHandler.npcs[player.clickedNpcIndex].absY = 0;
//            NPCHandler.npcs[player.clickedNpcIndex] = null;
//            player.petSummonId = -1;
//            player.hasFollower = false;
//           player.getItems().addItem(12654, 1);
//           break;
//       case 6638:
//            if (player.getItems().freeSlots() < 1) {
//                player.sendMessage("Your inventory is full.");
//                return;
//            }
//            NPCHandler.npcs[player.clickedNpcIndex].absX = 0;
//            NPCHandler.npcs[player.clickedNpcIndex].absY = 0;
//            NPCHandler.npcs[player.clickedNpcIndex] = null;
//            player.petSummonId = -1;
//            player.hasFollower = false;
//           player.getItems().addItem(12647, 1);
//           break;
		case 8781:
			Server.getDropManager().search(player, "Donator Boss");
			break;
		case 1428:
			player.getPrestige().openShop();
			break;
		case 1909:
			player.getDH().sendDialogues(903, 1909);
			break;
		case 2897:
			player.getPA().c.itemAssistant.openUpBank();
			break;
		case 2989:
			player.getPrestige().openShop();
			break;
		case 4321:
			player.getShops().openShop(119);
			player.sendMessage("You currently have @red@"+player.bloodPoints+" @bla@Blood Money Points!");
			break;

		case 6773:
			player.isSkulled = true;
			player.skullTimer = Configuration.EXTENDED_SKULL_TIMER;
			player.headIconPk = 0;
			player.getPA().requestUpdates();
			player.sendMessage("@cr10@@blu@You are now skulled.");
			break;
		case 2200:
			player.getPA().c.itemAssistant.openUpBank();
			break;
		case 1306:
			if (player.getItems().isWearingItems()) {
				player.sendMessage("You must remove your equipment before changing your appearance.");
				player.canChangeAppearance = false;
			} else {
				player.getPA().showInterface(3559);
				player.canChangeAppearance = true;
			}
			break;
		case 17: //Rug merchant - Nardah
			player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.TRAVEL_NARDAH);
			player.startAnimation(2262);
			AgilityHandler.delayFade(player, "NONE", 3402, 2916, 0, "You step on the carpet and take off...", "at last you end up in nardah.", 3);
			break;
		
		case 3936:
			AgilityHandler.delayFade(player, "NONE", 2310, 3782, 0, "You board the boat...", "And end up in Neitiznot", 3);
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.TRAVEL_NEITIZNOT);
			break;
			
		case 402:
		case 401:
		case 405:
		case 6797:
		case 7663:
		case 8761:
		case 5870:
			player.getShops().openShop(10);
			player.sendMessage("You currently have <col=a30027>" + Misc.insertCommas(player.getSlayer().getPoints()) + " </col>slayer points.");
			break;
		case 308:
			player.getDH().sendDialogues(548, 308);
			break;
		case 403:
			player.getDH().sendDialogues(12001, -1);
			break;
		case 836:
			player.getShops().openShop(103);
			break;
		case Npcs.BOB_BARTER_HERBS:
			PotionDecanting.decantInventory(player);
			player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.POTION_DECANT);
			break;
		case 2580:
			if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
				player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TELEPORT_ESSENCE_VAR);
			}
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.TELEPORT_ESSENCE_ARD);
			}
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
				player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.TELEPORT_ESSENCE_FAL);
			}
			player.getPA().startTeleport(2929, 4813, 0, "modern", false);
			break;
		}
	}

}
