package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * @author Jason MacKeigan
 * @date Apr 8, 2015, 2015, 10:45:54 AM
 */
public class ZamorakGodsword extends Special {

	public ZamorakGodsword() {
		super(5.0, 2.0, 1.10, new int[] { 11808, 20374 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7638);
		player.gfx0(1210);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() > 0) {
			if (target instanceof Player) {
				Player p = (Player) target;
				p.freezeTimer = 20;
				p.gfx0(369);
			} else if (target instanceof NPC) {
				NPC npc = (NPC) target;
				npc.freezeTimer = 20;
				npc.gfx0(369);
			}
		}
	}

}
