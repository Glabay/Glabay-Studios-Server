package dev.glabay.rsps.plugin.world.item.inter;

import io.xeros.model.entity.player.Player;

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
