package io.xeros.net.packets.npcoptions;

import io.xeros.Server;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.slayer.SlayerRewardsInterface;
import io.xeros.content.skills.slayer.SlayerRewardsInterfaceData;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/*
 * @author Matt
 * Handles all 4th options on non playable characters.
 */

public class NpcOptionFour {

	public static final Logger logger = LoggerFactory.getLogger(NpcOptionFour.class);

	public static void handleOption(Player player, int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) return;
		player.clickNpcType = 0;
		player.clickedNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;
		if (PetHandler.isPet(npcType))
			if (Objects.equals(PetHandler.getOptionForNpcId(npcType), "fourth"))
				if (PetHandler.pickupPet(player, npcType, true)) return;

		NPC npc = NPCHandler.npcs[player.clickedNpcIndex];
		var npcActionManager = Server.getNpcOptionActionManager();
		if (npcActionManager.findHandlerById(npcType).isPresent()) {
			var npcAction = npcActionManager.findHandlerById(npcType).get();
			if (npcAction.performedAction(player, npc, 4))
				return;
			else
				logger.error("Unhandled NPC Action 4: {} ", npcAction.getClass().getSimpleName());
		}

		switch (npcType) {
		case 17: //Rug merchant - Sophanem
			player.startAnimation(2262);
			AgilityHandler.delayFade(player, "NONE", 3285, 2815, 0, "You step on the carpet and take off...", "at last you end up in sophanem.", 3);
			break;

		case 2580:
			player.getPA().startTeleport(3039, 4788, 0, "modern", false);
			player.teleAction = -1;
			break;

		case 402:
		case 401:
		case 405:
		case 6797:
		case 7663:
		case 8761:
		case 5870:
			SlayerRewardsInterface.open(player, SlayerRewardsInterfaceData.Tab.TASK);
			//player.getSlayer().handleInterface("buy");
			break;
			
		case 1501:
			player.getShops().openShop(23);
			break;

		case 308:
			player.getDH().sendDialogues(545, npcType);
			break;
		}
	}

}
