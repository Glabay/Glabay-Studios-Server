package io.xeros.net.packets;

import java.util.Objects;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.items.bank.BankTab;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;

public class MoveItems implements PacketType {

	public static final int MOVE_ITEMS_IN_SAME_CONTAINER = 214;
	public static final int MOVE_ITEMS_BETWEEN_CONTAINERS = 242;
	public static final int MOVE_FROM_SEARCH_TO_TAB = 243;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().locked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		if (packetType == MOVE_FROM_SEARCH_TO_TAB) {
			int newTab = c.getInStream().readUnsignedWord();
			int itemId = c.getInStream().readUnsignedWord();

			if (c.debugMessage) {
				c.sendMessage("Move items (243): "
						+ ", newTab: " + newTab
						+ ", itemId: " + itemId
				);
			}

			if (newTab >= 0 && newTab < c.getBank().getBankTab().length) {
				BankTab toTab = c.getBank().getBankTab()[newTab];
				if (toTab.freeSlots() == 0) {
					c.sendMessage("That bank is full.");
				} else {
					for (BankTab tab : c.getBank().getBankTab()) {
						BankItem item = tab.getItem(new BankItem(itemId, 0));
						if (item != null) {
							if (tab.getTabId() == toTab.getTabId()) {
								c.sendMessage("That item is already in that tab.");
							} else {
								tab.getItems().remove(item);
								toTab.getItems().add(item);
								c.getItems().queueBankContainerUpdate();
							}
							return;
						}
					}
				}
			}
		} else if (packetType == MOVE_ITEMS_IN_SAME_CONTAINER) {
			int interfaceId = c.getInStream().readUnsignedWordBigEndianA();
			boolean insertMode = c.getInStream().readSignedByteC() == 1;
			int from = c.getInStream().readUnsignedWordBigEndianA();
			int to = c.getInStream().readUnsignedWordBigEndian();

			if (c.debugMessage) {
				c.sendMessage("Move items (214): "
						+ ", interfaceId: " + interfaceId
						+ ", insertMode: " + insertMode
						+ ", from: " + from
						+ ", to: " + to
				);
			}

			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.getBankPin().open(2);
				return;
			}
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot move items whilst trading.");
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You cannot move items right now.");
				return;
			}
			c.getItems().moveItems(from, to, interfaceId, insertMode);
		} else if (packetType == MOVE_ITEMS_BETWEEN_CONTAINERS) {
			int draggingTo = c.getInStream().readUnsignedWord();
			int draggingFrom = c.getInStream().readUnsignedWord();
			boolean insertMode = c.getInStream().readSignedByteC() == 1;
			int fromSlot = c.getInStream().readUnsignedWord();
			int toSlot = c.getInStream().readUnsignedWord();
			if (c.debugMessage) {
				c.sendMessage("Move items (241): "
						+ "to: " + draggingTo
						+ ", from: " + draggingFrom
						+ ", insertMode: " + insertMode
						+ ", fromSlot: " + fromSlot
						+ ", toSlot: " + toSlot
				);
			}

			if (c.getBank().moveItemsBetweenTabs(draggingFrom, fromSlot, draggingTo, toSlot, insertMode)) {
				return;
			}
		} else {
			throw new IllegalArgumentException("Invalid packet: " + packetType);
		}
	}
}