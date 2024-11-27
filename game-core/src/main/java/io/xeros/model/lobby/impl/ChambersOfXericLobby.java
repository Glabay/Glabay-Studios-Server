package io.xeros.model.lobby.impl;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.ServerState;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.lobby.Lobby;
import io.xeros.util.Misc;

public class ChambersOfXericLobby extends Lobby {

	public ChambersOfXericLobby() {
		
	}

	@Override
	public void onJoin(Player player) {
		player.getPA().movePlayer((3033+ Misc.random(-3,3)), (6054+Misc.random(-3,3)));
		player.sendMessage("You are now in the lobby for Chambers of Xeric.");
		String timeLeftString = formattedTimeLeft();
		player.sendMessage("Raid starts in: " + timeLeftString);
	}

	@Override
	public void onLeave(Player player) {
		player.getPA().movePlayer(3059, 9947, 0);
		player.sendMessage("@red@You have left the Chambers of Xeric.");
	}

	@Override
	public boolean canJoin(Player player) {
		if (player.totalLevel < player.getMode().getTotalLevelNeededForRaids()) {
			player.sendMessage("You need a total level of at least " + player.getMode().getTotalLevelNeededForRaids() + " to join this raid!");
			return false;
		}
		if (player.getRights().isOrInherits(Right.OWNER)) {
			return true;
		}
		// Don't allow multi-logging outside of debug
		if (!Server.isDebug()) {
			boolean accountInLobby = getFilteredPlayers()
					.stream()
					.anyMatch(lobbyPlr -> lobbyPlr.getMacAddress().equalsIgnoreCase(player.getMacAddress()));
			boolean ipInLobby = getFilteredPlayers()
					.stream()
					.anyMatch(lobbyPlr -> lobbyPlr.getIpAddress().equalsIgnoreCase(player.getIpAddress()));
			if (accountInLobby || ipInLobby) {
				player.sendMessage("You already have an account in the lobby!");
				return false;
			}
		}

		return true;
	}

	@Override
	public long waitTime() {
		return Server.isDebug() ? 5000 : 30000;
	}

	@Override
	public int capacity() {
		return 22;
	}

	@Override
	public String lobbyFullMessage() {
		return "Chambers of Xeric is currently full!";
	}

	@Override
	public boolean shouldResetTimer() {
		return this.getWaitingPlayers().isEmpty();
	}

	@Override
	public void onTimerFinished(List<Player> lobbyPlayers) {
		CycleEventHandler.getSingleton().addEvent(0, new Object(), new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
				List<Player> raidPlayers;
//				if (Server.getConfiguration().getServerState() != ServerState.DEBUG) {
//					Map<String, Player> macFilter = Maps.newConcurrentMap();
//					lobbyPlayers.forEach(plr -> macFilter.put(plr.getMacAddress(), plr));
//					raidPlayers = Lists.newArrayList(macFilter.values());
//					lobbyPlayers.stream().filter(plr -> !macFilter.values().contains(plr)).forEach(plr -> {
//						plr.sendMessage("You had a different account in this lobby, you will be added to the next one");
//						onJoin(plr);
//					});
//				} else {
					raidPlayers = Lists.newArrayList(lobbyPlayers);
				//}

				new Raids().startRaid(raidPlayers, false);
			}
		}, 1);
	}

	@Override
	public void onTimerUpdate(Player player) {
		player.addQueuedAction(plr -> {
			String timeLeftString = formattedTimeLeft();
			plr.getPA().sendString("Raid begins in: @gre@" + timeLeftString, 6570);
			plr.getPA().sendString("", 6572);
			plr.getPA().sendString("", 6664);
			plr.getPA().walkableInterface(6673);
		});
	}

	@Override
	public Boundary getBounds() {
		return Boundary.RAIDS_LOBBY;
	}

}
