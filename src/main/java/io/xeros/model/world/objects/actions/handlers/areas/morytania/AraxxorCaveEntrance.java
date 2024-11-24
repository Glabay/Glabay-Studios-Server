package io.xeros.model.world.objects.actions.handlers.areas.morytania;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.Objects.CAVE_42594;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class AraxxorCaveEntrance extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] {
            CAVE_42594
        };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {

        return true;
    }
}
