package io.xeros.content.commands.admin;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Changes the walk animation of a player.
 * 
 * @author Emiel
 *
 */
public class Setwalk extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		try {
			String[] args = input.split("-");
			if (args.length != 2) {
				throw new IllegalArgumentException();
			}
			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(args[0]);
			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();
				int walkAnim = Integer.parseInt(args[1]);
				walkAnim = walkAnim > 0 && walkAnim < 7157 ? walkAnim : 819;
				c2.playerWalkIndex = walkAnim;
				c2.getPA().requestUpdates();
			} else {
				throw new IllegalStateException();
			}
		} catch (IllegalStateException e) {
			c.sendMessage("You can only use the command on online players.");
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::setwalk-player-walkId");
		}
	}
}
