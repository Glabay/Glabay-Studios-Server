package io.xeros.content.skills.agility.impl;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

/**
 * Barbarian Agility
 * 
 * @author Matt
 */

public class BarbarianAgility {

	public static final int BARBARIAN_SWING_ROPE_OBJECT = 23131, BARBARIAN_LOG_BALANCE_OBJECT = 23144, BARBARIAN_NET_OBJECT = 20211, BARBARIAN_LEDGE_OBJECT = 23547,
			BARBARIAN_LADDER_OBJECT = 16682, BARBARIAN_WALL_OBJECT = 1948;

	public boolean barbarianCourse(final Player c, final int objectId) {
		switch (objectId) {

		case BARBARIAN_SWING_ROPE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			c.getAgilityHandler().resetAgilityProgress();
			if (c.getAgilityHandler().hotSpot(c, 2551, 3554) || c.getAgilityHandler().hotSpot(c, 2550, 3554)) {
				c.getAgilityHandler().move(c, 0, -1, c.getAgilityHandler().getAnimation(objectId), -1);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.isDisconnected()) {
							container.stop();
							return;
						}
						c.getPlayerAssistant().movePlayer(2551, 3549, 0);
						c.getAgilityHandler().agilityProgress[0] = true;
						c.getAgilityHandler().lapProgress(c, 0, objectId);
						container.stop();
					}
					@Override
					public void onStopped() {

					}
				}, 1);
			}
			return true;

		case BARBARIAN_LOG_BALANCE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 2551, 3546) || (c.getAgilityHandler().hotSpot(c, 2550, 3547))) {
				if(c.absY != 3546) {
					c.getPlayerAssistant().movePlayer(2550, 3546, 0);
				}
				c.setForceMovement(2541, 3546, 0, 200, "WEST", c.getAgilityHandler().getAnimation(objectId));
				if (c.getAgilityHandler().agilityProgress[0] == true) {
					c.getAgilityHandler().lapProgress(c, 0, objectId);
					c.getAgilityHandler().agilityProgress[1] = true;
				}

			} else if (c.absX > 2541 && c.absX < 2551 && c.absY == 3546) {
				c.getPlayerAssistant().movePlayer(2541, 3546, 0);
				c.getAgilityHandler().stopEmote(c);
			}
			return true;

		case BARBARIAN_NET_OBJECT: // pipe
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			AgilityHandler.delayEmote(c, "CLIMB_UP", 2538, c.absY, 1, 2);
			if (c.getAgilityHandler().agilityProgress[1] == true) {
				c.getAgilityHandler().lapProgress(c, 1, objectId);
				c.getAgilityHandler().agilityProgress[2] = true;
			}
			return true;

		case BARBARIAN_LEDGE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 2536, 3547)) {
				//c.getAgilityHandler().move(c, -4, 0, c.getAgilityHandler().getAnimation(objectId), -1);
				c.setForceMovement(2532, 3547, 0, 200, "WEST", c.getAgilityHandler().getAnimation(objectId));
			} else if (c.absX > 2532 && c.absX < 2536 && c.absY == 3547) {
				c.getPlayerAssistant().movePlayer(2532, 3547, 1);
				c.getAgilityHandler().stopEmote(c);
			}

			if (c.getAgilityHandler().agilityProgress[2] == true) {
				c.getAgilityHandler().lapProgress(c, 2, objectId);
				c.getAgilityHandler().agilityProgress[3] = true;
			}
			return true;

		case BARBARIAN_LADDER_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.absX, c.absY, 0, 2);
			if (c.getAgilityHandler().agilityProgress[3] == true) {
				c.getAgilityHandler().lapProgress(c, 3, objectId);
				c.getAgilityHandler().agilityProgress[4] = true;
			}
			return true;

		case BARBARIAN_WALL_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if(c.absX==2543){
				return false;
			}
			if (c.getAgilityHandler().agilityProgress[4] == true) {
				//c.getAgilityHandler().move(c, 2, 0, c.getAgilityHandler().getAnimation(objectId), -1);
				c.setForceMovement(2543, 3553, 0, 200, "EAST", c.getAgilityHandler().getAnimation(objectId));
				c.getAgilityHandler().agilityProgress[5] = true;
				c.getAgilityHandler().lapFinished(c, 5, 154, 8000);
				 Achievements.increase(c, AchievementType.AGIL, 1);
				c.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.BARBARIAN_AGILITY);
			} else {
				c.setForceMovement(2543, 3553, 0, 200, "EAST", c.getAgilityHandler().getAnimation(objectId));
			}
			return true;
		}
		return false;
	}

}
