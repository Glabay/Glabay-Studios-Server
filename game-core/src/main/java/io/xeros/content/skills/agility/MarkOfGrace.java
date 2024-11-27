package io.xeros.content.skills.agility;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Mark of grace
 * 
 * @author Matt
 *
 */
public class MarkOfGrace {

	private static final int MARK_OF_GRACE = 11849;
	private static final int[][] SEERS_COORDINATES = {
			{ 2728, 3495, 3 }, 
			{ 2707, 3492, 2 }, 
			{ 2713, 3479, 2 },
			{ 2698, 3463, 2 } 
	};
	private static final int[][] VARROCK_COORDINATES = {
			{ 3219, 3418, 3 }, 
			{ 3202, 3417, 3 }, 
			{ 3195, 3416, 1 },
			{ 3196, 3404, 3 }, 
			{ 3193, 3393, 3 }, 
			{ 3205, 3403, 3 }, 
			{ 3218, 3395, 3 }, 
			{ 3240, 3411, 3 } 
	};
	private static final int[][] ARDOUGNE_COORDINATES = {
			{ 2671, 3303, 3 }, 
			{ 2663, 3318, 3 }, 
			{ 2655, 3318, 3 },
			{ 2653, 3312, 3 }, 
			{ 2651, 3307, 3 }, 
			{ 2653, 3302, 3 }, 
			{ 2656, 3297, 3 }, 
			{ 2668, 3297, 0 } 
	};

	public static void spawnMarks(Player player, String location) {
		int chance = 0;
		
		switch (location) {
		case "ARDOUGNE":
			int ardougne = 
						  player.getRechargeItems().hasItem(13121) ? 20
						: player.getRechargeItems().hasItem(13122) ? 23
						: player.getRechargeItems().hasItem(13123) ? 25
						: player.getRechargeItems().hasItem(13124) || player.getRechargeItems().hasItem(20760) ? 30 : 17;
			chance = player.playerLevel[Player.playerAgility] / ardougne;
			break;
			
		case "SEERS":
			int seers = player.getRechargeItems().hasItem(13137) ? 20 
					  : player.getRechargeItems().hasItem(13138) ? 23
					  : player.getRechargeItems().hasItem(13139) ? 25
					  : player.getRechargeItems().hasItem(13140) ? 30 : 17;
			chance = player.playerLevel[Player.playerAgility] / seers;
			break;
			
		case "VARROCK":
			int varrock = player.getRechargeItems().hasItem(13104) ? 20 
					 	: player.getRechargeItems().hasItem(13105) ? 23
					 	: player.getRechargeItems().hasItem(13106) ? 25
					 	: player.getRechargeItems().hasItem(13107) ? 30 : 17;
			chance = player.playerLevel[Player.playerAgility] / varrock;
			break;
		case "CANAFIS":
			int canafis = player.getRechargeItems().hasItem(13104) ? 20 
					 	: player.getRechargeItems().hasItem(13105) ? 23
					 	: player.getRechargeItems().hasItem(13106) ? 25
					 	: player.getRechargeItems().hasItem(13107) ? 30 : 17;
			chance = player.playerLevel[Player.playerAgility] / canafis;
			break;
		}
		if (Misc.random(chance) == 0) {
			if (System.currentTimeMillis() - player.lastMarkDropped < 3000) {
				return;
			}
			player.getItems().addItemUnderAnyCircumstance(11849, 1);
			player.lastMarkDropped = System.currentTimeMillis();
		}
	}

}
