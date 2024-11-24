package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.util.Misc;

public class SaradominSwordBlessed extends Special {

	public SaradominSwordBlessed() {
		super(6.5, 1.0, 1.25, new int[] { 12809 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(1133);
		if (damage.getAmount() > 0) {
			int damage2 = MagicMaxHit.magiMaxHit(player) + (1 + Misc.random(15));
			player.getDamageQueue().add(new Damage(target, damage2, 2, player.playerEquipment, Hitmark.HIT, CombatType.MAGE));
			player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage2, Skill.ATTACK.getId()));
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
