package io.xeros.content.combat.npc;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.projectile.ProjectileBase;
import io.xeros.model.entity.player.Player;

public class NPCAutoAttackBuilder {
    private Animation animation;
    private ProjectileBase projectile;
    private Graphic startGraphic;
    private Graphic endGraphic;
    private CombatType combatType;
    private int hitDelay = 1;
    private int attackDelay = 4;
    private int maxHit;
    private int poisonDamage;
    private int distanceRequiredForAttack = 1;
    private boolean ignoreProjectileClipping;
    private boolean multiAttack;
    private Consumer<NPCCombatAttack> onAttack;
    private Consumer<NPCCombatAttackHit> onHit;
    private Function<NPCCombatAttack, Double> accuracyBonus;
    private Function<NPCCombatAttack, Double> maxHitBonus;
    private Function<NPCCombatAttack, Double> prayerProtectionPercentage;
    private Function<NPCCombatAttack, List<Player>> selectPlayersForMultiAttack;
    private Function<NPCCombatAttack, Boolean> selectAutoAttack;
    private boolean attackDamagesPlayer = true;
    private Function<NPCAutoAttackDamage, Integer> modifyDamage;

    public NPCAutoAttackBuilder setAnimation(Animation animation) {
        this.animation = animation;
        return this;
    }

    public NPCAutoAttackBuilder setProjectile(ProjectileBase projectile) {
        this.projectile = projectile;
        return this;
    }

    public NPCAutoAttackBuilder setStartGraphic(Graphic startGraphic) {
        this.startGraphic = startGraphic;
        return this;
    }

    public NPCAutoAttackBuilder setEndGraphic(Graphic endGraphic) {
        this.endGraphic = endGraphic;
        return this;
    }

    public NPCAutoAttackBuilder setCombatType(CombatType combatType) {
        this.combatType = combatType;
        return this;
    }

    public NPCAutoAttackBuilder setHitDelay(int hitDelay) {
        this.hitDelay = hitDelay;
        return this;
    }

    public NPCAutoAttackBuilder setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
        return this;
    }

    public NPCAutoAttackBuilder setMaxHit(int maxHit) {
        this.maxHit = maxHit;
        return this;
    }

    public NPCAutoAttackBuilder setPoisonDamage(int poisonDamage) {
        this.poisonDamage = poisonDamage;
        return this;
    }

    public NPCAutoAttackBuilder setDistanceRequiredForAttack(int distanceRequiredForAttack) {
        this.distanceRequiredForAttack = distanceRequiredForAttack;
        return this;
    }

    public NPCAutoAttackBuilder setIgnoreProjectileClipping(boolean ignoreProjectileClipping) {
        this.ignoreProjectileClipping = ignoreProjectileClipping;
        return this;
    }

    public NPCAutoAttackBuilder setMultiAttack(boolean multiAttack) {
        this.multiAttack = multiAttack;
        return this;
    }

    public NPCAutoAttackBuilder setOnAttack(Consumer<NPCCombatAttack> onAttack) {
        this.onAttack = onAttack;
        return this;
    }

    public NPCAutoAttackBuilder setOnHit(Consumer<NPCCombatAttackHit> onHit) {
        this.onHit = onHit;
        return this;
    }

    public NPCAutoAttackBuilder setAccuracyBonus(Function<NPCCombatAttack, Double> accuracyBonus) {
        this.accuracyBonus = accuracyBonus;
        return this;
    }

    public NPCAutoAttackBuilder setMaxHitBonus(Function<NPCCombatAttack, Double> maxHitBonus) {
        this.maxHitBonus = maxHitBonus;
        return this;
    }

    public NPCAutoAttackBuilder setPrayerProtectionPercentage(Function<NPCCombatAttack, Double> prayerProtectionPercentage) {
        this.prayerProtectionPercentage = prayerProtectionPercentage;
        return this;
    }

    public NPCAutoAttackBuilder setSelectPlayersForMultiAttack(Function<NPCCombatAttack, List<Player>> selectPlayersForMultiAttack) {
        this.selectPlayersForMultiAttack = selectPlayersForMultiAttack;
        return this;
    }

    public NPCAutoAttackBuilder setSelectAutoAttack(Function<NPCCombatAttack, Boolean> selectAutoAttack) {
        this.selectAutoAttack = selectAutoAttack;
        return this;
    }

    public NPCAutoAttackBuilder setAttackDamagesPlayer(boolean attackDamagesPlayer) {
        this.attackDamagesPlayer = attackDamagesPlayer;
        return this;
    }

    public NPCAutoAttackBuilder setModifyDamage(Function<NPCAutoAttackDamage, Integer> modifyDamage) {
        this.modifyDamage = modifyDamage;
        return this;
    }

    public NPCAutoAttack createNPCAutoAttack() {
        return new NPCAutoAttack(animation, projectile, startGraphic, endGraphic, combatType, hitDelay, attackDelay, maxHit, poisonDamage,
                distanceRequiredForAttack, ignoreProjectileClipping, multiAttack, onAttack, onHit, accuracyBonus, maxHitBonus, prayerProtectionPercentage,
                selectPlayersForMultiAttack, selectAutoAttack, attackDamagesPlayer, modifyDamage);
    }
}