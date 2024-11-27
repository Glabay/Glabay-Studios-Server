package io.xeros.net.packets.objectoptions;

import io.xeros.Server;
import io.xeros.content.dialogue.impl.OutlastLeaderboard;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class ObjectOptionFour extends ObjectAction {
	
	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		
		if (c.getRights().isOrInherits(Right.OWNER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 4:  "+objectType+"");

		if (OutlastLeaderboard.handleInteraction(c, objectType, 4))
			return;

		var objectActionManager = Server.getObjectActionManager();
		if (objectActionManager.findHandlerById(objectType).isPresent()) {
			var objectAction = objectActionManager.findHandlerById(objectType).get();
			if (objectAction.performedAction(c, objectType, obX, obY, c.getHeight(), 4))
				return;
			else
				logger.error("Unhandled Object Action 4: {} ", objectAction.getClass().getSimpleName());
		}

		switch (objectType) {
		case 31858:
		case 29150:
			c.setSidebarInterface(6, 938);
			c.playerMagicBook = 0;
			c.sendMessage("You feel a drain on your memory.");
			break;
		case 8356://streehosidius
			c.getPA().movePlayer(1679, 3541, 0);
			break;
		}
	}

}
