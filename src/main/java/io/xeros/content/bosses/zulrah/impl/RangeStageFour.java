package io.xeros.content.bosses.zulrah.impl;

import java.util.Arrays;

import io.xeros.content.bosses.zulrah.DangerousEntity;
import io.xeros.content.bosses.zulrah.DangerousLocation;
import io.xeros.content.bosses.zulrah.SpawnDangerousEntity;
import io.xeros.content.bosses.zulrah.Zulrah;
import io.xeros.content.bosses.zulrah.ZulrahLocation;
import io.xeros.content.bosses.zulrah.ZulrahStage;
import io.xeros.model.entity.CombatType;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

public class RangeStageFour extends ZulrahStage {

	public RangeStageFour(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead() || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int ticks = container.getTotalTicks();
		if (ticks == 4) {
			zulrah.getNpc().setFacePlayer(false);
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player,
					Arrays.asList(DangerousLocation.EAST, DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.TOXIC_SMOKE, 40), 1);
		} else if (ticks == 16) {
			CycleEventHandler.getSingleton().addEvent(player,
					new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.MINION_NPC), 1);
		} else if (ticks == 26) {
			zulrah.getNpc().setFacePlayer(true);
			zulrah.changeStage(5, CombatType.MAGE, ZulrahLocation.SOUTH);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
		}
	}

}
