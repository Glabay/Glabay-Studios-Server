package io.xeros.model.lobby.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import io.xeros.content.minigames.xeric.XericLobby;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.lobby.Lobby;
import io.xeros.util.Misc;

public class TrialsOfXericLobby extends Lobby {


	@Override
	public void onJoin(Player player) {
		
		player.getPA().movePlayer(3039+ Misc.random(-2,2), 9933+Misc.random(-2,2));
		player.sendMessage("Welcome to the Trials of Xeric Lobby.");
		player.sendMessage("The raid will begin in "+ formattedTimeLeft() + "!");		
		
	}

	@Override
	public void onLeave(Player player) {
		player.getPA().movePlayer(3050, 9950);
	}

	@Override
	public boolean canJoin(Player player) {
		if (player.calculateCombatLevel() < 70) {
			player.sendMessage("You need a combat level of 70 to join Trials of Xeric!");
			return false;
		}		
		boolean accountInLobby = getFilteredPlayers()
	            .stream()
	            .anyMatch(lobbyPlr -> lobbyPlr.getMacAddress().equalsIgnoreCase(player.getMacAddress()));
		if (accountInLobby) {
			player.sendMessage("You already have an account in the lobby!");
			return false;
		}
		return true;
	}

	@Override
	public void onTimerFinished(List<Player> lobbyPlayers) {
		Map<String, Player> macFilter = Maps.newConcurrentMap();

		lobbyPlayers.stream().forEach(plr -> macFilter.put(plr.getMacAddress(), plr));

		XericLobby.start(lobbyPlayers);

		lobbyPlayers.stream().filter(plr -> !macFilter.containsValue(plr)).forEach(plr -> {
			plr.sendMessage("You had a different account in this lobby, you will be added to the next one");
			onJoin(plr);
		});
	}

	@Override
	public void onTimerUpdate(Player p1) {
		String timeLeftString = formattedTimeLeft();
		p1.addQueuedAction(player -> {
			player.getPA().sendFrame126("Raid begins in: @gre@" + timeLeftString, 6570);
			player.getPA().sendFrame126("", 6571);
			player.getPA().sendFrame126("", 6572);
			player.getPA().sendFrame126("", 6664);
			player.getPA().walkableInterface(6673);
		});
	}

	@Override
	public long waitTime() {
		return 60000;
	}

	@Override
	public int capacity() {
		return 5;
	}

	@Override
	public String lobbyFullMessage() {
		// TODO Auto-generated method stub
		return "The lobby is currently full! Please wait for the next game!";
	}

	@Override
	public boolean shouldResetTimer() {
		return this.getWaitingPlayers().isEmpty();
	}

	@Override
	public Boundary getBounds() {
		return Boundary.XERIC_LOBBY;
	}
	
	

}
