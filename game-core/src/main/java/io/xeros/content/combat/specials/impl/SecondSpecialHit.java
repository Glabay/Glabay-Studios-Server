package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 6:01 PM
 */
public class SecondSpecialHit extends Special {

    public static SecondSpecialHit DRAGON_DAGGER_HIT_2 = new SecondSpecialHit(0, 1.15, 1.15, new int[] { 1215, 1231, 5680, 5698 });
    public static SecondSpecialHit ABYSSAL_DAGGER_HIT_2 = new SecondSpecialHit(0, 1.25, 0.85, new int[] { 1215, 1231, 5680, 5698 });
    public static SecondSpecialHit MAGIC_SHORTBOW_HIT_2 = new SecondSpecialHit(0, 1.43, 1.00, new int[] { 859, 861 });

    /**
     * Creates a new special attack for a particular weapon or set of weapons
     *
     * @param cost           the cost of {@link Player#specAmount} when activated
     * @param accuracy       the accuracy of the weapon
     * @param damageModifier the amount that any and all damage will be modified by
     * @param weapon         the weapons that activate this special
     */
    public SecondSpecialHit(double cost, double accuracy, double damageModifier, int[] weapon) {
        super(cost, accuracy, damageModifier, weapon);
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
