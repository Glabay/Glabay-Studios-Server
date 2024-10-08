package io.xeros.model.entity.npc.interactions.impl.ottosgrotto;

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
public class OttoGodblessed extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { OTTO_GODBLESSED, OTTO_GODBLESSED_2915 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendNpcChat2("Use Zammy Spear on me to get Hasta, cost 10m", "Use Hasta on me get Zammy Spear, cost 5m", OTTO_GODBLESSED,
                "Otto Godblessed");
        return true;
    }
}
