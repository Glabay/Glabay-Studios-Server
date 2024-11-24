package io.xeros.content.bosses.grotesqueguardians;

import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;
import io.xeros.model.projectile.ProjectileBase;
import io.xeros.model.projectile.ProjectileBaseBuilder;

import java.util.function.Function;

public class DawnRanged implements Function<GrotesqueGuardianNpc, NPCAutoAttack> {
    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setSendDelay(2)
                .setSpeed(30)
                .setStartHeight(90)
                .setProjectileId(1444)
                .createProjectileBase();
    }

    @Override
    public NPCAutoAttack apply(GrotesqueGuardianNpc dawn) {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(7770))
                .setCombatType(CombatType.RANGE)
                .setMaxHit(15)
                .setHitDelay(2)
                .setAttackDelay(6)
                .setDistanceRequiredForAttack(24)
                .setMultiAttack(false)
                .setPrayerProtectionPercentage(npcCombatAttack -> 0.3)
                .setProjectile(projectile())
                .createNPCAutoAttack();
    }
}
