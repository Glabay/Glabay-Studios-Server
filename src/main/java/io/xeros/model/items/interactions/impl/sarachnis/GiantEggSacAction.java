package io.xeros.model.items.interactions.impl.sarachnis;

import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.WorldItemAction;

import static io.xeros.model.Items.GIANT_EGG_SACFULL;
import static io.xeros.model.Items.RED_SPIDERS_EGGS_NOTED;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-04
 */
public class GiantEggSacAction extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { GIANT_EGG_SACFULL };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        if (player.getItems().playerHasItem(Items.KNIFE)) {
            player.getItems().deleteItem(GIANT_EGG_SACFULL, 1);
            player.getItems().addItem(RED_SPIDERS_EGGS_NOTED, 100);
        }
        else
            player.sendMessage("You need a knife to open this.");
        return true;
    }
}
