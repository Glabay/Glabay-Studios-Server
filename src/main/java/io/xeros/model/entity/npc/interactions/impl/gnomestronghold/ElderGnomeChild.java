package io.xeros.model.entity.npc.interactions.impl.gnomestronghold;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.ELDER_GNOME_CHILD;
import static io.xeros.model.Npcs.TOBY;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class ElderGnomeChild extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ELDER_GNOME_CHILD };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDiaryManager().getWesternDiary().claimReward();
        return true;
    }
}
