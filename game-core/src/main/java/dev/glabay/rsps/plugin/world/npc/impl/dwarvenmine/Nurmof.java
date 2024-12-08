package dev.glabay.rsps.plugin.world.npc.impl.dwarvenmine;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.NURMOF;
import static io.xeros.model.definition.Shops.NURMOF_SHOP;

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
