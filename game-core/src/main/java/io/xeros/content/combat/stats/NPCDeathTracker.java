package io.xeros.content.combat.stats;

import java.util.HashMap;
import java.util.Map;

import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.model.definition.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.Player;
import org.apache.commons.lang3.text.WordUtils;

public class NPCDeathTracker {

	/**
	 * The player this is relative to
	 */
	private final Player player;

	/**
	 * A mapping of npcs names with their corresponding kill count
	 */
	private Map<String, Integer> tracker = new HashMap<>();

	/**
	 * Creates a new {@link NPCDeathTracker} object for a singular player
	 * 
	 * @param player
	 *            the player
	 */
	public NPCDeathTracker(Player player) {
		this.player = player;
	}

	/**
	 * Made a mistake with kc, this will fix it.
	 * Goes through every npc death tracker and makes the name lowercase.
	 */
	public void normalise() {
		Map<String, Integer> newTracker = new HashMap<>();
		Map<String, Integer> oldTracker = tracker;
		tracker = newTracker;

		for (Map.Entry<String, Integer> entry : oldTracker.entrySet()) {
			String key = entry.getKey().toLowerCase();
			if (newTracker.containsKey(key)) {
				newTracker.put(key, newTracker.get(key) + entry.getValue());
			} else {
				newTracker.put(key, entry.getValue());
			}
		}

		// Fixes an issue with the old Kree'arra npc definition name being different from the current one
		tracker.put(NpcDef.forId(Npcs.KREEARRA).getName().toLowerCase(), tracker.getOrDefault("kree arra", 0)
				+ tracker.getOrDefault("kree'arra", 0));
		tracker.remove("kree arra"); // Remove the old one or we'll have ever-expanding kc!
	}

	public int getKc(String name) {
		name = name.toLowerCase();

		if (name.equalsIgnoreCase(TobConstants.THEATRE_OF_BLOOD)) {
			return player.tobCompletions;
		}

		if (name.equalsIgnoreCase("Chambers of Xeric") || name.equalsIgnoreCase("cox")) {
			return player.raidCount;
		}

		return tracker.getOrDefault(name, 0);
	}

	/**
	 * Attempts to add a kill to the total amount of kill for a single npc
	 * 
	 * @param name
	 *            the name of the npc
	 */
	public void add(String name, int combatLevel, int bossPoints) {
		if (name == null) {
			return;
		} else {
			name = name.toLowerCase();
			int kills = (tracker.get(name) == null ? 0 : tracker.get(name)) + 1;
			String killsStr = Integer.toString(kills);
			tracker.put(name, kills);
			if (name.equalsIgnoreCase("none")) {
				return;
			}

			if (name.contains("clue-scroll")) {
				return;
			}

			if (killsStr.endsWith("00") && combatLevel <= 150) {//every 100
				displayKcMessage(name, kills, bossPoints);
			} else if (killsStr.endsWith("0") && combatLevel <= 250) {//every 10
				displayKcMessage(name, kills, bossPoints);
			} else if (combatLevel > 250) {
				displayKcMessage(name, kills, bossPoints);
			}
		}
	}

	public void displayKcMessage(String name, int kills, int bossPoints) {
		StringBuilder builder = new StringBuilder();
		String formatted = name.replaceAll("_", " ");
		builder.append("Your " + WordUtils.capitalize(formatted)
				+ " kill count is: <col=FF0000>" + kills + "</col>");

		if (bossPoints > 0) {
			builder.append(", +<col=FF0000>" + bossPoints + "</col> points.");
		} else {
			builder.append(".");
		}

		player.sendMessage(builder.toString());
	}

	/**
	 * Determines the total amount of kills
	 * 
	 * @return the total kill count
	 */
	public long getTotal() {
		return tracker.values().stream().mapToLong(Integer::intValue).sum();
	}

	/**
	 * A mapping of npcs with their corresponding kill count
	 * 
	 * @return the map
	 */
	public Map<String, Integer> getTracker() {
		return tracker;
	}
}
