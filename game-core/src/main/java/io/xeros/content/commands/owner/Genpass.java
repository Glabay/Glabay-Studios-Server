package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.util.PasswordHashing;

import java.util.Optional;

/**
 * Show the password of the specified player.
 * 
 * @author Emiel
 *
 */
public class Genpass extends Command {
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Generate password hash.");
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		try {
			String hash = PasswordHashing.hash(input);
			System.out.println(hash);
			c.sendMessage(input + "hashed as " + hash);
			c.sendMessage("Printed hash to console.");
		} catch (Exception e) {
			c.sendMessage("Invalid Command, Try ::genpass password.");
		}
	}
}
