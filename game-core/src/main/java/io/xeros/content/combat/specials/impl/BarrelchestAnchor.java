package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class BarrelchestAnchor extends Special {

	public BarrelchestAnchor() {
		super(5.0, 2.0, 1.10, new int[] { 10887 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx0(1027);
		player.startAnimation(5870);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}
}