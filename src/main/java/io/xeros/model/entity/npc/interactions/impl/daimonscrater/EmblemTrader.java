package io.xeros.model.entity.npc.interactions.impl.daimonscrater;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.*;
import static io.xeros.model.Npcs.*;
import static io.xeros.model.Shops.EMBLEM_TRADER_SHOP;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class EmblemTrader extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { EMBLEM_TRADER };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(EMBLEM_TRADER_DIALOGUE_3, EMBLEM_TRADER);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(EMBLEM_TRADER_SHOP);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        player.getDH().sendDialogues(EMBLEM_TRADER_DIALOGUE_2, EMBLEM_TRADER);
        return true;
    }

    @Override
    public Boolean handleActionFour(Player player, NPC npc) {
        player.getDH().sendDialogues(EMBLEM_TRADER_DIALOGUE, npc.getNpcId());
        return true;
    }
}
