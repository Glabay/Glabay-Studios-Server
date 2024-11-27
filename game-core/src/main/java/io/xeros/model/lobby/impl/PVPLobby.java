package io.xeros.model.lobby.impl;

import java.util.List;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.lobby.Lobby;
import io.xeros.util.Misc;

//import io.xeros.model.minigames.pvptourny.TournamentLobby;

public class PVPLobby extends Lobby {


	@Override
	public void onJoin(Player player) {
		
		player.getPA().movePlayer((3040+ Misc.random(-2,2)), (9967+Misc.random(-2,2)));
		player.sendMessage("Welcome to the PvP Tournament Lobby.");
		player.sendMessage("The Tournament will begin in "+ formattedTimeLeft() + "!");		
		
	}

	@Override
	public void onLeave(Player player) {
		player.getPA().movePlayer(3050, 9950);
	}

	@Override
	public boolean canJoin(Player player) {
		return true;
	}

	@Override
	public void onTimerFinished(List<Player> lobbyPlayers) {
		// TODO Disable timer from counting down unless players are present
	//	TournamentLobby.start(lobbyPlayers);
		
	}

	@Override
	public void onTimerUpdate(Player player) {
		String timeLeftString = formattedTimeLeft();
		player.getPA().sendFrame126("Tournament begins in: @gre@" + timeLeftString, 6570);
		player.getPA().sendFrame126("", 6571);
		player.getPA().sendFrame126("", 6572);
		player.getPA().sendFrame126("", 6664);
		player.getPA().walkableInterface(6673);
	}

	@Override
	public long waitTime() {
		return 15000;
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
		return Boundary.TOURNY_LOBBY;
	}
	
	

}
