package io.xeros.content.combat;

import java.util.Optional;

import io.xeros.content.combat.specials.Special;
import io.xeros.content.combat.weapon.RangedWeaponType;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import lombok.Getter;

/**
 * 
 * @author Jason MacKeigan
 * @since Nov 6, 2014, 1:01:03 PM
 */
public class Damage {

	@Override
	public String toString() {
		return "Damage{" +
				"special=" + special +
				", amount=" + amount +
				", ticks=" + ticks +
				", target=" + target +
				", timestamp=" + timestamp +
				", success=" + success +
				'}';
	}

	/**
	 * The special attack assigned to this damage
	 * -- GETTER --
	 *  The special attack assigned to this damage
	 *

     */
	@Getter
	private Special special;

	/**
	 * The type of combat this damage is relative to
	 * -- GETTER --
	 *  The type of combat this damage is relative to
	 *

     */
	@Getter
	private CombatType combatType;

	private RangedWeaponType rangedWeaponType = RangedWeaponType.SHOT;

	/**
	 * The equipment worn by the player
	 * -- GETTER --
	 *  The equipment warn by the attacker
	 *

     */
	@Getter
	private Optional<int[]> equipment;

	/**
	 * The amount of damage caused by the weapon
	 * -- GETTER --
	 *  Returns the amount of damage
	 *

     */
	@Getter
	private int amount;

	/**
	 * The damage object will be stored in a queue. Every 600ms, or every game tick, the number of ticks will be reduced by 1 until the ticks is 0
	 * -- GETTER --
	 *  The number of ticks this damage is alive for
	 *

     */
	@Getter
	private int ticks;

	/**
	 * The target that this damage will be inflicted upon
	 */
	@Getter
	private Entity attacker, target;

	/**
	 * The time that the damage was created
	 * -- GETTER --
	 *  The time that the damage was created
	 *

     */
	@Getter
	private final long timestamp;

	/**
	 * The hitmark for this damage
	 * -- GETTER --
	 *  The hitmark for this damage
	 *

     */
	@Getter
	private Hitmark hitmark;

	@Getter
	private boolean success;

	/**
	 * Creates a new damage object with a specific amount registered
	 *
	 * @param amount the amount of damage
	 */
	public Damage(int amount) {
		success = amount > 0;
		this.amount = amount;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Creates a new {@link Damage} object that will be added to the damage queue for the target
	 *
	 * @param target the target being dealt the damage
	 * @param amount the amount of damage being dealt
	 * @param ticks the amount of ticks until the damage is applied
	 * @param hitmark the type of hitmark that is displayed
	 * @param combatType the special attack
	 */
	public Damage(Entity attacker, Entity target, int amount, int ticks, Hitmark hitmark, CombatType combatType) {
		this(amount);
		if (ticks < 0) {
			throw new IllegalStateException("The number of ticks for the damage is negative.");
		}
		success = amount > 0;
		this.attacker = attacker;
		this.target = target;
		this.ticks = ticks;
		this.hitmark = hitmark;
		this.combatType = combatType;
	}

	public Damage(Entity target, int amount, int ticks, int[] equipment, Hitmark hitmark, CombatType combatType) {
		this(amount);
		if (ticks < 0) {
			throw new IllegalStateException("The number of ticks for the damage is negative.");
		}
		success = amount > 0;
		this.target = target;
		this.ticks = ticks;
		this.equipment = Optional.of(equipment);
		this.hitmark = hitmark;
		this.combatType = combatType;
	}

	/**
	 * Creates a new {@link Damage} object that will be added to the damage queue for the target
	 *
	 * @param target the target being dealt the damage
	 * @param amount the amount of damage being dealt
	 * @param ticks the amount of ticks until the damage is applied
	 * @param hitmark the type of hitmark that is displayed
	 */
	public Damage(Entity target, int amount, int ticks, int[] equipment, Hitmark hitmark, CombatType combatType, RangedWeaponType rangedWeaponType,
				  Special special, boolean success) {
		this(target, amount, ticks, equipment, hitmark, combatType);
		this.special = special;
		this.success = success;
		this.rangedWeaponType = rangedWeaponType;
	}

	/**
	 * Modifies the amount of damage being dealt to the target
	 *
	 * @param amount the new amount of damage
	 * @return the damage to be dealt
	 */
	public int setAmount(int amount) {
		return this.amount = amount;
	}

	/**
	 * Removes a single tick from the total number of ticks
	 */
	public void removeTick() {
		ticks--;
	}

	public RangedWeaponType getRangedWeaponType() {
		return rangedWeaponType == null ? RangedWeaponType.SHOT : rangedWeaponType;
	}
}
