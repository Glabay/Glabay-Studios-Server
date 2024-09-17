package io.xeros.model.entity.npc.interactions.inter;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-16
 */
public interface NpcActionThree {
    default Boolean handleActionThree(Player player, NPC npc) {
        return false;
    }
}
