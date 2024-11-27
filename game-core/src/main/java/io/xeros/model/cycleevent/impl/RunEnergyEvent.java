package io.xeros.model.cycleevent.impl;

import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;

public class RunEnergyEvent extends Event<Player> {

	/**
	 * The maximum amount of ticks that we have to wait for our run to regenerate
	 */
	private static final int MAXIMUM_TICKS = 12;

	/**
	 * The minimum amount of ticks that we have to wait for our run to regenerate
	 */
	private static final int MINIMUM_TICKS = 10;

	/**
	 * The amount of agility levels that impact run energy regeneration
	 */
	private static final int INTERVAL = 99 / (MAXIMUM_TICKS - MINIMUM_TICKS);

	/**
	 * The amount of ticks that must pass before energy can be restored
	 */
	private int ticksRequired;

	public RunEnergyEvent(Player attachment, int ticks) {
		super(attachment, ticks);
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			super.stop();
			return;
		}
		if (attachment.getRunEnergy() == 100 || attachment.getRunningDistanceTravelled() > 0) {
			return;
		}
		if (super.getElapsedTicks() >= ticksRequired) {
			attachment.setRunEnergy(attachment.getRunEnergy() + 1, true);
			ticksRequired = super.getElapsedTicks() + updateTicksRequired();
		}
	}

	private final int updateTicksRequired() {
		int level = Integer.min(99, attachment.playerLevel[Skill.AGILITY.getId()]);
		int reduction = level < INTERVAL ? 0 : level / INTERVAL;
		return Integer.max(MINIMUM_TICKS, MAXIMUM_TICKS - reduction);
	}

}
