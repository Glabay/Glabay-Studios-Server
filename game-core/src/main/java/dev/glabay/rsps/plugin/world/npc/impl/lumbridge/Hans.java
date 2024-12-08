package dev.glabay.rsps.plugin.world.npc.impl.lumbridge;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import java.util.concurrent.TimeUnit;

import static io.xeros.model.definition.Npcs.HANS;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Hans extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { HANS };
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        long milliseconds = (long) player.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
        String time = String.format("%d days and %d hours", days, hours);
        player.getDH().sendNpcChat1(String.format("You've been playing for %s", time), HANS, "Hans");
        player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.HANS);
        return true;
    }
}
