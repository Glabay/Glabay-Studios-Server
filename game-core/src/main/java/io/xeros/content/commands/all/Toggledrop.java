package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Toggles whether a warning will be shown when attempting to drop an item on the ground.
 * 
 * @author Emiel
 *
 */
public class Toggledrop extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.setDropWarning(!c.showDropWarning());
		if (c.showDropWarning()) {
		} else {
			c.sendMessage("You will @red@no longer@bla@ be warned when attempting to drop an item.");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Toggles the item drop warning on or off");
	}

}
