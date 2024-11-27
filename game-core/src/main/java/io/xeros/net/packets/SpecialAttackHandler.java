package io.xeros.net.packets;

import io.xeros.Server;
import io.xeros.content.combat.melee.MeleeExtras;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.combat.specials.Specials;
import io.xeros.model.entity.player.Player;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;
import io.xeros.util.Misc;

import java.util.Arrays;

/**
 * @author Arthur Behesnilian 1:02 PM
 */
public class SpecialAttackHandler implements PacketType {

    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
        int specialButtonId = Misc.hexToInt(player.getInStream().buffer, 0, packetSize);
        int componentId = player.getInStream().readUnsignedWord();

        if (player.debugMessage) {
            player.sendMessage("SpecialButtonId: " + specialButtonId);
            player.sendMessage("SpecialComponentId: " + componentId);
        }
        processSpecial(player, specialButtonId);
    }

    private static void processSpecial(Player player, int specialButtonId) {
        switch(specialButtonId) {
            case 29063:
                DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                        MultiplayerSessionType.DUEL);
                if (session != null) {
                    if (session.getRules().contains(DuelSessionRules.Rule.NO_SPECIAL_ATTACK)) {
                        player.sendMessage("You are not permitted to activate special attacks during a duel.");
                        return;
                    }
                }
                Special special = Specials.DRAGON_BATTLEAXE.getSpecial();
                if (player.specAmount < special.getRequiredCost()) {
                    player.sendMessage("You don't have the special amount to use this.");
                    return;
                }
                if (!Arrays.stream(special.getWeapon()).anyMatch(axe -> player.getItems().isWearingItem(axe))) {
                    return;
                }
                special.activate(player, null, null);
                player.specAmount -= special.getRequiredCost();
                player.usingSpecial = false;
                player.getItems().updateSpecialBar();
                break;

            // Special bars
            case 29138:
                /* Scimitar */
            case 29163:
                /* Mace */
            case 29199:
                /* Battleaxe & Hatchets */
            case 29074:
                /* Halberd $ Staff of Light */
            case 33033:
                /* Spear */
            case 29238:
                /* Godswords & 2h Swords */
            case 30007:
                /* Whip */
            case 48034:
                /* Warhammer & Mauls */
            case 29049:
                /* Pickaxe */
            case 30043:
                /* Bows */
            case 29124:
                /* Throwing Axe & Javelins */
            case 29213:
                /* Claws and SOTD */
            case 30108:
            case 29188:
            case 29038:
            case 48023:
            case 29113:
                session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                        MultiplayerSessionType.DUEL);
                if (session != null) {
                    if (session.getRules().contains(DuelSessionRules.Rule.NO_SPECIAL_ATTACK)) {
                        player.sendMessage("You are not permitted to activate special attacks during a duel.");
                        return;
                    }
                }
                if (player.getItems().isWearingItem(1377, Player.playerWeapon)) {
                    Special dbaxe = Specials.DRAGON_BATTLEAXE.getSpecial();
                    if (player.specAmount >= dbaxe.getRequiredCost()) {
                        player.specAmount -= dbaxe.getRequiredCost();
                        dbaxe.activate(player, player, null);
                        player.specBarId = 7812;
                        player.usingSpecial = false;
                        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
                        return;
                    }
                    player.usingSpecial = false;
                    player.sendMessage("You do not have enough special to use this right now.");
                    return;
                }

                if (player.getItems().isWearingItem(6739, Player.playerWeapon)) {
                    Special daxe = Specials.DRAGON_AXE.getSpecial();
                    if (player.specAmount >= daxe.getRequiredCost()) {
                        player.specAmount -= daxe.getRequiredCost();
                        daxe.activate(player, player, null);
                        player.specBarId = 7812;
                        player.usingSpecial = false;
                        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
                        return;
                    }
                    player.usingSpecial = false;
                    player.sendMessage("You do not have enough special to use this right now.");
                    return;
                }
                if (player.getItems().isWearingItem(11920, Player.playerWeapon)) {
                    Special dpickaxe = Specials.DRAGON_PICKAXE.getSpecial();
                    if (player.specAmount >= dpickaxe.getRequiredCost()) {
                        player.specAmount -= dpickaxe.getRequiredCost();
                        dpickaxe.activate(player, player, null);
                        player.specBarId = 7812;
                        player.usingSpecial = false;
                        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
                        return;
                    }
                    player.usingSpecial = false;
                    player.sendMessage("You do not have enough special to use this right now.");
                    return;
                }
                if (player.getItems().isWearingItem(21028, Player.playerWeapon)) {
                    Special dharpoon = Specials.DRAGON_HARPOON.getSpecial();
                    if (player.specAmount >= dharpoon.getRequiredCost()) {
                        player.specAmount -= dharpoon.getRequiredCost();
                        dharpoon.activate(player, player, null);
                        player.specBarId = 7812;
                        player.usingSpecial = false;
                        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
                        return;
                    }
                    player.usingSpecial = false;
                    player.sendMessage("You do not have enough special to use this right now.");
                    return;
                }
                if (player.getItems().isWearingItem(11791, Player.playerWeapon)
                        || player.getItems().isWearingItem(12904, Player.playerWeapon)) {
                    Special sotd = Specials.STAFF_OF_THE_DEAD.getSpecial();
                    if (player.specAmount >= sotd.getRequiredCost()) {
                        player.specAmount -= sotd.getRequiredCost();
                        sotd.activate(player, player, null);
                        player.specBarId = 7812;
                        player.usingSpecial = false;
                        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
                        return;
                    }
                } else if (player.playerEquipment[Player.playerWeapon] == 4153 || player.playerEquipment[Player.playerWeapon] == 12848) {
                    MeleeExtras.graniteMaulSpecial(player, true);
                } else {
                    player.usingSpecial = !player.usingSpecial;
                    player.getItems().updateSpecialBar();
                }
                break;
        }
    }

}
