package io.xeros.content.combat;

import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 1:56 PM
 */
public class CombatConfigs {

    /**
     * Retrieves the combat style for the Npc
     * @param definition The npc definition
     * @return The combat style for the Npc
     */
    private static CombatStyle getCombatStyleForNpc(NpcCombatDefinition definition) {
        return switch (definition.getAttackStyle()) {
            case "Stab" -> CombatStyle.STAB;
            case "Slash" -> CombatStyle.SLASH;
            case "Crush" -> CombatStyle.CRUSH;
            case "Ranged" -> CombatStyle.RANGE;
            case "Magic" -> CombatStyle.MAGIC;
            default -> CombatStyle.SPECIAL;
        };
    }

    /**
     * Retrieves the Combat style for the different entity types
     * @param entity The entity whose Combat style is being checked
     * @return The combat style for the entity type
     */
    public static CombatStyle getCombatStyle(Entity entity) {
        if (entity instanceof NPC n) {
            var definition = NpcCombatDefinition.definitions.get(n.getNpcId());
            if (definition != null) {
                return getCombatStyleForNpc(definition);
            }
            return CombatStyle.SPECIAL;
        }
        else if (entity instanceof Player player) {
            return switch (getCombatType(player)) {
                case MAGE -> CombatStyle.MAGIC;
                case RANGE -> CombatStyle.RANGE;
                default -> player.getCombatConfigs().getWeaponMode().getCombatStyle();
            };
        }
        else throw new IllegalArgumentException("You cannot use that entity type here. ");
    }

    /**
     * Determines the combat type for the player
     * @param player The player
     * @return The combat type of the player
     */
    public static CombatType getCombatType(Player player) {
        if (player.usingMagic)
            return CombatType.MAGE;
        else if (player.usingBow || player.usingOtherRangeWeapons || player.usingCross || player.usingBallista)
            return CombatType.RANGE;
        else
            return CombatType.MELEE;
    }

}
