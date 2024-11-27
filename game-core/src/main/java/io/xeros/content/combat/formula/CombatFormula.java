package io.xeros.content.combat.formula;

import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import static io.xeros.content.combat.melee.CombatPrayer.*;

public class CombatFormula {

    /**
     * Effective level boost is what is added to the effective level
     * of all combat styles (attack, ranged, magic), usually it's 8.
     * Set to 24 to boost accuracy in general.
     */
    public static final int EFFECTIVE_LEVEL_BOOST = 24;

    public static int getEffectLevel(int level, int bonus) {
        return (int) Math.floor(level * (1.0 + (bonus / 64d)));
    }

    public static boolean rollAccuracy(double attackRoll, double defenceRoll) {
        return Misc.trueRand(100) <= getHitChance(attackRoll, defenceRoll);
    }

    public static int getHitChance(double attackRoll, double defenceRoll) {
        double accuracy;
        if (attackRoll > defenceRoll) {
            accuracy = 1 - (defenceRoll + 2) / (2 * (attackRoll + 1));
        } else {
            accuracy = attackRoll / (2 * (defenceRoll + 1));
        }
        return (int) Math.floor(accuracy * 100);
    }

    public static int getPrayerBoostedLevel(int currentLevel, double prayerBonus) {
        return (int) Math.floor((double) currentLevel * prayerBonus);
    }

    public static int getPrayerBoostedDefenceLevel(Player c) {
        return CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.DEFENCE), CombatFormula.getPrayerDefenceBonus(c));
    }

    public static double getPrayerMagicAccuracyBonus(Player c) {
        return c.prayerActive[AUGURY] ? 1.25
                : c.prayerActive[MYSTIC_WILL] ? 1.05
                : c.prayerActive[MYSTIC_LORE] ? 1.10
                : c.prayerActive[MYSTIC_MIGHT] ? 1.15
                : 1.0;
    }

    public static double getPrayerDefenceBonus(Player c) {
        return c.prayerActive[THICK_SKIN] ? 1.05
                : c.prayerActive[ROCK_SKIN] ? 1.10
                : c.prayerActive[STEEL_SKIN] ? 1.15
                : c.prayerActive[CHIVALRY] ? 1.20
                : c.prayerActive[PIETY] ? 1.25
                : c.prayerActive[AUGURY] ? 1.25
                : c.prayerActive[RIGOUR] ? 1.25
                : 1.0;
    }

    public static double getPrayerStrengthBonus(Player c) {
        if (c.prayerActive[BURST_OF_STRENGTH])
            return 1.05;
        else if (c.prayerActive[SUPERHUMAN_STRENGTH])
            return 1.1;
        else if (c.prayerActive[ULTIMATE_STRENGTH])
            return 1.15;
        else if (c.prayerActive[CHIVALRY])
            return 1.18;
        else if (c.prayerActive[PIETY])
            return 1.23;
        return 1;
    }

    public static double getPrayerMeleeAttackBonus(Player c) {
        if (c.prayerActive[CLARITY_OF_THOUGHT]) {
           return 1.05;
        } else if (c.prayerActive[IMPROVED_REFLEXES]) {
            return 1.1;
        } else if (c.prayerActive[INCREDIBLE_REFLEXES]) {
            return 1.15;
        } else if (c.prayerActive[CHIVALRY]) {
            return 1.15;
        } else if (c.prayerActive[PIETY]) {
            return 1.2;
        }

        return 1.0;
    }

    public static double getPrayerRangedAccuracyBonus(Player c) {
        if (c.prayerActive[SHARP_EYE]) {
            return 1.05;
        } else if (c.prayerActive[HAWK_EYE]) {
            return 1.10;
        } else if (c.prayerActive[EAGLE_EYE]) {
            return 1.15;
        } else if (c.prayerActive[RIGOUR]) {
            return 1.20;
        }

        return 1;
    }

    public static double getPrayerRangedStrengthBonus(Player c) {
        if (c.prayerActive[SHARP_EYE]) {
            return 1.05;
        } else if (c.prayerActive[HAWK_EYE]) {
            return 1.10;
        } else if (c.prayerActive[EAGLE_EYE]) {
            return 1.15;
        } else if (c.prayerActive[RIGOUR]) {
            return 1.23;
        }

        return 1;
    }
}
