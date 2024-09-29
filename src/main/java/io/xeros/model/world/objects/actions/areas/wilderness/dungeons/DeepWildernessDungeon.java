package io.xeros.model.world.objects.actions.areas.wilderness.dungeons;

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
public class DeepWildernessDungeon extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[]{ 16665 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return climbUpStairs(player, object);
    }

    public Boolean climbUpStairs(Player player, GlobalObject object) {
        player.getPA().movePlayer(3044, 3927, 0);
        return true;
    }
}
