package io.xeros.content.minigames.xeric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
/**
 * 
 * @author Patrity
 * 
 * TODO Deprecate old/unused code
 * 
 */
public class XericLobby {

	private static final List <Player> xericLobby = new CopyOnWriteArrayList<Player>();//gets players in lobby
	
	public static int xericLobbyTimer = 60;// how many seconds til timer starts

	public static int timeLeft;// how much time is left until raid starts
	
	private static final Xeric[] games = new Xeric[500];

	public static void removePlayer(Player player) {
		xericLobby.remove(player);
	}

	public static void start(List<Player> lobbyPlayers) {
		List<Player> joining = new ArrayList<>();
		int added = 0;
		Xeric xeric = new Xeric();
		addGame(xeric);
		
		for (Player p : lobbyPlayers) {
			
			if (added > 7) {// max amount of allowed players in a raid at a time
				p.sendMessage("The lobby is full at the moment, try again in a minute.");
				continue;
			}
			p.xericDamage = 0;
			p.setXeric(xeric);
			p.getPA().removeAllWindows();
			p.getPA().movePlayer((Misc.random(3) + 2715), (Misc.random(3) + 5470), xeric.getIndex() * 4);
			p.sendMessage("Welcome to the Trials of Xeric. Your first wave will start soon.", 255);
			joining.add(p);
			xericLobby.remove(p);
			added++;
			
		}	
		xeric.setXericTeam(joining);
		xeric.spawn();
		
	}
	
	private static int getFreeIndex() {
		for (int i = 0; i < games.length; i++) {
			if (games[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
	public static void addGame(Xeric xeric) {
		int freeIndex = getFreeIndex();
		games[freeIndex] = xeric;
		games[freeIndex].setIndex(freeIndex);
		xeric.setIndex(freeIndex);
	}
	
	public static void removeGame(Xeric xeric) {
		if (games[xeric.getIndex()] == null)
			return;
		games[xeric.getIndex()] = null;
		for (int i = 0; i < games.length; i++) {
			if (games[i] != null && games[i].getXericTeam().size() <= 0) {
				games[i] = null;
			}
		}
	}

}
	
	
	