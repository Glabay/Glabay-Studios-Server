package io.xeros.content.commands;

import java.sql.SQLException;
import java.util.Optional;

import io.xeros.model.entity.player.Player;

public abstract class Command {

	public String getCommand() {
		return getClass().getSimpleName().toLowerCase();
	}

	/**
	 * The command which is to be executed when it's called.
	 * 
	 * @param player The player to whom the command shouldc be applied.
	 * @param commandName
	 * @param input Any additional parameters.
	 * @throws SQLException
	 */
	public abstract void execute(Player player, String commandName, String input);
	/**
	 * Short description of the command which will be shown when using ::commands. Should be a small sentence at most or left untouched if no description should be given.
	 * 
	 * @return Short description of the command.
	 */
	public Optional<String> getDescription() {
		return Optional.empty();
	}

	public String getFormat() {
		return null;
	}

	/**
	 * List of parameters the command will take, which will be shown when using ::commands. Leave untouched if no parameters should be shown.
	 * 
	 * @return Parameters of the command.
	 */
	public Optional<String> getParameter() {
		return Optional.empty();
	}

	/**
	 * Hides the command from ::commands if the Optional value is set to true. Leave untouched if a command should be shown.
	 * 
	 * @return True if the command should be hidden from ::commands, false otherwise.
	 */
	public boolean isHidden() {
		return false;
	}

}
