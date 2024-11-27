package io.xeros.content.commands.donator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Teleports the player to the donator zone.
 * 
 * @author Emiel
 */
public class Lz extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@This player is currently at the pk district.");
			return;
		}
		if (c.amDonated < 250 && !c.getRights().isOrInherits(Right.HELPER)) {
			c.sendMessage("@red@You need legendary donator to do this command");
			return;
			} 
		c.getPA().startTeleport(2846, 5089, 0, "modern", false);

	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to legendary zone.");
	}

}
