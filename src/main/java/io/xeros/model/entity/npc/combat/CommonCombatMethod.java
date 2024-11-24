package io.xeros.model.entity.npc.combat;

import io.xeros.content.combat.core.HitExecutor;
import io.xeros.content.commands.owner.Npc;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@CombatScript
public abstract class CommonCombatMethod implements CombatMethod {

    public Entity entity, target;

    public void set(Entity entity, Entity target) {
        this.entity = entity;
        this.target = target;
    }

    public HitBuilder build(int damage, int animation, CombatType combatType) {
        return new HitBuilder(this.entity, this.target, damage, combatType).setAnimation(animation);
    }

    public void onRespawn(Npc npc) {

    }

    protected boolean isInsideCombatBoundary() {
        var center = calculateDistanceBetweenCenters();
        return center > calculateCombatBoundary();
    }
    protected double calculateCombatBoundary() {
        double maxAllowedDistance = Math.ceil(calculateMaxAllowedDistance());
        double maximumReach = maxAllowedDistance * maxAllowedDistance;
        return maximumReach * maximumReach;
    }

    protected boolean withinDistance() {
        double maxAllowedDistance = Math.ceil(calculateMaxAllowedDistance());
        double maximumReach = maxAllowedDistance * maxAllowedDistance;
        double distanceBetweenCenters = Math.floor(calculateDistanceBetweenCenters());
        return distanceBetweenCenters <= maximumReach;
    }

    protected boolean isReachable() {
        double distanceBetweenCenters = calculateDistanceBetweenCenters();
        double maxAllowedDistance = calculateMaxAllowedDistance();
        return Math.round(distanceBetweenCenters) <= Math.round(maxAllowedDistance);
    }

    private double calculateDistanceBetweenCenters() {
        double entityCenterX = entity.getPosition().getX() + entity.getEntitySize() / 2.0;
        double entityCenterY = entity.getPosition().getY() + entity.getEntitySize() / 2.0;
        double targetCenterX = target.getPosition().getX() + target.getEntitySize() / 2.0;
        double targetCenterY = target.getPosition().getY() + target.getEntitySize() / 2.0;
        double dx = entityCenterX - targetCenterX;
        double dy = entityCenterY - targetCenterY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double calculateMaxAllowedDistance() {
        return (double) entity.getEntitySize() / 2 + (double) target.getEntitySize() / 2;
    }


    public void process(Entity entity, @Nullable Entity target) {

    }

    /**
     * npc only
     */
    public void onDeath(@Nullable Player killer, Npc npc) {

    }

    public void postDefend(HitExecutor hit) {

    }

    public void postTargetDefend(HitExecutor hit, Entity mob) {

    }

    /**
     * npc only
     */
    public void postDamage(HitExecutor hit) {

    }

    /**
     * npc only
     */
    public void preDefend(HitExecutor hit) {
    }

    /**
     * Handler functions
     */

    public void init(NPC npc) {

    }

    public List<Entity> getPossibleTargets(Entity entity) {
        return getPossibleTargets(entity, 14, true, false);
    }

    public List<Entity> getPossibleTargets(Entity entity, int ratio, boolean players, boolean npcs) {
        List<Entity> possibleTargets = new ArrayList<>();
        if (players) {
            for (Player player : PlayerHandler.players) {
                if (player == null || player.isDead() || player.getPosition().getAbsDistance(entity.getCenterPosition()) > ratio || player.getPosition().getHeight() != entity.getPosition().getHeight()) {
                    continue;
                }
                possibleTargets.add(player);
            }
        }
        if (npcs) {
            for (var npc : NPCHandler.npcs) {
                if (npc == null || npc == entity || npc.isDead() || npc.getCenterPosition().getAbsDistance(entity.getCenterPosition()) > ratio || npc.getPosition().getHeight() != entity.getPosition().getHeight()) {
                    continue;
                }
                possibleTargets.add(npc);
            }
        }
        return possibleTargets;
    }
}
