package io.xeros.net.packets;

import java.util.Objects;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.fireofexchange.FireOfExchange;
import io.xeros.content.ItemSpawner;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.itemskeptondeath.perdu.PerduLostPropertyShop;
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
 * Remove Item
 **/
public class ContainerAction1 implements PacketType {

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
		c.interruptActions();
		int interfaceId = c.getInStream().readUnsignedWordA();
		int removeSlot = c.getInStream().readUnsignedWordA();
		int removeId = c.getInStream().readUnsignedWordA();

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_1, interfaceId, removeId, removeSlot);

		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen()) {
			if (c.getLootingBag().handleClickItem(removeId, 1)) {
				return;
			}
		}
		if (c.getRunePouch().handleClickItem(removeId, 1, interfaceId)) {
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
		if (c.debugMessage)
			c.sendMessage("ContainerAction1: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);

		if (PerduLostPropertyShop.handleContainerAction(c, action))
			return;
		if (c.viewingPresets && interfaceId != 21578 && (interfaceId < 21579 || interfaceId > 21589)) {
			if (!c.getItems().playerHasItem(removeId, 1, removeSlot))
				return;
			PresetManager.getSingleton().addInventoryItem(c, removeId, c.playerItemsN[removeSlot]);
			return;
		}
		
		if (interfaceId >= 21579 && interfaceId <= 21589) {
			PresetManager.getSingleton().removeEquipmentItem(c, interfaceId);
			return;
		}

		if (c.getBank().withdraw(interfaceId, removeId, 1)) {
			return;
		}

		if (c.getTobContainer().handleContainerAction1(interfaceId, removeSlot)) {

		}

		switch (interfaceId) {
		case GroupIronmanBank.INTERFACE_ITEM_CONTAINER_ID:
			GroupIronmanBank.processContainerAction(c, action);
			break;
		case ItemSpawner.CONTAINER_ID:
			ItemSpawner.spawn(c, removeId, 1);
			break;
		case 33405:

			if (FireOfExchangeBurnPrice.getBurnPrice(c, removeId, true) != -1  && c.getItems().playerHasItem(removeId)) {//checks if item is on foe list, and they actually have item.
				c.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(removeId, 1)));
				c.getPA().sendFrame126("@gre@" + c.exchangePoints, 33410);
				int price = FireOfExchangeBurnPrice.getBurnPrice(c, removeId, true);
				int ironManPrice = (int) (price * 1.1);
				if (price == -1) {
					c.getPA().sendFrame126("@red@0", 33409);
					c.currentExchangeItem = -1;
				} else {
					if (c.getMode().isIronmanType() && FireOfExchange.canBurnWithBranch(c)) {
						price = ironManPrice;
					}
					c.getPA().sendFrame126("@gre@" + price, 33409);
					c.currentExchangeItem = removeId;
				}
				return;
			}

			break;


			case 26022:
			Listing.buyListing(c, removeSlot, 1);
			break;
		case 21578:
			PresetManager.getSingleton().removeInventoryItem(c, removeId);
			break;
			case 41609:
				switch(c.boxCurrentlyUsing) {
					case 12789://youtube
						c.getYoutubeMysteryBox().roll(c);
						break;
					case 13346: //ultra rare
						c.getUltraMysteryBox().roll(c);
						break;
					case 6199:
						c.getNormalMysteryBox().roll(c);
						break;
					case 6828:
						c.getSuperMysteryBox().roll(c);
						break;
					case 8167:
							c.getFoeMysteryBox().roll(c);
						break;
				}
				break;
		
		case 48847:
			Listing.cancelListing(c, removeSlot, removeId);
		break;
		
		case 48500: //Listing interface
			if(c.isListing) {
				Listing.openSelectedItem(c, removeId, 1, 0);
			}
		break;

		case 7423:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot add items to the deposit box whilst trading.");
				return;
			}
			c.getItems().addToBank(removeId, 1, false);
			c.getItems().sendInventoryInterface(7423);
			break;
		case 4233:
		case 4239:
		case 4245:
			JewelryMaking.mouldItem(c, removeId, 1);
			break;

		// Remove equipment
		case 1688:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				
				Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot remove items whilst trading, trade declined.");
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			c.usingMagic = false;
			c.getItems().unequipItem(removeId, removeSlot);
			break;

		case 5064:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot add items to the bank whilst trading.");
				return;
			}
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.isBanking) {
				c.getItems().addToBank(removeId, 1, true);
			} else {
				GroupIronmanBank.processContainerAction(c, action);
			}
			break;
			
			/**
			 * Shop value
			 */
			
		case 64016:
			c.getShops().buyFromShopPrice(removeId, removeSlot);
			break;

		case 3900:
			c.getShops().buyFromShopPrice(removeId, removeSlot);
			break;

		case 3823:
			c.getShops().sellToShopPrice(removeId, removeSlot);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.addItem(c, new GameItem(removeId, 1));
			}
			break;

		case 3415:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 1));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 1));
			}
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			Smithing.readInput(c.playerLevel[Player.playerSmithing], Integer.toString(removeId), c, 1);
			break;

		}
	}

}
