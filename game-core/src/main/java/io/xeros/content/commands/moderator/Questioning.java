package io.xeros.content.commands.moderator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Questioning extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (optionalPlayer.isPresent()) {
			Player chosen_player = optionalPlayer.get();
			chosen_player.setTeleportToX(1952);
			chosen_player.setTeleportToY(4764);
			chosen_player.heightLevel = 1;
			player.setTeleportToX(1952);
			player.setTeleportToY(4768);
			player.heightLevel = 1;
				
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (player.isDisconnected()) {
							container.stop();
							return;
						}
						player.facePosition(1952, 4764);
						chosen_player.facePosition(1952, 4768);
						container.stop();
					}

					@Override
					public void onStopped() {

					}
				}, 3);
				
			chosen_player.isStuck = false;
			player.sendMessage("You have moved " + chosen_player.getDisplayName() + " to questioning.");
			chosen_player.sendMessage(player.getDisplayName() + " has moved you to questioning.");
		} else {
			player.sendMessage(input + " is offline. You can only teleport online players.");
		}
	}
}