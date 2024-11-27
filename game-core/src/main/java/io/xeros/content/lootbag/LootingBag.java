package io.xeros.content.lootbag;

import java.util.Iterator;
import java.util.Objects;

import io.xeros.Server;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.util.Misc;

/**
 * Looting bag functionality.
 *
 * @author Sky
 * @Author trees (Fixed)
 */
public class LootingBag {

    public enum LootingBagUseAction {
        OPTION, ONE, TEN, ALL, X
    }

    /**
     * The looting bag id
     */
    public static final int LOOTING_BAG = 11941;
    public static final int LOOTING_BAG_OPEN = 22586;
    public static final int DEPOSIT_INTERFACE_ID = 39448;
    public static final int WITHDRAW_INTERFACE_ID = 39349;
    public static final int ITEM_ON_ITEM_DIALOGUE_ID = 1234;
    public static final int OPTIONS_DIALOGUE_ID = 1235;

    private final Player player;
    private final LootingBagContainer lootingBagContainer;
    private boolean withdrawInterfaceOpen;
    private boolean depositInterfaceOpen;
    private boolean selectDepositAmountInterfaceOpen;
    private int selectedItem = -1;
    private int selectedSlot = -1;
    private LootingBagUseAction useAction = LootingBagUseAction.OPTION;

    public LootingBag(Player player) {
        this.player = player;
        lootingBagContainer = new LootingBagContainer(player);
    }

    public boolean hasLootingBagInInventory() {
        return player.getItems().playerHasItem(LOOTING_BAG_OPEN) || player.getItems().playerHasItem(LOOTING_BAG);
    }

    public static boolean isLootingBag(int itemId) {
        return itemId == LOOTING_BAG || itemId == LOOTING_BAG_OPEN;
    }

    public void toggleOpen() {
        if (canUseLootingBag()) {
            if (player.getItems().playerHasItem(LOOTING_BAG)) {
                player.getItems().setInventoryItemSlot(player.getItems().getInventoryItemSlot(LOOTING_BAG), LOOTING_BAG_OPEN);
                player.sendMessage("You open your looting bag.");
            } else if (player.getItems().playerHasItem(LOOTING_BAG_OPEN)) {
                player.getItems().setInventoryItemSlot(player.getItems().getInventoryItemSlot(LOOTING_BAG_OPEN), LOOTING_BAG);
                player.sendMessage("You close your looting bag.");
            }
        }
    }

    public void useItemOnBag(int itemId) {
        if (useAction == LootingBagUseAction.OPTION) {
            setSelectedItem(itemId);
            selectDepositAmountInterfaceOpen = true;
            player.getDH().sendDialogues(ITEM_ON_ITEM_DIALOGUE_ID, 0);
        } else if (useAction == LootingBagUseAction.X) {
            player.getPA().sendEnterAmount(DEPOSIT_INTERFACE_ID);
            setSelectedItem(itemId);
        } else {
            getLootingBagContainer().deposit(itemId, useAction == LootingBagUseAction.ALL ? Integer.MAX_VALUE
                    : useAction == LootingBagUseAction.TEN ? 10  : 1);
        }
    }

    /**
     * Handles deposit and withdrawal of items
     *
     * @param id     The item being configured
     * @param amount The amount of the item being configured
     * @return
     */
    public boolean handleClickItem(int id, int amount) {
        if (!canUseLootingBag()) {
            player.sendMessage("You cannot do this right now.");
            return false;
        } else if (isWithdrawInterfaceOpen() || player.getEnterAmountInterfaceId() == WITHDRAW_INTERFACE_ID) {
            if (amount == -1) {
                player.getPA().sendEnterAmount(DEPOSIT_INTERFACE_ID);
                setSelectedItem(id);
            } else {
                getLootingBagContainer().removeMultipleItemsFromBag(id, amount);
                updateItemContainers();
            }
            return true;
        } else if (isDepositInterfaceOpen() || player.getEnterAmountInterfaceId() == DEPOSIT_INTERFACE_ID
                || selectDepositAmountInterfaceOpen) {
            if (amount == -1) {
                player.getPA().sendEnterAmount(DEPOSIT_INTERFACE_ID);
                setSelectedItem(id);
            } else {
                if (selectDepositAmountInterfaceOpen) {
                    player.getPA().closeAllWindows();
                    selectDepositAmountInterfaceOpen = false;
                }
                getLootingBagContainer().deposit(id, amount);
                updateItemContainers();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Opens withdrawal mode
     */
    public void openWithdrawalMode() {
        if (!canUseLootingBag()) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        if (!player.getItems().playerHasItem(LOOTING_BAG) && !player.getItems().playerHasItem(LOOTING_BAG_OPEN)) {
            return;
        }

        reset();
        updateItemContainers();
        player.getPA().sendTabAreaOverlayInterface(39342);
        setWithdrawInterfaceOpen(true);
    }

    /**
     * Opens deposit mode
     */
    public void openDepositMode() {
        if (!canUseLootingBag()) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        if (!player.getItems().playerHasItem(LOOTING_BAG) && !player.getItems().playerHasItem(LOOTING_BAG_OPEN)) {
            return;
        }
        if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
            return;
        }
        if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS)) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        if (!player.getPosition().inWild()) {
            player.sendMessage("You can only do this in the wilderness.");
            return;
        }
        reset();
        updateItemContainers();
        player.getPA().sendTabAreaOverlayInterface(39443);
        setDepositInterfaceOpen(true);
    }

