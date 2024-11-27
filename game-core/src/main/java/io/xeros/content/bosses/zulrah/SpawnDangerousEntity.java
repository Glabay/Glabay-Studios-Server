package io.xeros.content.bosses.zulrah;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.xeros.Server;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

public class SpawnDangerousEntity extends CycleEvent {

	private final Zulrah zulrah;

	private final Player player;

	private final Queue<DangerousLocation> locations = new LinkedList<>();

	private final DangerousEntity entity;

	private int duration;

	public SpawnDangerousEntity(Zulrah zulrah, Player player, List<DangerousLocation> locations, DangerousEntity entity) {
		this.player = player;
		this.zulrah = zulrah;
		this.entity = entity;
		this.locations.addAll(locations);
	}

	public SpawnDangerousEntity(Zulrah zulrah, Player player, List<DangerousLocation> locations, DangerousEntity entity, int duration) {
		this(zulrah, player, locations, entity);
		this.duration = duration;
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (player == null || zulrah == null || zulrah.getNpc() == null || zulrah.getInstancedZulrah() == null
				|| zulrah.getNpc().getHealth().getCurrentHealth() == 0 || zulrah.getNpc().isDead()
				|| zulrah.getNpc().needRespawn) {
			container.stop();
			return;
		}
		zulrah.getNpc().setFacePlayer(false);
		int ticks = container.getTotalTicks();
		DangerousLocation location = locations.peek();
		if (location == null) {
			container.stop();
			zulrah.getNpc().facePlayer(player.getIndex());
			return;
		}
		if (ticks == 4 || ticks == 8 || ticks == 12 || ticks == 16) {
			for (Point point : location.getPoints()) {
				if (entity.equals(DangerousEntity.TOXIC_SMOKE)) {
					Server.getGlobalObjects().add(new GlobalObject(Zulrah.TOXIC_SMOKE, point.x, point.y, zulrah.getInstancedZulrah().getHeight(), 0, 10, duration, -1)
							.setInstance(zulrah.getInstancedZulrah()));
				} else if (entity.equals(DangerousEntity.MINION_NPC)) {
					NPC npc = NPCSpawning.spawnNpc(player, Zulrah.SNAKELING, point.x, point.y, zulrah.getInstancedZulrah().getHeight(), 0, 11, true, false);
					zulrah.getInstancedZulrah().add(npc);
				}
			}
			locations.remove();
		}
		if (ticks == 2 || ticks == 6 || ticks == 10 || ticks == 14) {
			zulrah.getNpc().facePosition(location.getPoints()[0].x, location.getPoints()[0].y);
			int npcX = zulrah.getNpc().getX();
			int npcY = zulrah.getNpc().getY();
			for (Point point : location.getPoints()) {
				int targetY = (npcX - (int) point.getX()) * -1;
				int targetX = (npcY - (int) point.getY()) * -1;
				int projectile = entity.equals(DangerousEntity.TOXIC_SMOKE) ? 1044 : 1047;
				player.getPA().createPlayersProjectile(npcX, npcY, targetX, targetY, 50, 85, projectile, 70, 0, -1, 65);
			}
			zulrah.getNpc().startAnimation(5068);
		}
	}

}
