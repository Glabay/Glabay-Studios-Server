package io.xeros.content.skills;

import io.xeros.model.entity.player.Player;

public class SkillHandler {

	public static boolean[] isSkilling = new boolean[25];

	public static boolean noInventorySpace(Player c, String skill) {
		if (c.getItems().freeSlots() == 0) {
			c.sendMessage("You haven't got enough inventory space to continue " + skill + "!");
			c.getPA().sendStatement("You haven't got enough inventory space to continue " + skill + "!");
			return false;
		}
		return true;
	}
	public static void resetPlayerSkillVariables(Player c) {
		for (int i = 0; i < 20; i++) {
			if (c.playerSkilling[i]) {
				for (int l = 0; l < 15; l++) {
					c.playerSkillProp[i][l] = -1;
				}
			}
		}
	}

	public static boolean hasRequiredLevel(final Player c, int id, int lvlReq, String skill, String event) {
		if (c.playerLevel[id] < lvlReq) {
			c.sendMessage("You need a " + skill + " level of " + lvlReq + " to " + event + ".");
			return false;
		}
		return true;
	}

	public static void deleteTime(Player c) {
		c.amountToCook--;
	}
}