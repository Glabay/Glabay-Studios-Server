package io.xeros.model.items.interactions.inter;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-04
 */
public interface ItemActionThree {
    default Boolean handleActionThree(Player player, int itemId, int slotId, int interfaceId) {
        return false;
    }
}
