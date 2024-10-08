package io.xeros.net.packets.npcoptions;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.*;
import io.xeros.content.bosses.nightmare.NightmareActionHandler;
import io.xeros.content.skills.Fishing;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.thieving.Thieving;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.npc.pets.Probita;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.xeros.model.Dialogues.*;
import static io.xeros.model.Items.FIRE_CAPE;
import static io.xeros.model.Items.TZREK_JAD;
import static io.xeros.model.Npcs.TZHAAR_MEJ_JAL;
import static io.xeros.model.Shops.OZIACH_SHOP;

/*
 * @author Matt
 * Handles all 2nd options on non playable characters.
 */

public class NpcOptionTwo {

    public static final Logger logger = LoggerFactory.getLogger(NpcOptionTwo.class);

    public static void handleOption(Player player, int npcType) {
        if (Server.getMultiplayerSessionListener().inAnySession(player))
            return;
        player.clickNpcType = 0;
        player.clickedNpcIndex = player.npcClickIndex;
        player.npcClickIndex = 0;

        if (PetHandler.isPet(npcType))
            if (Objects.equals(PetHandler.getOptionForNpcId(npcType), "second"))
                if (PetHandler.pickupPet(player, npcType, true))
                    return;

        if (NightmareActionHandler.clickNpc(player, npcType, 2))
            return;

        NPC npc = NPCHandler.npcs[player.clickedNpcIndex];
        var npcActionManager = Server.getNpcOptionActionManager();
        if (npcActionManager.findHandlerById(npcType).isPresent()) {
            var npcAction = npcActionManager.findHandlerById(npcType).get();
            if (npcAction.performedAction(player, npc, 2))
                return;
            else
                logger.error("Unhandled NPC Action 2: {} ", npcAction.getClass().getSimpleName());
        }
        switch (npcType) {
            case 3550:
                player.getThieving().steal(Thieving.Pickpocket.MENAPHITE_THUG, npc);
                break;
            case 6094:
                player.getThieving().steal(Thieving.Pickpocket.GNOME, npc);
                break;
            case 3106:
                player.getThieving().steal(Thieving.Pickpocket.HERO, npc);
                break;
            case 637:
                player.getShops().openShop(6);
                break;
            case 3219:
                player.getShops().openShop(113);
                break;
            case 534:
                if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
                    player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.DRESS_FOR_SUCESS);
                }
                player.getShops().openShop(114);
                break;
            case 3216:
                player.getShops().openShop(8);
                break;
            case 532:
                player.getShops().openShop(47);
                break;
            case 1599:
                player.getShops().openShop(10);
                player.sendMessage("You currently have <col=a30027>" + Misc.insertCommas(player.getSlayer().getPoints()) + " </col>slayer points.");
                break;
            case 1785:
                player.getShops().openShop(8);
                break;

            case 3218:// magic supplies
                player.getShops().openShop(6);
                break;
            case 3217:// range supplies
                player.getShops().openShop(48);
                break;
            case 3796:
                player.getShops().openShop(6);
                break;

            case 1860:
                player.getShops().openShop(6);
                break;
            case 519:
                player.getShops().openShop(7);
                break;

            case 2258:

                break;
            case 506:
                if (player.getMode().isIronmanType()) {
                    player.getShops().openShop(41);
                } else {
                    player.sendMessage("You must be an Iron Man to access this shop.");
                }
                break;
            case 507:
                player.getShops().openShop(2);
                break;

            case 528:
                player.getShops().openShop(9);
                break;

        }
    }

}
