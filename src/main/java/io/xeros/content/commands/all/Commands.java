package io.xeros.content.commands.all;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import io.xeros.content.commands.Command;
import io.xeros.content.commands.CommandManager;
import io.xeros.content.commands.CommandPackage;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Shows a list of commands.
 * 
 * @author Emiel
 *
 */
public class Commands extends Command {

	public static void displayCommandsInterface(Player c, String... ignore) {
		Map<String, List<Command>> commandMap = new HashMap<>();
		List<Command> allCommands = new ArrayList<>(CommandManager.getCommands(c, ignore));
		if (allCommands.isEmpty()) {
			c.sendMessage("No more commands to display.");
			return;
		}

		allCommands.sort((c1, c2) -> {
			boolean desc1 = c1.getDescription().isPresent();
			boolean desc2 = c2.getDescription().isPresent();
			return desc1 && desc2 ? 0 : desc1 ? 1 : -1;
		});

		for (Command command : allCommands) {
			CommandPackage commandPackage = CommandManager.getPackage(command);
			if (!commandMap.containsKey(commandPackage.packagePath())) {
				commandMap.put(commandPackage.packagePath(), Lists.newArrayList());
			}

			commandMap.get(commandPackage.packagePath()).add(command);
		}

		List<String> lines = Lists.newArrayList();
		for (String header : commandMap.keySet()) {
			if (lines.size() > 0)
				lines.add("");
			lines.add("<col=017291>~ " + Misc.formatPlayerName(header) + " Commands ~");

			StringBuilder line = new StringBuilder();
			int commandsOnLine = 0;
			for (Command command : commandMap.get(header)) {
				if (command.getDescription().isPresent()) {
					if (commandsOnLine > 0) {
						lines.add(line.toString());
						line = new StringBuilder();
						commandsOnLine = 0;
					}

					line.append("<col=962e05>");
					line.append("::").append(command.getClass().getSimpleName().toLowerCase()).append(" (").append(command.getDescription().get()).append(")");
					lines.add(line.toString());
					if (command.getFormat() != null)
						lines.add(command.getFormat());
					line = new StringBuilder();
				} else if (command.getFormat() != null) {
					lines.add("<col=962e05>" + command.getCommand() + ": " + command.getFormat());
				} else {
					line.append(command.getCommand());
					line.append(", ");

					if (++commandsOnLine >= 4) {
						lines.add(line.toString());
						line = new StringBuilder();
						commandsOnLine = 0;
					}
				}
			}
		}

		//if (lines.size() > 100) {
			//c.sendMessage("Too many commands do ::commands 2");
			//lines = lines.stream().limit(100).collect(Collectors.toList());
		//}

		c.getPA().openQuestInterfaceNew("Commands", lines);
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		displayCommandsInterface(c, "admin", "owner", "moderator", "helper");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Shows a list of all commands");
	}

}
