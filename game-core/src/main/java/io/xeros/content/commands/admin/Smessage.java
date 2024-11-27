package io.xeros.content.commands.admin;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Send a fake server message to a given player.
 * 
 * @author Emiel
 */
public class Smessage extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split("-");
		if (args.length != 2) {
			c.sendMessage("Improper syntax; type ::smessage-player-message");
			return;
		}
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(args[0]);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c2.sendMessage(args[1]);
		} else {
			c.sendMessage(args[0] + " is not online. You can only send messages to online players.");
		}
	}
}
