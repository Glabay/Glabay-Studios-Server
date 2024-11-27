package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Transform a given player into an npc.
 * 
 * @author Emiel
 *
 */
public class Unpnpc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {

		c.isNpc = false;
		c.setUpdateRequired(true);
		c.appearanceUpdateRequired = true;
	}
}
