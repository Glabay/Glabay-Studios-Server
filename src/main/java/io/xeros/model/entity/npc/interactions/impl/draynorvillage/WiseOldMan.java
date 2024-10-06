package io.xeros.model.entity.npc.interactions.impl.draynorvillage;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.WISE_OLD_MAN_7;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class WiseOldMan extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { WISE_OLD_MAN_7 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getShops().openSkillCape();
        return true;
    }
}
