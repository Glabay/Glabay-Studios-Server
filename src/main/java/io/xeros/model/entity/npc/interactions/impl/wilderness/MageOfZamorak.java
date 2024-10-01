package io.xeros.model.entity.npc.interactions.impl.wilderness;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.MAGE_OF_ZAMORAK;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class MageOfZamorak extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MAGE_OF_ZAMORAK };
    }

    @Override
    public Boolean handleActionFour(Player player, NPC npc) {
        player.getPA().startTeleport(3039, 4788, 0, "modern", false);
        player.teleAction = -1;
        return true;
    }
}
