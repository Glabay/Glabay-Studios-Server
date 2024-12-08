package dev.glabay.rsps.plugin.world.npc.impl.global;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.tradingpost.Listing;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class GrandExchangeClerk extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { GRAND_EXCHANGE_CLERK, GRAND_EXCHANGE_CLERK_2149, GRAND_EXCHANGE_CLERK_2150, GRAND_EXCHANGE_CLERK_2151 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getPA().player.itemAssistant.openUpBank();
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (player.getMode().isIronmanType()) {
            player.sendMessage("@red@You are not permitted to make use of this.");
            return false;
        }
        Listing.openPost(player, false);
        return true;
    }
}