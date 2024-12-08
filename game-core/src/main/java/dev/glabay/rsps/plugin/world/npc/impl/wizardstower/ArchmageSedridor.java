package dev.glabay.rsps.plugin.world.npc.impl.wizardstower;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.ARCHMAGE_SEDRIDOR;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class ArchmageSedridor extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ARCHMAGE_SEDRIDOR };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getPA().startTeleport(2929, 4813, 0, "modern", false);
        player.getDiaryManager().getLumbridgeDraynorDiary()
                .progress(LumbridgeDraynorDiaryEntry.TELEPORT_ESSENCE_LUM);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getPA().startTeleport(2929, 4813, 0, "modern", false);
        player.getDiaryManager().getLumbridgeDraynorDiary()
                .progress(LumbridgeDraynorDiaryEntry.TELEPORT_ESSENCE_LUM);
        return true;
    }
}
