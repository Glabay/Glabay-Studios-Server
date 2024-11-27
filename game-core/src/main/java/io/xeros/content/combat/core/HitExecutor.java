package io.xeros.content.combat.core;

import java.util.Optional;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.effects.damageeffect.DamageEffect;
import io.xeros.content.combat.effects.damageeffect.impl.TridentOfTheSwampEffect;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.skills.herblore.PoisonedWeapon;
import io.xeros.model.Graphic;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.EntityReference;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

/**
 * This class class processes hits queued by the {@link HitDispatcher}.
 */
public abstract class HitExecutor {

    public static HitExecutor getDelayedHit(Player c, Entity defender, Damage damage) {
        if (defender.isNPC()) {
            return new HitExecutorNpc(c, defender, damage);
        } else {
            return new HitExecutorPlayer(c, defender, damage);
        }
    }

    protected final Player attacker;
    protected final Entity defender;
    protected final Damage damage;

    public abstract void onHit();

    public HitExecutor(Player attacker, Entity defender, Damage damage) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
    }

    public void hit() {
        if (defender.isDead || defender.getHealth().getCurrentHealth() <= 0) {
            return;
        }

        if (defender.isAutoRetaliate()) {
            defender.attackEntity(attacker);
        }

        onHit();

        if (damage.getSpecial() != null) {
            damage.getSpecial().hit(attacker, defender, damage);
        }

        attacker.lastAttackedEntity = EntityReference.getReference(defender);

        if (defender instanceof Player) {
            Player d = (Player) defender;
            d.lastDefend = EntityReference.getReference(attacker);
            d.lastDefendTime = System.currentTimeMillis();
        }
        if (damage.isSuccess()) {
            if (!defender.getHealth().isNotSusceptibleTo(HealthStatus.POISON)) {
                damage.getEquipment().ifPresent(equipment -> {
                    Optional<PoisonedWeapon.PoisonLevel> poison = Optional.empty();
                    for (int equipmentItem : equipment) {
                        if (equipmentItem == attacker.playerEquipment[Player.playerWeapon] || equipmentItem == attacker.playerEquipment[Player.playerArrows]) {
                            poison = PoisonedWeapon.getPoisonLevel(equipmentItem);
                            if (poison.isPresent()) {
                                break;
                            }
                        }
                    }
                    poison.ifPresent(pl -> {
                        if (RandomUtils.nextInt(0, pl.getPoisonProbability()) == 1) {
                            defender.getHealth().proposeStatus(HealthStatus.POISON, pl.getPoisonDamage(), Optional.of(attacker));
                        }
                    });
                });
            }
        }

        if (damage.getCombatType() != null) {
            if (defender.attackTimer > 3) {
                if (defender.hasBlockAnimation()) {
                    defender.startAnimation(defender.getBlockAnimation());
                }
            }

            switch (damage.getCombatType()) {
                case MELEE:
                    defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                    break;

                case RANGE:
                    if (attacker.dbowSpec) {
                        attacker.dbowSpec = false;
                    }

                    attacker.rangeEndGFX = RangeData.getRangeEndGFX(attacker, damage.getRangedWeaponType().noArrows());
                    attacker.ignoreDefence = false;
                    attacker.multiAttacking = false;

                    if (attacker.rangeEndGFX > 0) {
                        defender.startGraphic(new Graphic(attacker.rangeEndGFX, attacker.rangeEndGFXHeight ? Graphic.GraphicHeight.LOW : Graphic.GraphicHeight.MIDDLE));
                    }
                    defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                    break;

                case MAGE:
                    if (attacker.spellSwap) {
                        attacker.spellSwap = false;
                        attacker.setSidebarInterface(6, 16640);
                        attacker.playerMagicBook = 2;
                        attacker.gfx0(-1);
                    }

                    if (damage.isSuccess()) {
                        defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                        if (attacker.oldSpellId > -1) {
                            switch (CombatSpellData.MAGIC_SPELLS[attacker.oldSpellId][0]) {
                                case 12939://smoke spells
                                case 12963:
                                    defender.getHealth().proposeStatus(HealthStatus.POISON, 2, Optional.of(attacker));
                                    break;
                                case 12951:
                                case 12975:
                                    defender.getHealth().proposeStatus(HealthStatus.POISON, 4, Optional.of(attacker));
                                    break;
                                case 12901:
                                case 12919: // blood spells
                                case 12911:
                                case 12929:
                                    int heal = Misc.random(damage.getAmount() / 2);
                                    attacker.getHealth().increase(heal);
                                    attacker.getPA().refreshSkill(3);
                                    break;
                            }

                            if (attacker.getSpellId() == SanguinestiStaff.COMBAT_SPELL_INDEX && Misc.trueRand(6) == 0) {
                                attacker.getHealth().increase(damage.getAmount() / 2);
                                attacker.startGraphic(new Graphic(1542));
                            }
                        }

                        DamageEffect tridentOfTheSwampEffect = new TridentOfTheSwampEffect();
                        if (tridentOfTheSwampEffect.isExecutable(attacker)) {
                            tridentOfTheSwampEffect.execute(attacker, defender, new Damage(6));
                        }
                    } else {
                        defender.startGraphic(new Graphic(85, Graphic.GraphicHeight.MIDDLE));
                    }
                    break;

                default:
                    break;
            }
        }

        attacker.multiAttacking = false;
        defender.setUpdateRequired(true);
        attacker.usingMagic = false;
        attacker.oldSpellId = 0;
        if (attacker.bowSpecShot <= 0) {
            attacker.oldNpcIndex = 0;
            attacker.weaponUsedOnAttack = 0;
            attacker.bowSpecShot = 0;
        }
        if (attacker.bowSpecShot >= 2) {
            attacker.bowSpecShot = 0;
        }
        if (attacker.bowSpecShot == 1) {
            attacker.hitDelay = 2;
            attacker.bowSpecShot = 0;
        }
    }

}
