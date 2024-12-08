package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.morytania;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Objects.CAVE_42594;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class AraxxorCaveEntrance extends WorldObjectAction {
    @Override
    public Integer[] getIds() {
        return new Integer[] {
            CAVE_42594
        };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {

        return true;
    }
}
