package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.util.Misc;

/**
 * Magic on items
 **/
public class MagicOnItems implements PacketType {

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
		int slot = c.getInStream().readSignedWord();
		int itemId = c.getInStream().readSignedWordA();
		int junk = c.getInStream().readSignedWord();
		int spellId = c.getInStream().readSignedWordA();

		c.usingMagic = true;
		if (Misc.isInDuelSession(c)) return;
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		c.getPA().magicOnItems(itemId, slot, spellId);
		c.usingMagic = false;
		c.debug(c.getLoginName() + " - Magic on item spellId: " + spellId);
	}

}
