package io.xeros.content.boosts.other;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class IasorBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+10% Drop Rate (" + Misc.cyclesToDottedTime((int) Hespori.IASOR_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.IASOR_TIMER > 0;
    }
}
