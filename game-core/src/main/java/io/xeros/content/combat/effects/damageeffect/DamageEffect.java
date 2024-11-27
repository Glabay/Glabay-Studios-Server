package io.xeros.content.combat.effects.damageeffect;

import io.xeros.content.combat.Damage;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * 
 * @author Jason MacKeigan
 * @date Nov 24, 2014, 9:47:29 PM
 */
public interface DamageEffect {

	default void execute(Player attacker, Entity defender, Damage damage) {
		if (defender.isNPC()) {
			execute(attacker, defender.asNPC(), damage);
		} else {
			execute(attacker, defender.asPlayer(), damage);
		}
	}

	/**
	 * Executes some effect during the damage step of a player attack
	 * 
	 * @param attacker the attacking player in combat
	 * @param defender the defending player in combat
	 * @param damage the damage dealt during this step
	 */
	void execute(Player attacker, Player defender, Damage damage);

	/**
	 * Executes some effect during the damage step of a player attack
	 * 
	 * @param attacker the attacking player in combat
	 * @param defender the defending npc in combat
	 * @param damage the damage dealt during this step
	 */
	void execute(Player attacker, NPC defender, Damage damage);

	/**
	 * Determines if the event is executable by the operator
	 * 
	 * @param operator the player executing the effect
	 * @return true if it can be executed based on some operation, otherwise false
	 */
	boolean isExecutable(Player operator);

}
