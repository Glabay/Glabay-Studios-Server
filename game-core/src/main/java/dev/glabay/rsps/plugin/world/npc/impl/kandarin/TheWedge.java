package dev.glabay.rsps.plugin.world.npc.impl.kandarin;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.THE_WEDGE;

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
