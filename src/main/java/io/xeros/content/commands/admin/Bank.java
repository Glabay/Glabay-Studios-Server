package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Open the banking interface.
 * 
 * @author Emiel
 */
public class Bank extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getPA().player.itemAssistant.openUpBank();
		c.inBank = true;
	}
}
