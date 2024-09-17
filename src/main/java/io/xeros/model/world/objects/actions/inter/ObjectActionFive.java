package io.xeros.model.world.objects.actions.inter;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-16
 */
public interface ObjectActionFive {
    default Boolean handleActionFive(Player player, GlobalObject object) {
        return false;
    }
}
