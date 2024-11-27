package io.xeros.content.bosses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.xeros.Server;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class Skotizo extends LegacySoloPlayerInstance {

	/**
	 * Player variables, start coordinates.
	 */
	private static final int START_X = 1700, START_Y = 9893;
	
	/**
	 * Npc variables, start coordinates.
	 */
	public static final int SPAWN_X = 1688,
			SPAWN_Y = 9880,
			SKOTIZO_ID = 7286,
			AWAKENED_ALTAR_NORTH = 7288,
			AWAKENED_ALTAR_SOUTH = 7290,
			AWAKENED_ALTAR_WEST = 7292,
			AWAKENED_ALTAR_EAST = 7294,
			REANIMATED_DEMON = 7287,
			DARK_ANKOU = 7296;

	private static final String[] ALTAR_MAP_DIRECTION = {"NORTH", "SOUTH", "WEST", "EAST"};

	public boolean northAltar, southAltar, eastAltar, westAltar, ankouSpawned, demonsSpawned;
    public Map<Integer, String> altarMap = Collections.synchronizedMap(new HashMap<Integer, String>());
    public int altarCount;
	public boolean firstHit = true;
    
	public Skotizo(Player player) {
		super(player, Boundary.SKOTIZO_BOSSROOM);
	}

	private int getAltar() { return (Misc.random(3) + 1); }
	
	public int calculateSkotizoHit(Player attacker, int damage) {
		if (altarCount == 0) {
			if (attacker.debugMessage)
				player.sendMessage("full hit");
		} else if (attacker.getSkotizo().altarCount == 1) {
			if (attacker.debugMessage)
				attacker.sendMessage("3/4 hit");
				damage = (int)(damage * .75);
		} else if (attacker.getSkotizo().altarCount == 2) {
			if (attacker.debugMessage)
				attacker.sendMessage("1/2 hit");
				damage = (int)(damage * .50);
		} else if (attacker.getSkotizo().altarCount == 3) {
			if (attacker.debugMessage)
				attacker.sendMessage("1/4 hit");
				damage = (int)(damage * .25);
		} else if (attacker.getSkotizo().altarCount == 4) {
			if (attacker.debugMessage)
				attacker.sendMessage("0 hit");
				damage = 0;
		}
		return damage;
	}
	
	public void arclightEffect(NPC npc) {
		if (npc.getNpcId() == AWAKENED_ALTAR_NORTH) {
			NPCHandler.kill(AWAKENED_ALTAR_NORTH, getHeight());
		} else if (npc.getNpcId() == AWAKENED_ALTAR_SOUTH) {
			NPCHandler.kill(AWAKENED_ALTAR_SOUTH, getHeight());
		} else if (npc.getNpcId() == AWAKENED_ALTAR_WEST) {
			NPCHandler.kill(AWAKENED_ALTAR_WEST, getHeight());
		} else if (npc.getNpcId() == AWAKENED_ALTAR_EAST) {
			NPCHandler.kill(AWAKENED_ALTAR_EAST, getHeight());
		}
	}
	
	public void skotizoSpecials() {
		NPC SKOTIZO = NPCHandler.getNpc(SKOTIZO_ID, getHeight());
		
		if (SKOTIZO.isDead()) {
			return;
		}
		
		int random = Misc.random(16);
		
		if (random == 1) {
			
			int altarNumber = getAltar();
			boolean unique = false;

			while (!unique) {
				if ((altarMap.get(1) == "NORTH") && (altarMap.get(2)== "SOUTH") && (altarMap.get(3) == "WEST") && (altarMap.get(4) == "EAST")) {
					player.sendMessage("@or2@Your hits do not effect Skotizo... Maybe I should kill some of the altars...");
					break;
				}
				String altar = altarMap.get(altarNumber);
				if(altar == null) {
					altarMap.put(altarNumber, ALTAR_MAP_DIRECTION[altarNumber-1]);
					unique = true;
					if (ALTAR_MAP_DIRECTION[altarNumber-1] == "NORTH") {
						player.sendMessage("@or2@The north altar has just awakened!");
						player.getPA().sendChangeSprite(29232, (byte) 1);
						Server.getGlobalObjects().remove(28924, 1694, 9904, getHeight(), this); // Remove North - Empty Altar
						Server.getGlobalObjects().add(new GlobalObject(28923, 1694, 9904, getHeight(), 2, 10, -1, -1).setInstance(this)); // North - Awakened Altar
						add(NPCSpawning.spawnNpcOld(player, AWAKENED_ALTAR_NORTH, 1694, 9904, getHeight(), 0, 100, 10, 200, 200, false, false));
						altarCount++;
						northAltar = true;
					} else if (ALTAR_MAP_DIRECTION[altarNumber-1] == "SOUTH") {
						player.sendMessage("@or2@The south altar has just awakened!");
						player.getPA().sendChangeSprite(29233, (byte) 1);
						Server.getGlobalObjects().remove(28924, 1696, 9871, getHeight(), this); // Remove South - Empty Altar
						Server.getGlobalObjects().add(new GlobalObject(28923, 1696, 9871, getHeight(), 0, 10, -1, -1).setInstance(this)); // South - Awakened Altar
						add(NPCSpawning.spawnNpcOld(player, AWAKENED_ALTAR_SOUTH, 1696, 9871, getHeight(), 0, 100, 10, 200, 200, false, false));
						altarCount++;
						southAltar = true;
					} else if (ALTAR_MAP_DIRECTION[altarNumber-1] == "WEST") {
						player.sendMessage("@or2@The west altar has just awakened!");
						player.getPA().sendChangeSprite(29234, (byte) 1);
						Server.getGlobalObjects().remove(28924, 1678, 9888, getHeight(), this); // Remove West - Empty Altar
						Server.getGlobalObjects().add(new GlobalObject(28923, 1678, 9888, getHeight(), 1, 10, -1, -1).setInstance(this)); // West - Awakened Altar
						add(NPCSpawning.spawnNpcOld(player, AWAKENED_ALTAR_WEST, 1678, 9888, getHeight(), 0, 100, 10, 200, 200, false, false));
						altarCount++;
						westAltar = true;
					} else if (ALTAR_MAP_DIRECTION[altarNumber-1] == "EAST") {
						player.sendMessage("@or2@The east altar has just awakened!");
						player.getPA().sendChangeSprite(29235, (byte) 1);
						Server.getGlobalObjects().remove(28924, 1714, 9888, getHeight(), this); // Remove East - Empty Altar
						Server.getGlobalObjects().add(new GlobalObject(28923, 1714, 9888, getHeight(), 3, 10, -1, -1).setInstance(this)); // East - Awakened Altar
						add(NPCSpawning.spawnNpcOld(player, AWAKENED_ALTAR_EAST, 1714, 9888, getHeight(), 0, 100, 10, 200, 200, false, false));
						altarCount++;
						eastAltar = true;
					}
				} else {
					altarNumber = getAltar();
				}
			}
		} else if (random == 2 || random == 3) {
			if (SKOTIZO.getHealth().getCurrentHealth() < 225 && !demonsSpawned) {
				NPCHandler.npcs[SKOTIZO.getIndex()].forceChat("Gar mulno ful taglo!");
				add(NPCSpawning.spawnNpc(player, REANIMATED_DEMON, player.absX + 1, player.absY, getHeight(), 0, 8, true, false));
				add(NPCSpawning.spawnNpc(player, REANIMATED_DEMON, player.absX - 1, player.absY, getHeight(), 0, 8, true, false));
				add(NPCSpawning.spawnNpc(player, REANIMATED_DEMON, player.absX, player.absY + 1, getHeight(), 0, 8, true, false));
				demonsSpawned = true;
			}
		} else if (random == 4 && Misc.random(5) == 0) {
			if (SKOTIZO.getHealth().getCurrentHealth() < 150 && !ankouSpawned) {
				add(NPCSpawning.spawnNpc(player, DARK_ANKOU, player.absX, player.absY - 1, getHeight(), 0, 8, true, false));
				ankouSpawned = true;
			}
		}
	}

	/**
	 * Constructs the content by creating an event
	 */
	public void init() {
		add(NPCSpawning.spawnNpc(player, SKOTIZO_ID, SPAWN_X, SPAWN_Y, getHeight(), 0, 30, true, false));
		
		player.getPA().movePlayer(START_X, START_Y, getHeight());
		add(player);
		
		player.getPA().sendChangeSprite(29232, (byte) 0);
		player.getPA().sendChangeSprite(29233, (byte) 0);
		player.getPA().sendChangeSprite(29234, (byte) 0);
		player.getPA().sendChangeSprite(29235, (byte) 0);
		
		Server.getGlobalObjects().add(new GlobalObject(28924, 1696, 9871, getHeight(), 0, 10, -1, -1).setInstance(this)); // South - Empty Altar
		Server.getGlobalObjects().add(new GlobalObject(28924, 1694, 9904, getHeight(), 2, 10, -1, -1).setInstance(this)); // North - Empty Altar
		Server.getGlobalObjects().add(new GlobalObject(28924, 1678, 9888, getHeight(), 1, 10, -1, -1).setInstance(this)); // West - Empty Altar
		Server.getGlobalObjects().add(new GlobalObject(28924, 1714, 9888, getHeight(), 3, 10, -1, -1).setInstance(this)); // East - Empty Altar
	}

	/**
	 * Disposes of the content by moving the player and finalizing and or removing any left over content.
	 */
	public final void end() {
		killNpcs();
	}

	@Override
	public void onDispose() {
		end();
	}
}
