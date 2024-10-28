package io.xeros.content.combat.core;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;

public class Hit {
    final Entity attacker, target;
    public int damage, delay;

    public Hit(Entity attacker, Entity target, int damage, int delay) {
        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
        this.delay = delay;
    }

    public Hit submit() {
        if (this.target == null) return this;
        Hitmark hitmark = this.damage > 0 ? Hitmark.HIT : Hitmark.MISS;
        Damage damage = new Damage(this.attacker, this.target, this.damage, this.delay, hitmark, CombatType.RANGE);
        damage.getTarget().appendDamage(damage.getAmount(), damage.getHitmark());
        System.out.println("delay: " + this.delay);
        System.out.println("adding damage: " + damage);
        return this;
    }
}
