package dev.glabay.rsps.plugin.world.loc.actions.inter;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-16
 */
public interface ObjectActionTwo {
    default Boolean handleActionTwo(Player player, GlobalObject object) {
        return false;
    }
}
