package io.xeros.model.entity.npc.interactions.impl.fossilisland;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.GIVEAWAY_MANAGER;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class GiveawayManager extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { GIVEAWAY_MANAGER };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getEventCalendar().openCalendar();
        return true;
    }
}
