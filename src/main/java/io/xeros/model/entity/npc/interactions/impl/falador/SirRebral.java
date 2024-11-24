package io.xeros.model.entity.npc.interactions.impl.falador;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.SIR_REBRAL;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class SirRebral extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SIR_REBRAL };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDiaryManager().getFaladorDiary().claimReward();
        return true;
    }
}
