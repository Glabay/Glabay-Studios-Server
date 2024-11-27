package io.xeros.model.entity.npc.interactions.impl.varrock;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.INFORMATION_CLERK_DIALOGUE_2;
import static io.xeros.model.definition.Dialogues.INFORMATION_CLERK_DIALOGUE_3;
import static io.xeros.model.definition.Npcs.INFORMATION_CLERK;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class InformationClerk extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { INFORMATION_CLERK  };
    }

    @Override
    public Boolean handleActionTwo(Player player,NPC npc) {
        player.getDH().sendDialogues(INFORMATION_CLERK_DIALOGUE_2, INFORMATION_CLERK);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        player.getDH().sendDialogues(INFORMATION_CLERK_DIALOGUE_3, INFORMATION_CLERK);
        return true;
    }
}
