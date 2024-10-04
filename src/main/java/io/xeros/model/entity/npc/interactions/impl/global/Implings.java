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
        return new Integer[] { BABY_IMPLING, BABY_IMPLING_2, YOUNG_IMPLING, YOUNG_IMPLING_2, GOURMET_IMPLING, GOURMET_IMPLING_2, EARTH_IMPLING, EARTH_IMPLING_2, ESSENCE_IMPLING, ESSENCE_IMPLING_2, ECLECTIC_IMPLING, ECLECTIC_IMPLING_2, NATURE_IMPLING, NATURE_IMPLING_2, MAGPIE_IMPLING, MAGPIE_IMPLING_2, NINJA_IMPLING, NINJA_IMPLING_2, DRAGON_IMPLING, DRAGON_IMPLING_2, LUCKY_IMPLING, LUCKY_IMPLING_2 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        Impling.catchImpling(player, npc);
        return true;
    }
}