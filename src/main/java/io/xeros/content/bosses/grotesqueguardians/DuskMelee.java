package io.xeros.content.bosses.grotesqueguardians;

import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;

import java.util.function.Function;

public class DuskMelee implements Function<GrotesqueGuardianNpc, NPCAutoAttack> {

    @Override
    public NPCAutoAttack apply(GrotesqueGuardianNpc nightmare) {
        int anim = 7785;
        if (nightmare.getInstance() != null) {
            if (((GrotesqueInstance) nightmare.getInstance()).phase == 4) {
                anim = 7800;
            }
        }
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(anim))
                .setCombatType(CombatType.MELEE)
                .setMaxHit(15)
                .setHitDelay(2)
                .setAttackDelay(6)
                .setDistanceRequiredForAttack(1)
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.2d;
                    }
                })
                .createNPCAutoAttack();
    }
}