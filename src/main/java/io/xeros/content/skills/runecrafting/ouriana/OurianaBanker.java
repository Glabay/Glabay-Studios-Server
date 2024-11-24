package io.xeros.content.skills.runecrafting.ouriana;

import io.xeros.content.PlayerEmotes;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class OurianaBanker {

    private static final int BANKER_ID = 3094;
    private static final int PAYMENT_RUNE_AMOUNT = 20;
    private static final int[] PAYMENT_RUNES = {Items.AIR_RUNE, Items.EARTH_RUNE, Items.WATER_RUNE, Items.FIRE_RUNE};

    public static void welcomePlayer(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(BANKER_ID).npc("Welcome to the Ouriana altar, friend.", "Unfortunately a magical force blocks teleportation so", "you'll have to use the ladder to leave."));
        NPC npc = NPCHandler.getNpc(BANKER_ID);
        if (npc != null) {
            npc.facePlayer(player.getIndex());
            npc.startAnimation(PlayerEmotes.PLAYER_ANIMATION_DATA.WAVE.getAnimation());
        }
    }

    public static boolean clickNpc(Player player, NPC npc, int option) {
        if (npc.getNpcId() == BANKER_ID) {
            if (option == 1 || option == 2) {
                for (int rune : PAYMENT_RUNES) {
                    if (player.getItems().getItemAmount(rune) >= PAYMENT_RUNE_AMOUNT) {
                        player.start(new DialogueBuilder(player).itemStatement(rune, 200, "Ouriana Banker",
                                "You pay 20 " + ItemDef.forId(rune).getName() + " to access the bank.").exit(plr -> openBank(plr, rune)));
                        return true;
                    }
                }

                player.start(new DialogueBuilder(player).setNpcId(BANKER_ID).npc("I'm sorry sir, I'm afraid I'll need a payment of",
                        "20 Air, Earth, Water or Fire runes to use my services."));
            }
            return true;
        }
        return false;
    }
    public static boolean clickObject(Player player, int objectId, int option) {
        if (objectId == 10355 && Boundary.isIn(player, Boundary.OURIANA_ALTAR)) {
            if (option == 1 || option == 2) {
                for (int rune : PAYMENT_RUNES) {
                    if (player.getItems().getItemAmount(rune) >= PAYMENT_RUNE_AMOUNT) {
                        player.start(new DialogueBuilder(player).itemStatement(rune, 200, "Ouriana Banker",
                                "You pay 20 " + ItemDef.forId(rune).getName() + " to access the bank.").exit(plr -> openBank(plr, rune)));
                        player.inBank = true;
                        return true;
                    }
                }

                player.start(new DialogueBuilder(player).setNpcId(BANKER_ID).npc("I'm sorry sir, I'm afraid I'll need a payment of",
                        "20 Air, Earth, Water or Fire runes to use my services."));
            }
            return true;
        }
        return false;
    }
    private static void openBank(Player plr, int runeId) {
        if (plr.getItems().getItemAmount(runeId) >= PAYMENT_RUNE_AMOUNT) {
            plr.getItems().deleteItem(runeId, PAYMENT_RUNE_AMOUNT);
            plr.getItems().openUpBank();
        }
    }

}
