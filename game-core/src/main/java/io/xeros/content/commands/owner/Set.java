package io.xeros.content.commands.owner;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Set extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		
		switch (args[0]) {
		
		case "":
			c.sendMessage("Usage: ::set minions or slayer");
			break;

		case "slayer":
			c.getSlayer().setPoints(Integer.parseInt(args[1]));
			c.getQuestTab().updateInformationTab();
			c.sendMessage("Slayer points set to: "+ Integer.parseInt(args[1]));
			break;
			
		case "dp":
			c.donatorPoints += Integer.parseInt(args[1]);
			c.sendMessage("Amount of donator points added: "+ Integer.parseInt(args[1]));
			c.getQuestTab().updateInformationTab();
			break;
			
		case "pkp":
			c.pkp += Integer.parseInt(args[1]);
			c.getQuestTab().updateInformationTab();
			c.sendMessage("Amount of pk points added: "+ Integer.parseInt(args[1]));
			break;
			
		case "players":
			Configuration.PLAYERMODIFIER = Integer.parseInt(args[1]);
			c.sendMessage("Player Count Modifier: +"+ Integer.parseInt(args[1]));
			break;
		
		}
	}
}
