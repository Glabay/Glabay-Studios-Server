package io.xeros.content.combat.core;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;

public class Hit {
    final Entity attacker, target;
    public Hitmark hitType;
    public int damage, delay;

    public Hit(Entity attacker, Entity target, int damage, Hitmark hitType) {
        this(attacker, target, damage, 0);
        this.hitType = hitType;
    }

    public Hit(Entity attacker, Entity target, int damage, int delay) {
        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
        this.delay = delay;
        this.hitType = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
    }

    public Hit submit() {
        if (this.target == null) return this;
        Damage damage = new Damage(this.attacker, this.target, this.damage, this.delay, hitType, CombatType.RANGE);
        damage.getTarget().appendDamage(damage.getAmount(), damage.getHitmark());
        System.out.println("delay: " + this.delay);
        System.out.println("adding damage: " + damage);
        return this;
    }
}
