package io.xeros.model.entity.npc.interactions.impl.lumbridge;

import io.xeros.content.dialogue.impl.IronmanNpcDialogue;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Adam extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ADAM };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.start(new IronmanNpcDialogue(player, npc));
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        IronmanNpcDialogue.giveIronmanArmour(player, npc);
        return true;
    }
}
