package io.xeros.model.entity.npc.interactions.impl.rellekka;

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
public class Sailor extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SAILOR, SAILOR_2 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendNpcChat1("Right click on me and i will take you on-board.", 3936, "Sailor");
        return true;
    }
}
