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
		case 8781:
			Server.getDropManager().search(player, "Donator Boss");
			break;
		case 1428:
			player.getPrestige().openShop();
			break;
		case 1909:
			player.getDH().sendDialogues(903, 1909);
			break;
		case 2989:
			player.getPrestige().openShop();
			break;
		case 4321:
			player.getShops().openShop(119);
			player.sendMessage("You currently have @red@"+player.bloodPoints+" @bla@Blood Money Points!");
			break;
		case 2200:
			player.getPA().c.itemAssistant.openUpBank();
			break;
		case 3936:
			AgilityHandler.delayFade(player, "NONE", 2310, 3782, 0, "You board the boat...", "And end up in Neitiznot", 3);
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.TRAVEL_NEITIZNOT);
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
		}
	}

}
