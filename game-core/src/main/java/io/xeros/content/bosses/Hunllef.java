package io.xeros.content.bosses;


import java.util.function.Consumer;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;


public class Hunllef extends LegacySoloPlayerInstance {

	public static final int MELEE_PROTECT = 9021;
	public static final int RANGED_PROTECT = 9022;
	public static final int MAGE_PROTECT = 9023;

	public static void rewardPlayers(Player player) {
		if (Boundary.isIn(player, Boundary.HUNLLEF_CAVE)) {
			Achievements.increase(player, AchievementType.HUNLLEF, 1);
			player.getPA().spellTeleport(3278, 6050, 0, false);
			player.getItems().addItemUnderAnyCircumstance(23776, 1);
			player.hunllefDead = false;
		}
		player.hunllefDead = false;
	}

	public static void start(Player c) {
		c.getPA().closeAllWindows();
		Hunllef instance = new Hunllef(c);
		Consumer<Player> start = (player) -> {
			c.resetDamageTaken();
			instance.add(c);
			c.getPA().closeAllWindows();
			respawn(instance);
		};

		AgilityHandler.delayFade(c, "CRAWL", 1172, 9943, instance.getHeight(), "You crawl into the cave.",
				"and end up at the Hunllef's lair", 1, start);
		c.getItems().deleteItem(23951, 1);
	}

	public static void respawn(InstancedArea instance) {
		Hunllef hunllef = (Hunllef) instance;
		hunllef.npc = NPCSpawning.spawn(9021, 1170, 9934, instance.getHeight(), 1, 30, true);
		instance.add(hunllef.npc);
	}

	private Player player;
	private NPC npc;

	public Hunllef(final Player player) {
		super(player, Boundary.HUNLLEF_BOSS_ROOM);
		this.player = player;
	}

	@Override
	public void onDispose() {
		if (npc != null) {
			if (npc.isDead())
				return;
			npc.unregister();
		}
		if (player != null && player.getInstance() == this) {
			player.removeFromInstance();
		}
		player = null;
	}
}
