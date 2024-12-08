package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.fremennikisles.neitiznot;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Objects.ROPE_BRIDGE_21311;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class FremIslesWestBridgeNorthEntrance extends WorldObjectAction {

    @Override
    public Integer[] getIds() {
        return new Integer[]{ ROPE_BRIDGE_21311 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return EnterBridge(player, object);
    }

    private Boolean EnterBridge(Player player, GlobalObject object) {
        player.getPA().movePlayer(2314, 3839, 0);
        return true;
    }
}