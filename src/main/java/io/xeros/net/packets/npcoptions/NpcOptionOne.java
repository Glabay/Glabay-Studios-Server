package io.xeros.net.packets.npcoptions;

import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.bosses.nightmare.NightmareActionHandler;
import io.xeros.content.dialogue.impl.IronmanNpcDialogue;
import io.xeros.content.dialogue.impl.MonkChaosAltarDialogue;
import io.xeros.content.skills.Fishing;
import io.xeros.content.skills.crafting.Tanning;
import io.xeros.content.skills.mining.Mineral;
import io.xeros.content.skills.thieving.Thieving;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.group.GroupIronman;
import io.xeros.model.entity.player.mode.group.GroupIronmanDialogue;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class NpcOptionOne {

    public static final Logger logger = LoggerFactory.getLogger(NpcOptionOne.class);

    public static void handleOption(Player player, int npcType) {
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            return;
        }
        player.clickNpcType = 0;
        player.clickedNpcIndex = player.npcClickIndex;
        player.npcClickIndex = 0;

        if (PetHandler.isPet(npcType))
            if (Objects.equals(PetHandler.getOptionForNpcId(npcType), "first"))
                if (PetHandler.pickupPet(player, npcType, true))
                    return;

        if (NightmareActionHandler.clickNpc(player, npcType, 1)) return;
        NPC npc = NPCHandler.npcs[player.clickedNpcIndex];

        var npcActionManager = Server.getNpcOptionActionManager();
        if (npcActionManager.findHandlerById(npcType).isPresent()) {
            var npcAction = npcActionManager.findHandlerById(npcType).get();
            if (npcAction.performedAction(player, npc, 1))
                return;
            logger.error("Unhandled NPC Action 1: {} ", npcAction.getClass().getSimpleName());
        }
    }
}