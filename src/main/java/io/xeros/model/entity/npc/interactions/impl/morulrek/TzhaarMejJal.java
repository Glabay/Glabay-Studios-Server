package io.xeros.model.entity.npc.interactions.impl.morulrek;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.TZHAAR_MEJ_JAL_DIALOGUE;
import static io.xeros.model.Npcs.TZHAARMEJJAL;

/**
 * @author Zeighe | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Zeighe
 * @since 2024-10-01
 */
public class TzhaarMejJal extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { TZHAARMEJJAL };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(TZHAAR_MEJ_JAL_DIALOGUE, TZHAARMEJJAL);
        return true;
    }
    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(TZHAAR_MEJ_JAL_DIALOGUE, TZHAARMEJJAL);
        return true;
    }
}
