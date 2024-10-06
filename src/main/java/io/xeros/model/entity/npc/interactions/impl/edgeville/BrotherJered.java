package io.xeros.model.entity.npc.interactions.impl.edgeville;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.BROTHER_JERED;

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
        player.getDH().sendDialogues(2401, BROTHER_JERED);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(2400, BROTHER_JERED);
        return true;
    }
}
