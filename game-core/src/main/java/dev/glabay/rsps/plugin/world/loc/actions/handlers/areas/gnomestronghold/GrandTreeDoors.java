package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.gnomestronghold;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/4/2024
 */
public class GrandTreeDoors extends WorldObjectAction {

    @Override
    public Integer[] getIds() {return new Integer[] { 1967, 1968 }; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.absY == 3493) player.getPA().movePlayer(2466, 3491, 0);
        else if (player.absY == 3491) player.getPA().movePlayer(2466, 3493, 0);
        return true;
    }
}
