package io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned;

import io.xeros.content.combat.Damage;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.EquipmentSet;

/**
 * @author Arthur Behesnilian 12:33 PM
 */
public interface AmuletOfTheDamnedEffect {

    /**
     * Determines whether the {@link} Player can use the effect associated
     * @param player The {@link Player} to be checked
     * @return True if the effect can be used
     */
    default boolean canUseEffect(Player player) {
        return EquipmentSet.AMULET_OF_THE_DAMNED.isWearing(player) && hasExtraRequirement(player);
    }

    /**
     * Checks if the {@link Player} has the extra requirements to process this effect
     * @param player The {@link Player} to be checked
     * @return True if the effect can be used
     */
    boolean hasExtraRequirement(Player player);

    /**
     * Activates the effect that is associated
     * @param player The {@link Player} that is wearing the Amulet
     * @param other The {@link Entity} that is interacting with the {@link Player} wearing the amulet
     * @param damage The {@link Damage} coming from the attack
     */
    void useEffect(Player player, Entity other, Damage damage);

}
