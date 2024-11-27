package io.xeros.content.skills;

import java.util.concurrent.TimeUnit;

import io.xeros.content.bonus.DoubleExperience;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;


/** 
 * @author Aaron Whittle
 * @date Sep 22th 2019
 */

public class DoubleExpScroll {

	/**
	 * Convert the amount of hours to milliseconds, divide by 600 to get the tick amount.
	 */
	private static final long TIME = TimeUnit.HOURS.toMillis(1) / 600;

	public static void openScroll(Player player) {
		if(DoubleExperience.isDoubleExperience()){
			player.sendMessage("@red@Bonus XP Weekend is @gre@active@red@, use it another day!");
			return;
		} else if (player.xpScroll) {
			player.sendMessage("You already have the xp bonus.");
			return;
		}

		player.xpScroll = true;
		player.xpScrollTicks = TIME;
	}

	/**
	 * Gives the player one hour of bonus xp.
	 */
	public static void giveBonusScrollXP(Player player) {
		player.xpScroll = true;
		player.xpScrollTicks = TIME;
		player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, 60);
		player.getQuestTab().updateInformationTab();
	}
	

}
	
