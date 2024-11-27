package io.xeros.content.combat.effects.damageeffect;

import io.xeros.content.combat.effects.damageeffect.DamageEffect;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public interface DamageBoostingEffect extends DamageEffect {

    double getMaxHitBoost(Player attacker, Entity defender);

}
