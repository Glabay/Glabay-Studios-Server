package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.content.tradingpost.Listing;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class TradingPostManager extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { BANKER_2897 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (player.getMode().isIronmanType()) {
            player.sendMessage("@red@You are not permitted to make use of this.");
            return false;
        }
        Listing.openPost(player, false);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (player.getMode().isIronmanType()) {
            player.sendMessage("@red@You are not permitted to make use of this.");			}
        Listing.collectMoney(player);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        player.getPA().player.itemAssistant.openUpBank();
        return true;
    }
}