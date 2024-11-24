package io.xeros.model.entity.npc.data;

import io.xeros.Server;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.skills.hunter.trap.impl.BirdSnare;
import io.xeros.content.skills.hunter.trap.impl.BoxTrap;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.npc.NPC;

public class RespawnTime {

    public static int get(NPC npc) {
        if (Server.isDebug()) return 5;
        int id = npc.getNpcId();

        for (var boxTrap : BoxTrap.BoxTrapData.values())
            if (id == boxTrap.getNpcId())
                return boxTrap.getRespawn();

        for (var birdSnare : BirdSnare.BirdData.values())
            if (id == birdSnare.getNpcId())
                return birdSnare.getRespawn();

        return switch (id) {
            case Npcs.SARACHNIS, 492 -> 20;
            case 6600, 6601, 6602, 320, 1049, 6617, 3118, 3120, 6768, Npcs.SKELETON_HELLHOUND, 2402, 2401, 2400, 2399, 5916, 7604, 7605, 7606, 7585,
                 5129, FragmentOfSeren.FRAGMENT_ID, FragmentOfSeren.NPC_ID, FragmentOfSeren.CRYSTAL_WHIRLWIND, TheUnbearable.NPC_ID, 7563, 7573, 7544,
                 7566, 7559, 7553, 7554, 7555, 7560, 7527, 7528, 7529, 5001, 6477, 5462, 7858, 7859 -> -1;
            case 5862 -> //cerberus
                15;//anti-santa
            case 963, 965 -> 10;
            case 8609, 6611, 6612, 8781 -> 40;
            case 6618, 6619, 319, 5890 -> 30;
            case 1046, 465 -> 60;
            case 6609 ->//callisto
                40;
            case 2265, 2266, 2267 -> 70;
            case 2558, 2559, 2560, 2561, 2562, 2563, 2564, 2205, 2215, 3129, 3162, 1641, 1642 -> 100;
            case 1643 -> 180;
            case 1654 -> 250;
            case 2216, 2217, 2218, 3163, 3164, 3165, 2206, 2207, 2208, 3130, 3131, 3132 -> 40;
            case 3777, 3778, 3779, 3780, 7302 -> 500;
            default -> 35;
        };
    }
}
