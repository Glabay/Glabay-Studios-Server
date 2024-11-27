package io.xeros.model.items.bank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Apr 11, 2014
 */
public class BankTab {

	CopyOnWriteArrayList<BankItem> bankItems = new CopyOnWriteArrayList<>();

	private int tabId;

	private final Bank bank;

	/**
	 * 
	 * @param tabId The bank tab id
	 */
	public BankTab(int tabId, Bank bank) {
		this.setTabId(tabId);
		this.bank = bank;
	}

	/**
	 *
	 * @param bankItem The object that contains the item id and amount
	 */
	public void add(BankItem bankItem) {
		if (bankItem.amount() < 0)
			return;
		for (BankItem item : bankItems) {
			if (item.id() == bankItem.id()) {
				// TODO this is wrong
				item.setAmount(bankItem.amount() + item.amount());
				if (item.amount() > Integer.MAX_VALUE)
					item.setAmount(Integer.MAX_VALUE);
				return;
			}
		}
		bankItems.add(bankItem);
	}

	/**
	 * 
	 * @param bankItem Removes the BankItem object from the ArrayList
	 */
	public void remove(BankItem bankItem, int type, boolean placeHolder) {
		Collection<BankItem> items = new ArrayList<>();
		for (BankItem item : bankItems) {
			if (item != null && item.id() == bankItem.id()) {
				if (item.amount() - bankItem.amount() <= 0) {
					if (placeHolder && type == 0) {
						item.setAmount(0);
					}
					else
					items.add(item); 
				} else {
					//Stil some item amount left
					item.setAmount(item.amount() - bankItem.amount());
					if (item.amount() <= 0 && placeHolder) {
						item.setAmount(0);
					}
				}
				break;
			}
		}
		bankItems.removeAll(items);
	}

	/**
	 * 
	 * @return The current amount of items in the bank tab
	 */
	public int size() {
		return bankItems.size();
	}

	/**
	 * 
	 * @return The amount of free slots remaining in this tab
	 */
	public int freeSlots() {
		if (bank.getItemCount() >= bank.getBankCapacity()) {
			return 0;
		} else {
			return bank.getBankCapacity() - bank.getItemCount();
		}
	}

	public boolean contains(BankItem bankItem) {
		for (int i = 0; i < bankItems.size(); i++)
			if (bankItems.get(i) != null)
				if (bankItems.get(i).id() == bankItem.id())
					return true;
		return false;
	}

	public boolean containsAmount(BankItem bankItem) {
		for (int i = 0; i < bankItems.size(); i++)
			if (bankItems.get(i) != null)
				if (bankItems.get(i).id() == bankItem.id())
					return bankItems.get(i).amount() >= bankItem.amount();
		return false;
	}

	public boolean spaceAvailable(BankItem bankItem) {
		for (int i = 0; i < bankItems.size(); i++) {
			if (bankItems.get(i) != null) {
				if (bankItems.get(i).id() == bankItem.id()) {
					long total = (long) bankItems.get(i).amount() + (long) bankItem.amount();
					// TODO this doesn't work, the integer will roll over
					return total <= Integer.MAX_VALUE;
				}
			}
		}
		return true;
	}

	public int getItemAmount(BankItem bankItem) {
		for (BankItem item : bankItems)
			if (item.id() == bankItem.id())
				return item.amount();
		return 0;
	}

	public BankItem getItem(int slot) {
		if (slot < bankItems.size()) {
			return bankItems.get(slot);
		}
		return null;
	}

	public BankItem getItem(BankItem item) {
		for (BankItem items : bankItems)
			if (items.id() == item.id())
				return items;
		return null;
	}

	public void setItem(int slot, BankItem item) {
		bankItems.set(slot, item);
	}

	public CopyOnWriteArrayList<BankItem> getItems() {
		return bankItems;
	}

	public int getTabId() {
		return tabId;
	}

	public void setTabId(int tabId) {
		this.tabId = tabId;
	}

}
