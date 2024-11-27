package io.xeros.content.skills.agility.impl.rooftop;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.agility.MarkOfGrace;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

/**
 * Rooftop Agility Rellekka
 * 
 * @author Robbie
 */

public class RooftopRellekka {

	public static final int ROUGH_WALL = 14964, GAP1 = 14947, TIGHTROPE = 14987, GAP2 = 14990,
							GAP3 = 14991, TIGHTROPE2 = 14992, FISH = 14994;

	public static int[] RELLEKKA_OBJECTS = { ROUGH_WALL, GAP1, TIGHTROPE, GAP2, GAP3, TIGHTROPE2,
											FISH };

	public boolean execute(final Player c, final int objectId) {
		
		for (int id : RELLEKKA_OBJECTS) {
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "RELLEKKA");
			}
		}
		
		switch (objectId) {
		case ROUGH_WALL:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 2626, 3676, 3, 2);
			c.getAgilityHandler().agilityProgress[0] = true;
		return true;
			
		case GAP1:
			if (AgilityHandler.failObstacle(c, 2622, 3670, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 2622, 3668, 3, 2);
				c.getAgilityHandler().agilityProgress[1] = true;
		return true;
			
		case TIGHTROPE:
			c.getPA().movePlayer(2622, 3658, c.heightLevel);
			c.facePosition(2626, 3654);
			c.setForceMovement(2626, 3654, 0, 250, "SOUTH", 762);
			c.getAgilityHandler().agilityProgress[2] = true;
		return true;
			
		case GAP2:
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                int ticks;
                @Override
                public void execute(CycleEventContainer container) {
                    if (c.isDisconnected()) {
                        onStopped();
                        return;
                    }
                    switch (ticks++) {
                    case 0:
                    	c.startAnimation(3067);
                        AgilityHandler.delayEmote(c, "JUMP", 2629, 3658, 3, 1);
                    break;
                    case 1:
                    	c.facePosition(2631, 3662);
                    	AgilityHandler.delayEmote(c, "HANG_ON_POST", 2635, 3658, 3, 1);
                    break;
                    case 2:
                        c.setForceMovement(2640, 3653, 0, 250, "SOUTH", 762);
                        c.getAgilityHandler().agilityProgress[3] = true;
                        c.stopAnimation();
                        container.stop();
                    break;
                    }
                }
                @Override
                public void onStopped() {
                }
            }, 3);
		return true;
			
		case GAP3:
			if (AgilityHandler.failObstacle(c, 3048, 3359, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 2643, 3657, 3, 2);
				c.getAgilityHandler().agilityProgress[4] = true;
		return true;
			
		case TIGHTROPE2:
			c.getPA().movePlayer(2647, 3663, c.heightLevel);
			c.setForceMovement(2655, 3671, 0, 350, "NORTH-EAST", 762);
			c.getAgilityHandler().agilityProgress[5] = true;
		return true;
			
		case FISH:
			c.getAgilityHandler().roofTopFinished(c, 5, 780, 25000);
			AgilityHandler.delayEmote(c, "JUMP", 2653, 3676, 0, 2);
			 Achievements.increase(c, AchievementType.AGIL, 1);
		return true;
		}
		return false;
	}

}
