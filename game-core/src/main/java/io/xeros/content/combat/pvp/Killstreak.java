package io.xeros.content.combat.pvp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;

/**
 * A killstreak can be described as a conjunction of successful kills that are obtained one after the other.
 * 
 * @author Jason MacKeigan
 * @date Jan 10, 2015, 3:38:55 AM
 */
public class Killstreak {

	/**
	 * A mapping of the different killstreaks the player has
	 */
	private final Map<Type, Integer> killstreaks = new HashMap<>();

	/**
	 * The player receiving the killstreaks
	 */
	private final Player player;

	/**
	 * Creates a new object that will manage player killstreaks
	 * 
	 * @param player the player
	 */
	public Killstreak(Player player) {
		this.player = player;
	}

	/**
	 * Resets the type of killstreak for the player
	 * 
	 * @param type the type of killstreak
	 */
	public void reset(Type type) {
        killstreaks.remove(type);
	}

	/**
	 * Resets all player killstreaks. This is generally called when a player dies during PVP combat.
	 */
	public void resetAll() {
		killstreaks.clear();
	}

	/**
	 * Increases the killstreak for the specific type
	 * 
	 * @param type the type of killstreak
	 */
	public void increase(Type type) {
		int value = 1 + killstreaks.getOrDefault(type, 0);
		killstreaks.put(type, value);
		reward(type);
	}

	/**
	 * Rewards the player with some item, points, and or some other form of currency.
	 * 
	 * @param type the type of killstreak
	 */
	private void reward(Type type) {
		int streak = killstreaks.getOrDefault(type, 0);
		final int streak1 = streak;
		Optional<KillstreakReward> reward = Arrays.stream(type.rewards).filter(s -> s.getKillstreak() == streak1).findFirst();
		reward.ifPresent(killstreakReward -> giveKillstreakReward(type, streak1));
	}

	/**
	 * The amount of killstreaks a player has for the type of killstreak
	 * 
	 * @param type the type of killstreak
	 * @return zero will be returned if there is no mapping for the killstreak, otherwise the value for the killstreak mapping will be returned.
	 */
	public int getAmount(Type type) {
		return killstreaks.getOrDefault(type, 0);
	}

	/**
	 * A mapping of all of the killstreaks
	 * 
	 * @return a mapping
	 */
	public Map<Type, Integer> getKillstreaks() {
		return killstreaks;
	}

	/**
	 * Returns the sum of all killstreaks.
	 * 
	 * @return The sum of all killstreaks.
	 */
	public int getTotalKillstreak() {
		int total = 0;
		for (Type type : Type.values()) {
			total += getAmount(type);
		}
		return total;
	}

	private void giveKillstreakReward(Type type, int killstreak) {
		if (type == Type.ROGUE) {
			player.getItems().addItemUnderAnyCircumstance(2996, killstreak);
			player.sendMessage("You are on a " + killstreak + " rogue killstreak, you have been given " + killstreak + " pk tickets.");
		} else if (type == Type.HUNTER) {
			player.getItems().addItemUnderAnyCircumstance(2996, 5);
			player.sendMessage("You are on a 3 hunter killstreak, you have been given 5 pk tickets.");
		}
	}

	/**
	 * There are several different types of killstreaks. Allowing early support for different types of killstreaks will allow for the addition of more in the future without having
	 * to do an overhaul.
	 */
	public enum Type {
		ROGUE(10,
				new KillstreakReward(2, 2),
				new KillstreakReward(3, 3),
				new KillstreakReward(4, 4),
				new KillstreakReward(5, 5),
				new KillstreakReward(6, 6),
				new KillstreakReward(7, 7),
				new KillstreakReward(8, 8),
				new KillstreakReward(9, 9),
				new KillstreakReward(10, 10)
		),

		HUNTER(10,
				new KillstreakReward(2, 3),
				new KillstreakReward(3, 5),
				new KillstreakReward(4, 7),
				new KillstreakReward(5, 9),
				new KillstreakReward(6, 11),
				new KillstreakReward(7, 13),
				new KillstreakReward(8, 15),
				new KillstreakReward(9, 17),
				new KillstreakReward(10, 20)
		);

		private final int maximumKillstreak;
		private final KillstreakReward[] rewards;

		Type(int maximumKillstreak, KillstreakReward... rewards) {
			this.maximumKillstreak = maximumKillstreak;
			this.rewards = rewards;
		}

		public static Type get(String name) {
			Optional<Type> op = Arrays.asList(values()).stream().filter(t -> t.name().equals(name)).findFirst();
			return op.orElse(null);
		}

	}
}