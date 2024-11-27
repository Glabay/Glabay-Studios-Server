package io.xeros.content.bosses.hespori;


import io.xeros.Server;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.content.bosses.hespori.Hespori.X;


/**
 * Currently only handles spawning 1 NPC but more can be added.
 */

public class HesporiSpawner {

	public enum Npcs {

		HESPORI(Hespori.NPC_ID, "Hespori", 65000, 2, 250, 3000);

		private final int npcId;

		private final String monsterName;

		private final int hp;

		private final int maxHit;

		private final int attack;

		private final int defence;

		Npcs(final int npcId, final String monsterName, final int hp, final int maxHit, final int attack, final int defence) {
			this.npcId = npcId;
			this.monsterName = monsterName;
			this.hp = hp;
			this.maxHit = maxHit;
			this.attack = attack;
			this.defence = defence;
		}

		public int getNpcId() {
			return npcId;
		}

		public String getMonsterName() {
			return monsterName;
		}

		public int getHp() {
			return hp;
		}

		public int getMaxHit() {
			return maxHit;
		}

		public int getAttack() {
			return attack;
		}

		public int getDefence() {
			return defence;
		}
	}

	/**
	 * The spawnNPC method which handles the spawning of the NPC and the global
	 * message sent.
	 *
	 * @param c
	 */

	private static NPC hespori;

	public static void spawnNPC() {
		Npcs npcType = Npcs.HESPORI;
		Server.getGlobalObjects().add(new GlobalObject(-1, Hespori.X, Hespori.Y, 0, 1, 10, -1, -1)); // West - Empty Altar
		hespori = NPCSpawning.spawnNpcOld(Hespori.NPC_ID, X, Hespori.Y, 0, 0, npcType.getHp(), npcType.getMaxHit(), npcType.getAttack(), npcType.getDefence());
	}

	public static void despawn() {
		if (hespori != null) {
			if (hespori.getIndex() > 0) {
				hespori.unregister();
			}
			hespori = null;
		}
	}

	public static boolean isSpawned() {
		return getHespori() != null;
	}

	public static NPC getHespori() {
		return hespori;
	}
}