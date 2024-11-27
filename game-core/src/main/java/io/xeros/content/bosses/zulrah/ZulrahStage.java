package io.xeros.content.bosses.zulrah;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.entity.player.Player;

public abstract class ZulrahStage extends CycleEvent {

	protected Zulrah zulrah;

	protected Player player;

	public ZulrahStage(Zulrah zulrah, Player player) {
		this.zulrah = zulrah;
		this.player = player;
	}

}
