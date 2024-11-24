package io.xeros.model.entity.npc.interactions.impl.edgeville;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.BROTHER_JERED_DIALOGUE;
import static io.xeros.model.definition.Dialogues.BROTHER_JERED_DIALOGUE_2;
import static io.xeros.model.definition.Npcs.BROTHER_JERED;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class BrotherJered extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { BROTHER_JERED };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(BROTHER_JERED_DIALOGUE, BROTHER_JERED);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(BROTHER_JERED_DIALOGUE_2, BROTHER_JERED);
        return true;
    }
}
