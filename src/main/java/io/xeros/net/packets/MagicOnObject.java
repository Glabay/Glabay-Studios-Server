package io.xeros.net.packets;

import io.xeros.content.skills.crafting.OrbCharging;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class MagicOnObject implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
//		int x = c.getInStream().readSignedWordBigEndian();
//		int y = c.getInStream().readSignedWordBigEndianA();
//		int magicId = c.getInStream().readUnsignedWord();
		
//		c.objectX = c.getInStream().readSignedWordBigEndianA();
//		c.objectId = c.getInStream().readUnsignedWord();
//		c.objectY = c.getInStream().readUnsignedWordA();
		
		int x = c.getInStream().readSignedWordBigEndian();
		int magicId = c.getInStream().readUnsignedWord();
		int y = c.getInStream().readUnsignedWordA();
		int objectId = c.getInStream().readInteger();
		

		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (Misc.isInDuelSession(c)) return;

		c.facePosition(x, y);

		System.out.println("Spell ID: " + magicId);
		System.out.println("Object used on: X: " + x + ", Y: " + y + ", ID: " + objectId);
		
		c.usingMagic = true;
		
		switch (objectId) {
		case 2153:
		case 2152:
		case 2151:
		case 2150:
			OrbCharging.chargeOrbs(c, magicId, objectId);
			break;
		}

	}

}
