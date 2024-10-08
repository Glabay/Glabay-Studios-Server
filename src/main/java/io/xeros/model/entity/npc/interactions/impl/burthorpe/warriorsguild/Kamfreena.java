package io.xeros.model.entity.npc.interactions.impl.burthorpe.warriorsguild;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.KAMFREENA;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Kamfreena extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { KAMFREENA };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getWarriorsGuild().handleDoor();
        return true;
    }
}
