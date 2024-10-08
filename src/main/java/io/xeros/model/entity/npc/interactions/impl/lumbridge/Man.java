package io.xeros.model.entity.npc.interactions.impl.lumbridge;

import io.xeros.content.skills.thieving.Thieving;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.MAN_6987;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Man extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MAN_6987 };
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getThieving().steal(Thieving.Pickpocket.MAN, npc);
        return true;
    }
}
