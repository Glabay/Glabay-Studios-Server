package io.xeros.net.packets;

import java.util.Objects;

import io.xeros.Server;
import io.xeros.content.ItemSpawner;
import io.xeros.content.preset.PresetManager;
import io.xeros.content.skills.crafting.JewelryMaking;
import io.xeros.content.skills.smithing.Smithing;
import io.xeros.content.tradingpost.Listing;
import io.xeros.model.ContainerAction;
import io.xeros.model.ContainerActionType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanBank;
import io.xeros.model.items.GameItem;
import io.xeros.model.multiplayersession.MultiplayerSession;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.flowerpoker.FlowerPokerSession;
import io.xeros.model.multiplayersession.trade.TradeSession;

/**
 * Bank 5 Items
 **/
public class ContainerAction2 implements PacketType {

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
		int interfaceId = c.getInStream().readUnsignedWord();
		int removeId = c.getInStream().readUnsignedWord();
		int removeSlot = c.getInStream().readSignedWordBigEndian();

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_2, interfaceId, removeId, removeSlot);

		if (c.debugMessage)
			c.sendMessage("ContainerAction2: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);


		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.viewingPresets) {
			PresetManager.getSingleton().addItemToEquipment(c, removeId, c.playerItemsN[removeSlot]);
			return;
		}
		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen()) {
			if (c.getLootingBag().handleClickItem(removeId, 5)) {
				return;
			}
		}
		if (c.viewingRunePouch) {
			if (c.getRunePouch().handleClickItem(removeId, 5, interfaceId)) {
				return;
			}
		}

		if (c.getBank().withdraw(interfaceId, removeId, 5)) {
			return;
		}
		
		switch (interfaceId) {
		case GroupIronmanBank.INTERFACE_ITEM_CONTAINER_ID:
			GroupIronmanBank.processContainerAction(c, action);
			break;
		case ItemSpawner.CONTAINER_ID:
			ItemSpawner.spawn(c, removeId,5000);
			break;
		case 26022:
			Listing.buyListing(c, removeSlot, 5);
			break;
	
		case 48500: //Listing interface
			if(c.isListing) {
				int amount = 5;
				if(c.getItems().getItemAmount(removeId) < 5)
					amount = c.getItems().getItemAmount(removeId);
				Listing.openSelectedItem(c, removeId, amount, 0);
			}
		break;
		
		case 4233:
		case 4239:
		case 4245:
			JewelryMaking.mouldItem(c, removeId, 5);
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			Smithing.readInput(c.playerLevel[Player.playerSmithing], Integer.toString(removeId), c, 5);
			break;

		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 1);
			break;

		case 64016:
			c.getShops().buyItem(removeId, removeSlot, 1);
			break;

		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 1);
			break;

		case 5064:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot add items to the bank whilst trading.");
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.isBanking) {
				c.getItems().addToBank(removeId, 5, true);
			} else {
				GroupIronmanBank.processContainerAction(c, action);
			}
			break;
		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.addItem(c, new GameItem(removeId, 5));
			}
			break;

		case 3415:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 5));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 5));
			}
			break;

		}
	}

}
