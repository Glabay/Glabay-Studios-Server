package dev.glabay.rsps.plugin.world.npc.impl.rellekka;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.SIGMUND_THE_MERCHANT;
import static io.xeros.model.definition.Shops.SIGMUND_THE_MERCHANT_SHOP;

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
