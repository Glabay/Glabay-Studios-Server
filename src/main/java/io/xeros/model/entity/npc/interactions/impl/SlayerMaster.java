package io.xeros.model.entity.npc.interactions.impl;

import io.xeros.content.skills.slayer.SlayerRewardsInterface;
import io.xeros.content.skills.slayer.SlayerRewardsInterfaceData;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.*;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class SlayerMaster extends NpcOptionAction {

    @Override
    protected Integer[] getIds() {
        return new Integer[] {
            TURAEL,
            MAZCHNA,
            VANNAKA,
            CHAELDAR,
            DURADEL,
            NIEVE,
            KRYSTILIA,
            ELF_TRACKER,
            KONAR_QUO_MATEN
        };
    }


    @Override
    public Boolean handleActionFour(Player player, NPC npc) {
        SlayerRewardsInterface.open(player, SlayerRewardsInterfaceData.Tab.TASK);
        return true;
    }
}
