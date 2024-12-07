package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import static io.xeros.Configuration.HOME_POSITION;

/**
 * Teleport the player to home.
 * 
 * @author Emiel
 */
public class Home extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) return;

		if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
			player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		if (player.getPosition().inWild()) {
			player.sendMessage("You can't use this command in the wilderness.");
			return;
		}
		player.getPlayerAssistant().startTeleport(HOME_POSITION, "modern",  true);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to home");
	}

}
