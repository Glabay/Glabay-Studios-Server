package io.xeros.model.entity.npc.combat.scripts;

import io.xeros.Server;
import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.combat.CommonCombatMethod;
import io.xeros.model.entity.npc.combat.HitBuilder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.projectile.ProjectileEntity;
import lombok.NonNull;

import javax.annotation.Nonnull;

public class VenenatisCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        if (isReachable()) {
            meleeAttack(entity, target);
        } else {
            if (Server.random.rollDie(3, 1)) rangeAttack(entity, target);
            else magicAttack(entity, target);
        }
        return true;
    }

    public void meleeAttack(@NonNull final Entity entity, @NonNull Entity target) {
        entity.startAnimation(new Animation(9991));
        new HitBuilder(entity, target, Server.random.inclusive(0, 25), CombatType.RANGE).submit();
    }

    public void magicAttack(@Nonnull final Entity entity, @Nonnull Entity target) {
        var tile = entity.getCenterPosition().translate(4, 4);
        var tileDist = tile.getManhattanDistance(target.getCenterPosition());
        var duration = (tileDist * 2) + 25;
        this.build(Server.random.inclusive(0, 25), 9990, CombatType.MAGE).consume(builder -> {
            ProjectileEntity projectile = new ProjectileEntity(entity, target, 2358, 25, duration, 37, 22, 14, 4, 48, 2);
            projectile.sendProjectile();
            int speed = projectile.getSpeed();
            builder.setDelay((int) (speed / 30D));
            builder.setEndGraphic(2359, speed, 50);
        }).execute();
    }

    private void rangeAttack(@Nonnull final Entity entity, @Nonnull Entity target) {
        var tile = entity.getCenterPosition().translate(4, 4);
        var tileDist = tile.getManhattanDistance(target.getCenterPosition());
        var duration = (tileDist * 2) + 25;
        this.build(Server.random.inclusive(0, 25), 9989, CombatType.RANGE).consume(builder -> {
            ProjectileEntity projectile = new ProjectileEntity(entity, target, 2356, 25, duration, 37, 22, 14, 4, 2, true);
            projectile.sendProjectile();
            int speed = projectile.getSpeed();
            builder.setDelay((int) (speed / 30D));
            builder.setEndGraphic(2357, speed, 50);
        }).execute();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}
