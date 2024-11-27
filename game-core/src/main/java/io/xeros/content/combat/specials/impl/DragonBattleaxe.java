package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class DragonBattleaxe extends Special {

	public DragonBattleaxe() {
		super(10.0, 1.0, 1.0, new int[] { 1377 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		int[] decreased = {Player.playerAttack, Player.playerDefence, Player.playerRanged, Player.playerMagic};
		int[] increased = {Player.playerStrength};
		for (int skill : decreased) {

			player.playerLevel[skill] -= player.getPA().getLevelForXP(player.playerXP[skill]) * .1;
			if (player.playerLevel[skill] < 1) {
				player.playerLevel[skill] = 1;
			}
			player.getPA().refreshSkill(skill);
		}

		for (int skill : increased) {
			player.playerLevel[skill] = player.getPA().getLevelForXP(player.playerXP[skill]);
			if (player.playerLevel[skill] <=  player.getPA().getLevelForXP(player.playerXP[skill])) {
				player.playerLevel[skill] += player.getPA().getLevelForXP(player.playerXP[skill]) * .2;
				player.getPA().refreshSkill(skill);
			}

		}

		player.attackTimer += 1;
		player.forcedChat("Raarrrrrgggggghhhhhhh!");
		player.gfx0(246);
		player.startAnimation(1056);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
