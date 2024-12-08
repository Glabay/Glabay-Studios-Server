package dev.glabay.rsps.plugin.world.npc.impl.tutorialisland;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanDialogue;

import static io.xeros.model.definition.Npcs.RECRUITER;
import static io.xeros.model.definition.Npcs.RECRUITER_8973;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Recruiter extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { RECRUITER, RECRUITER_8973 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.start(new GroupIronmanDialogue(player));
        return true;
    }
}
