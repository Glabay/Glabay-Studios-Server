package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Opens the vote page in the default web browser.
 * 
 * @author Emiel
 */
public class Checkanima extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.sendMessage(Hespori.activeAnimaBonus());
	}


	@Override
	public Optional<String> getDescription() {
		return Optional.of("Tells you if the anima patch is active.");
	}

}

