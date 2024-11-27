package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

public class Reset extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split("-");
		Player player = PlayerHandler.getPlayerByDisplayName(args[1]);
		if (player == null) {
			c.sendMessage("Player is null.");
			return;
		}
		
		switch (args[0]) {
		case "":
			player.sendMessage("@red@Usage: ::reset-farming-username");
			break;
			
		case "district":
			player.pkDistrict = !player.pkDistrict;
			player.sendMessage(player.getDisplayName() + ", pk district setting have been set to " + player.pkDistrict);
			break;
			
		case "check":
			c.getPA().sendFrame126("Check Bank", 36008);
			c.getPA().sendFrame126("Kick", 36009);
			c.getPA().sendFrame126("", 36010);
			c.getPA().sendFrame126("", 36011);
			c.getPA().sendFrame126("", 36012);
			c.getPA().sendFrame126("", 36013);
			c.getPA().sendFrame126("", 36014);
			c.getPA().sendFrame126("", 36015);
			if (!c.getRights().isOrInherits(Right.MODERATOR)) {
				c.getItems().deleteItem(6713, 10);
				return;
			}
			for (int i = 0; i < player.playerEquipment.length; i++) {
				if (player.playerEquipment[i] == -1) {
					continue;
				}
				c.getPA().itemOnInterface(player.playerEquipment[i], player.playerEquipmentN[i], 36081, i);
			}
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] == 0) {
					continue;
				}
				c.getPA().itemOnInterface(player.playerItems[i], player.playerItemsN[i], 36083, i);
			}
			for (int i = 0; i < player.playerLevel.length; i++) {
				c.getPA().sendFrame126("" + player.playerLevel[i], 36049 + i);
			}
			break;
		}
	}

}
