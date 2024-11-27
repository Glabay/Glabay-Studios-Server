package io.xeros.content.boosts.xp;

import io.xeros.content.boosts.PlayerSkillWrapper;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.util.Misc;

public class AttasBoost extends ExperienceBooster {
    @Override
    public String getDescription() {
        return "+50% XP (" + Misc.cyclesToDottedTime((int) Hespori.ATTAS_TIMER) + ")";
    }

    @Override
    public boolean applied(PlayerSkillWrapper playerSkillWrapper) {
        return Hespori.ATTAS_TIMER > 0;
    }
}
