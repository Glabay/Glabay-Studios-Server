package io.xeros.content.minigames.tob.bosses;

import com.google.common.collect.Lists;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobBoss;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class Nylocas extends TobBoss {

    private static final int MELEE_FORM = 8355;
    private static final int MAGIC_FORM = 8356;
    private static final int RANGE_FORM = 8357;

    private int transformTimer = 0;
    private long lastTransform;

    public Nylocas(InstancedArea instancedArea) {
        super(Npcs.NYLOCAS_VASILIAS_8355, new Position(3294, 4247, instancedArea.getHeight()), instancedArea);
        updateAutoAttack(CombatType.MELEE);
    }

    @Override
    public int getDeathAnimation() {
        if (getNpcId() == RANGE_FORM) {
            return 7998;
        }

        return 8005;
    }

    @Override
    public void process() {
        if (transformTimer++ >= 30) {
            transform();
            transformTimer = 0;
        }

        super.process();
    }

    private void transform() {
        lastTransform = System.currentTimeMillis();
        switch (getNpcId()) {
            case MELEE_FORM:
                requestTransform(MAGIC_FORM);
                updateAutoAttack(CombatType.MAGE);
                break;
            case MAGIC_FORM:
                requestTransform(RANGE_FORM);
                updateAutoAttack(CombatType.RANGE);
                break;
            case RANGE_FORM:
                requestTransform(MELEE_FORM);
                updateAutoAttack(CombatType.MELEE);
                break;
        }
    }

    private void updateAutoAttack(CombatType combatType) {
        int attackAnimation = 8004;
        if (combatType == CombatType.RANGE) {
            attackAnimation = 8001;
        }

        setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                .setAnimation(new Animation(attackAnimation))
                .setAttackDelay(4)
                .setMaxHit(40)
                .setCombatType(combatType)
                .setHitDelay(1)
                .createNPCAutoAttack()));
        maxHit = 70;
    }

    @Override
    public int modifyDamage(Player player, int damage) {
        switch (getNpcId()) {
            case MELEE_FORM:
                if (player.attacking.getCombatType() != CombatType.MELEE) {
                    reflect(player, damage);
                    return 0;
                }
                break;
            case MAGIC_FORM:
                if (player.attacking.getCombatType() != CombatType.MAGE) {
                    reflect(player, damage);
                    return 0;
                }
                break;
            case RANGE_FORM:
                if (player.attacking.getCombatType() != CombatType.RANGE) {
                    reflect(player, damage);
                    return 0;
                }
                break;
        }
        return damage;
    }

    private void reflect(Player player, int damage) {
        if (player.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
            return;
        if (damage > 0 && System.currentTimeMillis() - lastTransform > 1_000) {
            player.appendDamage(damage, Hitmark.HIT);
            getHealth().increase(damage);
            player.sendMessage("@red@Nylocas reflects your attack back at you and heals.");
        }
    }
}
