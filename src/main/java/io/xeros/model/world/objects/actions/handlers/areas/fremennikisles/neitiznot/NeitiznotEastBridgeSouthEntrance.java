package io.xeros.model.world.objects.actions.handlers.areas.fremennikisles.neitiznot;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.Objects.ROPE_BRIDGE_21308;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class NeitiznotEastBridgeSouthEntrance extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {
        return new Integer[]{ ROPE_BRIDGE_21308 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return EnterBridge(player, object);
    }

    private Boolean EnterBridge(Player player, GlobalObject object) {
        player.getPA().movePlayer(2343, 3828, 0);
        return true;
    }
}