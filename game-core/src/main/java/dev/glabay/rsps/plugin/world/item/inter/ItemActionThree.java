package dev.glabay.rsps.plugin.world.item.inter;

import io.xeros.model.entity.player.Player;

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
