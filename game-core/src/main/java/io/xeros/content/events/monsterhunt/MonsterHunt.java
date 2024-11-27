package io.xeros.content.events.monsterhunt;

import java.util.Arrays;
import java.util.List;

import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.util.Misc;

/**
 * MonsterHunt.java
 * 
 * @author Jashy
 *
 * Re-written to make it cleaner - @Emre
 * 
 * A class to spawn NPC's at different locations in the wilderness.
 *
 */

public class MonsterHunt {

	public enum Npcs {
		FRAGMENT_OF_SEREN(FragmentOfSeren.FRAGMENT_ID, "Seren", 15000, 30, 250, 250),
		UNBEARABLE(TheUnbearable.NPC_ID, "Unbearable", 15000, 55, 400, 350);
		
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

	public static boolean spawned;
	
	private static int npcType;
	
	public static long monsterKilled = System.currentTimeMillis();
	
	private static final MonsterHuntLocation[] locations = {
			new MonsterHuntLocation(3258, 3878, "Demonic Ruins <col=ff0000>(45)</col>"),
			new MonsterHuntLocation(3233, 3638, "Chaos Altar <col=ff0000>(14)</col>"),
			new MonsterHuntLocation(3199, 3887, "Lava Dragons <col=ff0000>(46)</col>"),
			new MonsterHuntLocation(3307, 3933, "Rogues' Castle <col=ff0000>(52)</col>"),
			new MonsterHuntLocation(3306, 3668, "Hill Giants <col=ff0000>(19)</col>")};

	private static MonsterHuntLocation currentLocation;
	private static String name;
	private static boolean isSeren = true;

	public static void despawn() {
		if (npcType == FragmentOfSeren.FRAGMENT_ID) {
			if (!FragmentOfSeren.activePillars.isEmpty()) {
				for(NPC pillar : FragmentOfSeren.activePillars) {
					if (pillar != null) {
						NPCHandler.despawn(pillar.getNpcId(), 0);
					}
				}
				FragmentOfSeren.activePillars.clear();
			}
			FragmentOfSeren.currentSeren.setDead(true);
		}
		NPCHandler.despawn(npcType, 0);
		NPCHandler.despawn(FragmentOfSeren.FRAGMENT_ID, 0);
		NPCHandler.despawn(FragmentOfSeren.NPC_ID, 0);
		NPCHandler.despawn(FragmentOfSeren.CRYSTAL_WHIRLWIND, 0);
		spawned = false;
		currentLocation = null;
		monsterKilled = System.currentTimeMillis();
	}

	public static void spawnNPC() {
		List<MonsterHuntLocation> locationsList = Arrays.asList(locations);
		MonsterHuntLocation randomLocation = Misc.randomTypeOfList(locationsList);
		currentLocation = randomLocation;
		Npcs randomNpc = isSeren ? Npcs.FRAGMENT_OF_SEREN : Npcs.UNBEARABLE;
		isSeren = !isSeren;
		name = randomNpc.getMonsterName();
		npcType = randomNpc.getNpcId();
		if (npcType == FragmentOfSeren.FRAGMENT_ID) {
			FragmentOfSeren.currentSeren = NPCSpawning.spawnNpcOld(randomNpc.getNpcId(), randomLocation.getX(), randomLocation.getY(), 0, 1, randomNpc.getHp(), randomNpc.getMaxHit(), randomNpc.getAttack(), randomNpc.getDefence()/*, false*/);
		} else {
			NPCSpawning.spawnNpcOld(randomNpc.getNpcId(), randomLocation.getX(), randomLocation.getY(), 0, 1, randomNpc.getHp(), randomNpc.getMaxHit(), randomNpc.getAttack(), randomNpc.getDefence()/*, false*/);
		}

		spawned = true;
	}

	public static MonsterHuntLocation getCurrentLocation() {
		return currentLocation;
	}

	public static void setCurrentLocation(MonsterHuntLocation currentLocation) {
		MonsterHunt.currentLocation = currentLocation;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		MonsterHunt.name = name;
	}
	
	public static String getTimeLeft() {
		if (spawned) {
			return "Wildy Event: @gre@" + (isSeren ? "The Unbearable" : "Seren");
		}
		
		long timeLeft = System.currentTimeMillis() - monsterKilled;
		int minutesPassed = (int) (timeLeft / (1000 * 60));
		return "Wildy Event: @red@" + (40 - minutesPassed) + " minutes";
	}
}