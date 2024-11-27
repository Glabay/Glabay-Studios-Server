package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;

import java.util.Optional;

/**
 * Kick a player from a group ironman group.
 */
public class Gimkick extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String loginName = input.toLowerCase();
		GroupIronmanGroup group = GroupIronmanRepository.getGroupForOffline(loginName).orElse(null);

		if (group == null) {
			c.sendMessage("No group for player with login name '" + loginName + "'.");
			return;
		}

		new DialogueBuilder(c).option("Kick '" + loginName + "' from '" + group.getName() + "'",
				new DialogueOption("Yes, kick player", plr -> complete(c, group, loginName)),
				DialogueOption.nevermind()
		).send();
	}

	private void complete(Player player, GroupIronmanGroup group, String loginName) {
		player.getPA().closeAllWindows();

		if (!group.isGroupMember(loginName))
			return;

		Player other = PlayerHandler.getPlayerByLoginName(loginName);
		if (other != null) {
			GroupIronmanRepository.removeFromGroup(other, group);
		} else {
			GroupIronmanRepository.removeFromGroup(loginName, group);
		}

		if (group.getJoined() > 0)
			group.setJoined(group.getJoined() - 1);

		player.sendMessage("Player with login name '" + loginName + "' removed from group '" + group.getName() + "' and added join");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Kick a player from GIM group by login name [::gimkick gim player]");
	}
}
