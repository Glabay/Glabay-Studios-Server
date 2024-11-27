package io.xeros.model.entity.player;

import io.xeros.model.entity.npc.NPC;
import io.xeros.net.packets.npcoptions.*;
import io.xeros.net.packets.objectoptions.ObjectOptionFour;
import io.xeros.net.packets.objectoptions.ObjectOptionOne;
import io.xeros.net.packets.objectoptions.ObjectOptionThree;
import io.xeros.net.packets.objectoptions.ObjectOptionTwo;

public class ActionHandler {

	private final Player c;

	public ActionHandler(Player Client) {
		this.c = Client;
	}

	public void firstClickObject(int objectType, int obX, int obY) {
		ObjectOptionOne.handleOption(c, objectType, obX, obY);
	}

	public void secondClickObject(int objectType, int obX, int obY) {
		ObjectOptionTwo.handleOption(c, objectType, obX, obY);
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		ObjectOptionThree.handleOption(c, objectType, obX, obY);
	}

	public void fourthClickObject(int objectType, int obX, int obY) {
		ObjectOptionFour.handleOption(c, objectType, obX, obY);
	}

	public void firstClickNpc(NPC npc) {
		NpcOptionOne.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 1);
	}

	public void secondClickNpc(NPC npc) {
		NpcOptionTwo.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 2);
	}

	public void thirdClickNpc(NPC npc) {
		NpcOptionThree.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 3);
	}

	public void fourthClickNpc(NPC npc) {
		NpcOptionFour.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 4);
	}

}