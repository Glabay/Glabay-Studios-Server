package dev.glabay.rsps.plugin.world.item.impl.rings;

import dev.glabay.rsps.plugin.world.item.WorldItemAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Items.RING_OF_THIRD_AGE;
import static io.xeros.model.definition.Npcs.ACORN;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */

public class RingOf3rdAge extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { RING_OF_THIRD_AGE };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        if (!player.getPA().morphPermissions()) {
        for (int i = 0; i <= 12; i++) player.setSidebarInterface(i, 6014);
        player.npcId2 = ACORN;
        player.isNpc = true;
        player.playerStandIndex = -1;
        player.setUpdateRequired(true);
        player.morphed = true;
        player.setAppearanceUpdateRequired(true);
            }
        return true;
    }
}
