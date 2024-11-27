package io.xeros.content.commands.helper;

import io.xeros.content.commands.Command;
import io.xeros.content.help.HelpDatabase;
import io.xeros.model.entity.player.Player;

/**
 * Opens an interface containing all help tickets.
 * 
 * @author Emiel
 */
public class Helpdb extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		HelpDatabase.getDatabase().openDatabase(c);
	}
}
