package io.xeros.model.entity.npc.combat.scripts;

import io.xeros.Server;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.combat.CommonCombatMethod;
import io.xeros.model.entity.player.Player;
import io.xeros.model.projectile.ProjectileEntity;
import lombok.NonNull;

public class CallistoCombat extends CommonCombatMethod {

    private int roarCount = 0;
    private int trapState = 0;
    public boolean performingAnimation = false;

    @Override
    public int moveCloseToTargetTileRange(@NonNull final Entity entity) {
        return 2;
    }

    @Override
    public int getAttackSpeed(@NonNull final Entity entity) {
        return 0;
    }

    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        if (isReachable()) {
            if (Server.random.rollDie(3, 1)) {
                rangeAttack(entity);
            } else magicAttack(entity);
        } else meleeAttack();
      /*  trapState++;
        double hpPercentage = ((double) entity.getHealth().getCurrentHealth() / entity.getHealth().getMaximumHealth());
        if (hpPercentage <= .66 && trapState == Server.random.inclusive(1, 2) || hpPercentage <= .66 && roarCount == 0) {
            entity.startAnimation(new Animation(10015));
            entity.startGraphic(new Graphic(2352));
        }
        if (hpPercentage <= .33 && trapState == Server.random.inclusive(1, 2) || hpPercentage <= .33 && roarCount == 1) {
            entity.startAnimation(new Animation(10015));
            entity.startGraphic(new Graphic(2352));
        }*/
        return true;
    }

    private void meleeAttack() {
        this.build(Server.random.inclusive(0, 35), 10012, CombatType.MELEE).execute();
    }

    private void rangeAttack(@NonNull final Entity entity) {
        this.build(Server.random.inclusive(0, 35), 10013, CombatType.RANGE).consume(builder -> {
            for (var t : getPossibleTargets(entity)) {
                int tileDist = entity.getCenterPosition().getManhattanDistance(t.getCenterPosition());
                int duration = (25 + 10 + (10 * tileDist));
                ProjectileEntity p = new ProjectileEntity(entity, t, 2350, 25, duration, 20, 20, 0, entity.getEntitySize(), 10, true);
                p.sendProjectile();
                builder.setDelay((int) (p.getSpeed() / 30D));
                builder.setEndGraphic(2351, p.getSpeed(), 50);
            }
        }).execute();
    }

    private void magicAttack(@NonNull final Entity entity) {
        this.build(Server.random.inclusive(0, 35), 10014, CombatType.MAGE).consume(builder -> {
            for (var t : getPossibleTargets(entity)) {
                int tileDist = entity.getCenterPosition().getManhattanDistance(t.getCenterPosition());
                int duration = (55 + 10 + (10 * tileDist));
                ProjectileEntity p = new ProjectileEntity(entity, t, 133, 55, duration, 50, 31, 0, 5, 10, true);
                p.sendProjectile();
                builder.setDelay((int) (p.getSpeed() / 30D));
                builder.setEndGraphic(134, p.getSpeed(), 50);
            }
        }).execute();
    }
}