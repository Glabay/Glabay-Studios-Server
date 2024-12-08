package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.prifddinas;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.item.lootable.impl.HunllefChest;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Items.HUNLLEFS_KEY;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/3/2024
 */
public class HunllefsChest extends WorldObjectAction {

    @Override
    public Integer[] getIds() {return new Integer[] {32508}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        player.objectDistance = 13;
        if (!(System.currentTimeMillis() - player.chestDelay > 2000)) {
            player.getDH().sendStatement("Please wait before doing this again.");
        }

        if (player.getItems().freeSlots() < 3) {
            player.getDH().sendStatement("@red@You need at least 3 free slots for safety");
        }
        if (player.getItems().playerHasItem(HUNLLEFS_KEY, 1)) {
            new HunllefChest().roll(player);
            player.chestDelay = System.currentTimeMillis();
        }
        player.getDH().sendStatement("@red@You need Hunllef's key to unlock this chest.");
        return true;
    }

}
