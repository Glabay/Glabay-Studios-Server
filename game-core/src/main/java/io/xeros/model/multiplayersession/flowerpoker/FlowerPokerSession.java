package io.xeros.model.multiplayersession.flowerpoker;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v1.util.SimpleMapEntry;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.*;
import io.xeros.model.multiplayersession.*;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ItemFlowerPokerTradeLog;

import java.util.*;

public class FlowerPokerSession extends MultiplayerSession {

    private final int[] TRADE_SCREEN = {3323, 3443};

    private final String NOTHING = "Absolutely nothing!";

    public FlowerPokerSession(List<Player> players, MultiplayerSessionType type) {
        super(players, type);
    }

    @Override
    public void accept(Player player, Player recipient, int stageId) {
        switch (stageId) {
            case MultiplayerSessionStage.OFFER_ITEMS:
                if (recipient.getItems().freeSlots() < getItems(player).size()) {
                    player.sendMessage(recipient.getDisplayName() + " only has " + recipient.getItems().freeSlots() + ", you need to remove items.");
                    player.getPA().sendFrame126("You have offered more items than " + recipient.getDisplayName() + " has free space.", 3431);
                    recipient.getPA().sendFrame126("You do not have enough inventory space to accept this gamble session.", 3431);
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
                if (!FlowerPokerHand.canGamble()) {
                    player.getPA().sendFrame126("<col=ff0000>No lanes are currently open! Please wait or decline.", 3431);
                    player.sendMessage("<col=ff0000>No lanes are currently open! Please wait or decline.");
                    recipient.sendMessage("<col=ff0000>No lanes are currently open! Please wait or decline.");
                    return;
                }
                if (!canAccept(player, recipient)) {
                    player.getPA().sendFrame126("<col=ff0000>You cannot gamble the combined amount. You won't have room to claim it!", 3431);
                    player.sendMessage("<col=ff0000>You cannot gamble that combined amount. You won't have room to claim it!");
                    recipient.sendMessage("<col=ff0000>You cannot gamble that combined amount. You won't have room to claim it!");
                    return;
                }

                player.getPA().sendFrame126("Waiting for other player...", 3431);
                stage.setAttachment(player);
                recipient.getPA().sendFrame126("Other player has accepted", 3431);
                break;

            case MultiplayerSessionStage.CONFIRM_DECISION:
                if (recipient.getItems().freeSlots() < getItems(player).size()) {
                    player.sendMessage(recipient.getDisplayName() + " only has " + recipient.getItems().freeSlots() + ", the items could not be gambled, they would be lost.");
                    recipient.sendMessage(player.getDisplayName() + " had too many items to offer, they could have been lost in the gamble session.");
                    finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
                    return;
                }
                if (!FlowerPokerHand.canGamble()) {
                    player.sendMessage("<col=ff0000>No lanes are currently open! Please wait or decline.");
                    recipient.sendMessage("<col=ff0000>No lanes are currently open! Please wait or decline.");
                    return;
                }
                if (!canAccept(player, recipient)) {
                    player.sendMessage("<col=ff0000>You cannot gamble that combined amount. You won't have room to claim it!");
                    recipient.sendMessage("<col=ff0000>You cannot gamble that combined amount. You won't have room to claim it!");
                    return;
                }
                if (stage.hasAttachment() && stage.getAttachment() != player) {

                    List<GameItem> pot = Lists.newArrayList();

                    for (Player h : items.keySet()) {
                        if (Objects.isNull(h)) {
                            continue;
                        }
                        if (items.get(h).size() <= 0) {
                            continue;
                        }
                        for (GameItem item : items.get(h)) {
                            pot.add(new GameItem(item.id(), item.amount()));
                        }
                    }
                    player.getPA().closeAllWindows();
                    recipient.getPA().closeAllWindows();

                    player.getFlowerPoker().setOther(recipient).setPrizePool(pot);
                    recipient.getFlowerPoker().setOther(player).setPrizePool(pot);
                    Server.getLogging().write(new ItemFlowerPokerTradeLog(player, recipient.getLoginName(), items.get(player), items.get(recipient)));

                    Server.getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.DISPOSE_ITEMS);
                    Server.getMultiplayerSessionListener().finish(recipient, MultiplayerSessionFinalizeType.DISPOSE_ITEMS);
                    player.getFlowerPoker().beginSession();
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
            player.getPA().sendFrame126("@red@Flower Poker@yel@ with: " + getOther(player).getDisplayName() + " who has @gre@" + getOther(player).getItems().freeSlots() + " free slots.", 3417);
        }
    }

    @Override
    public boolean itemAddable(Player player, GameItem item) {
        if (item.id() == 299 || item.id() == 12006 || item.id() == 12650 || item.id() == 12649 || item.id() == 12651 || item.id() == 12652 || item.id() == 12644 || item.id() == 12645 || item.id() == 12643 || item.id() == 11995 || item.id() == 15568 || item.id() == 12653 || item.id() == 12655 || item.id() == 13178 || item.id() == 12646 || item.id() == 13179 || item.id() == 13177 || item.id() == 12921 || item.id() == 13181 || item.id() == 12816 || item.id() == 12647) {
            player.sendMessage("You cannot gamble this item.");
            return false;
        }
        if (!player.getItems().isTradable(item.id())) {
            player.sendMessage("You cannot gamble this item, it is deemed as untradable.");
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

    public boolean canAccept(Player player, Player other) {
        List<GameItem> totalItems = Lists.newArrayList();

        Map<Integer, Long> total = Maps.newConcurrentMap();

        totalItems.addAll(getItems(player));
        totalItems.addAll(getItems(other));

        for (GameItem s : totalItems) {
            long itemAmount = s.amount();
            total.put(s.id(), itemAmount);
            if (total.get(s.id()) != null) {
                long currentAmount = total.get(s.id()).intValue();
                if ((total.get(s.id()) + itemAmount) > Integer.MAX_VALUE) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void updateMainComponent() {
        if (stage.getStage() == MultiplayerSessionStage.OFFER_ITEMS) {
            for (Player player : players) {
                player.getItems().sendInventoryInterface(TRADE_SCREEN[0] - 1);
                refreshItemContainer(player, player, 3415);
                refreshItemContainer(player, player, 3416);
                player.getPA().sendFrame126("@red@Flower Poker@yel@ with: " + getOther(player).getDisplayName() + " who has @gre@" + getOther(player).getItems().freeSlots() + " free slots.", 3417);
                player.getPA().sendFrame126("", 3431);
                player.getPA().sendFrame126("Are you sure you want to @red@Flower Poker@cya@ these items?", 3535);
                player.getPA().sendFrame248(TRADE_SCREEN[0], 3321);
            }
        } else if (stage.getStage() == MultiplayerSessionStage.CONFIRM_DECISION) {
            for (Player player : players) {
                Player recipient = getOther(player);
                player.getItems().sendInventoryInterface(3214);
                List<GameItem> items = getItems(player);
                String SendTrade = NOTHING;
                boolean first = true;
                for (GameItem item : items) {
                    if (item.id() > 0 && item.amount() > 0) {
                        if (first) {
                            SendTrade = ItemAssistant.getItemName(item.id());
                            first = false;
                        } else {
                            SendTrade = SendTrade + "\\n" + ItemAssistant.getItemName(item.id());
                        }
                        if (item.isStackable() && item.amount() > 1) {
                            SendTrade = SendTrade + " x " + Misc.getValueRepresentation(item.amount());
                        }
                    }
                }
                player.getPA().sendFrame126(SendTrade, 3557);
                SendTrade = NOTHING;
                first = true;
                items = getItems(recipient);
                for (GameItem item : items) {
                    if (item.id() > 0 && item.amount() > 0) {
                        if (first) {
                            SendTrade = ItemAssistant.getItemName(item.id());
                            first = false;
                        } else {
                            SendTrade = SendTrade + "\\n" + ItemAssistant.getItemName(item.id());
                        }
                        if (item.isStackable() && item.amount() > 1) {
                            SendTrade = SendTrade + " x " + Misc.getValueRepresentation(item.amount());
                        }
                    }
                }
                updateSecondTradeScreen(player);
                player.getPA().sendFrame126(SendTrade, 3558);
                player.getPA().sendFrame126("", 3607);

                player.getPA().sendFrame248(TRADE_SCREEN[1], 197);
            }
        }
    }

    private void updateSecondTradeScreen(Player player) {
            player.getPA().sendFrame126("Are you sure you want to @red@Flower Poker@cya@ these items?", 3535);
            player.getPA().sendFrame126("There is NO WAY to get your items back once you accept!", 3536);
            player.getPA().sendFrame126("You are about to gamble:", 3533);
            player.getPA().sendFrame126("On a win you will receive:", 3534);
    }

    @Override
    public void give() {
        // if (players.stream().anyMatch(client -> Objects.isNull(client))) {
        //    finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
        //    return;
        // }

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
        ArrayList<Map.Entry<Player, String>> participantList = new ArrayList<>();
        for (Player player : items.keySet()) {
            String items = createItemList(player);
            Map.Entry<Player, String> participant = new SimpleMapEntry(player, items);
            participantList.add(participant);
        }
    }

    private String createItemList(Player player) {
        if (items.get(player).size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (GameItem item : items.get(player)) {
            sb.append(ItemAssistant.getItemName(item.id()));
            if (item.amount() != 1) {
                sb.append(" x" + item.amount());
            }
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2).replaceAll("'", "\\\\'");
    }

}
