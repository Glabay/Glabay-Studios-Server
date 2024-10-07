package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.content.skills.crafting.Tanning;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.CRAFTING_AND_TANNER;
import static io.xeros.model.Shops.CRAFTING_SHOP;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Tanner extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { CRAFTING_AND_TANNER };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        Tanning.sendTanningInterface(player);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(CRAFTING_SHOP);
        return true;
    }
}
