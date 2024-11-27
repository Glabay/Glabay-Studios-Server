package io.xeros.content.commands.admin;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Executing bonus events by {String input}
 * 
 * @author Matt
 */

public class Bonus extends Command {

	public void execute(Player player, String commandName, String input) {
		
		switch (input) {
		case "":
			player.sendMessage("@red@Usage: ::bonus xp, vote, pc, pkp, drops");
			break;
		
		case "xp":
			Configuration.BONUS_WEEKEND = !Configuration.BONUS_WEEKEND;
			player.sendMessage("Bonus XP is now " + (Configuration.BONUS_WEEKEND ? "enabled" : "disabled") + ".");
			break;

		case "vote":
			Configuration.DOUBLE_VOTE_INCENTIVES = !Configuration.DOUBLE_VOTE_INCENTIVES;
			player.sendMessage("Double vote incentives are now " + (Configuration.DOUBLE_VOTE_INCENTIVES ? "enabled" : "disabled") + ".");
			break;

		case "pc":
			Configuration.BONUS_PC = !Configuration.BONUS_PC;
			player.sendMessage("Bonus pc is now " + (Configuration.BONUS_PC ? "enabled" : "disabled") + ".");
			break;

		case "pkp":
			Configuration.DOUBLE_PKP = !Configuration.DOUBLE_PKP;
			player.sendMessage("Double pkp is now " + (Configuration.DOUBLE_PKP ? "enabled" : "disabled") + ".");
			break;

		case "drops":
			Configuration.DOUBLE_DROPS = !Configuration.DOUBLE_DROPS;
			player.sendMessage("Double drops are now " + (Configuration.DOUBLE_DROPS ? "enabled" : "disabled") + ".");
			break;	

		case "pursuit":
			Configuration.wildyPursuit = !Configuration.wildyPursuit;
			player.sendMessage("Wildy Pursuit is now " + (Configuration.wildyPursuit ? "enabled" : "disabled") + ".");
			break;		
		}
	}

}
