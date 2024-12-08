package dev.glabay.rsps.plugin.world.npc.impl.alkharid.duelarena;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.minigames.pest_control.PestControl;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Items.BANDAGES;
import static io.xeros.model.definition.Npcs.SURGEON_GENERAL_TAFANI;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class SurgeonGeneralTafani extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SURGEON_GENERAL_TAFANI };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        PestControl.refreshPlayer(player);
        player.getDH().sendItemStatement("Restored your HP, Prayer, Run Energy, and Spec", BANDAGES);
        player.nextChat = -1;
        return true;
    }
}
