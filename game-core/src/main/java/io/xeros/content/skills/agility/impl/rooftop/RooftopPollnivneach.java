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
 * Rooftop Agility Pollnivneach
 * 
 * @author Robbie
 */

public class RooftopPollnivneach {

	public static final int BASKET = 14935, STALL = 14936, BANNER = 14937, GAP1 = 14938,
							TREE = 14939, WALL = 14940, BARS = 14941, TREE2 = 14944,
							LINE = 14945, LADDER = 6260;

	public static int[] POLLNIVNEACH_OBJECTS = { BASKET, STALL, BANNER, GAP1, TREE, WALL,
											BARS, TREE2, LINE, LADDER };

	public boolean execute(final Player c, final int objectId) {
		
		for (int id : POLLNIVNEACH_OBJECTS) {
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");
			}
		}
		
		switch (objectId) {
		case BASKET:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3351, 2964, 1, 2);
			c.getAgilityHandler().agilityProgress[0] = true;
		return true;
			
		case STALL:
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
                        AgilityHandler.delayEmote(c, "JUMP", 3349, 2972, 1, 1);
                        c.facePosition(3352, 2973);
                    break;
                    case 2:
                        c.startAnimation(3067);
                        AgilityHandler.delayEmote(c, "JUMP", 3352, 2974, 1, 1);
                        c.getAgilityHandler().agilityProgress[1] = true;
                        container.stop();
                    break;
                    }
                }
                @Override
                public void onStopped() {
                }
            }, 2);
		return true;
			
		case BANNER:
			 c.startAnimation(3067);
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
                        AgilityHandler.delayEmote(c, "HANG_ON_POST", 3357, 2978, 2, 1);
                        c.facePosition(3358, 2978);
                    break;
                    case 1:
                    	c.startAnimation(1118);
                    break;
                    case 2:
                        AgilityHandler.delayEmote(c, "HANG_ON_POST", 3360, 2978, 1, 1);
                        c.getAgilityHandler().agilityProgress[2] = true;
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
			
		case GAP1:
			if (AgilityHandler.failObstacle(c, 3048, 3359, 0)) {
				return false;
			}
				AgilityHandler.delayEmote(c, "JUMP", 3366, 2976, 1, 2);
				c.getAgilityHandler().agilityProgress[3] = true;
		return true;
			
		case TREE:
			if (AgilityHandler.failObstacle(c, 3366, 2979, 0)) {
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
                        AgilityHandler.delayEmote(c, "JUMP", 3368, 2979, 3, 1);
                        c.facePosition(3368, 2982);
                    break;
                    case 2:
                        c.startAnimation(3067);
                        AgilityHandler.delayEmote(c, "JUMP", 3367, 2982, 1, 1);
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
			
		case WALL:
			c.facePosition(3365, 2983);
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3365, 2983, 2, 2);
			c.getAgilityHandler().agilityProgress[5] = true;
		return true;
			
		case BARS:
			c.getPA().movePlayer(3358, 2984, c.heightLevel);
			c.setForceMovement(3358, 2992, 0, 250, "WEST", 744);
			c.getAgilityHandler().agilityProgress[6] = true;
		return true;
		
		case TREE2:
			if (AgilityHandler.failObstacle(c, 3358, 2998, 0)) {
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
                        AgilityHandler.delayEmote(c, "JUMP", 3360, 2997, 2, 1);
                        c.facePosition(3359, 3000);
                    break;
                    case 2:
                        c.startAnimation(3067);
                        AgilityHandler.delayEmote(c, "JUMP", 3359, 3000, 2, 1);
                        c.getAgilityHandler().agilityProgress[7] = true;
                        container.stop();
                    break;
                    }
                }
                @Override
                public void onStopped() {
                }
            }, 2);
		return true;
		
		case LINE:
			if (AgilityHandler.failObstacle(c, 3361, 3000, 0)) {
				return false;
			}
			c.facePosition(3363, 3000);
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
                        AgilityHandler.delayEmote(c, "JUMP", 3364, 3000, 2, 1);
                        c.facePosition(3364, 3002);
                    break;
                    case 2:
                        c.startAnimation(3067);
                        AgilityHandler.delayEmote(c, "JUMP", 3364, 3001, 1, 1);
                        c.getAgilityHandler().agilityProgress[8] = true;
                        container.stop();
                    break;
                    }
                }
                @Override
                public void onStopped() {
                }
            }, 2);
		return true;
		
		case LADDER:
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3364, 3002, 0, 1);
				c.getAgilityHandler().roofTopFinished(c, 8, 890, 22000);
				 Achievements.increase(c, AchievementType.AGIL, 1);
		return true;
		}
		
		return false;
	}

}
