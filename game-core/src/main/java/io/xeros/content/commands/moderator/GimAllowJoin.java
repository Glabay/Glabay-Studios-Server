package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;

import java.util.Optional;

/**
 * Kick a player from a group ironman group.
 */
public class GimAllowJoin extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Player other = PlayerHandler.getPlayerByDisplayName(input.toLowerCase());

		if (other == null) {
			c.sendMessage("No player online with display name: " + input);
			return;
		}

		GroupIronmanGroup group = GroupIronmanRepository.getGroupForOffline(other.getLoginName()).orElse(null);
		if (group != null) {
			c.sendMessage("Player is already in a group, [::gimkick display name] them first.");
			return;
		}

		other.setJoinedIronmanGroup(false);
		c.sendMessage("Player {} can now join another Ironman Group.", other.getDisplayNameFormatted());
		other.sendMessage("You can now join another Ironman Group.");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Allow a player to join another ironman group.");
	}
}
