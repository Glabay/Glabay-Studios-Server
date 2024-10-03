package io.xeros.model.entity.npc.interactions.impl.zeah.catacombs;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.LILLIA_DIALOGUE;
import static io.xeros.model.Npcs.LILLIA;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Lillia extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { LILLIA };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(LILLIA_DIALOGUE, LILLIA);
        return true;
    }
}
