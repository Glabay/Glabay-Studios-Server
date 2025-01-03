package dev.glabay.rsps.plugin.world.npc.impl.desert.menaphos;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.skills.thieving.Thieving;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.MENAPHITE_THUG;
import static io.xeros.model.definition.Npcs.MENAPHITE_THUG_3550;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class MenaphiteThug extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MENAPHITE_THUG, MENAPHITE_THUG_3550 };
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getThieving().steal(Thieving.Pickpocket.MENAPHITE_THUG, npc);
        return true;
    }
}
