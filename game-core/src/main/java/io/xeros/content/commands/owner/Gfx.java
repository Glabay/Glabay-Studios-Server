package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.Graphic;
import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Open a specific interface.
 * 
 * @author Emiel
 *
 */
public class Gfx extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {

		String[] args = input.split(" ");

		List<Graphic> graphics = new ArrayList<>();
		for (String arg : args) {
			try {
				int graphicId = Integer.parseInt(arg);
				graphics.add(new Graphic(graphicId));
			} catch (NumberFormatException e) {
				System.err.println("Invalid graphic ID: " + arg + ", skipping.");
			}
		}

		String graphicIds = graphics.stream()
				.map(graphic -> String.valueOf(graphic.getId()))
				.collect(Collectors.joining(", "));

		c.startGraphic(graphics.toArray(new Graphic[0]));
		c.sendMessage("Playing Graphics: " + graphicIds);
	}
}
