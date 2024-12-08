package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.waterbithisland;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class WaterbirthIslandDungeon extends WorldObjectAction {

    @Override
    public Integer[] getIds() {
        return new Integer[]{ 21597, 21598, 21599 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return ExitDungeon(player, object);
    }

    private Boolean ExitDungeon(Player player, GlobalObject object) {
        player.objectDistance = 3;
        player.getPA().movePlayer(2523, 3739, 0);
        return true;
    }
}
