package io.xeros.model.world.objects.actions.areas.canifis;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/1/2024
 */
public class TallTree extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {return new Integer[] {14843}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        player.getRooftopCanafis().execute(player, 14843);
        return true;
    }
}
