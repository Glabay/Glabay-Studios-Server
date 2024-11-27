package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;


public class Ginfo extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getPA().showInterface(50635);
		String[] Types = {"Total Level", "Total Xp", "GP Value", "Exchange Points", "Boss Points", "Slayer Points", "PK Points", "Vote Points"};
		Integer[] Values = {1, 2, 3, 4, 5, 6, 7, 8};
		for (int i = 0, typeId = 50643, valueId = 50666; i <  Types.length; i += 1, typeId += 2, valueId += 2) {
			c.getPA().sendFrame126(Types[i], typeId);
			c.getPA().sendFrame126("" + Values[i], valueId);
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the group information interface.");
	}

}
