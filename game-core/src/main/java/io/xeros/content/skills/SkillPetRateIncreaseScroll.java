package io.xeros.content.skills;

import io.xeros.content.bonus.DoubleExperience;
import io.xeros.model.entity.player.Player;

import java.util.concurrent.TimeUnit;


public class SkillPetRateIncreaseScroll {

	private static final long TIME = TimeUnit.MINUTES.toMillis(30) / 600;

	public static void openScroll(Player player) {
		if (player.skillingPetRateScroll) {
			player.sendMessage("You already have a bonus skill pet rate going.");
			return;
		}

		player.skillingPetRateScroll = true;
		player.skillingPetRateTicks = TIME;
	}
	

}
	
