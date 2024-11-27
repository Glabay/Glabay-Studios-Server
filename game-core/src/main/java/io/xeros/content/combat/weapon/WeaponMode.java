package io.xeros.content.combat.weapon;

import java.util.Objects;

public class WeaponMode {

    private final int index;
    private final AttackStyle attackStyle;
    private final CombatStyle combatStyle;

    public WeaponMode(int index, AttackStyle attackStyle, CombatStyle combatStyle) {
        this.index = index;
        this.attackStyle = attackStyle;
        this.combatStyle = combatStyle;
    }

    public WeaponMode(int index, AttackStyle attackStyle) {
        this.index = index;
        this.attackStyle = attackStyle;
        this.combatStyle = null;
    }

    @Override
    public String toString() {
        return "{" +
                "" + index +
                ", " + attackStyle +
                ", " + combatStyle +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeaponMode that = (WeaponMode) o;
        return index == that.index &&
                attackStyle == that.attackStyle &&
                combatStyle == that.combatStyle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, attackStyle, combatStyle);
    }

    public int getIndex() {
        return index;
    }

    public AttackStyle getAttackStyle() {
        return attackStyle;
    }

    public CombatStyle getCombatStyle() {
        return combatStyle;
    }
}
