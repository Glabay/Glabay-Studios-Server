package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Show the current position.
 * 
 * @author Emiel
 *
 */
public class Invincible extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (player.invincible) {
			player.invincible = false;
			player.sendMessage("Invincibility Disabled.");
		} else {
			player.invincible = true;
			player.sendMessage("Invincibility Enabled.");
		}
		
	}
}
