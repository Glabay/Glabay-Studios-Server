package io.xeros.model.entity.npc.interactions.impl.global;

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
public class Banker extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
    return new Integer[] { BANKER, BANKER_2, BANKER_3, BANKER_4, BANKER_5, BANKER_6, BANKER_7, BANKER_8, BANKER_9, BANKER_10, BANKER_11, BANKER_12, BANKER_13, BANKER_14, BANKER_15,
        BANKER_16, BANKER_17, BANKER_18, BANKER_19, BANKER_20, BANKER_21, BANKER_22, BANKER_23, BANKER_24, BANKER_25, BANKER_26, BANKER_27, BANKER_28, BANKER_29,
        BANKER_30, BANKER_31, BANKER_32, BANKER_33, BANKER_34, BANKER_35, BANKER_36, BANKER_37, BANKER_38, BANKER_39, BANKER_40, BANKER_41, BANKER_42, BANKER_43,
        BANKER_44, BANKER_45, BANKER_46, BANKER_47, BANKER_48, BANKER_49, BANKER_50, BANKER_51, BANKER_52, BANKER_53, BANKER_54, BANKER_55, BANKER_56, BANKER_57,
        BANKER_58, BANKER_59, GHOST_BANKER, GNOME_BANKER, NARDAH_BANKER, SIRSAL_BANKER};
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getPA().c.itemAssistant.openUpBank();
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getPA().c.itemAssistant.openUpBank();
        return true;
    }
}
