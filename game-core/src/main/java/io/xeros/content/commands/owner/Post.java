package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.tradingpost.Listing;
import io.xeros.model.entity.player.Player;

public class Post extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Listing.openPost(c, false);
	}
}
