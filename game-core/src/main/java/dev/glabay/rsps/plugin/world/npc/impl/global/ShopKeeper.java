package dev.glabay.rsps.plugin.world.npc.impl.global;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.SHOP_KEEPER;
import static io.xeros.model.definition.Shops.GENERAL_SHOP_2;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class ShopKeeper extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SHOP_KEEPER };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getShops().openShop(GENERAL_SHOP_2);
        return true;
    }
}
