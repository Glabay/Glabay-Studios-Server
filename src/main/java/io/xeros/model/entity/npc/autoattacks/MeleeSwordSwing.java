package io.xeros.model.entity.npc.autoattacks;

import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;

public class MeleeSwordSwing extends NPCAutoAttackBuilder {

    public MeleeSwordSwing(int maxHit) {
        setAttackDelay(4);
        setMaxHit(maxHit);
        setAnimation(new Animation(451));
        setCombatType(CombatType.MELEE);
    }
}
