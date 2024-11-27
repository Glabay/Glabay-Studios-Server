package io.xeros.content.skills.runecrafting.ouriana;

import java.util.Arrays;

import com.google.common.collect.Lists;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.autoattacks.MagicAirStrike;
import io.xeros.model.entity.npc.autoattacks.MeleeSwordSwing;
import io.xeros.model.entity.npc.autoattacks.RangedShootArrow;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class ZamorakGuardian extends NPC {

    private static final int[] MAGES = {7422, 7423};
    private static final int[] WARRIORS = {7418, 7419};
    private static final int[] RANGERS = {7420, 7421};

    private static final Position[] SPAWN_POSITIONS = {
            new Position(3015, 5581, 0),
            new Position(3017, 5575, 0),
            new Position(3014, 5575, 0),
            new Position(3022, 5577, 0),
            new Position(3029, 5573, 0),
            new Position(3033, 5576, 0),
            new Position(3033, 5572, 0),
            new Position(3034, 5581, 0),
            new Position(3043, 5578, 0),
            new Position(3046, 5575, 0),
            new Position(3018, 5583, 0),
            new Position(3032, 5580, 0),
    };

    public static void spawn() {
        for (Position position : SPAWN_POSITIONS) {
            new ZamorakGuardian(position);
        }
    }

    private static int getRandomNpc() {
        int rand = Misc.trueRand(3);
        if (rand == 0) {
            return MAGES[Misc.trueRand(MAGES.length)];
        } else if (rand == 1) {
            return WARRIORS[Misc.trueRand(WARRIORS.length)];
        } else {
            return RANGERS[Misc.trueRand(RANGERS.length)];
        }
    }

    public ZamorakGuardian(Position position) {
        super(getRandomNpc(), position);
        if (Arrays.stream(MAGES).anyMatch(id -> id == getNpcId())) {
            setNpcAutoAttacks(Lists.newArrayList(new MagicAirStrike(4).createNPCAutoAttack()));
        } else if (Arrays.stream(WARRIORS).anyMatch(id -> id == getNpcId())) {
            setNpcAutoAttacks(Lists.newArrayList(new MeleeSwordSwing(4).createNPCAutoAttack()));
        } else {
            setNpcAutoAttacks(Lists.newArrayList(new RangedShootArrow(4).createNPCAutoAttack()));
        }
    }

    @Override
    public NPC provideRespawnInstance() {
        return new ZamorakGuardian(new Position(makeX, makeY));
    }
}
