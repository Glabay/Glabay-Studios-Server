package dev.glabay.rsps.plugin.world.npc.impl.pestcontrol;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.SQUIRE_2949;
import static io.xeros.model.definition.Shops.PEST_CONTROL_SHOP;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Squire extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SQUIRE_2949 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getPestControlRewards().showInterface();
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(PEST_CONTROL_SHOP);
        return true;
    }
}
