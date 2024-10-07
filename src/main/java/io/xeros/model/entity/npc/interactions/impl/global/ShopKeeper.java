package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.PERDU_DIALOGUE;
import static io.xeros.model.Npcs.PERDU;
import static io.xeros.model.Npcs.SHOP_KEEPER;
import static io.xeros.model.Shops.GENERAL_SHOP;

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
        player.getShops().openShop(GENERAL_SHOP);
        return true;
    }
}
