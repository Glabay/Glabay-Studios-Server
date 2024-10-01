package io.xeros.model.entity.npc.interactions.impl;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.EMBLEM_TRADER_DIALOGUE;
import static io.xeros.model.Npcs.EMBLEM_TRADER;

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
    public Boolean handleActionFour(Player player, NPC npc) {
        player.getDH().sendDialogues(EMBLEM_TRADER_DIALOGUE, npc.getNpcId());
        return true;
    }
}
