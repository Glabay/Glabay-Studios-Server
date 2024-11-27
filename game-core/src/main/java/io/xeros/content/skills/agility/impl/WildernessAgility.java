package io.xeros.content.skills.agility.impl;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.model.entity.player.Player;

/**
 * WildernessAgility
 * 
 * @author Andrew (I'm A Boss on Rune-Server and Mr Extremez on Mopar & Runelocus)
 */

public class WildernessAgility {

	public static final int WILDERNESS_PIPE_OBJECT = 23137, WILDERNESS_SWING_ROPE_OBJECT = 23132,
			WILDERNESS_STEPPING_STONE_OBJECT = 23556, WILDERNESS_LOG_BALANCE_OBJECT = 23542,
			WILDERNESS_ROCKS_OBJECT = 23640;

	public boolean wildernessCourse(final Player c, final int objectId) {
		switch (objectId) {
		case 23552:
		case 23555:
		case 23554:
			if (c.getAgilityHandler().hotSpot(c, 2998, 3916)) {
				c.setForceMovement(2998, 3931, 0, 400, "NORTH", 762);
				c.getPA().addSkillXPMultiplied(1, Player.playerAgility, true);
			}
			if (c.getAgilityHandler().hotSpot(c, 2998, 3931) || c.getAgilityHandler().hotSpot(c, 2997, 3931)) {
				c.setForceMovement(2998, 3916, 0, 400, "SOUTH", 762);
				c.getPA().addSkillXPMultiplied(1, Player.playerAgility, true);
			}
			if (c.getAgilityHandler().hotSpot(c, 2997, 3931)) {
				c.setForceMovement(2998, 3916, 0, 400, "SOUTH", 762);
				c.getPA().addSkillXPMultiplied(1, Player.playerAgility, true);
			}
			if (c.getAgilityHandler().hotSpot(c, 2996, 3931)) {
				c.setForceMovement(2998, 3916, 0, 400, "SOUTH", 762);
				c.getPA().addSkillXPMultiplied(1, Player.playerAgility, true);
			}
			return true;
			
		case WILDERNESS_PIPE_OBJECT: // pipe
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3004, 3937)) {
				c.setForceMovement(3004, 3950, 0, 400, "NORTH", c.getAgilityHandler().getAnimation(objectId));
			}
			c.getAgilityHandler().resetAgilityProgress();
			c.getAgilityHandler().agilityProgress[0] = true;

			return true;
		case WILDERNESS_SWING_ROPE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.absY >= 3955) {
				c.getPA().movePlayer(3005, 3958);
			}
			c.startAnimation(751);
			c.setForceMovement(3005, 3958, 0, 40, "NORTH", -1);
			if (c.getAgilityHandler().agilityProgress[0]) {
				c.getAgilityHandler().agilityProgress[1] = true;
			}
			return true;
		case WILDERNESS_STEPPING_STONE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			c.setForceMovement(2996, 3960, 0, 255, "WEST", c.getAgilityHandler().getAnimation(objectId));
			if (c.getAgilityHandler().agilityProgress[1] == true) {
				c.getAgilityHandler().agilityProgress[3] = true;
			}
			return true;

		case WILDERNESS_LOG_BALANCE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3002, 3945)) {
				c.setForceMovement(2994, 3945, 0, 200, "WEST", c.getAgilityHandler().getAnimation(objectId));
			}
			if (c.getAgilityHandler().agilityProgress[3] == true) {
				c.getAgilityHandler().agilityProgress[5] = true;
			}
			return true;

		case WILDERNESS_ROCKS_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			c.setForceMovement(c.absX, 3933, 0, 50, "SOUTH", c.getAgilityHandler().getAnimation(objectId));
			c.getAgilityHandler().lapFinished(c, 5, 571, 6000);
			c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.WILDERNESS_AGILITY);
			 Achievements.increase(c, AchievementType.AGIL, 1);
			return true;
		}
		return false;
	}

}
