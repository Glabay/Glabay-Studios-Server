package io.xeros.model.entity.npc.interactions.impl.edgeville;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.KRYSTILIA_2_DIALOGUE;
import static io.xeros.model.definition.Dialogues.KRYSTILIA_DIALOGUE;
import static io.xeros.model.definition.Npcs.KRYSTILIA;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Krystilia extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { KRYSTILIA };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(KRYSTILIA_DIALOGUE, KRYSTILIA);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(KRYSTILIA_2_DIALOGUE, KRYSTILIA);
        return true;
    }
}
