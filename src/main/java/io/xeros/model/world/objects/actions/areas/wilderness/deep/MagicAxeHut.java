package io.xeros.model.world.objects.actions.areas.wilderness.deep;

import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;
import io.xeros.util.Misc;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 9/29/2024
 */
public class MagicAxeHut extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[]{11726};
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return successfulUnlock(player, object);
    }

    @Override
    public Boolean handleActionTwo(Player player, GlobalObject object) {
        return successfulUnlock(player, object);
    }

    private Boolean successfulUnlock(Player player, GlobalObject object) {
        if (player.getItems().hasItemOnOrInventory(Items.LOCKPICK)) {
            int pY = player.getY();
            int yOffset = pY >= object.getY() ? -1 : 1;
            if (object.getX() == 3190 && object.getY() == 3957 || object.getX() == 3191 && object.getY() == 3963) {
                player.sendMessage("You attempt to pick the lock...");
                boolean isLucky = Misc.isLucky(50);
                if (isLucky) {
                    player.moveTo(player.getPosition().translate(0, yOffset));
                    return true;
                } else {
                    player.sendMessage("You fail to pick the lock!");
                    return false;
                }
            } else return false;
        } else {
            player.sendMessage("You need a lockpick to pick this lock.");
            return false;
        }
    }
}
