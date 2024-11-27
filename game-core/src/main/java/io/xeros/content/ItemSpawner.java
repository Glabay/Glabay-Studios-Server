package io.xeros.content;

import io.xeros.Server;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ImmutableItem;

public class ItemSpawner {

    public static final int INTERFACE_ID = 43214;
    public static final int CONTAINER_ID = 43218;
    private static final int TEXT_INTERFACE_ID = 43216;

    public static void open(Player player) {
        if (Server.isTest() || player.getRights().isOrInherits(Right.OWNER)) {
            player.getPA().sendFrame126("", TEXT_INTERFACE_ID, true);
            player.getPA().showInterface(INTERFACE_ID);
        }
    }

    public static void spawn(Player player, int itemId, int amount) {
        if (amount == -1) {
            player.getPA().sendEnterAmount("Enter the amount of items to spawn", (p, enteredAmount) -> spawn(p, itemId, enteredAmount));
            return;
        }

        if (Server.isTest() || player.getRights().isOrInherits(Right.OWNER)) {
            player.getItems().addItem(itemId, amount);
            player.sendMessage("@dre@Spawned x" + amount + " " + ItemDef.forId(itemId).getName() + ".");
        }
    }

}
