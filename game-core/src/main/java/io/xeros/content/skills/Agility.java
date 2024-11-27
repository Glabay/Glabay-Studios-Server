package io.xeros.content.skills;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

/**
 * Agility.java
 * 
 * @author Acquittal
 *
 *
 **/

public class Agility {

	public Player client;

	public Agility(Player c) {
		client = c;
	}

	public void brimhavenMonkeyBars(Player c, String Object, int level, int x, int y, int a, int b, int xp) {
		if (c.playerLevel[Player.playerAgility] < level) {
			c.sendMessage("You need a Agility level of " + level + " to pass this " + Object + ".");
			return;
		}
		if (c.absX == a && c.absY == b) {
			c.getPA().walkTo3(x, y);
			c.getPA().addSkillXP(xp, Player.playerAgility, true);
			c.getPA().refreshSkill(Player.playerAgility);
		}
	}

	/*
	 * Wilderness course
	 */

	public void wildernessEntrance(Player c, String Object, int level, int x, int y, int a, int b) {
		if (c.playerLevel[Player.playerAgility] < level) {
			c.sendMessage("You need a Agility level of " + level + " to pass this " + Object + ".");
			return;
		}
		if (c.absX == a && c.absY == b) {
			c.getPA().walkTo3(x, y);
		}
	}

	public void doWildernessEntrance(final Player c, int x, int y, boolean reverse) {
		if (c.freezeTimer > 0) {
			return;
		}
		c.freezeTimer = 17;
		c.stopMovement();
		c.playerWalkIndex = 762;
		c.setUpdateRequired(true);
		c.appearanceUpdateRequired = true;
		c.getAgility().wildernessEntrance(c, "Door", 1, 0, reverse ? -15 : +15, x, y);// 2998, 3916
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				c.playerStandIndex = 0x328;
				c.playerTurnIndex = 0x337;
				c.playerWalkIndex = 0x333;
				c.playerTurn180Index = 0x334;
				c.playerTurn90CWIndex = 0x335;
				c.playerTurn90CCWIndex = 0x336;
				c.playerRunIndex = 0x338;
				c.setUpdateRequired(true);
				c.appearanceUpdateRequired = true;
				c.getPA().addSkillXPMultiplied(40, Player.playerAgility, true);
				c.getPA().refreshSkill(Player.playerAgility);
				container.stop();
			}

			@Override
			public void onStopped() {
			}
		}, 17);
	}
}