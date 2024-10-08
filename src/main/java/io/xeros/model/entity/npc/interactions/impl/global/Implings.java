package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.content.skills.hunter.impling.Impling;
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
public class Implings extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { BABY_IMPLING, BABY_IMPLING_1645, YOUNG_IMPLING, YOUNG_IMPLING_1646, GOURMET_IMPLING, GOURMET_IMPLING_1647, EARTH_IMPLING, EARTH_IMPLING_1648, ESSENCE_IMPLING, ESSENCE_IMPLING_1649, ECLECTIC_IMPLING, ECLECTIC_IMPLING_1650, NATURE_IMPLING, NATURE_IMPLING_1651, MAGPIE_IMPLING, MAGPIE_IMPLING_1652, NINJA_IMPLING, NINJA_IMPLING_1653, DRAGON_IMPLING, DRAGON_IMPLING_1654, LUCKY_IMPLING, LUCKY_IMPLING_7302 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        Impling.catchImpling(player, npc);
        return true;
    }
}