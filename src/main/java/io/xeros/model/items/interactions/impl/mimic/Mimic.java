package io.xeros.model.items.interactions.impl.mimic;

import io.xeros.content.bosses.mimic.MimicCasket;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.WorldItemAction;

import static io.xeros.model.definition.Items.MIMIC;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */

public class Mimic extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MIMIC };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        MimicCasket.open(player);
        return true;
    }
}
