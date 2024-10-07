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

import static io.xeros.model.Dialogues.INFORMATION_CLERK_DIALOGUE;
import static io.xeros.model.Items.FIRE_CAPE;
import static io.xeros.model.Items.TZREK_JAD;
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
            case 7520:
                player.getDH().sendDialogues(855, 7520);
                break;

            case 6774:
                player.getShops().openShop(117);
                break;

            case 4407:
                player.getShops().openShop(19);
                break;
            case 3105:
                long milliseconds = (long) player.playTime * 600;
                long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
                long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
                String time = days + " days and " + hours + " hours.";
                player.getDH().sendNpcChat1("You've been playing " + Configuration.SERVER_NAME + " for " + time, 3105, "Hans");
                player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.HANS);
                break;
            case 3680:
                AgilityHandler.delayFade(player, "NONE", 2674, 3274, 0, "The sailor brings you onto the ship.",
                        "and you end up in ardougne.", 3);
                player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.SAIL_TO_ARDOUGNE);
                break;

            case 5034:
                player.getPA().startTeleport(2929, 4813, 0, "modern", false);
                player.getDiaryManager().getLumbridgeDraynorDiary()
                        .progress(LumbridgeDraynorDiaryEntry.TELEPORT_ESSENCE_LUM);
                break;

            case 5906:
                Probita.cancellationOfPreviousPet(player);
                break;

            case 2180:
                player.getDH().sendDialogues(70, 2180);
                break;
            case 6747:
                player.getShops().openShop(77);
                break;
            case 3936:
                player.getDH().sendDialogues(459, -1);
                break;
            case 6970:
                player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.PICKPOCKET_GNOME);
                player.getThieving().steal(Thieving.Pickpocket.MAN, npc);
                break;
            case 3295: //for diary

                if (Boundary.isIn(player, Boundary.ARDOUGNE)) {
                    player.getThieving().steal(Thieving.Pickpocket.HERO, npc);
                    player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_ARD);
                    player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_HERO);
                }
                break;
            case 6987:
                player.getThieving().steal(Thieving.Pickpocket.MAN, npc);
                break;
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
