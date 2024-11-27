package io.xeros.content.bosses.nightmare.phase;

import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareAttack;
import io.xeros.content.bosses.nightmare.NightmarePhase;
import io.xeros.content.bosses.nightmare.NightmareStatus;
import io.xeros.content.bosses.nightmare.attack.GraspingClaws;
import io.xeros.content.bosses.nightmare.attack.Spores;
import io.xeros.content.bosses.nightmare.attack.Surge;

public class Phase3 implements NightmarePhase {


    @Override
    public void start(Nightmare nightmare) {

    }

    @Override
    public NightmareStatus getStatus() {
        return NightmareStatus.PHASE_3;
    }

    @Override
    public NightmareAttack[] getAttacks() {
        return new NightmareAttack[] { new GraspingClaws(), new Spores(), new Surge() };
    }
}
