package dev.glabay.rsps.plugin.world.item.inter;

import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-05
 */
public interface ItemOnObject {
    default Boolean handleItemOnObject(Player player, WorldObject objectUsedOn, int itemId) {
        return false;
    }
}
