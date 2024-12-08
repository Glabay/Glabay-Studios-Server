package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.canifis;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.content.skills.agility.impl.rooftop.RooftopCanafis.*;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/2/2024
 */
public class Gap extends WorldObjectAction {

    @Override
    public Integer[] getIds() {return new Integer[] {14845, 14848, 14846, 14894, 14847, 14897, 14844}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object ) {
        if (player.objectX == 3505 && player.objectY == 3498 && player.heightLevel == 2) {
            player.getRooftopCanafis().execute(player, GAP);
        } else if (player.objectX == 3496 && player.objectY == 3504 && player.heightLevel == 2) {
            player.getRooftopCanafis().execute(player, GAP2);
        } else if (player.objectX == 3485 && player.objectY == 3499 && player.heightLevel == 2) {
            player.getRooftopCanafis().execute(player, GAP3);
        } else if (player.objectX == 3478 && player.objectY == 3491 && player.heightLevel == 3) {
            player.getRooftopCanafis().execute(player, GAP4);
        } else if (player.objectX == 3480 && player.objectY == 3483 && player.heightLevel == 2) {
            player.getRooftopCanafis().execute(player, VAULT);
        } else if (player.objectX == 3503 && player.objectY == 3476 && player.heightLevel == 3) {
            player.getRooftopCanafis().execute(player, GAP5);
        } else if (player.objectX == 3510 && player.objectY == 3483 && player.heightLevel == 2) {
            player.getRooftopCanafis().execute(player, FINAL_GAP);
        }
        return true;
    }

}
