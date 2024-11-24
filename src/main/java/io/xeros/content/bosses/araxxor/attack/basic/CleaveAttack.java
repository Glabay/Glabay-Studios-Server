package io.xeros.content.bosses.araxxor.attack.basic;

import io.xeros.Server;
import io.xeros.content.bosses.araxxor.Araxxor;
import io.xeros.content.bosses.araxxor.attack.Attack;
import io.xeros.model.CombatType;
import io.xeros.model.entity.npc.combat.HitBuilder;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class CleaveAttack implements Attack {
    @Override
    public void invoke(Araxxor araxxor, Player target) {
        if (target == null || target.isDead()) return;
        araxxor.forceChat("Skree!");
        araxxor.startAnimation(11483);
        new HitBuilder(araxxor, target, Server.random.inclusive(0, 38), CombatType.MELEE).submit();
    }
}
