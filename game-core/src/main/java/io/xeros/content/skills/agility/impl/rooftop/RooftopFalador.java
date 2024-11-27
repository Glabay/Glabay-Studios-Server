package io.xeros.content.skills.agility.impl.rooftop;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.agility.MarkOfGrace;
import io.xeros.model.entity.player.Player;

/**
 * Rooftop Agility Falador
 * 
 * @author Robbie
 */

public class RooftopFalador {

	public static final int ROUGH_WALL = 14898, TIGHT_ROPE = 14899, HAND_HOLDS = 14901, GAP1 = 14903,
							GAP2 = 14904, TIGHT_ROPE2 = 14905, TIGHT_ROPE3 = 14911, GAP3 = 14919,
							GAP4 = 14920, GAP5 = 14921, GAP6 = 14922, GAP7 = 14924, GAP8 = 14925;

	public static int[] FALADOR_OBJECTS = { ROUGH_WALL, TIGHT_ROPE, HAND_HOLDS, GAP1, GAP2, TIGHT_ROPE2,
											TIGHT_ROPE3, GAP3, GAP4, GAP5, GAP6, GAP7, GAP8 };

	public boolean execute(final Player c, final int objectId) {
		
		for (int id : FALADOR_OBJECTS) {
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "FALADOR");
			}
		}
		
		switch (objectId) {
		case ROUGH_WALL:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3036, 3342, 3, 2);
			c.getAgilityHandler().agilityProgress[0] = true;
		return true;
			
		case TIGHT_ROPE:
			c.getPA().movePlayer(3040, 3343, c.heightLevel);
			c.setForceMovement(3048, 3343, 0, 225, "EAST", 762);
			c.getAgilityHandler().agilityProgress[1] = true;
		return true;
			
		case HAND_HOLDS:
				AgilityHandler.delayEmote(c, "JUMP", 3048, 3363, 3, 2);
				c.getPA().movePlayer(3048, 3363, 3);
				c.getAgilityHandler().agilityProgress[2] = true;
		return true;
			
		case GAP1:
			if (AgilityHandler.failObstacle(c, 3048, 3359, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3047, 3361, 3, 2);
				c.getAgilityHandler().agilityProgress[3] = true;
		return true;
			
		case GAP2:
			if (AgilityHandler.failObstacle(c, 3043, 3362, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3041, 3361, 3, 2);
				c.getAgilityHandler().agilityProgress[4] = true;
		return true;
			
		case TIGHT_ROPE2:
			c.getPA().movePlayer(3034, 3361, c.heightLevel);
			c.setForceMovement(3027, 3355, 0, 225, "SOUTH-WEST", 762);
			c.getAgilityHandler().agilityProgress[5] = true;
		return true;
			
		case TIGHT_ROPE3:
			c.getPA().movePlayer(3026, 3353, c.heightLevel);
			c.setForceMovement(3020, 3353, 0, 225, "WEST", 762);
			c.getAgilityHandler().agilityProgress[6] = true;
		return true;
		
		case GAP3:
			if (AgilityHandler.failObstacle(c, 3019, 3351, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3017, 3349, 3, 2);
				c.getAgilityHandler().agilityProgress[7] = true;
		return true;
		
		case GAP4:
				AgilityHandler.delayEmote(c, "JUMP", 3014, 3346, 3, 2);
				c.getAgilityHandler().agilityProgress[8] = true;
		return true;
		
		case GAP5:
				AgilityHandler.delayEmote(c, "JUMP", 3012, 3342, 3, 2);
				c.getAgilityHandler().agilityProgress[9] = true;
		return true;
		
		case GAP6:
				AgilityHandler.delayEmote(c, "JUMP", 3012, 3333, 3, 2);
				c.getAgilityHandler().agilityProgress[10] = true;
		return true;
		
		case GAP7:
			if (AgilityHandler.failObstacle(c, 3018, 3330, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3019, 3334, 3, 2);
				c.getAgilityHandler().agilityProgress[11] = true;
		return true;
		
		case GAP8:
			c.getAgilityHandler().roofTopFinished(c, 11, 440, 16000);
			AgilityHandler.delayEmote(c, "JUMP", 3028, 3333, 0, 2);
			 Achievements.increase(c, AchievementType.AGIL, 1);
		return true;
		}
		
		return false;
	}

}