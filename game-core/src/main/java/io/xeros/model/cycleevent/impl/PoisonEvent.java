package io.xeros.model.cycleevent.impl;

import java.util.Optional;

import io.xeros.content.combat.Hitmark;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.Health;
import io.xeros.model.entity.HealthStatus;

public class PoisonEvent extends Event<Entity> {

	private int damage;

	private int hits;

	private final Optional<Entity> inflictor;

	public PoisonEvent(Entity attachment, int damage, Optional<Entity> inflictor) {
		super("health_status", attachment, 100);
		this.damage = damage;
		this.inflictor = inflictor;
	}

	@Override
	public void execute() {
		if (attachment == null) {
			super.stop();
			return;
		}

		Health health = attachment.getHealth();

		if (health.isNotSusceptibleTo(HealthStatus.POISON)) {
			super.stop();
			return;
		}

		if (attachment.getHealth().getCurrentHealth() <= 0) {
			super.stop();
			return;
		}

		attachment.appendDamage(null, damage, Hitmark.POISON);
		inflictor.ifPresent(inf -> attachment.addDamageTaken(inf, damage));

		hits++;

		if (hits >= 4) {
			damage--;
			hits = 0;
		}

		if (damage <= 0) {
			super.stop();
			return;
		}
	}

}
