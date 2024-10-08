package io.xeros.model.entity.npc.interactions.impl.rellekka;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.*;
import static io.xeros.model.Shops.SIGMUND_THE_MERCHANT_SHOP;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class SigmundTheMerchant extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SIGMUND_THE_MERCHANT };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getShops().openShop(SIGMUND_THE_MERCHANT_SHOP);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(SIGMUND_THE_MERCHANT_SHOP);
        return true;
    }
}
