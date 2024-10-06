package io.xeros.model.entity.npc.interactions.impl.zeah.mountkaruulm;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.*;
import static io.xeros.model.Npcs.KONAR_QUO_MATEN;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class KonarQuoMaten extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { KONAR_QUO_MATEN };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(SLAYER_MASTER_IMPOSSIBLE_TASK_DIALOGUE, KONAR_QUO_MATEN);
        return true;
    }
}
