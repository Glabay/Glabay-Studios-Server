package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class VestaLongsword extends Special {

	public VestaLongsword() {
		super(2.5, 1.0, 0.20, new int[] { 22613 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7515);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
