package io.xeros.model.items.interactions.impl.global;

import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.WorldItemAction;

import static io.xeros.Configuration.GRAND_TREE_X;
import static io.xeros.model.Items.ROYAL_SEED_POD;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */

public class RoyalSeedPod extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ROYAL_SEED_POD };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        if (player.wildLevel > 30) {
            player.sendMessage("You can't teleport above level 30 in the wilderness.");
        }
        player.getPA().startTeleport(GRAND_TREE_X, Configuration.GRAND_TREE_Y, 0, "pod", false);
        return true;
    }
}
