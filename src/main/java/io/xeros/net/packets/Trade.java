package io.xeros.net.packets;

import java.util.Objects;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().locked() || c.getLock().cannotInteract(c))
			return;
		c.interruptActions();
		int tradeId = c.getInStream().readSignedWordBigEndian();
		if (tradeId < 0 || tradeId >= PlayerHandler.players.length) {
			return;
		}

		Player requested = PlayerHandler.players[tradeId];
		if (requested == null) {
			return;
		}
		if (c.isNpc) {
			return;
		}
		c.getPA().resetFollow();

		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		if (requested.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			c.sendMessage("Other player is busy");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.stopMovement();
			c.sendMessage("@cr10@You cannot trade from here.");
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			c.sendMessage("You cannot trade whilst inside the duel arena.");
			return;
		}
		if (Objects.equals(requested, c)) {
			c.sendMessage("You cannot trade yourself.");
			return;
		}
		if (Boundary.isIn(c, Boundary.OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)) {
			c.sendMessage("You cannot trade in the arena.");
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (requested.getInterfaceEvent().isActive()) {
			c.sendMessage("That player needs to finish what they're doing.");
			return;
		}
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}

		if (c.getTrade().requestable(requested)) {
			c.getTrade().request(requested);
			return;
		}
	}

}