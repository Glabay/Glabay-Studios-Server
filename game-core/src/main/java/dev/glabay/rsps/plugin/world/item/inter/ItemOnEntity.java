package dev.glabay.rsps.plugin.world.item.inter;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-05
 */
public interface ItemOnEntity {
    default Boolean handleItemOnPlayer(Player player, Player target, int itemId, int slot) {
        return false;
    }

    default Boolean handleItemOnNpc(Player player, NPC target, int itemId, int slot) {
        return false;
    }
}
