package io.xeros.model.items.bank;

import java.util.Arrays;
import java.util.Objects;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;

public class Bank {

	public static final int INTERFACE_ID = 5292;
	public static final int BANK_TAB_CONFIG = 1357;
	public static final int BANK_SEARCH_CONTAINER = 41583;
	public static final int BANK_CAPACITY_STRING_ID = 58062;

	/**
	 * The item containers for each tab, respectively.
	 */
	public static final int[] ITEM_CONTAINERS = {41573, 41574, 41575, 41576, 41577, 41578, 41579, 41580, 41581};

	private final BankTab[] bankTabs = {
			new BankTab(0, this),
			new BankTab(1, this),
			new BankTab(2, this),
			new BankTab(3, this),
			new BankTab(4, this),
			new BankTab(5, this),
			new BankTab(6, this),
			new BankTab(7, this),
			new BankTab(8, this)
	};


	Player player;
	private BankTab currentTab = getBankTab()[0];

	public Bank(Player player) {
		this.player = player;
	}

	public void onLogin() {
		player.getPA().sendString(String.valueOf(getBankCapacity()), BANK_CAPACITY_STRING_ID);
		openTab(0);
	}

	public void deleteAllItems() {
		for (BankTab tab : bankTabs) {
			if (tab == null) {
				continue;
			}
			if (tab.size() > 0) {
				tab.bankItems.clear();
			}
		}
	}

	/**
	 * Gets the tab for the interface id. This returns the current tab if
	 * your not inside the main tab. If you're inside the main tab
	 * it will return the tab for the interface id.
	 */
	public BankTab getBankTabForInterfaceId(int interfaceId) {
		for (int index = 0; index < ITEM_CONTAINERS.length; index++) {
			if (interfaceId == ITEM_CONTAINERS[index]) {
				return bankTabs[index];
			}
		}
		return null;
	}

	public BankTab getBankForInput(int interfaceId, int itemId) {
		if (interfaceId == BANK_SEARCH_CONTAINER) {
			for (BankTab tab : bankTabs) {
				for (GameItem item : tab.getItems()) {
					if (item.id() == itemId + 1) {
						return tab;
					}
				}
			}

			return null;
		} else {
			return getBankTabForInterfaceId(interfaceId);
		}
	}

	public int getItemSlot(BankTab bankTab, int interfaceId, int itemId, int slot) {
		if (interfaceId == BANK_SEARCH_CONTAINER) {
			for (int index = 0; index < bankTab.size(); index++) {
				if (bankTab.getItem(index).id() == itemId + 1) {
					return index;
				}
			}

			return -1;
		} else {
			return slot;
		}
	}

	public boolean withdrawFromSlot(int interfaceId, int itemId, int slot, int amount) {
		if (amount != -1) {
			BankTab tab = getBankForInput(interfaceId, itemId);
			if (tab != null) {
				slot = getItemSlot(tab, interfaceId, itemId, slot);
				if (slot != -1) {
					GameItem item = tab.getItem(slot);
					if (item != null) {
						return withdraw(interfaceId, item.id() - 1, amount);
					}
				}
			}
		}
		return false;
	}

