package io.xeros.content.skills.agility.impl.rooftop;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.agility.MarkOfGrace;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObjects;

/**
 * Rooftop Agility Al Kharid
 * 
 * @author Robbie
 */

public class RooftopAlkharid {

	public static final int ROUGH_WALL = 11633, 
			TIGHTROPE = 14398, CABLE = 14402, 
			ZIPLINE = 14403, TREE = 14404, 
			BEAMS = 11634, TIGHTROPE2 = 14409,
			GAP = 14399;
	
	public static int[] ALKHARID_OBJECTS = { ROUGH_WALL, TIGHTROPE, CABLE, ZIPLINE, TREE, BEAMS, TIGHTROPE2, GAP };

	public boolean execute(final Player c, final int objectId) {
		
		for (int id : ALKHARID_OBJECTS) {
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "ALKHARID");
			}
		}
		
		switch (objectId) {
		case ROUGH_WALL:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3273, 3192, 3, 2);
			c.getAgilityHandler().agilityProgress[0] = true;
		return true;
			
		case TIGHTROPE:
			if (AgilityHandler.failObstacle(c, 3274, 3176, 0)) {
		return false;
			}
			c.getPA().movePlayer(3272, 3181, c.heightLevel);
			c.setForceMovement(3272, 3171, 0, 250, "SOUTH", 762);
			c.getAgilityHandler().agilityProgress[1] = true;
			Server.getGlobalObjects().updateRegionObjects(c);
		return true;
			
		case CABLE:
			AgilityHandler.delayEmote(c, "CABLE", 3284, 3164, 3, 2);
			c.getAgilityHandler().agilityProgress[2] = true;
		return true;
			
		case ZIPLINE:
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
	                        c.getPA().movePlayer(3304, 3163, 1);
	                        c.facePosition(3315, 3163);
	                    break;
	                    case 1:
	                    	c.startAnimation(1601);
	                    break;
	                    case 2:
	                    	c.setForceMovement(3315, 3163, 0, 225, "EAST", 1602);
	                        c.getAgilityHandler().agilityProgress[3] = true;
	                        container.stop();
	                    break;
	                    }
	                }
	                @Override
	                public void onStopped() {
	                }
	            }, 2);
		return true;
			
		case TREE:
			if (AgilityHandler.failObstacle(c, 3351, 2971, 0)) {
				return false;
			}
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
                        AgilityHandler.delayEmote(c, "JUMP", 3318, 3169, 2, 1);
                        c.facePosition(3317, 3174);
                    break;
                    case 2:
                        c.startAnimation(3067);
                        AgilityHandler.delayEmote(c, "JUMP", 3317, 3174, 2, 1);
                        c.getAgilityHandler().agilityProgress[4] = true;
                        container.stop();
                    break;
                    }
                }
                @Override
                public void onStopped() {
                }
            }, 2);
		return true;
			
		case BEAMS:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3316, 3180, 3, 2);
			c.getAgilityHandler().agilityProgress[5] = true;
		return true;
			
		case TIGHTROPE2:
			c.getPA().movePlayer(3313, 3186, 3);
			c.setForceMovement(3302, 3186, 0, 250, "WEST", 762);
			c.getAgilityHandler().agilityProgress[6] = true;
		return true;
		
		case GAP:
			AgilityHandler.delayEmote(c, "JUMP", 3301, 3195, 0, 2);
			c.getAgilityHandler().roofTopFinished(c, 6, 180, 8000);
			Achievements.increase(c, AchievementType.AGIL, 1);
		return true;
		}
		return false;
	}

}
