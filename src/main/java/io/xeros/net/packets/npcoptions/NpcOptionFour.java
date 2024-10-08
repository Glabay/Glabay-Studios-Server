package io.xeros.net.packets.npcoptions;

import io.xeros.Server;
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
			logger.error("Unhandled NPC Action 4: {} ", npcAction.getClass().getSimpleName());
		}
	}

}
