package io.xeros.model.entity.npc.interactions.impl.kandarin;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.THE_WEDGE;
import static io.xeros.model.Npcs.TOBY;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class TheWedge extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { THE_WEDGE };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDiaryManager().getKandarinDiary().claimReward();
        return true;
    }
}
