package io.xeros.model.entity.npc.interactions.impl.canifis;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.LESABR;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class LeSabre extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { LESABR };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDiaryManager().getMorytaniaDiary().claimReward();
        return true;
    }
}
