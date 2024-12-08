package dev.glabay.rsps.plugin.world.npc.impl.global;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.PERDU_DIALOGUE;
import static io.xeros.model.definition.Npcs.PERDU;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Perdu extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { PERDU };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(PERDU_DIALOGUE, PERDU);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getPerduLostPropertyShop().open(player);
        return true;
    }
}
