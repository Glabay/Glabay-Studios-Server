package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.leaderboards.LeaderboardInterface;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class Leaderboards extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		LeaderboardInterface.openInterface(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the leaderboards interface.");
	}
}
