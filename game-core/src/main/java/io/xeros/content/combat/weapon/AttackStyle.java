package io.xeros.content.combat.weapon;

import com.google.common.base.Preconditions;

public enum AttackStyle {
    ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE;

    /**
     * Fetches the AttackStyle for the Attack style
     * @param style
     * @return
     */
    public static AttackStyle forStyle(int style) {
        return AttackStyle.values()[style];
    }

}
