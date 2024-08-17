package io.xeros.net.packets;

import io.xeros.Server;
import io.xeros.content.combat.magic.MagicRequirements;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Magic on floor items
 **/
public class MagicOnFloorItems implements PacketType {

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
		int itemY = c.getInStream().readSignedWordBigEndian();
		int itemId = c.getInStream().readUnsignedWord();
		int itemX = c.getInStream().readSignedWordBigEndian();
		int spellId = c.getInStream().readUnsignedWordA();

		if (!Server.itemHandler.itemExists(itemId, itemX, itemY, c.heightLevel)) {
			c.stopMovement();
			return;
		}
		c.usingMagic = true;
		if (!MagicRequirements.checkMagicReqs(c, 51, true)) {
			c.stopMovement();
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (Misc.isInDuelSession(c)) return;
		if (c.goodDistance(c.getX(), c.getY(), itemX, itemY, 12)) {
			/*
			 * int offY = (c.getX() - itemX) * -1; int offX = (c.getY() - itemY) * -1; c.teleGrabX = itemX; c.teleGrabY = itemY; c.teleGrabItem = itemId; c.turnPlayerTo(itemX,
			 * itemY); c.teleGrabDelay = System.currentTimeMillis(); c.startAnimation(c.MAGIC_SPELLS[51][2]); c.gfx100(c.MAGIC_SPELLS[51][3]); c.getPA().createPlayersStillGfx(144,
			 * itemX, itemY, 0, 72); c.getPA().createPlayersProjectile(c.getX(), c.getY(), offX, offY, 50, 70, c.MAGIC_SPELLS[51][4], 50, 10, 0, 50);
			 * c.getPA().addSkillXP(c.MAGIC_SPELLS[51][7], 6); c.getPA().refreshSkill(6); //c.stopMovement();
			 */
			c.sendMessage("Telegrab is disabled.");
		}
	}

}
