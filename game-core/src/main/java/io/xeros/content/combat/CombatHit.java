package io.xeros.content.combat;

import lombok.Getter;

@Getter
public class CombatHit {

    public static CombatHit miss() {
        return new CombatHit(false, 0);
    }

    private final boolean success;
    private final int damage;

    public CombatHit(boolean success, int damage) {
        this.success = success;
        this.damage = damage;
    }

    public boolean missed() {
        return !success;
    }

}