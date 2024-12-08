package dev.glabay.rsps.plugin.world.npc.impl.pestcontrol;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.ELITE_VOID_KNIGHT_DIALOGUE;
import static io.xeros.model.definition.Npcs.ELITE_VOID_KNIGHT;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class EliteVoidKnight extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ELITE_VOID_KNIGHT };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(ELITE_VOID_KNIGHT_DIALOGUE, ELITE_VOID_KNIGHT);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(ELITE_VOID_KNIGHT_DIALOGUE, ELITE_VOID_KNIGHT);
        return true;
    }
}
