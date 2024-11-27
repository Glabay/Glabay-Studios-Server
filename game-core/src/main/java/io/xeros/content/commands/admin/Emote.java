package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Force the player to perform a given emote.
 * @author Emiel
 * 
 * And log if args extend to 2.
 * @author Matt
 *
 */
public class Emote extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split("-");
		if (Integer.parseInt(args[0]) > 10000) {
			c.sendMessage("Max animation id is: 10000");
			return;
		}
		if (args.length != 2) {
			c.startAnimation(Integer.parseInt(args[0]));
			c.getPA().requestUpdates();
			c.sendMessage("Performing emote: " + Integer.parseInt(args[0]));
			c.emoteCommandId = Integer.parseInt(args[0]);
		} else {
			
			c.startAnimation(Integer.parseInt(args[0]));
			c.getPA().requestUpdates();
			c.sendMessage("Performing emote: " + Integer.parseInt(args[0]));
			c.sendMessage("Logging info: Emote: "+ args[0] +" Description: "+ args[1]);
		}
	}
}
