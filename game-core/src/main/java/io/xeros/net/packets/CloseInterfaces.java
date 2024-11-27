package io.xeros.net.packets;

import java.util.Objects;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.multiplayersession.MultiplayerSession;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;

/**
 * Clicking stuff (interfaces)
 **/
public class CloseInterfaces implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE);
		if (session != null && Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			c.sendMessage("You have declined the trade.");
			session.getOther(c).sendMessage(c.getDisplayName() + " has declined the trade.");
			session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}
		MultiplayerSession flowerSession = Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.FLOWER_POKER);
		if (flowerSession != null && Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.FLOWER_POKER)) {
			c.sendMessage("You have declined the setup for flowerpoker.");
			flowerSession.getOther(c).sendMessage(c.getDisplayName() + " has declined the setup for flowerpoker.");
			flowerSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}

		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("You have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}
		c.closedInterface();
		c.interruptActions();
		c.getPA().closeAllWindows(false);
	}

}
