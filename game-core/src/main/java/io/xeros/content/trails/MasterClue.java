package io.xeros.content.trails;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class MasterClue {
	
	public static void exchangeClue(Player player) {
		if (player.getItems().getItemCount(19835, false) > 0) {
			player.sendMessage("You already have a master clue scroll, complete it and then come back.");
			return;
		}
		if (player.getItems().playerHasItem(2677) && player.getItems().playerHasItem(2801) && player.getItems().playerHasItem(2722)) {
			player.getItems().deleteItem(2677, 1);
			player.getItems().deleteItem(2801, 1);
			player.getItems().deleteItem(2722, 1);
			player.getItems().addItem(19835, 1);
			generateRequirement(player);
			player.sendMessage("Here you go, a master clue just for you;");
			player.sendMessage("A " + Skill.forId(player.masterClueRequirement[0]).name().toLowerCase() + " level of " + player.masterClueRequirement[1] + " and " + Skill.forId(player.masterClueRequirement[2]).name().toLowerCase() + " level of " + player.masterClueRequirement[3] + " is what you need.");
		} else {
			player.sendMessage("One of each, easy, medium and hard clue scroll is what I ask from you.");
			return;
		}
	}

	public static boolean checkRequirementOnOpen(Player player) {
		if (player.playerLevel[player.masterClueRequirement[0]] >= player.masterClueRequirement[1] && player.playerLevel[player.masterClueRequirement[2]] >= player.masterClueRequirement[3]) {
			player.masterClueRequirement[0] = 0;
			player.masterClueRequirement[1] = 0;
			player.masterClueRequirement[2] = 0;
			player.masterClueRequirement[3] = 0;
			return true;
		} else {
			player.sendMessage("A " + Skill.forId(player.masterClueRequirement[0]).name().toLowerCase() + " level of " + player.masterClueRequirement[1] + " and " + Skill.forId(player.masterClueRequirement[2]).name().toLowerCase() + " level of " + player.masterClueRequirement[3] + " is what you need.");
			return false;
		}
	}

	public static void generateRequirement(Player player) {
		int SKILL_ONE = Misc.random(21);
		int SKILL_TWO = Misc.random(21);
		int LEVEL_REQUIREMENT_ONE = Misc.random(29) + 70;
		int LEVEL_REQUIREMENT_TWO = Misc.random(29) + 70;
		
		player.masterClueRequirement[0] = SKILL_ONE;
		player.masterClueRequirement[1] = LEVEL_REQUIREMENT_ONE;
		player.masterClueRequirement[2] = SKILL_TWO;
		player.masterClueRequirement[3] = LEVEL_REQUIREMENT_TWO;
	}

}
