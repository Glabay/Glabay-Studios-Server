package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.varlamore;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Objects.COLOSSEUM_ENTRANCE;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class ColosseumEntrance extends WorldObjectAction {
    @Override
    public Integer[] getIds() {
        return new Integer[] { COLOSSEUM_ENTRANCE };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        // TODO: Create Colosseum Instance
        return false;
    }
}
