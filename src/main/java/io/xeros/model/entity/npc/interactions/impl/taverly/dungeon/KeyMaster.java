package io.xeros.model.entity.npc.interactions.impl.taverly.dungeon;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.*;
import static io.xeros.model.definition.Items.COINS;
import static io.xeros.model.definition.Npcs.KEY_MASTER;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class KeyMaster extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[]{KEY_MASTER};
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(YOU_NEED_TO_LEAVE_DIALOGUE, KEY_MASTER);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (player.getSlayer().getTask().isPresent()) {
            player.getDH().sendDialogues(SLAYER_MASTER_TASK_RESET_DIALOGUE, KEY_MASTER);
        } else {
            if (player.getLevel(Skill.SLAYER) < 91) {
                player.getDH().sendStatement("You need a Slayer level of 91 to kill these.");
                return false;
            }
            if (player.getSlayer().getTask().isPresent()) {
                player.getDH().sendStatement("Please finish your current task first.");
                return false;
            }
            if (!player.getItems().playerHasItem(COINS, 3_000_000)) {
                player.getDH().sendStatement("Come back when you've got the 3m ready.");
                return false;
            }
            player.getItems().deleteItem2(COINS, 3_000_000);
            player.getSlayer().createNewTask(KEY_MASTER, true);
            player.getDH().sendNpcChat("You have been assigned " + player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask().get().getPrimaryName());
            player.nextChat = -1;
        }
        return true;
    }
}
