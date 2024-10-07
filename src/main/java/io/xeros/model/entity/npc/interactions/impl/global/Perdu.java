package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.DIANGO_DIALOGUE;
import static io.xeros.model.Dialogues.PERDU_DIALOGUE;
import static io.xeros.model.Npcs.DIANGO;
import static io.xeros.model.Npcs.PERDU;

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
