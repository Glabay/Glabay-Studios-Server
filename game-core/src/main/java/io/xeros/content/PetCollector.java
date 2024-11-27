package io.xeros.content;

import io.xeros.Configuration;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

public class PetCollector {


    public static final int NPC_ID = 5_906;

    public static void exchangePetForGp(Player c, int item) {
        if (Configuration.DISABLE_FOE) {
            c.sendMessage("The Exchange has been temporarily disabled.");
            return;
        }
        if (getExchangeGpRate(item) == -1) {
            c.sendMessage("@red@You may only exchange skilling or boss pets for GP.");
            return;
        }
        if (!c.getItems().playerHasItem(item)) {
            c.sendMessage("You no longer have this item on you.");
            return;
        }
        c.getItems().deleteItem2(item, 1);
        c.getItems().addItem(995, getExchangeGpRate(item));
        Discord.writeServerSyncMessage("[PET EXCHANGE] "+ c.getDisplayName() +" exchanged " + ItemAssistant.getItemName(item));
        c.sendMessage("You exchange your @blu@" + ItemAssistant.getItemName(item) + "@bla@ for @blu@" + Misc.formatCoins(getExchangeGpRate(item)) + " GP!");
    }
    public static void petCollectorDialogue(Player player) {
        player.start(new DialogueBuilder(player).statement("I'll take your boss pet for 10m or your skilling pet for 5m.\\n Please use it on me when you are ready."));
    }
    /**
     * Gp exchange rate for pet
     */
    public static int getExchangeGpRate(int id) {
        return switch (id) {
            //skilling pets start
            case 13320, 13321, 21187, 21188, 21189, 21192, 21193, 21194, 21196, 21197, 13322, 13323, 13324, 13325, 13326, 20659, 20661, 20663, 20665,
                 20667, 20669, 20671, 20673, 20675, 20677, 20679, 20681, 20683, 20685, 20687, 20689, 20691, 20693 -> 5_000_000;
            //skilling end

            //boss pet start
            case 12650, 12649, 12651, 12652, 12644, 12645, 12643, 11995, 12653, 12655, 13178, 12646, 13179, 13180, 13177, 12648, 13225, 13247, 21273,
                 12921, 12939, 12940, 21992, 13181, 12816, 12654, 22318, 12647, 13262, 19730, 22376, 22378, 22380, 22382, 22384, 20851, 22473, 21291,
                 22319, 22746, 22748, 22750, 22752, 23760, 23757, 23759, 24491 ->
                //boss pet end
                10_000_000;
            default -> -1;
        };
    }
}
