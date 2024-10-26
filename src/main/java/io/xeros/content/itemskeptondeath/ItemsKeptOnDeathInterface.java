package io.xeros.content.itemskeptondeath;

import io.xeros.Server;
import io.xeros.content.itemskeptondeath.modifiers.AlwaysKeptDeathItem;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.collections.VariableStringCollection;

import java.util.List;

public class ItemsKeptOnDeathInterface {

    private static final int ITEMS_KEPT = 17_108;
    private static final int KEPT_CONTAINER = 10_494;
    private static final int LOST_CONTAINER = 10_600;
    private static final int INTERFACE_ID = 17_100;
    private static final int INFORMATION_CONTAINER = 17_109;

    public static void open(Player player) {
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            return;
        }

        ItemsLostOnDeathList items = ItemsLostOnDeath.generate(player);
        List<GameItem> kept = items.getKept();
        List<GameItem> lost = items.getLost();
        kept.removeIf(it -> AlwaysKeptDeathItem.items().contains(it.id()));
        lost.removeIf(it -> AlwaysKeptDeathItem.items().contains(it.id()));

        player.getItems().sendItemContainer(KEPT_CONTAINER, kept);
        player.getItems().sendItemContainer(LOST_CONTAINER, lost);

        sendInformation(player);
        player.getPA().sendString(ITEMS_KEPT, "~ " + ItemsLostOnDeath.getKeptItemAmount(player) + " ~");
        player.getPA().showInterface(INTERFACE_ID);
    }

    public static void refreshIfOpen(Player player) {
        if (player.isInterfaceOpen(INTERFACE_ID)) {
            open(player);
        }
    }

    private static void sendInformation(Player player) {
        VariableStringCollection s = new VariableStringCollection().setLineAfterEachEntry("");
        s.add("Use <col=ffffff>::wild</col> for more info.");
        s.add("Items that are always", "kept are not shown here.");
        s.add("Items contained in a", "loot bag, rune pouch, etc", "are dropped on death.");
        s.add("Pets are always kept.");
        s.add("Untradeables like Trident", "Abyssal tentacle, etc,", "if not protected, are", "uncharged and dropped.");
        s.add("All other untradeables,", "when lost, can be bought", "back from Perdu.");

        player.getPA().sendStringContainer(INFORMATION_CONTAINER, s.getList());
    }
}
