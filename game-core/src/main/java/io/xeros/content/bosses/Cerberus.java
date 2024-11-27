package io.xeros.content.bosses;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.xeros.Server;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.content.minigames.rfd.DisposeTypes;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class Cerberus extends LegacySoloPlayerInstance {

	/**
	 * Player variables, start coordinates.
	 */
	private static final int START_X = 1240, START_Y = 1241;
	
	/**
	 * Npc variables, start coordinates.
	 */
	private static final int SPAWN_X = 1238, SPAWN_Y = 1251, CERBERUS_ID = 5862, SUMMONED_SOUL_RANGE = 5867, SUMMONED_SOUL_MAGIC = 5868, SUMMONED_SOUL_MELEE = 5869;

	public static final Position EXIT = new Position(1328, 1252);

	public Cerberus(Player player, Boundary boundary) {
		super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
	}
	
	public static boolean cerberusSpecials(NPC npc, Player player) {
		if (System.currentTimeMillis() - npc.getAttributes().getLong("cerberus_special", 0) >= 20_000) {
			boolean arrrroo = npc.getHealth().getCurrentHealth() < 400;
			boolean groundAttack = npc.getHealth().getCurrentHealth() < 201;

			if (arrrroo && groundAttack) {
				int rand = Misc.trueRand(18);
				if (rand == 0) {
					npc.getAttributes().setLong("cerberus_special", System.currentTimeMillis());
					return aaarrrooo(npc, player);
				} else if (rand == 1) {
					npc.getAttributes().setLong("cerberus_special", System.currentTimeMillis());
					player.CERBERUS_ATTACK_TYPE = "GROUND_ATTACK";
					return true;
				}
			} else if (arrrroo && Misc.random(18) == 0) {
				npc.getAttributes().setLong("cerberus_special", System.currentTimeMillis());
				return aaarrrooo(npc, player);
			}
		}

		return false;
	}

	private static boolean aaarrrooo(NPC npc, Player player) {
		List<NPC> ghost = Arrays.asList(NPCHandler.npcs);
		if (ghost.stream().filter(Objects::nonNull)
				.anyMatch(n -> n.getNpcId() == SUMMONED_SOUL_RANGE || n.getNpcId() == SUMMONED_SOUL_MAGIC
						|| n.getNpcId() == SUMMONED_SOUL_MELEE && npc.getHeight() == n.heightLevel && !n.isDead())) {
			return false;
		}

		NPCHandler.npcs[npc.getIndex()].forceChat("Aaarrrooooooo");
		player.CERBERUS_ATTACK_TYPE = "GHOST_ATTACK";
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int ticks = 0;
			@Override
			public void execute(CycleEventContainer container) {
				if (player.isDisconnected() || npc.isDeadOrDying()) {
					onStopped();
					return;
				}

				switch (ticks++) {
					case 1:
						NPC spawn = NPCSpawning.spawnNpc(player, SUMMONED_SOUL_RANGE, 1241, 1256, npc.getHeight(), 0, 30, true, false);
						spawn.getBehaviour().setRespawn(false);
						player.CERBERUS_ATTACK_TYPE = "MELEE";
						break;

					case 2:
						spawn = NPCSpawning.spawnNpc(player, SUMMONED_SOUL_MAGIC, 1240, 1256, npc.getHeight(), 0, 30, true, false);
						spawn.getBehaviour().setRespawn(false);
						break;

					case 3:
						spawn = NPCSpawning.spawnNpc(player, SUMMONED_SOUL_MELEE, 1239, 1256, npc.getHeight(), 0, 30, true, false);
						spawn.getBehaviour().setRespawn(false);
						break;

					case 5:
						container.stop();
						break;
				}
			}

			@Override
			public void onStopped() {
				NPCHandler.kill(SUMMONED_SOUL_RANGE, npc.getHeight());
				NPCHandler.kill(SUMMONED_SOUL_MAGIC, npc.getHeight());
				NPCHandler.kill(SUMMONED_SOUL_MELEE, npc.getHeight());
			}
		}, 2);

		return true;
	}

	/**
	 * Constructs the content by creating an event
	 */
	public static void init(Player player) {
		Cerberus cerberus = new Cerberus(player, Boundary.CERBERUS_ROOM_WEST);

		NPC npc = NPCSpawning.spawnNpc(player, CERBERUS_ID, SPAWN_X, SPAWN_Y, cerberus.getHeight(), 0, 23, false, false);
		npc.getBehaviour().setRespawnWhenPlayerOwned(true);
		npc.getBehaviour().setAggressive(true);
		cerberus.add(npc);

		cerberus.add(player);
		player.getPA().movePlayer(START_X, START_Y, cerberus.getHeight());
		player.sendMessage("Walk forward and prepare to fight...");

		player.CERBERUS_ATTACK_TYPE = "FIRST_ATTACK";
		Server.getGlobalObjects().add(new GlobalObject(23105, 1241, 1242, cerberus.getHeight(), 0, 10, -1, -1).setInstance(cerberus));
		Server.getGlobalObjects().add(new GlobalObject(23105, 1240, 1242, cerberus.getHeight(), 0, 10, -1, -1).setInstance(cerberus));
		Server.getGlobalObjects().add(new GlobalObject(23105, 1239, 1242, cerberus.getHeight(), 0, 10, -1, -1).setInstance(cerberus));
		Server.getGlobalObjects().add(new GlobalObject(23105, 1240, 1236, cerberus.getHeight(), 0, 10, -1, -1).setInstance(cerberus));
	}

	/**
	 * Disposes of the content by moving the player and finalizing and or removing any left over content.
	 * 
	 * @param dispose the type of dispose
	 */
	public final void end(DisposeTypes dispose) {
		if (player == null) {
			return;
		}

		Server.getGlobalObjects().remove(new GlobalObject(23105, 1241, 1242, getHeight(), 0, 10, -1, -1).setInstance(this));
		Server.getGlobalObjects().remove(new GlobalObject(23105, 1240, 1242, getHeight(), 0, 10, -1, -1).setInstance(this));
		Server.getGlobalObjects().remove(new GlobalObject(23105, 1239, 1242, getHeight(), 0, 10, -1, -1).setInstance(this));
		Server.getGlobalObjects().remove(new GlobalObject(23105, 1240, 1236, getHeight(), 0, 10, -1, -1).setInstance(this));
	}

	@Override
	public void onDispose() {
		end(DisposeTypes.INCOMPLETE);
	}
	
}
