package io.xeros.model.world.objects.actions.areas.falador;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.Items.*;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/3/2024
 */
public class MoleHill extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {return new Integer[] {12202}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (!player.getItems().playerHasItem(SPADE)) {
            player.sendMessage("You need a spade to dig into this hole.");
            return false;
        }
        player.getPA().movePlayer(1761, 5186, 0);
        player.sendMessage("You use your spade to dig into the hole and entered the Mole Lair.");
        return true;
    }
}
