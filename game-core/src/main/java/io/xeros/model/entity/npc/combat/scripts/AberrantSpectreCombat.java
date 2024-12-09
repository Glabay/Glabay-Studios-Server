package io.xeros.model.entity.npc.combat.scripts;

import io.xeros.Server;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.combat.CommonCombatMethod;
import io.xeros.model.entity.player.Player;
import io.xeros.model.projectile.ProjectileEntity;

public class AberrantSpectreCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        var tileDist = entity.getCenterPosition().getManhattanDistance(target.getCenterPosition());
        int duration = 51 + -5 + (10 * tileDist);
        this.build(Server.random.inclusive(0,10), 1507, CombatType.MAGE).consume(builder -> {
            var projectile = new ProjectileEntity(entity, target, 336, 51, duration, 43, 31, 1, 10, true);
                projectile.sendProjectile();
            int speed = projectile.getSpeed();
            builder.setDelay((int) (speed / 30D));
        }).execute();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.attackTimer;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }

}