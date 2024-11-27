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
public class E extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");

		if (args.length == 1) {
			c.startAnimation(Integer.parseInt(args[0]));
			c.sendMessage("Performing animation: " + Integer.parseInt(args[0]));
			c.emoteCommandId = Integer.parseInt(args[0]);
			
		} else {

			switch (args[1]) {
			case "plus":
				c.startAnimation(c.emoteCommandId);
				c.sendMessage("Performing animation: " + c.emoteCommandId);
				c.emoteCommandId += Integer.parseInt(args[2]);
				break;

			case "minus":
				c.startAnimation(c.emoteCommandId);
				c.sendMessage("Performing animation: " + c.emoteCommandId);
				c.emoteCommandId -= Integer.parseInt(args[2]);
				break;
			}
		}
	}
}
