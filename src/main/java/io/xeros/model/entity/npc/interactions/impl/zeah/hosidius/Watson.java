package io.xeros.model.entity.npc.interactions.impl.zeah.hosidius;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Watson extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { WATSON, WATSON_2 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.start(new DialogueBuilder(player).npc(WATSON,
                "I will trade a full set of clue scrolls for a master one.",
                "Just hand them over."));
        return true;
    }
}
