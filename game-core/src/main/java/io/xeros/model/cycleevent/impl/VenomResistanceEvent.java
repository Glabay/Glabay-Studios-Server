package io.xeros.model.cycleevent.impl;

import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.Health;
import io.xeros.model.entity.HealthStatus;

public class VenomResistanceEvent extends Event<Entity> {

	public VenomResistanceEvent(Entity attachment, int ticks) {
		super("venom_resistance_event", attachment, ticks);
	}

	@Override
	public void execute() {
		super.stop();
		if (attachment == null) {
			return;
		}
		Health health = attachment.getHealth();
		health.removeNonsusceptible(HealthStatus.VENOM);
	}

}
