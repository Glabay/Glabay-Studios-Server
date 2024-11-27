package io.xeros.model.items.interactions.inter;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-05
 */
public interface ItemOperation {
    default Boolean handleItemOperation(Player player, int itemId) {
        return false;
    }
}
