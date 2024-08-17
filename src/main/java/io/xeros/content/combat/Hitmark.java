package io.xeros.content.combat;

import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import lombok.Getter;

/**
 * An enumeration of different hitmarks. Each type of hitmark has an identification value that seperates it from the rest.
 * 
 * @author Jason MacKeigan
 * @date Jan 26, 2015, 2:41:46 AM
 */
@Getter
public enum Hitmark {
	/**
	 * Corruption has a chance to apply on the target of the player who is under the effects of either Lesser Corruption or Greater Corruption during a successful hit.
	 * Corruption which drains prayer points over a short period of time.
	 */
	CORRUPTION(0),

	/**
	 * Used for ironman to show the hit is not yours
	 */
	BLOCK_HIT(1),

	/**
	 * Poison hitsplat.png 	Poison, which damages players at set intervals, and decreases over time.
	 * Can also indicate eating a poison karambwan as well as being harmed by Poison Gas. Green eggs fired at enemies in Barbarian Assault also show this.
	 * Poison damages entities over time, lowering the damage by one every four hit splat cycles.
	 */
	POISON(68, 69, 70),

	/**
	 * Disease, which drains a player's stats at set intervals, excluding Hitpoints and Prayer.
	 * Indicates the player being under the effects of a disease, which periodically drains stats.
	 */
	DISEASE(4),

	/**
	 * Venom damages entities over time, increasing the damage by one every four hit splat cycles, capping out at 20.
	 */
	VENOM(5),

	/**
	 * Used to represent an NPC healing its hitpoints, though it is mostly reserved for boss encounters.
	 */
	NPC_HEAL(6), // generally used for NPCs -> NO player distinction

	/**
	 * Indicates a hit of zero damage.
	 */
	MISS(12, 13),

	/**
	 * Indicates a successful hit that dealt damage. In the Nightmare Zone,
	 * drinking an absorption potion will cause all monster-inflicted damage to be zero,
	 * but will still use this red damage hit splat to indicate the successful hit.
	 */
	HIT(16, 17, 43),

	/**
	 * Indicates damage dealt to Verzik Vitur’s, The Nightmare’s and Tempoross’ shields.
	 * While the inactive icon is defined in the config, it seems to be a placeholder.
	 */
	SHIELD(18, 19, 44),

	/**
	 * Indicates damage dealt to Zalcano’s stone armour. The dynamic id is never actually used.
	 * Armour Shown when damaging Zalcano's stone armour.
	 */
	ARMOUR(20, 21, 45),

	/**
	 * Indicates totems being healed while charging them during the fight against The Nightmare.
	 * Shown when The Nightmare's totems are charged or "healed".
	 */
	CHARGE(22, 23, 46),

	/**
	 * Indicates totems being damaged while the parasites discharge them during the fight against The Nightmare.
	 * The dynamic is never actually used.
	 * Shown when The Nightmare's totems are uncharged or "damaged" by parasites.
	 */
	UNCHARGE(24, 25, 47),

	/**
	 * Dodged damage from a negated non-typeless attack. Currently unused.
	 */
	POISE(53, 38, 55),

	/**
	 * Alternate charge hitsplat representing the growth of the Palm of Resourcefulness,
	 * as well as the restoration of Kephri's scarab shield within the Tombs of Amascut raid.
	 */
	ALT_CHARGE(39, 40),

	/**
	 * Alternate uncharge hitsplat representing the crocodile's damage towards the Palm of Resourcefulness.
	 */
	ALT_UNCHARGE(41, 42),

	/**
	 * Shown when draining the Phantom Muspah's prayer shield. Currently, the max hit variant is unused.
	 */
	PRAYER_DRAIN(59, 60, 61),

	/**
	 * Bleed attack indicating damage dealt over time from Vardorvis' Swinging Axes attack.
	 */
	BLEED(67),

	/**
	 * Used to represent the restoration of the player's sanity.
	 */
	RESTORE_SANITY(72),

	/**
	 * Sanity drain from Lost Souls and The Whisperer within the Shadow Realm.
	 */
	DRAIN_SANITY(71);

	private final int id;
	private final int secondary;
	private final int maxHit;

	Hitmark(int id) {
		this(id, -1, -1);
	}

	Hitmark(int id, int secondary) {
		this(id, secondary, -1);
	}

	Hitmark(int id, int secondary, int max) {
		this.id = id;
		this.secondary = secondary;
		this.maxHit = max;
	}

	public int getMark(int damage, Entity source, Entity target, Player observer) {
		return this.getObservedType(damage, source, target, observer, false);
	}

	public int getObservedType(int damage, Entity source, Entity target, Player observer, boolean isMaxHit) {
		if (isMaxHit && source == observer) {
            switch (this) {
                case CHARGE:
                    return CHARGE.maxHit;
                case SHIELD:
                    return SHIELD.maxHit;
                case POISON:
                    return POISON.maxHit;
                default:
                    return HIT.maxHit;
            }
		}
		if (source == observer || target == observer) {
			if (damage == 0) {
                switch (this) {
                    case CHARGE:
                        return CHARGE.id;
                    case SHIELD:
                        return SHIELD.id;
                    case POISON:
                        return POISON.id;
                    default:
                        return MISS.id;
                }
			}
			return id;
		}
		return damage == 0 ? MISS.secondary : secondary;
	}

	public boolean isMiss() {
		return equals(MISS);
	}
}
