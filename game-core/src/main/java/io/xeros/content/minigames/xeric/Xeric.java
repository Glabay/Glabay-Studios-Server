package io.xeros.content.minigames.xeric;

import java.util.ArrayList;
import java.util.List;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

/**
 * 
 * @author Patrity, Arithium, Fox News
 * 
 */
public class Xeric {

	private int killsRemaining;
	private int xericWaveId;
	public static boolean xericEnabled;

	public Xeric() {
	}
	
	private int index;

	
	private List <Player> xericTeam = new ArrayList<Player>();
	private final List<NPC> spawns = new ArrayList<>();
	
	public void spawn() {
		final int[][] type = XericWave.LEVEL;
		if (xericWaveId >= type.length) {
			stop();
			return;
		}
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer event) {
				
				if (getXericTeam().size() == 0) {
					System.err.println("Size of team is 0.");
					event.stop();
					return;
				}
				if (xericWaveId != 0 && xericWaveId < type.length) {
					for (Player xericTeam : xericTeam) 
						xericTeam.sendMessage("You are now on wave " + (xericWaveId + 1) + " of " + type.length + ". - Total damage done: " + xericTeam.xericDamage + ".", 255);
				}
				
				
				setKillsRemaining(type[xericWaveId].length);
				
				System.out.print("Kills Remaining: " + killsRemaining);
				for (int i = 0; i < killsRemaining; i++) {
					int npcType = type[xericWaveId][i];
				
					/* OLD SPAWNING (SPAWNS ON A RANDOM PLAYER)
					Player p = getXericTeam().get(Misc.random(getXericTeam().size() - 1));
					int x = p.absX + Misc.random(-3, 3);
					int y = p.absY + Misc.random(-3, 3);*/
					
					/*
					 * NEW SPAWNING METHOD (USING SET SPAWN POINTS)
					 */
					int index = Misc.random(XericWave.SPAWN_DATA.length - 1);
					int x = (XericWave.SPAWN_DATA[index][0] + (Misc.random(-3,3)));
					int y = (XericWave.SPAWN_DATA[index][1] + (Misc.random(-3,3)));
					NPC npc = NPCSpawning.spawn(npcType, x,y, getIndex() * 4, 1, XericWave.getMax(npcType), true);
					npc.getBehaviour().setRespawn(false);
					spawns.add(npc);
				}
				event.stop();
			}

			@Override
			public void onStopped() {

			}
		}, 16);
	}
	
	public void stop() {
		for (Player xericTeam : xericTeam) {
			Player p = PlayerHandler.getPlayerByLoginName(xericTeam.getLoginNameLower());
			XericRewards.giveReward(p.xericDamage, p);
			p.getPA().movePlayer(3050, 9950, 0);
			p.getDH().sendStatement("Congratulations on finishing the Trials of Xeric!");
			p.nextChat = 0;
			p.setRunEnergy(100, true);
			killAllSpawns();
			XericLobby.removeGame(this);
		}
	}

	public void leaveGame(Player player, boolean death) {
		player.getPA().movePlayer(3050, 9950, 0);
		player.getDH().sendStatement((death ? "Unfortunately you died" : "You've left the game") + " on wave " + (player.getXeric().xericWaveId + 1) + ". Better luck next time.");
		player.nextChat = 0;
		removePlayer(player);
	}

	public void killAllSpawns() {

		for (NPC npc : getSpawns()) {
			if (npc != null) {
				NPCHandler.npcs[npc.getIndex()] = null;
			}
		}
		for (int i = 0; i < NPCHandler.npcs.length; i++) {
			NPC npc = NPCHandler.npcs[i];
			if (npc != null) {
				if (Boundary.isIn(npc, Boundary.XERIC) && npc.getHeight() == index) {
					npc.unregister();
				}
			}
		}
	}

	public int getKillsRemaining() {
		return killsRemaining;
	}	
	
	public void setKillsRemaining(int remaining) {
		this.killsRemaining = remaining;
	}

	
	public static void drawInterface(Player p) {//draws the interface for how much time is left in lobby
		int seconds = XericLobby.xericLobbyTimer;
		p.getPA().sendFrame126("Raid begins in: @gre@"+(seconds - (XericLobby.timeLeft %seconds)), 6570);
		p.getPA().sendFrame126("", 6571);
		p.getPA().sendFrame126("", 6572);
		p.getPA().sendFrame126("", 6664);
	}

	public List<Player> getXericTeam() {
		return xericTeam;
	}
	
	public List<NPC> getSpawns() {
		return spawns;
	}

	public void setXericTeam(List<Player> list) {
		this.xericTeam = new ArrayList<>(list);
	}
	public void removePlayer(Player player) {
		
		xericTeam.remove(player);
		if (xericTeam.size() == 0) {
			stop();
		}
		player.setXeric(null);
		
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int z) {
		this.index = z;
	}

	public int getWaveId() {
		return xericWaveId;
	}
	public void incWaveID() {
		xericWaveId ++;
	}

}
