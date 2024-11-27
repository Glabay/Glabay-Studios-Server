package io.xeros.model.entity.npc.interactions.impl.burthorpe;

import io.xeros.content.dialogue.impl.MacDialogue;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.MAC;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Mac extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MAC };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.start(new MacDialogue(player, true));
        return true;
    }
}
