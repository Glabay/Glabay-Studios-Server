package io.xeros.model.world.objects.actions.handlers.areas.gnomestronghold;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/4/2024
 */
public class GrandTreeDoors extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {return new Integer[] { 1967, 1968 }; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.absY == 3493) player.getPA().movePlayer(2466, 3491, 0);
        else if (player.absY == 3491) player.getPA().movePlayer(2466, 3493, 0);
        return true;
    }
}
