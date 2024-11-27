package io.xeros.model.world.objects.actions.handlers.global;

import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.definition.Objects.*;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/4/2024
 */
public class Ladder extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {return new Integer[] {LADDER_2884, LADDER_16679, LADDER_16683, LADDER_16684, LADDER_24082}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.getHeight() >= 3) {
            AgilityHandler.delayEmote(player, "CLIMB_DOWN", player.getX(), player.getY(),player.getHeight() - 1, 2);
            return true;
        }
        if (player.getHeight() >= 1 && player.getHeight() <= 3) {
        if (Boundary.isIn(player, Boundary.GRAND_TREE_1) || Boundary.isIn(player, Boundary.GRAND_TREE_2)) {
            AgilityHandler.delayEmote(player, "CLIMB_UP", player.getX(), player.getY(), player.getHeight() + 1, 2);
        } else {
            AgilityHandler.delayEmote(player, "CLIMB_DOWN", player.getX(), player.getY(),player.getHeight() - 1, 2);
            return true;
        }
    }
        AgilityHandler.delayEmote(player, "CLIMB_UP", player.getX(), player.getY(), player.getHeight() + 1, 2);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, GlobalObject object) {
        AgilityHandler.delayEmote(player, "CLIMB_UP", player.getX(), player.getY(), player.getHeight() + 1, 2);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, GlobalObject object) {
        AgilityHandler.delayEmote(player, "CLIMB_DOWN", player.getX(), player.getY(), player.getHeight() - 1, 2);
        return true;
    }
}
