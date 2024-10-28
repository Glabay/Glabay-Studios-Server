package io.xeros.model.entity.npc.combat;

import io.xeros.content.combat.core.HitExecutor;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public interface CombatMethod {
    boolean prepareAttack(Entity entity, Player target);

    int getAttackSpeed(Entity entity);

    int moveCloseToTargetTileRange(Entity entity);

    default boolean customOnDeath(HitExecutor hit) {
        return false;
    }

    default boolean canMultiAttackInSingleZones() {
        return false;
    }

    default boolean ignoreEntityInteraction() {
        return false;
    }

    default HitBuilder build(Entity entity, Entity target, int damage, int animation, CombatType combatType) {
        return new HitBuilder(entity, target, damage, combatType).setAnimation(animation);
    }

    default boolean isReachable(Entity entity, Entity target) {
        double distanceBetweenCenters = calculateDistanceBetweenCenters(entity, target);
        double maxAllowedDistance = calculateMaxAllowedDistance(entity, target);
        return Math.round(distanceBetweenCenters) <= Math.round(maxAllowedDistance);
    }

    private double calculateDistanceBetweenCenters(Entity entity, Entity target) {
        double entityCenterX = entity.getPosition().getX() + entity.getEntitySize() / 2.0;
        double entityCenterY = entity.getPosition().getY() + entity.getEntitySize() / 2.0;
        double targetCenterX = target.getPosition().getX() + target.getEntitySize() / 2.0;
        double targetCenterY = target.getPosition().getY() + target.getEntitySize() / 2.0;
        double dx = entityCenterX - targetCenterX;
        double dy = entityCenterY - targetCenterY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double calculateMaxAllowedDistance(Entity entity, Entity target) {
        return (double) entity.getEntitySize() / 2 + (double) target.getEntitySize() / 2;
    }

}
