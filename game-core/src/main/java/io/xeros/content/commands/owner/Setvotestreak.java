package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.content.vote_panel.VoteUser;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Setvotestreak extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		try {
			String[] data = input.split("-");
			String displayName = data[0];
			int streak = Integer.parseInt(data[1]);

			Player other = PlayerHandler.getPlayerByDisplayName(displayName);
			if (other == null) {
				c.sendMessage("No player with display name {}", displayName);
				return;
			}

			VoteUser user = VotePanelManager.getUser(other);
			if (user == null) {
				other.sendMessage("Player doesn't have a VotePanel user.");
				return;
			}

			user.setDayStreak(streak);
			c.sendMessage("Set {} streak to {}", displayName, streak);
			other.sendMessage("{} has set your vote streak to {}.", c.getDisplayNameFormatted(), streak);
		} catch (Exception e) {
			c.sendMessage("Invalid format, ::setvotestreak-player name-4");
			e.printStackTrace(System.err);
		}
	}

}
