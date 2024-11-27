package io.xeros.content.skills.agility.impl.rooftop;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.agility.MarkOfGrace;
import io.xeros.model.entity.player.Player;

/**
 * Rooftop Agility Canafis
 * 
 * @author Robbie
 */

public class RooftopCanafis {

	public static final int TALL_TREE = 14843, 
			GAP = 14844, GAP2 = 14845, 
			GAP3 = 14848, GAP4 = 14846, 
			VAULT = 14894, GAP5 = 14847,
			FINAL_GAP = 14897;
	
	public static int[] CANAFIS_OBJECTS = { TALL_TREE, GAP, GAP2, GAP3, GAP4, VAULT, GAP5, FINAL_GAP };

	public boolean execute(final Player c, final int objectId) {
		
		for (int id : CANAFIS_OBJECTS) {
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "CANAFIS");
			}
		}
		
		switch (objectId) {
		case TALL_TREE:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3506, 3492, 2, 2);
			c.getAgilityHandler().agilityProgress[0] = true;
		return true;
			
		case GAP:
			if (AgilityHandler.failObstacle(c, 3504, 3500, 0)) {
		return false;
			}
			AgilityHandler.delayEmote(c, "JUMP", 3503, 3504, 2, 2);
			c.getAgilityHandler().agilityProgress[1] = true;
		return true;
			
		case GAP2:
			if (AgilityHandler.failObstacle(c, 3495, 3501, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3492, 3504, 2, 2);
				c.getAgilityHandler().agilityProgress[2] = true;
		return true;
			
		case GAP3:
			if (AgilityHandler.failObstacle(c, 3483, 3499, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3479, 3499, 3, 2);
				c.getAgilityHandler().agilityProgress[3] = true;
		return true;
			
		case GAP4:
			if (AgilityHandler.failObstacle(c, 3480, 3489, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3478, 3486, 2, 2);
				c.getAgilityHandler().agilityProgress[4] = true;
		return true;
			
		case VAULT:
			AgilityHandler.delayEmote(c, "CABLE", 3489, 3476, 3, 2);
			c.getAgilityHandler().agilityProgress[5] = true;
		return true;
			
		case GAP5:
			if (AgilityHandler.failObstacle(c, 3506, 3476, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3510, 3475, 2, 2);
				c.getAgilityHandler().agilityProgress[6] = true;
		return true;
		
		case FINAL_GAP:
			c.getAgilityHandler().roofTopFinished(c, 6, 240, 14000);
			AgilityHandler.delayEmote(c, "JUMP", 3508, 3485, 0, 2);
			 Achievements.increase(c, AchievementType.AGIL, 1);
		return true;
		}
		return false;
	}

}
