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
        switch (id) {
            //skilling pets start
            case 13320:
            case 13321:
            case 21187:
            case 21188:
            case 21189:
            case 21192:
            case 21193:
            case 21194:
            case 21196:
            case 21197:
            case 13322:
            case 13323:
            case 13324:
            case 13325:
            case 13326:
            case 20659:
            case 20661:
            case 20663:
            case 20665:
            case 20667:
            case 20669:
            case 20671:
            case 20673:
            case 20675:
            case 20677:
            case 20679:
            case 20681:
            case 20683:
            case 20685:
            case 20687:
            case 20689:
            case 20691:
            case 20693:
                return 5_000_000;
            //skilling end

            //boss pet start
            case 12650:
            case 12649:
            case 12651:
            case 12652:
            case 12644:
            case 12645:
            case 12643:
            case 11995:
            case 12653:
            case 12655:
            case 13178:
            case 12646:
            case 13179:
            case 13180:
            case 13177:
            case 12648:
            case 13225:
            case 13247:
            case 21273:
            case 12921:
            case 12939:
            case 12940:
            case 21992:
            case 13181:
            case 12816:
            case 12654:
            case 22318:
            case 12647:
            case 13262:
            case 19730:
            case 22376:
            case 22378:
            case 22380:
            case 22382:
            case 22384:
            case 20851:
            case 22473:
            case 21291:
            case 22319:
            case 22746:
            case 22748:
            case 22750:
            case 22752:
            case 23760:
            case 23757:
            case 23759:
            case 24491:
                //boss pet end
                return 10_000_000;
        }
        return -1;
    }
}
