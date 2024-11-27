package io.xeros.content.combat.effects.damageeffect.impl;

import java.util.Objects;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.effects.damageeffect.DamageEffect;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * @author Jason MacKeigan
 * @date Dec 11, 2014, 4:44:33 AM
 */
public class DragonfireShieldEffect implements DamageEffect {

	static final long ATTACK_DELAY_REQUIRED = 120_000;

	private int cycle;

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer eventContainer) {
				if (Objects.isNull(attacker) || Objects.isNull(defender)) {
					eventContainer.stop();
					return;
				}
				if (defender.getHealth().getCurrentHealth() <= 0 || defender.isDead) {
					eventContainer.stop();
					return;
				}
				cycle++;
				if (cycle == 1) {
					attacker.startAnimation(6696);
					attacker.gfx0(1165);
					attacker.setDragonfireShieldCharge(attacker.getDragonfireShieldCharge() - 1);
				} else if (cycle == 4) {
					attacker.getPA().createPlayersProjectile2(attacker.getX(), attacker.getY(), (attacker.getY() - defender.getY()) * -1, (attacker.getX() - defender.getX()) * -1,
							50, 50, 1166, 30, 30, -defender.getIndex() - 1, 30, 5);
				} else if (cycle >= 5) {
					if (defender.playerEquipment[Player.playerShield] == 11283 || defender.playerEquipment[Player.playerShield] == 11284) {
						defender.appendDamage(attacker, (damage.getAmount() / 2) + (Misc.random(damage.getAmount() / 2)), damage.getAmount() > 0 ? Hitmark.HIT : Hitmark.MISS);
						eventContainer.stop();
						return;
					}
					defender.appendDamage(attacker, damage.getAmount(), damage.getAmount() > 0 ? Hitmark.MISS : Hitmark.HIT);
					eventContainer.stop();
				}
			}
		}, 1);
	}

	@Override
	public boolean isExecutable(Player operator) {
		if (operator.getDragonfireShieldCharge() <= 0) {
			operator.sendMessage("Your dragonfire shield is out of charges, you need to refill it.");
			return false;
		}
		if (System.currentTimeMillis() - operator.getLastDragonfireShieldAttack() < ATTACK_DELAY_REQUIRED) {
			operator.sendMessage("You must let your dragonfire shield cool down before using it again.");
			return false;
		}
		return true;
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer eventContainer) {
				if (Objects.isNull(attacker) || Objects.isNull(defender)) {
					eventContainer.stop();
					return;
				}
				if (defender.getHealth().getCurrentHealth() <= 0 || defender.isDead() || attacker.teleTimer > 0) {
					eventContainer.stop();
					return;
				}
				if (Misc.distanceToPoint(attacker.getX(), attacker.getY(), defender.getX(), defender.getY()) > 12) {
					eventContainer.stop();
					return;
				}
				cycle++;
				if (cycle == 1) {
					attacker.startAnimation(6696);
					attacker.gfx0(1165);
				} else if (cycle == 4) {
					attacker.getPA().createPlayersProjectile2(attacker.getX(), attacker.getY(), (attacker.getY() - defender.getY()) * -1, (attacker.getX() - defender.getX()) * -1,
							50, 50, 1166, 30, 30, -attacker.oldNpcIndex - 1, 30, 5);
				} else if (cycle >= 5) {
					defender.underAttack = true;
					defender.hitDiff = damage.getAmount();
					defender.appendDamage(attacker, damage.getAmount(), Hitmark.HIT);
					defender.hitUpdateRequired = true;
					defender.setUpdateRequired(true);
					eventContainer.stop();
				}
			}
		}, 1);
	}

}
