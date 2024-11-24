package io.xeros.content.skills.herblore;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definition.Items;
import io.xeros.model.SlottedItem;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

/**
 * @author Chris | 7/5/21
 */
public class HerbCleaner {

    private static final int CLEAN_HERB_PRICE = 100;
    private static final int PRICE_WARN_AMOUNT = 20_000;

    public static int getPriceForInventory(Player player) {
        return player.getItems().getInventoryItems().stream().mapToInt(it -> {
            Herb herb = Herb.forNotedOrUnNotedGrimyHerb(it.id());
            return herb != null ? CLEAN_HERB_PRICE * it.amount() : 0;
        }).sum();
    }

    public static void cleanHerbsFromInventory(Player player) {
        player.getPA().closeAllWindows();
        for (SlottedItem item : player.getItems().getInventoryItems()) {
            Herb herb = Herb.forNotedOrUnNotedGrimyHerb(item.id());
            if (herb == null)
                continue;
            if (!cleanHerb(player, item, herb, false)) {
                break;
            }
        }
    }

    /**
     * @return if {@param warn} set to false, return true if potion was made successfully.
     *         If {@param warn} is true and dialogue sent, return true.
     */
    public static boolean cleanHerb(Player player, SlottedItem gameItem, Herb herb, boolean warn) {
        ItemDef usedItemDef = ItemDef.forId(gameItem.id());

        int price = gameItem.amount() * CLEAN_HERB_PRICE;
        if (warn && price >= PRICE_WARN_AMOUNT) {
            String priceString = Misc.formatCoins(price);
            new DialogueBuilder(player).option(
                    "Spend " + priceString + "?",
                    new DialogueOption("Yes, spend " + priceString + " to clean these herbs.", plr -> clean(player, gameItem, usedItemDef, herb, price)),
                    new DialogueOption("No, that's too much!", plr -> plr.getPA().closeAllWindows())
            ).send();
            return true;
        }

        return clean(player, gameItem, usedItemDef, herb, price);
    }

    /**
     * @return true if the potion was made successfully.
     */
    private static boolean clean(Player player, SlottedItem gameItem, ItemDef usedItemDef, Herb herb, int price) {
        if (!player.getItems().playerHasItem(gameItem.id(), gameItem.amount()))
            return false;

        if (player.getLevelForXP(player.playerXP[Player.playerHerblore]) < herb.getLevel()) {
            player.sendMessage("You need a Herblore level of " + herb.getLevel() + " to clean this herb.");
            player.getPA().closeAllWindows();
            return false;
        }

        if (!player.getItems().playerHasItem(Items.COINS, price)) {
            player.sendMessage("You don't have enough coins to do that!");
            return false;
        }

        int unfPotionId = herb.getClean();
        ItemDef unfPotionDef = ItemDef.forId(unfPotionId);

        GameItem unfPotionsItem = new GameItem(usedItemDef.isNoted() ? unfPotionDef.getNotedItemIfAvailable() : unfPotionDef.getId(), gameItem.amount());

        player.getItems().deleteItem(Items.COINS, price);
        player.getItems().deleteItem(gameItem.id(), gameItem.amount());
        player.getItems().addItemUnderAnyCircumstance(unfPotionsItem.id(), unfPotionsItem.amount());
        return true;
    }
}
