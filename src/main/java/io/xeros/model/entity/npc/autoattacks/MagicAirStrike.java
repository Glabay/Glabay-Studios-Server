package io.xeros.model.entity.npc.autoattacks;

import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.projectile.ProjectileBaseBuilder;

public class MagicAirStrike extends NPCAutoAttackBuilder {

    public MagicAirStrike(int maxHit) {
        setAttackDelay(5);
        setMaxHit(maxHit);
        setAnimation(new Animation(711));
        setCombatType(CombatType.RANGE);
        setDistanceRequiredForAttack(10);
        setHitDelay(3);
        setStartGraphic(new Graphic(90, Graphic.GraphicHeight.MIDDLE));
        setEndGraphic(new Graphic(92, Graphic.GraphicHeight.MIDDLE));
        setProjectile(new ProjectileBaseBuilder().setSendDelay(3).setProjectileId(91).createProjectileBase());
    }
}
