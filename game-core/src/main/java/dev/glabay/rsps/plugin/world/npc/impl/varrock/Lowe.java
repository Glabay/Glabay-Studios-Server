package dev.glabay.rsps.plugin.world.npc.impl.varrock;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.LOWE;
import static io.xeros.model.definition.Shops.RANGE_SHOP;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Lowe extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { LOWE };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getShops().openShop(RANGE_SHOP);
        return true;
    }
}
