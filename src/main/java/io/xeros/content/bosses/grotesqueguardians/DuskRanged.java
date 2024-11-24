package io.xeros.content.bosses.grotesqueguardians;

import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.projectile.ProjectileBase;
import io.xeros.model.projectile.ProjectileBaseBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

public class DuskRanged implements Function<GrotesqueGuardianNpc, NPCAutoAttack> {

    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setSendDelay(2)
                .setSpeed(30)
                .setStartHeight(90)
                .setProjectileId(1444)
                .createProjectileBase();
    }

    @Override
    public NPCAutoAttack apply(GrotesqueGuardianNpc nightmare) {
        NPCAutoAttack second = new NPCAutoAttackBuilder()
                .setAnimation(new Animation(7801))
                .setCombatType(CombatType.RANGE)
                .setMaxHit(9)
                .setHitDelay(4)
                .setAttackDelay(6)
                .setDistanceRequiredForAttack(24)
                .setMultiAttack(false)
                .setPrayerProtectionPercentage(npcCombatAttack -> 0.3)
                .setProjectile(new ProjectileBaseBuilder()
                        .setSendDelay(3)
                        .setSpeed(30)
                        .setStartHeight(90)
                        .setProjectileId(1444)
                        .createProjectileBase())
                .createNPCAutoAttack();
        Consumer<NPCCombatAttack> onAttack = t -> {
            if (nightmare.inPhase(4)) {
                nightmare.attack(nightmare.target, second);
            }
        };
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(7801))
                .setCombatType(CombatType.RANGE)
                .setMaxHit(9)
                .setHitDelay(3)
                .setAttackDelay(6)
                .setDistanceRequiredForAttack(24)
                .setMultiAttack(false)
                .setOnAttack(onAttack)
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.3;
                    }
                })
                .setProjectile(projectile())
                .createNPCAutoAttack();
    }
}
