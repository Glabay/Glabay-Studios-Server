package io.xeros.model.entity.npc.interactions.impl.tutorialisland;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.COMBAT_INSTRUCTOR_DIALOGUE;
import static io.xeros.model.Npcs.COMBAT_INSTRUCTOR;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class CombatInstructor extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { COMBAT_INSTRUCTOR };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(COMBAT_INSTRUCTOR_DIALOGUE, COMBAT_INSTRUCTOR);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getPA().showInterface(37700);
        player.sendMessage("Set different colors for specific items for easier looting!");
        return true;
    }
}