    private void updateItemContainers() {
        player.getItems().sendInventoryInterface(DEPOSIT_INTERFACE_ID);
        player.getItems().sendItemContainer(WITHDRAW_INTERFACE_ID, getLootingBagContainer().items);
        updateTotalCost();
    }

    /**
     * Closing the looting bag and resetting
     */
    public void closeLootbag() {
        player.getPA().sendTabAreaOverlayInterface(0);
        reset();
    }

    public void reset() {
        setWithdrawInterfaceOpen(false);
        setDepositInterfaceOpen(false);
    }

    public void depositAllLootBag() {
        if (!canUseLootingBag()) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        if (!player.getPosition().Area(1592, 1670, 3659, 3696) && !player.getPosition().inBank() || player.getPosition().inWild()) {
            player.sendMessage("You must be at home to do this.");
            return;
        }
        if (getLootingBagContainer().items.isEmpty()) {
            player.sendMessage("You don't have any items in your lootbag.");
            return;
        }
        if (player.getMode().isUltimateIronman()){
            player.sendMessage("Your mode can not bank their items.");
            return;
        }
        for (Iterator<LootingBagItem> iterator = getLootingBagContainer().items.iterator(); iterator.hasNext(); ) {
            LootingBagItem item = iterator.next();
            if (item == null) {
                continue;
            }
            if (item.id() <= 0 || item.amount() <= 0) {
                continue;
            }
            player.getItems().addItemToBankOrDrop(item.id(), item.amount());
            iterator.remove();
        }
        updateItemContainers();
    }

    public boolean handleButton(int buttonId) {
        if (buttonId == 153177 || buttonId == 154022) {
            closeLootbag();
            return true;
        } else if (buttonId == 153179) {
            depositAllLootBag();
            return true;
        }
        return false;
    }

    /**
     * Checks whether or not a player is allowed to configure the looting bag
     */
    public boolean canUseLootingBag() {
        if (/*player.underAttackBy > 0 || player.underAttackBy2 > 0 ||*/ player.getPosition().inDuelArena() || player.getPosition().inPcGame()
                || player.getPosition().inPcBoat() || player.getPosition().isInJail() || player.getInterfaceEvent().isActive()
                || player.getPA().viewingOtherBank || player.isDead || player.viewingRunePouch) {
            return false;
        }

        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return false;
        }
        DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                MultiplayerSessionType.DUEL);
        if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
                && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
            player.sendMessage("Your actions have declined the duel.");
            duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
            duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return false;
        }

        if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
            player.sendMessage("You must decline the trade.");
            return false;
        }

        if (player.isStuck) {
            player.isStuck = false;
            player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
            return false;
        }
        return true;
    }

    /**
     * Calculating the price of the loot bag
     */
    public void updateTotalCost() {
        int total = 0;
        for (LootingBagItem item : getLootingBagContainer().items) {
            if (item == null) {
                continue;
            }
            if (item.id() <= 0 || item.amount() <= 0) {
                continue;
            }
            if (ItemDef.forId(item.id()) != null) {
                if (player.debugMessage)
                    player.sendMessage("Name: " + ItemDef.forId(item.id()).getName() + "- Value:" + ItemDef.forId(item.id()).getShopValue() + " - Amount:" + item.amount());
                total += ((ItemDef.forId(item.id()).getShopValue() * item.amount()));
            }
        }
        player.getPA().sendFrame126("Value: " + Misc.insertCommas(total), 39348);
    }

    public LootingBagContainer getLootingBagContainer() {
        return lootingBagContainer;
    }

    public boolean isWithdrawInterfaceOpen() {
        return withdrawInterfaceOpen;
    }

    public void setWithdrawInterfaceOpen(boolean withdrawInterfaceOpen) {
        this.withdrawInterfaceOpen = withdrawInterfaceOpen;
    }

    public boolean isDepositInterfaceOpen() {
        return depositInterfaceOpen;
    }

    public void setDepositInterfaceOpen(boolean depositInterfaceOpen) {
        this.depositInterfaceOpen = depositInterfaceOpen;
    }

    public boolean isSelectDepositAmountInterfaceOpen() {
        return selectDepositAmountInterfaceOpen;
    }

    public void setSelectDepositAmountInterfaceOpen(boolean selectDepositAmountInterfaceOpen) {
        this.selectDepositAmountInterfaceOpen = selectDepositAmountInterfaceOpen;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public LootingBagUseAction getUseAction() {
        return useAction;
    }

    public void setUseAction(LootingBagUseAction useAction) {
        this.useAction = useAction;
    }
}
