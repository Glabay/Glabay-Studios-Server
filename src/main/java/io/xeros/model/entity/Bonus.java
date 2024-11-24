package io.xeros.model.entity;

import io.xeros.content.combat.weapon.CombatStyle;

public enum Bonus {

	ATTACK_STAB, ATTACK_SLASH, ATTACK_CRUSH, ATTACK_MAGIC, ATTACK_RANGED,
	DEFENCE_STAB, DEFENCE_SLASH, DEFENCE_CRUSH, DEFENCE_MAGIC, DEFENCE_RANGED,
	STRENGTH, 
	RANGED_STRENGTH,
	MAGIC_DMG,
	PRAYER;

	public static Bonus attackBonusForCombatStyle(CombatStyle combatStyle) {
        return switch (combatStyle) {
            case SLASH -> ATTACK_SLASH;
            case CRUSH -> ATTACK_CRUSH;
            case MAGIC, SPECIAL -> ATTACK_MAGIC;
            case RANGE -> ATTACK_RANGED;
            default -> ATTACK_STAB;
        };
	}
	
}