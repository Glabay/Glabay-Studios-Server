package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.*;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Banker extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
    return new Integer[] { BANKER, GHOST_BANKER, GNOME_BANKER, NARDAH_BANKER, SIRSAL_BANKER};
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getPA().player.itemAssistant.openUpBank();
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getPA().player.itemAssistant.openUpBank();
        return true;
    }
}
