package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Hpray extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.inGodmode()) {
			c.getHealth().setMaximumHealth(c.getLevelForXP(c.playerXP[Player.playerHitpoints]));
			c.getHealth().reset();
			c.playerLevel[Player.playerPrayer] = c.getLevelForXP(c.playerXP[Player.playerPrayer]);
			c.getPA().refreshSkill(Player.playerPrayer);
			c.specAmount = 10.0;
			c.getPA().requestUpdates();
			c.setSafemode(false);
			c.setGodmode(false);
			c.sendMessage("Mode is now: Off");
		} else {
			c.getHealth().setMaximumHealth(2_000_000_000);
			c.getHealth().reset();
			c.playerLevel[Player.playerPrayer] = 2_000_000_000;
			c.getPA().refreshSkill(Player.playerPrayer);
			c.specAmount = 9999;
			c.getPA().requestUpdates();
			c.setSafemode(true);
			c.setGodmode(true);
			c.sendMessage("Mode is now: On");
		}
	}
}
