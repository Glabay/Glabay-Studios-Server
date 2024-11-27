package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Kick a player from a group ironman group.
 */
public class Gim extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String loginName = input.toLowerCase();
		GroupIronmanGroup group = GroupIronmanRepository.getGroupForOffline(loginName).orElse(null);
		if (group == null) {
			c.sendMessage("No group that has player with login name '{}'.", loginName);
			return;
		}

		c.sendMessage("'{}' members (login names): {}", group.getName(),
				group.getMembers().stream().map(it -> "'" + it + "'").collect(Collectors.joining(", ")));
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Get a player's ironman group [::gim login name]");
	}
}
