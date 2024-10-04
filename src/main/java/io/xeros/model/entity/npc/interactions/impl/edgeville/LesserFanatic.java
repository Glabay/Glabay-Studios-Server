package io.xeros.model.entity.npc.interactions.impl.edgeville;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.LESSER_FANATIC;
import static io.xeros.model.Npcs.TOBY;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class LesserFanatic extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { LESSER_FANATIC };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDiaryManager().getWildernessDiary().claimReward();
        return true;
    }
}
