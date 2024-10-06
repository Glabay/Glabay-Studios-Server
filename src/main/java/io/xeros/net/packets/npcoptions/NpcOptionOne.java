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
            else
                logger.error("Unhandled NPC Action 1: {} ", npcAction.getClass().getSimpleName());
        }

        switch (npcType) {
            case 6599:
                player.getShops().openShop(12);
                break;
            case 2578:
                player.getDH().sendDialogues(2401, npcType);
                break;
            case 3789:
                player.getShops().openShop(75);
                break;
            // FISHING
            case 3913: // NET + BAIT
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 1);
                break;
            case 3317:
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 14);
                break;
            case 4712:
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 15);
                break;
            case 1524:
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 11);
                break;
            case 3417: // TROUT
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 4);
                break;
            case 3657:
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 8);
                break;
            case 635:
                Fishing.attemptdata(player, 13); // DARK CRAB FISHING
                break;
            case 6825: // Anglerfish
                player.clickNpcType = 1;
                Fishing.attemptdata(player, 16);
                break;
            case 1520: // LURE
            case 310:
            case 314:
            case 317:
            case 318:
            case 328:
            case 331:
                Fishing.attemptdata(player, 9);
                break;
            case 944:
                player.getDH().sendOption5("Hill Giants", "Hellhounds", "Lesser Demons", "Chaos Dwarf", "-- Next Page --");
                player.teleAction = 7;
                break;

            case 559:
                player.getShops().openShop(16);
                break;
            case 5809:
                Tanning.sendTanningInterface(player);
                break;
            case 2913:
                player.getShops().openShop(22);
                break;
            case 403:
                player.getDH().sendDialogues(2300, npcType);
                break;
            case 1599:
                break;
            case 1986:
                player.getDH().sendDialogues(2244, player.npcType);
                break;
            case 1576:
                player.getShops().openShop(4);
                break;
            case 1577:
                player.getShops().openShop(8);
                break;
            case 1578:
                player.getShops().openShop(6);
                break;
            case 6747:
                player.getShops().openShop(77);
                break;
            case 1785:
                player.getShops().openShop(8);
                break;

            case 1860:
                player.getShops().openShop(47);
                break;
            case 519:
                player.getShops().openShop(48);
                break;

            case 548:
                player.getDH().sendDialogues(69, player.npcType);
                break;

            case 2258:
                player.getDH().sendOption2("Teleport me to Runecrafting Abyss.", "I want to stay here, thanks.");
                player.dialogueAction = 2258;
                break;
            case 532:
                player.getShops().openShop(47);
                break;
            case 7913:
                if (player.getMode().isIronmanType()
                    || player.getRights().contains(Right.OWNER) || player.getRights().contains(Right.ADMINISTRATOR)) {
                    player.getShops().openShop(41);
                }
                else {
                    player.sendMessage("You must be an Iron Man to access this shop.");
                }
                break;
            case 7769:
                player.getShops().openShop(2);
                break;
        }
    }
}