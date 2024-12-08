package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.keboslowlands.mountquidamortem;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.item.lootable.impl.RaidsChestCommon;
import io.xeros.content.item.lootable.impl.RaidsChestRare;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/3/2024
 */
public class RaidsRewardsChest extends WorldObjectAction {

    @Override
    public Integer[] getIds() {return new Integer[] {30107}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.getItems().freeSlots() < 3) {
            player.getDH().sendStatement("@red@You need at least 3 free slots for safety");
            return false;
        }
        if (player.getItems().playerHasItem(Raids.COMMON_KEY, 1)) {
            new RaidsChestCommon().roll(player);
        }
        if (player.getItems().playerHasItem(Raids.RARE_KEY, 1)) {
            new RaidsChestRare().roll(player);
        }
        player.getDH().sendStatement("@red@You need either a rare or common key.");
        return true;
    }
}
