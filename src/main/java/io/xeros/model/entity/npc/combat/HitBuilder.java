package io.xeros.model.entity.npc.combat;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.rework.MagicCombatFormula;
import io.xeros.content.combat.formula.rework.MeleeCombatFormula;
import io.xeros.content.combat.formula.rework.RangeCombatFormula;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@Data
public class HitBuilder {
    @Nonnull
    final Entity attacker, defender;
    final CombatType combatType;
    Hitmark hitmark;
    public int damage, delay;
    boolean accurate, checkNegatedDamage, immune;
    @Nonnull
    private Consumer<HitBuilder> delayedExecutionConsumer;

    public HitBuilder(@NotNull final Entity attacker, @NotNull final Entity defender, int damage, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.combatType = combatType;
    }

    public HitBuilder(@NotNull final Entity attacker, @NotNull final Entity defender, int damage, CombatType combatType, boolean checkNegatedDamage) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.combatType = combatType;
        this.checkNegatedDamage = checkNegatedDamage;
    }

    public HitBuilder(@NotNull final Entity attacker, @NotNull final Entity defender, int damage, CombatType combatType, boolean checkNegatedDamage, boolean isImmune) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.combatType = combatType;
        this.checkNegatedDamage = checkNegatedDamage;
        this.immune = isImmune;
    }

    public HitBuilder setAnimation(int id) {
        final Animation animation = new Animation(id);
        this.attacker.startAnimation(animation);
        return this;
    }

    public HitBuilder setStartGraphic(int id, int speed, int height) {
        final Graphic graphic = new Graphic(id, speed, height);
        this.attacker.startGraphic(graphic);
        return this;
    }

    public HitBuilder setEndGraphic(int id, int speed, int height) {
        Graphic graphic = new Graphic(id, speed, height);
        this.defender.startGraphic(graphic);
        return this;
    }


    final boolean isAccurate() {
        var roll = Server.random.get().nextDouble();
        double accurate = 0.0D;
        Player player = (Player) this.defender;
        if (this.combatType == null) return true;
        if (this.combatType.equals(CombatType.RANGE)) {
            if (!this.checkNegatedDamage && CombatPrayer.isPrayerOn(player, CombatPrayer.PROTECT_FROM_RANGED))
                return false;
            accurate = RangeCombatFormula.STANDARD.getAccuracy(this.attacker, this.defender, 1, 1);
        } else if (this.combatType.equals(CombatType.MAGE)) {
            if (!this.checkNegatedDamage && CombatPrayer.isPrayerOn(player, CombatPrayer.PROTECT_FROM_MAGIC))
                return false;
            accurate = MagicCombatFormula.STANDARD.getAccuracy(this.attacker, this.defender, 1, 1);
        } else if (this.combatType.equals(CombatType.MELEE)) {
            if (!this.checkNegatedDamage && CombatPrayer.isPrayerOn(player, CombatPrayer.PROTECT_FROM_MELEE))
                return false;
            accurate = MeleeCombatFormula.STANDARD.getAccuracy(this.attacker, this.defender, 1, 1);
        }
        return accurate >= roll;
    }

    public HitBuilder submit() {
        Player player = (Player) this.defender;
        if (this.checkNegatedDamage && this.damage >= 2) this.negate();
        this.setAccurate(this.isAccurate());
        if (this.immune) this.immunity();
        else if (!this.accurate && this.damage <= 0) this.block();
        else if (this.accurate && this.damage > 0) this.setHitmark(Hitmark.HIT);
        else this.block();
        if (player.inGodmode()) this.block();
        this.defender.appendDamage(this.damage, this.hitmark);
        return this;
    }

    final void block() {
        this.setDamage(0);
        this.setAccurate(false);
        this.setHitmark(Hitmark.MISS);
    }

    final void immunity() {
        this.setDamage(0);
        this.setAccurate(false);
        this.setHitmark(Hitmark.MISS);
    }

    final void negate() {
        if (this.damage > 2) {
            var damage = this.damage / 2;
            this.setDamage(damage);
        }
    }

    public final void execute() {
        delayedExecutionConsumer = consume();
        delayedExecutionConsumer.accept(this);
    }

    public final HitBuilder consume(Consumer<HitBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    final Consumer<HitBuilder> consume() {
        return hitBuilder -> CycleEventHandler.getSingleton().addEvent(hitBuilder.getAttacker(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                hitBuilder.submit();
                container.stop();
            }
        }, this.delay + 1);
    }
}
