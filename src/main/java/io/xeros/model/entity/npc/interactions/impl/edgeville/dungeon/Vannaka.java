package io.xeros.model.entity.npc.interactions.impl.edgeville.dungeon;

import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.*;
import static io.xeros.model.Npcs.TURAEL;
import static io.xeros.model.Npcs.VANNAKA;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Vannaka extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { VANNAKA };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (player.combatLevel < 40) {
            player.getDH().sendNpcChat2("Do not waste my time peasent.", "You need a Combat level of 40.", VANNAKA, "Vannaka");
            return false;
        }
        player.getDH().sendDialogues(SLAYER_MASTER_IMPOSSIBLE_TASK_DIALOGUE, VANNAKA);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(CHANGE_TASK_DIALOGUE, VANNAKA);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        player.getDH().sendDialogues(CHANGE_TASK_DIALOGUE, VANNAKA);
        return true;
    }
}
