package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.wilderness.deep;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 9/29/2024
 */
public class DeepWildernessDungeonHut extends WorldObjectAction {
    @Override
    public Integer[] getIds() {
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
