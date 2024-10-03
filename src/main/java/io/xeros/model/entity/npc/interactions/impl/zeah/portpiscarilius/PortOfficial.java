package io.xeros.model.entity.npc.interactions.impl.zeah.portpiscarilius;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.PORT_OFFICIAL;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class PortOfficial extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { PORT_OFFICIAL };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(55870, 6999);
        return true;
    }
}