	public boolean withdraw(int interfaceId, int itemId, int amount) {
		if (amount != -1) {
			BankTab tab = getBankForInput(interfaceId, itemId);
			if (tab != null) {
				if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
					Server.getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					player.sendMessage("You cannot remove items from the bank whilst trading.");
					return true;
				}
				DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener()
						.getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.nonNull(duelSession)
						&& duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
						&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
					player.sendMessage("You have declined the duel.");
					duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
					duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					return true;
				}
				player.getItems().removeFromBank(tab, itemId, amount, true);
				return true;
			}
		}
		return false;
	}

	public int getAllButOne(int interfaceId, int itemId) {
		BankTab tab = getBankForInput(interfaceId, itemId);
		if (tab != null) {
			int amount = tab.getItemAmount(new BankItem(itemId + 1));
			if (amount <= 0)
				return -1;
			if (amount == 1) {
				player.sendMessage("Your bank only contains one of this item.");
				return -1;
			}
			return amount - 1;
		}
		return -1;
	}

	public boolean moveItemsBetweenTabs(int fromInterfaceId, int fromSlot, int toInterfaceId, int toSlot, boolean insertMode) {
		BankTab from = getBankTabForInterfaceId(fromInterfaceId);
		BankTab to = getBankTabForInterfaceId(toInterfaceId);
		if (from != null && to != null) {
			BankItem first = from.getItem(fromSlot);
			BankItem second = to.getItem(toSlot);
			if (first != null) {
				if (insertMode) {
					// insert
					from.remove(first, 0, false);
					if (toSlot >= to.getItems().size()) {
						to.getItems().add(first);
					} else {
						to.getItems().add(toSlot, first);
					}
				} else {
					// swap
					if (toSlot >= to.size()) {
						to.getItems().add(first);
					} else {
						to.setItem(toSlot, first);
					}
					if (second != null) {
						from.setItem(fromSlot, second);
					} else {
						from.remove(first, 0, false);
					}
				}
				player.getItems().queueBankContainerUpdate();
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean moveItems(int from, int to, int moveWindow, boolean insertMode) {
		BankTab tab = getBankTabForInterfaceId(moveWindow);
		if (tab != null) {
			if (!player.isBanking) {
				player.getPA().removeAllWindows();
				player.getItems().queueBankContainerUpdate();
				return true;
			}
			if (player.getPA().viewingOtherBank) {
				player.getPA().resetOtherBank();
				return true;
			}
			if (player.getBankPin().requiresUnlock()) {
				player.getItems().queueBankContainerUpdate();
				player.isBanking = false;
				player.getBankPin().open(2);
				return true;
			}
			if (to > 999) {
				// Moving items to a tab view dragging into the tab icons at the top of the bank
				int tabId = to - 1000;
				if (tabId < 0)
					tabId = 0;
				if (tabId == tab.getTabId()) {
					player.sendMessage("You cannot add an item from it's tab to the same tab.");
					player.getItems().queueBankContainerUpdate();
					return true;
				}
				if (from > tab.size()) {
					player.getItems().queueBankContainerUpdate();
					return true;
				}
				BankItem item = tab.getItem(from);
				if (item == null) {
					player.getItems().queueBankContainerUpdate();
					return true;
				}
				if (player.getBank().getBankTab()[tabId].freeSlots() == 0) {
					player.sendMessage("That tab is full.");
					return true;
				}
				tab.remove(item, 0, false);
				player.getBank().getBankTab()[tabId].add(item);
			} else {
				// Moving items inside a bank tab
				if (from > tab.size() - 1 || to > tab.size() - 1) {
					player.getItems().queueBankContainerUpdate();
					return true;
				}
				if (!insertMode) {
					BankItem item = tab.getItem(from);
					if (item == null) {
						player.getItems().queueBankContainerUpdate();
						return true;
					}
					tab.setItem(from, tab.getItem(to));
					tab.setItem(to, item);
				} else {
					int tempFrom = from;
					for (int tempTo = to; tempFrom != tempTo;)
						if (tempFrom > tempTo) {
							player.getItems().swapBankItem(tab, tempFrom, tempFrom - 1);
							tempFrom--;
						} else if (tempFrom < tempTo) {
							player.getItems().swapBankItem(tab, tempFrom, tempFrom + 1);
							tempFrom++;
						}
				}
			}

			player.getItems().queueBankContainerUpdate();
			return true;
		}

		return false;
	}

	public boolean containsItem(int itemId) {
		return Arrays.stream(getBankTab()).anyMatch(bank -> bank.contains(new BankItem(itemId)));
	}

	public void openTab(int tab) {
		player.getPA().sendConfig(BANK_TAB_CONFIG, tab);
	}

	public int getItemCount() {
		return Arrays.stream(bankTabs).mapToInt(tab -> tab.getItems().size()).sum();
	}

	public int getBankCapacity() {
		return Configuration.BANK_CAPACITY + (int) (Right.DONATOR_SET.stream().filter(
				right -> player.getRights().getSet().stream().anyMatch(
						right2 -> right2.isOrInherits(right))).count() * 15);
	}

	public boolean hasRoomFor(int id, int amount) {
		return hasRoomFor(new BankItem(id, amount));
	}

	public boolean hasRoomFor(BankItem bankingItem) {
		int slotsFilled = 0;
		for (BankTab bankTab : bankTabs) {
			for (GameItem bankedItem : bankTab.getItems()) {
				if (bankedItem.id() + 1 == bankingItem.id()) {
					long amount = (long) bankedItem.amount() + (long) bankingItem.amount();
					if (amount > Integer.MAX_VALUE) {
						player.sendMessage("Your bank doesn't have room for this item.");
						return false;
					} else {
						return true;
					}
				}

				slotsFilled++;
			}
		}

		if (slotsFilled + 1 <= getBankCapacity()) {
			return true;
		} else {
			player.sendMessage("You're bank is full.");
			return false;
		}
	}

	public int getTotalSize() {
		return Arrays.stream(bankTabs).mapToInt(BankTab::size).sum();
	}

	public BankTab[] getBankTab() {
		return bankTabs;
	}

	public BankTab getBankTab(int tabId) {
		for (BankTab tab : bankTabs)
			if (tab.getTabId() == tabId)
				return tab;
		return bankTabs[0];
	}

	public void setBankTab(int tabId, BankTab tab) {
		this.bankTabs[tabId] = tab;
	}

	public BankTab getCurrentBankTab() {
		if (currentTab == null)
			currentTab = getBankTab()[0];
		return this.currentTab;
	}

	public BankTab setCurrentBankTab(BankTab bankTab) {
		return this.currentTab = bankTab;
	}

}
