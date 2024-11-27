package io.xeros.model.items.bank;

import io.xeros.model.items.GameItem;

/**
 * @author Jason http://www.rune-server.org/members/jason
 * @author Michael Sasse (https://github.com/mikeysasse/)
 * @date Apr 11, 2014
 */
public class BankItem extends GameItem {

    /**
     * @param itemId     The item id of the bank item
     * @param itemAmount The amount of items for the bank item
     */
    public BankItem(int itemId, int itemAmount) {
        super(itemId, itemAmount);
    }

    /**
     * @param itemId The item id of the bank item
     */
    public BankItem(int itemId) {
        this(itemId, 0);
    }

}
