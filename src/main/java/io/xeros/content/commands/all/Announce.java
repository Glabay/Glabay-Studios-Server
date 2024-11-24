package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Show the current position.
 * 
 * @author Noah
 *
 */
public class Announce extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		player.announce = !player.announce;
		player.sendMessage("@blu@Global rare announcements are now %s"
			.formatted(player.announce ? "@gre@enabled." : "@red@disabled."));
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Toggle drop announcements.");
	}
}
