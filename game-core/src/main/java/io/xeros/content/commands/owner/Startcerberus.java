package io.xeros.content.commands.owner;

import io.xeros.content.bosses.Cerberus;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Update the shops.
 * 
 * @author Emiel
 *
 */
public class Startcerberus extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		Cerberus.init(player);
	}
}
