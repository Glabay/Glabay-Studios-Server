package io.xeros.model.entity.npc.interactions.impl.lumbridge;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.*;
import static io.xeros.model.definition.Shops.BOB_BRILLIANT_AXES_SHOP;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Bob extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { BOB_10619 };
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(BOB_BRILLIANT_AXES_SHOP);
        return true;
    }
}
