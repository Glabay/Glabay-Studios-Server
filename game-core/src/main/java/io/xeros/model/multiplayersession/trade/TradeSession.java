package io.xeros.model.multiplayersession.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mchange.v1.util.SimpleMapEntry;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.multiplayersession.MultiplayerSession;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.logging.player.ItemTradeLog;

public class TradeSession extends MultiplayerSession {

	public TradeSession(List<Player> players, MultiplayerSessionType type) {
		super(players, type);
	}
	
	private final int[] TRADE_SCREEN = {3323, 3443};
	private final String NOTHING = "Absolutely nothing!";

	@Override
	public void accept(Player player, Player recipient, int stageId) {
		switch (stageId) {
		case MultiplayerSessionStage.OFFER_ITEMS:
			if (recipient.getItems().freeSlots() < getItems(player).size()) {
				player.sendMessage(recipient.getDisplayName() + " only has " + recipient.getItems().freeSlots() + ", you need to remove items.");
				player.getPA().sendFrame126("You have offered more items than " + recipient.getDisplayName() + " has free space.", 3431);
				recipient.getPA().sendFrame126("You do not have enough inventory space to accept this trade.", 3431);
				return;
			}
			for (Player p : players) {
				GameItem overlap = getOverlappedItem(p);
				if (overlap != null) {
					p.getPA().sendFrame126("Too many of one item! The other player has " + Misc.getValueRepresentation(overlap.amount()) + " "
							+ ItemAssistant.getItemName(overlap.id()) + " in their inventory.", 3431);
					getOther(p).getPA().sendFrame126("The other player has offered too many of one item, they must remove some.", 3431);
					return;
				}
			}
			if (stage.hasAttachment() && stage.getAttachment() != player) {
				stage.setStage(MultiplayerSessionStage.CONFIRM_DECISION);
				stage.setAttachment(null);
				updateMainComponent();
				return;
			}
			player.getPA().sendFrame126("Waiting for other player...", 3431);
			stage.setAttachment(player);
			recipient.getPA().sendFrame126("Other player has accepted", 3431);
			break;

		case MultiplayerSessionStage.CONFIRM_DECISION:
			if (recipient.getItems().freeSlots() < getItems(player).size()) {
				player.sendMessage(recipient.getDisplayName() + " only has " + recipient.getItems().freeSlots() + ", the items could not be traded, they would be lost.");
				recipient.sendMessage(player.getDisplayName() + " had too many items to offer, they could have been lost in the trade.");
				finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (stage.hasAttachment() && stage.getAttachment() != player) {
/*				PlayerLogging.write(LogType.TRADE_LOG, player,
					"Receiver : " + player.playerName + " "
							+ Arrays.toString(player.getTradeContainer().containerCopy()), " Trader:"
							+ recipient.playerName + " " + Arrays.toString(recipient.getTradeContainer().containerCopy()));
				PlayerLogging.write(LogType.TRADE_LOG, recipient,
					"Receiver : " + recipient.playerName + " "
							+ Arrays.toString(itemList), " Trader:"
							+ player.playerName + " " + Arrays.toString(player.getTradeContainer().containerCopy()));*/
				finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);
				player.sendMessage("Trade successfully completed with " + recipient.getDisplayName());
				recipient.sendMessage("Trade successfully completed with " + player.getDisplayName());

				String gave1 = items.get(player).stream().map(GameItem::getFormattedString).collect(Collectors.joining(", "));
				String gave2 = items.get(recipient).stream().map(GameItem::getFormattedString).collect(Collectors.joining(", "));

				Discord.writeServerSyncMessage("[TRADE] " + player.getDisplayName() + " gave to " + recipient.getDisplayName() + ": " + gave1);
				Discord.writeServerSyncMessage("[TRADE] " + recipient.getDisplayName() + " gave to " + player.getDisplayName() + ": " + gave2);
				return;
			}
			stage.setAttachment(player);
			player.getPA().sendFrame126("Waiting for other player...", 3535);
			recipient.getPA().sendFrame126("Other player has accepted", 3535);

			break;

		default:
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			break;
		}
	}

	@Override
	public void updateOfferComponents() {
		for (Player player : items.keySet()) {
			player.getItems().sendInventoryInterface(3322);
			refreshItemContainer(player, player, 3415);
			refreshItemContainer(player, getOther(player), 3416);
			player.getPA().sendFrame126("", 3431);
			player.getPA().sendFrame126("@red@Trading@yel@ with: " + getOther(player).getDisplayName() + " who has @gre@" + getOther(player).getItems().freeSlots() + " free slots.", 3417);
		}
	}

	@Override
	public boolean itemAddable(Player player, GameItem item) {
		if (item.id() == 12650 || item.id() == 12649 || item.id() == 12651 || item.id() == 12652 || item.id() == 12644 || item.id() == 12645 || item.id() == 12643 || item.id() == 11995 || item.id() == 15568 || item.id() == 12653 || item.id() == 12655 || item.id() == 13178 || item.id() == 12646 || item.id() == 13179 || item.id() == 13177 || item.id() == 12921 || item.id() == 13181 || item.id() == 12816 || item.id() == 12647) {
			player.sendMessage("You cannot trade this item, it is deemed as untradable.");
			return false;
		}
		if (item.id() == 12006) {
			player.sendMessage("You cannot trade this item, it is deemed as untradable.");
			return false;
		}
		if (!player.getItems().isTradable(item.id())) {
			player.sendMessage("You cannot trade this item, it is deemed as untradable.");
			return false;
		}
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return true;
	}

