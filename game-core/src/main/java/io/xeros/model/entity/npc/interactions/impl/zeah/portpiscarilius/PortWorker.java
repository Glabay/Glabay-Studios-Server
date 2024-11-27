package io.xeros.model.entity.npc.interactions.impl.zeah.portpiscarilius;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.PORT_WORKER_DIALOGUE;
import static io.xeros.model.definition.Npcs.PORT_WORKER;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class PortWorker extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { PORT_WORKER };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(PORT_WORKER_DIALOGUE, PORT_WORKER);
        return true;
    }
}
