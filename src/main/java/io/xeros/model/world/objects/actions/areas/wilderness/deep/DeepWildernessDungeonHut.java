package io.xeros.model.world.objects.actions.areas.wilderness.deep;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 9/29/2024
 */
public class DeepWildernessDungeonHut extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { 16664 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return climbDownStairs(player, object);
    }

    public Boolean climbDownStairs(Player player, GlobalObject object) {
        player.getPA().movePlayer(3045, 10323, 0);
        return true;
    }
}
