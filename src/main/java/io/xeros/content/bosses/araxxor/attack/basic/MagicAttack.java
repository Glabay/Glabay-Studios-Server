package io.xeros.content.bosses.araxxor.attack.basic;

import io.xeros.Server;
import io.xeros.content.bosses.araxxor.Araxxor;
import io.xeros.content.bosses.araxxor.attack.Attack;
import io.xeros.model.CombatType;
import io.xeros.model.entity.npc.combat.HitBuilder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.projectile.ProjectileEntity;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class MagicAttack implements Attack {
    @Override
    public void invoke(Araxxor araxxor, Player target) {
        if (target == null || target.isDead()) return;
        var tile = araxxor.getCenterPosition().translate(3, 3);
        var tileDist = tile.getManhattanDistance(target.getCenterPosition());
        var duration = (tileDist * 2) + 25;
        new HitBuilder(araxxor, target, Server.random.inclusive(0, 21), CombatType.MAGE)
            .consume(builder -> {
            var projectile = new ProjectileEntity(
                araxxor,
                target,
                2934,
                25,
                duration,
                37,
                22,
                14,
                4,
                48,
                2
            );
            projectile.sendProjectile();
            int speed = projectile.getSpeed();
            builder.setDelay((int) (speed / 30D));
        }).execute();
    }
}
