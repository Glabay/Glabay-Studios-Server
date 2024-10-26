package io.xeros.model.items.interactions.impl.boxes;

import io.xeros.content.item.lootable.impl.ResourceBoxSmall;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.WorldItemAction;

import static io.xeros.content.item.lootable.impl.ResourceBoxSmall.BOX_ITEM;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */

public class SmallResourceBox extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { BOX_ITEM };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        new ResourceBoxSmall().roll(player);
        return true;
    }
}
