package io.xeros.content.bosses;

import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;

public class CorporealBeast {

	/**
	 * Checks wether or not a core is alive, else spawns when corp goes below certain hp
	 */
	public static void checkCore(NPC corp) {
		if (corp == null || corp.getAttributes().getBoolean("spawned_core")) {
			return;
		}

		if (corp.getHealth().getCurrentHealth() < 1000) {
			NPCSpawning.spawnNpc(Npcs.DARK_ENERGY_CORE, 2982, 4377, corp.heightLevel, 0, 13);
			corp.getAttributes().setBoolean("spawned_core", true);
		}
	}

	public static void healWhenNoPlayers(NPC corp) {
		if (!corp.getHealth().isMaximum()) {
			if (Boundary.getPlayersInBoundary(Boundary.CORPOREAL_BEAST_LAIR) == 0) {
				corp.getHealth().reset();
				NPC core = NPCHandler.getNpc(Npcs.DARK_ENERGY_CORE);
				if (core != null)
					core.getHealth().reset();
			}
		}
	}
}
