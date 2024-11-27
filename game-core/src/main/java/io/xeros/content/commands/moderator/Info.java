package io.xeros.content.commands.moderator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

/**
 * Shows the IP and Mac address of a given player.
 * 
 * @author Emiel
 */
public class Info extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (c2.getRights().contains(Right.MODERATOR)) {
				c.sendMessage("You cannot do this to a member of staff.");
				return;
			}
			c.sendMessage("<col=CC0000>IP of " + c2.getDisplayName() + " : " + c2.connectedFrom);
			c.sendMessage("<col=CC0000>Mac Address of " + c2.getDisplayName() + " : " + c2.getMacAddress());
			c.sendMessage("<col=CC0000>Connected from:");
			for (String connected : c2.lastConnectedFrom) {
				c.sendMessage("<col=CD1000> > " + connected);
			}
		} else {
			c.sendMessage(input + " is not online. You can request the info of online players.");
		}
	}
}
