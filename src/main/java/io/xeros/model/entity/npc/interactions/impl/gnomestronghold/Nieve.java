package io.xeros.model.entity.npc.interactions.impl.gnomestronghold;

import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.*;
import static io.xeros.model.definition.Npcs.NIEVE;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Nieve extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { NIEVE };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(SLAYER_MASTER_IMPOSSIBLE_TASK_DIALOGUE, NIEVE);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (player.fullVoidMelee()) {
            player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.FULL_VOID);
        }
        if (player.getSlayer().getTask().isPresent()) {
            player.getDH().sendDialogues(SLAYER_MASTER_TASK_RESET_DIALOGUE, NIEVE);
        } else {
            player.getDH().sendDialogues(SLAYER_MASTER_TASK_CHOICE_DIALOGUE, NIEVE);
        }
        return true;
    }
}
