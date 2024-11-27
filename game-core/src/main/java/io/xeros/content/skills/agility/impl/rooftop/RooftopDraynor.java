package io.xeros.content.skills.agility.impl.rooftop;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.agility.MarkOfGrace;
import io.xeros.model.entity.player.Player;

/**
 * Rooftop Agility Draynor
 * 
 * @author Robbie
 */

public class RooftopDraynor {

	public static final int ROUGH_WALL = 11404, 
			TIGHTROPE = 11405, TIGHT_ROPE = 11406, 
			NARROW_WALL = 11430, UP_WALL = 11630, 
			GAP = 11631, CRATE = 11632;
	
	public static int[] DRAYNOR_OBJECTS = { ROUGH_WALL, TIGHTROPE, TIGHT_ROPE, NARROW_WALL, UP_WALL, GAP, CRATE };

	public boolean execute(final Player c, final int objectId) {
		
		for (int id : DRAYNOR_OBJECTS) {
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "DRAYNOR");
			}
		}
		
		switch (objectId) {
		case ROUGH_WALL:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3102, 3279, 3, 2);
			c.getAgilityHandler().agilityProgress[0] = true;
		return true;
			
		case TIGHTROPE:
			if (AgilityHandler.failObstacle(c, 3094, 3274, 0)) {
		return false;
			}
			c.setForceMovement(3090, 3277, 0, 200, "WEST", 762);
			c.getAgilityHandler().agilityProgress[1] = true;
		return true;
			
		case TIGHT_ROPE:
			 	c.getPA().movePlayer(3092, 3276, c.heightLevel);
				c.setForceMovement(3092, 3266, 0, 225, "SOUTH", 762);
				c.getAgilityHandler().agilityProgress[2] = true;
		return true;
			
		case NARROW_WALL:
			c.setForceMovement(3088, 3261, 0, 170, "SOUTH", 762);
			c.getAgilityHandler().agilityProgress[3] = true;
		return true;
			
		case UP_WALL:
			if (c.getAgilityHandler().agilityProgress[3] == true) {
				AgilityHandler.delayEmote(c, "JUMP", c.absX, 3255, 3, 2);
				c.getAgilityHandler().agilityProgress[4] = true;
			} else {
				c.appendDamage(null, 1, Hitmark.HIT);
				c.sendMessage("Apperantly I skipped a gap, ouch..");
			}
		return true;
			
		case GAP:
			AgilityHandler.delayEmote(c, "JUMP", 3096, 3256, 3, 2);
			c.getAgilityHandler().agilityProgress[5] = true;
		return true;
			
		case CRATE:
			c.getAgilityHandler().roofTopFinished(c, 5, 120, 8000);
			AgilityHandler.delayEmote(c, "JUMP", 3103, 3261, 0, 2);
			 Achievements.increase(c, AchievementType.AGIL, 1);
		return true;
		}
		return false;
	}

}
