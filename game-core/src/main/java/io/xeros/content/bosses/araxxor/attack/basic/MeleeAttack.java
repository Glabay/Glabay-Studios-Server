package io.xeros.content.bosses.araxxor.attack.basic;

import io.xeros.Server;
import io.xeros.content.bosses.araxxor.Araxxor;
import io.xeros.content.bosses.araxxor.attack.Attack;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.npc.combat.HitBuilder;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class MeleeAttack implements Attack {
    @Override
    public void invoke(Araxxor araxxor, Player target) {
        if (target == null || target.isDead()) return;
        araxxor.startAnimation(11480);
        new HitBuilder(araxxor, target, Server.random.inclusive(0, 38), CombatType.MELEE).submit();
    }
}