	@Override
	public boolean itemRemovable(Player player, GameItem item) {
		if (!Server.getMultiplayerSessionListener().inAnySession(player)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return true;
	}

	@Override
	public void updateMainComponent() {
		if (stage.getStage() == MultiplayerSessionStage.OFFER_ITEMS) {
			for (Player player : players) {
				player.setTrading(true);
				player.getItems().sendInventoryInterface(TRADE_SCREEN[0]-1);
				refreshItemContainer(player, player, 3415);
				refreshItemContainer(player, player, 3416);
				player.getPA().sendFrame126("@red@Trading@yel@ with: " + getOther(player).getDisplayName() + " who has @gre@" + getOther(player).getItems().freeSlots() + " free slots.", 3417);
				player.getPA().sendFrame126("", 3431);
				player.getPA().sendFrame126("Are you sure you want to make this @red@trade@cya@?", 3535);
				player.getPA().sendFrame248(TRADE_SCREEN[0], 3321);
			}
		} else if (stage.getStage() == MultiplayerSessionStage.CONFIRM_DECISION) {
			for (Player player : players) {
				/*for (int i = 3500; i < 4000; i++) {
					player.getPA().sendFrame126("*"+i, i);
				}*/
				Player recipient = getOther(player);
				player.getItems().sendInventoryInterface(3214);
				List<GameItem> items = getItems(player);
				StringBuilder SendTrade = new StringBuilder(NOTHING);
                boolean first = true;
				for (GameItem item : items) {
					if (item.id() > 0 && item.amount() > 0) {
						if (first) {
							SendTrade = new StringBuilder(ItemAssistant.getItemName(item.id()));
							first = false;
						} else {
							SendTrade.append("\\n").append(ItemAssistant.getItemName(item.id()));
						}
						
						if (item.isStackable() && item.amount() > 1) {
							SendTrade.append(" x ").append(Misc.getValueRepresentation(item.amount()));
						}
					}
				}
				
				player.getPA().sendFrame126(SendTrade.toString(), 3557);
				SendTrade = new StringBuilder(NOTHING);
                first = true;
				
				
				items = getItems(recipient);
				for (GameItem item : items) {
					if (item.id() > 0 && item.amount() > 0) {
						//player.getPA().sendFrame126(ItemAssistant.getItemName(item.getId()) + " x " + Misc.getValueRepresentation(item.getAmount()), 3558);
					
						if (first) {
							SendTrade = new StringBuilder(ItemAssistant.getItemName(item.id()));
							first = false;
						} else {
							SendTrade.append("\\n").append(ItemAssistant.getItemName(item.id()));
						}
						
						if (item.isStackable() && item.amount() > 1) {
							SendTrade.append(" x ").append(Misc.getValueRepresentation(item.amount()));
						}
					
					}
				}
				updateSecondTradeScreen(player);
				player.getPA().sendFrame126(SendTrade.toString(), 3558);
				player.getPA().sendFrame126("", 3607);
				player.getPA().sendFrame248(TRADE_SCREEN[1], 197);
			}
		}
	}

	private void updateSecondTradeScreen(Player player) {
		player.getPA().sendFrame126("Are you sure you want to make this @red@trade@cya@?", 3535);
		player.getPA().sendFrame126("There is NO WAY to reverse a trade if you change your mind", 3536);
		player.getPA().sendFrame126("You are about to give:", 3533);
		player.getPA().sendFrame126("In return you will receive", 3534);
	}

	@Override
	public void give() {
		if (players.stream().anyMatch(Objects::isNull)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		for (Player player : items.keySet()) {
			if (Objects.isNull(player)) {
				continue;
			}
			if (items.get(player).size() <= 0) {
				continue;
			}
			for (GameItem item : items.get(player)) {
				getOther(player).getItems().addItem(item.id(), item.amount());
			}

			final Player other = getOther(player);
			Server.getLogging().write(new ItemTradeLog(player, other.getLoginName(), items.get(player), items.get(other)));
			Server.getLogging().write(new ItemTradeLog(other, player.getLoginName(), items.get(other), items.get(player)));
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public void withdraw() {
		for (Player player : items.keySet()) {
			if (Objects.isNull(player)) {
				continue;
			}
			if (items.get(player).size() <= 0) {
				continue;
			}
			for (GameItem item : items.get(player)) {
				player.getItems().addItem(item.id(), item.amount());
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void logSession(MultiplayerSessionFinalizeType type) {
		if (type == MultiplayerSessionFinalizeType.WITHDRAW_ITEMS) {
			return;
		}
		ArrayList<Entry<Player, String>> participantList = new ArrayList<>();
		for (Player player : items.keySet()) {
			String items = createItemList(player);
			Entry<Player, String> participant = new SimpleMapEntry(player, items);
			participantList.add(participant);
		}
	}

	private String createItemList(Player player) {
		if (items.get(player).isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (GameItem item : items.get(player)) {
			sb.append(ItemAssistant.getItemName(item.id()));
			if (item.amount() != 1) {
				sb.append(" x").append(item.amount());
			}
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2).replaceAll("'", "\\\\'");
	}

}
