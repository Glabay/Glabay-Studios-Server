package io.xeros.model.entity.npc.interactions.impl.global.fishingspots;

import io.xeros.content.skills.Fishing;
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
public class FishingSpot extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] {
                317,
                324,

                AYESHA,
                DR_JEKYLL_2,
                DARK_CORE,
                REACHER_3,
                REACHER_4,
                REACHER_5,
                MIME,
                DRUNKEN_DWARF,
                STRANGE_WATCHER_3,

                FISHING_SPOT_12,
                FISHING_SPOT_16,
                FISHING_SPOT_31,
                FISHING_SPOT_33,
                FISHING_SPOT_34,
                FISHING_SPOT_46,

                ROD_FISHING_SPOT_14,
                ROD_FISHING_SPOT_16,
        };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.clickNpcType = 1;
        switch (npc.getNpcId()) {
            case FISHING_SPOT_34 -> Fishing.attemptdata(player, 1);
            case ROD_FISHING_SPOT_14 -> Fishing.attemptdata(player, 4);
            case FISHING_SPOT_33 -> Fishing.attemptdata(player, 8);
            case FISHING_SPOT_12,
                 AYESHA,
                 DR_JEKYLL_2,
                 317,
                 DARK_CORE,
                 REACHER_3,
                 REACHER_5 -> Fishing.attemptdata(player, 9);
            case FISHING_SPOT_16 -> Fishing.attemptdata(player, 11);
            case FISHING_SPOT -> Fishing.attemptdata(player, 13);
            case FISHING_SPOT_31 -> Fishing.attemptdata(player, 14);
            case FISHING_SPOT_46 -> Fishing.attemptdata(player, 15);
            case ROD_FISHING_SPOT_16 -> Fishing.attemptdata(player, 16);
        }
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.clickNpcType = 1;
        switch (npc.getNpcId()) {
            case FISHING_SPOT_34 -> Fishing.attemptdata(player, 2);
            case AYESHA,
                 DR_JEKYLL_2,
                 317,
                 DARK_CORE,
                 REACHER_3,
                 REACHER_4,
                 REACHER_5,
                 ROD_FISHING_SPOT_14,
                 ROD_FISHING_SPOT_16 -> Fishing.attemptdata(player, 6);

            case FISHING_SPOT_33,
                 MIME,
                 324 -> Fishing.attemptdata(player, 7);

            case FISHING_SPOT_12,
                 DRUNKEN_DWARF,
                 STRANGE_WATCHER_3 -> Fishing.attemptdata(player, 10);
        }
        return true;
    }
}
