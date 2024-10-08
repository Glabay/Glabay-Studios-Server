package io.xeros.model.entity.npc.interactions.impl.dwarvenmine;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.NURMOF;
import static io.xeros.model.Shops.NURMOF_SHOP;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Nurmof extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { NURMOF };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getShops().openShop(NURMOF_SHOP);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(NURMOF_SHOP);
        return true;
    }
}
