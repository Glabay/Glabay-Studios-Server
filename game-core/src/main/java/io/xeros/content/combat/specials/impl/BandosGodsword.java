package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class BandosGodsword extends Special {

	public BandosGodsword() {
		super(5.0, 2.0, 1.21, new int[] { 11804, 20370 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7642);
		player.gfx0(1212);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() > 0) {
			if (target instanceof Player) {
				Player playerTarget = ((Player) target);
				int[] skillOrder = { 1, 2, 5, 0, 6, 4};

				int totalDamage = damage.getAmount();

				for (int i : skillOrder) {
					if (totalDamage <= 0)
						break;

					int currentLevel = playerTarget.playerLevel[i];
					if (currentLevel > 0) {
						int drainAmount = Math.min(totalDamage, currentLevel);

						playerTarget.playerLevel[i] -= drainAmount;
						playerTarget.getPA().refreshSkill(i);

						totalDamage -= drainAmount;
					}
				}

			} else {
				NPC npc = ((NPC) target);
				if (player.debugMessage) {
					player.sendMessage("BGS, npc defence before: " + npc.getDefence());
				}
				npc.lowerDefence(0.3);
				if (player.debugMessage) {
					player.sendMessage("BGS, npc defence after: " + npc.getDefence());
				}
			}
		}
	}

}
