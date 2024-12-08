package dev.glabay.rsps.plugin.world.npc.impl.draynorvillage;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.DIANGO_DIALOGUE;
import static io.xeros.model.definition.Npcs.DIANGO;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Diango extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { DIANGO };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(DIANGO_DIALOGUE, DIANGO);
        return true;
    }
}
