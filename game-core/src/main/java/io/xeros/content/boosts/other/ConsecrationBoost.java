package io.xeros.content.boosts.other;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class ConsecrationBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+5 PC Points (" + Misc.cyclesToDottedTime((int) Hespori.CONSECRATION_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.CONSECRATION_TIMER > 0;
    }
}
