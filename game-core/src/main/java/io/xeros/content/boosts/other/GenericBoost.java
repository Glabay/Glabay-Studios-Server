package io.xeros.content.boosts.other;

import io.xeros.content.boosts.BoostType;
import io.xeros.content.boosts.Booster;
import io.xeros.model.entity.player.Player;

public abstract class GenericBoost implements Booster<Player> {
    @Override
    public BoostType getType() {
        return BoostType.GENERIC;
    }
}
