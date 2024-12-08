package dev.glabay.rsps.plugin.world.npc.impl.global.fishingspots;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.skills.Fishing;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.*;
import static io.xeros.model.definition.Objects.FISHING_SPOT_46;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class FishingSpot extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] {
                317,
                324,

                AYESHA,
                DR_JEKYLL_314,
                DARK_CORE,
                REACHER_328,
                REACHER_329,
                REACHER_331,
                MIME,
                DRUNKEN_DWARF,
                STRANGE_WATCHER_334,

                FISHING_SPOT_1520,
                FISHING_SPOT_1524,
                FISHING_SPOT_3317,
                FISHING_SPOT_3657,
                FISHING_SPOT_3913,
                FISHING_SPOT_4712,

                ROD_FISHING_SPOT_3417,
                ROD_FISHING_SPOT_6825,
        };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.clickNpcType = 1;
        switch (npc.getNpcId()) {
            case FISHING_SPOT_3913 -> Fishing.attemptdata(player, 1);
            case FISHING_SPOT_1522 -> Fishing.attemptdata(player, 4);
            case FISHING_SPOT_3657 -> Fishing.attemptdata(player, 8);
            case FISHING_SPOT_1520,
                 AYESHA,
                 DR_JEKYLL_314,
                 317,
                 DARK_CORE,
                 REACHER_328,
                 REACHER_331 -> Fishing.attemptdata(player, 9);
            case FISHING_SPOT_1524 -> Fishing.attemptdata(player, 11);
            case FISHING_SPOT -> Fishing.attemptdata(player, 13);
            case FISHING_SPOT_3317 -> Fishing.attemptdata(player, 14);
            case FISHING_SPOT_46 -> Fishing.attemptdata(player, 15);
            case ROD_FISHING_SPOT_6825 -> Fishing.attemptdata(player, 16);
        }
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.clickNpcType = 1;
        switch (npc.getNpcId()) {
            case FISHING_SPOT_3913 -> Fishing.attemptdata(player, 2);
            case AYESHA,
                 DR_JEKYLL_314,
                 317,
                 DARK_CORE,
                 REACHER_328,
                 REACHER_329,
                 REACHER_331,
                 ROD_FISHING_SPOT_3417,
                 ROD_FISHING_SPOT_6825 -> Fishing.attemptdata(player, 6);

            case FISHING_SPOT_3657,
                 MIME,
                 324 -> Fishing.attemptdata(player, 7);

            case FISHING_SPOT_1520,
                 DRUNKEN_DWARF,
                 STRANGE_WATCHER_334 -> Fishing.attemptdata(player, 10);
        }
        return true;
    }
}
