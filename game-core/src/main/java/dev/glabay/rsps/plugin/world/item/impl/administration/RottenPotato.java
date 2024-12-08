package dev.glabay.rsps.plugin.world.item.impl.administration;

import dev.glabay.rsps.plugin.world.item.WorldItemAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import static io.xeros.model.definition.Items.ROTTEN_POTATO;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-05
 */
public class RottenPotato extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ROTTEN_POTATO };
    }

    @Override
    public Boolean handleItemOnNpc(Player player, NPC target, int itemId, int slot) {
        System.out.println("Potato Action: Npc");
        return true;
    }

    @Override
    public Boolean handleItemOnPlayer(Player player, Player target, int itemId, int slot) {
        System.out.println("Potato Action: Player");
        return true;
    }

    @Override
    public Boolean handleItemOnItem(Player player, GameItem itemUsed, GameItem itemUsedOn) {
        System.out.println("Potato Action: ItemOnItem");
        return true;
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        System.out.println("Potato Action: 1");
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, int itemId, int slotId, int interfaceId) {
        System.out.println("Potato Action: 2");
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, int itemId, int slotId, int interfaceId) {
        System.out.println("Potato Action: 3");
        return true;
    }

    @Override
    public Boolean handleActionFour(Player player, int itemId, int slotId, int interfaceId) {
        System.out.println("Potato Action: 4");
        return true;
    }
}
