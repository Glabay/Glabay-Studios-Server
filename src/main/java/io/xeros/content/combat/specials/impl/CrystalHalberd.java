package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.core.HitDispatcher;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.CombatType;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class CrystalHalberd extends Special {

	public CrystalHalberd() {
		super(3.0, 1.00, 1.10, new int[] {Items.CRYSTAL_HALBERD });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx100(1232);
		player.startAnimation(1203);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (target instanceof NPC) {
			NPC other = (NPC) target;
			if (other != null && player.npcAttackingIndex > 0) {
				if (player.goodDistance(player.getX(), player.getY(), other.getX(), other.getY(), other.getSize() + 2) && other.getSize() > 1) {
					HitDispatcher.getHitEntity(player, target).playerHitEntity(CombatType.MELEE, null);
				}
			}
		}
	}

}
