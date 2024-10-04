package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.SHERLOCK;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Sherlock extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SHERLOCK };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendNpcChat1("No shirt, Sherlock", SHERLOCK, "Sherlock");
        player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.SHERLOCK);
        return true;
    }
}
