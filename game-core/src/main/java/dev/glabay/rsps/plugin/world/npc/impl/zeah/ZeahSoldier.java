package dev.glabay.rsps.plugin.world.npc.impl.zeah;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.ZEAH_SOLDIER_DIALOGUE;
import static io.xeros.model.definition.Npcs.WOUNDED_SOLDIER_6838;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class ZeahSoldier extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { WOUNDED_SOLDIER_6838 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(ZEAH_SOLDIER_DIALOGUE, WOUNDED_SOLDIER_6838);
        return true;
    }
}
