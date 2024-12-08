package dev.glabay.rsps.plugin.world.npc.impl.zeah.mountkaruulm.dungeon;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.TARFOL_QUO_MATEN_DIALOGUE;
import static io.xeros.model.definition.Npcs.TARFOL_QUO_MATEN;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class TarfolQuoMaten extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { TARFOL_QUO_MATEN };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (player.playerLevel[18] >= 95) {
            player.getDH().sendDialogues(TARFOL_QUO_MATEN_DIALOGUE, TARFOL_QUO_MATEN);
            return false;
        }
        player.sendMessage("You need a slayer level of 95 to get a task from me.");
        return true;
    }
}
