package io.xeros.content.boosts.other;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class KronosBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Raids Keys (" + Misc.cyclesToDottedTime((int) Hespori.KRONOS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.KRONOS_TIMER > 0;
    }
}
