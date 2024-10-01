package io.xeros.model.entity.npc.interactions.impl;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.HUNTER_STORE;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Aleck extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { HUNTER_STORE };
    }

    @Override
    public Boolean handleActionFour(Player player, NPC npc) {
        player.getShops().openShop(23);
        return true;
    }
}
