package io.xeros.content.bosses;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.util.Misc;
import org.apache.commons.lang3.Range;

public class Scorpia {
	
	/**
	 * Checks the healer stage to avoid multiple spawns
	 */
	public static int stage;
	
	/**
	 * Spawns halears when scorpia reaches below a certain health
	 */
	public static void spawnHealer() {
		NPC scorpia = NPCHandler.getNpc(6615);
		if (scorpia == null) {
			return;
		}

		List<NPC> healer = Arrays.asList(NPCHandler.npcs);
		if (healer.stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == 6617 && !n.isDead() && n.getHealth().getCurrentHealth() > 0)) {
			return;
		}

		int maximumHealth = scorpia.getHealth().getMaximumHealth();
		int currentHealth = scorpia.getHealth().getCurrentHealth();
		int percentRemaining = (int) (((double) currentHealth / (double) maximumHealth) * 100D);

		if (percentRemaining > 50) {
			return;
		}

		if (!Misc.passedProbability(Range.between(0, percentRemaining), 10, true)) {
			return;
		}
		if (stage == 0) {
			NPCSpawning.spawnNpc(6617, 3238, 10338, 0, 0, 13);
			NPCSpawning.spawnNpc(6617, 3238, 10346, 0, 0, 13);
			NPCSpawning.spawnNpc(6617, 3228, 10338, 0, 0, 13);
			NPCSpawning.spawnNpc(6617, 3228, 10346, 0, 0, 13);
			stage = 1;
		}
	}

}
