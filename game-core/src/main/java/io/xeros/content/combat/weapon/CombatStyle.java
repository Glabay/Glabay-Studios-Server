package io.xeros.content.combat.weapon;

import io.xeros.util.Misc;

public enum CombatStyle {
    STAB, SLASH, CRUSH, MAGIC, RANGE, SPECIAL;

    @Override
    public String toString() {
        return Misc.formatPlayerName(name().toLowerCase());
    }
}
