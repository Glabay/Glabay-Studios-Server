package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class Dice extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		if (c.getPosition().inWild()) {
			return;
		}
		c.getPA().spellTeleport(3111, 3506, 0, false);
		c.sendMessage("@red@[WARNING] Recording can help if a scam occurs.");

	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teles you to gambling area");
	}

}
