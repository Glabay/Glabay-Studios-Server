package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Opens the experience lock interface
 * 
 * @author Tyler
 */
public class Resetkdr extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (!c.getItems().playerHasItem(995, 5000000)) {
			c.sendMessage("@blu@You need at least @red@5M@blu@ to reset your kdr.");
			return;
			}
			c.getItems().deleteItem(995, 5000000);
			c.killcount = 0;
			c.deathcount = 0;
			c.sendMessage("@blu@You have succesfully reset your kdr.");
	        c.forcedChat("My Kill/Death ratio is " + c.killcount + " Kills: " + c.deathcount + " Deaths ");	
		}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Reset your kill/death ratio.");
	}

}
