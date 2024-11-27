package io.xeros.model.items.interactions.impl.global;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.WorldItemAction;

import static io.xeros.model.definition.Items.DIANGOS_CLAWS;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */

public class DiangosClaws extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { DIANGOS_CLAWS };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        player.startAnimation(7514);
        player.gfx0(1282);
        return true;
    }
}
