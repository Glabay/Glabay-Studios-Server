package io.xeros.model.cycleevent.impl;

import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.content.bosses.hespori.*;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class BonusApplianceEvent extends Event<Object> {
	
	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCycles(1, TimeUnit.SECONDS);

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public BonusApplianceEvent() {
		super("", new Object(), INTERVAL);
	}

	private void bonusExpiredMessage(String action) {
		PlayerHandler.executeGlobalMessage(Wogw.WOGW_MESSAGE_HEADER + "The Well of Goodwill is no longer granting " + action + ".");
	}

	@Override
	public void execute() {
		if (Wogw.EXPERIENCE_TIMER > 0) {
			Wogw.EXPERIENCE_TIMER--;
			if (Wogw.EXPERIENCE_TIMER == 1) {
				bonusExpiredMessage("bonus experience");
			}
		}
		if (Wogw.PC_POINTS_TIMER > 0) {
			Wogw.PC_POINTS_TIMER--;
			if (Wogw.PC_POINTS_TIMER == 1) {
				bonusExpiredMessage("bonus PC points");
			}
		}
		if (Configuration.DOUBLE_DROPS_TIMER > 0) {
			Configuration.DOUBLE_DROPS_TIMER--;
			if (Configuration.DOUBLE_DROPS_TIMER == 1) {
				bonusExpiredMessage("double drops");
			}
		}
		if (Wogw._20_PERCENT_DROP_RATE_TIMER > 0) {
			Wogw._20_PERCENT_DROP_RATE_TIMER--;
			if (Wogw._20_PERCENT_DROP_RATE_TIMER == 1) {
				bonusExpiredMessage("+20% drop rate");
			}
		}

		/*
		 * Hespori Seeds
		 */
		if (Hespori.ATTAS_TIMER > 0) {
			Hespori.ATTAS_TIMER--;
			if (Hespori.ATTAS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Attas plant is no longer granting XP!");
				new AttasBonus().deactivate();
			}
		}
		if (Hespori.KRONOS_TIMER > 0) {
			Hespori.KRONOS_TIMER--;
			if (Hespori.KRONOS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Kronos plant is no longer granting double Raids 1 keys!");
				new KronosBonus().deactivate();
			}
		}
		if (Hespori.IASOR_TIMER > 0) {
			Hespori.IASOR_TIMER--;
			if (Hespori.IASOR_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Iasor plant is no longer granting drop rate bonus!");
				new IasorBonus().deactivate();
			}
		}

		if (Hespori.GOLPAR_TIMER > 0) {
			Hespori.GOLPAR_TIMER--;
			if (Hespori.GOLPAR_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Golpar plant is no longer granting more loot!");
				new GolparBonus().deactivate();
			}
		}
		if (Hespori.BUCHU_TIMER > 0) {
			Hespori.BUCHU_TIMER--;
			if (Hespori.BUCHU_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Buchu plant is no longer granting 2x Boss points!");
				new BuchuBonus().deactivate();
			}
		}
		if (Hespori.KELDA_TIMER > 0) {
			Hespori.KELDA_TIMER--;
			if (Hespori.KELDA_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Kelda plant is no longer granting 2x Larren's Keys!");
				new KeldaBonus().deactivate();
			}
		}
		if (Hespori.NOXIFER_TIMER > 0) {
			Hespori.NOXIFER_TIMER--;
			if (Hespori.NOXIFER_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Noxifer plant is no longer granting 2x Slayer points!");
				new NoxiferBonus().deactivate();
			}
		}
		if (Hespori.CELASTRUS_TIMER > 0) {
			Hespori.CELASTRUS_TIMER--;
			if (Hespori.CELASTRUS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@The Celastrus plant is no longer granting x2 Brimstone keys!");
				new CelastrusBonus().deactivate();
			}
		}


	}
}
