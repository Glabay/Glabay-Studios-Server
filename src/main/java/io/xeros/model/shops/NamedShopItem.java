package io.xeros.model.shops;

import io.xeros.model.items.NamedItem;
import io.xeros.util.ItemConstants;
import lombok.Getter;

@Getter
public class NamedShopItem extends NamedItem {

    private int price;
    public ShopItem toShopItem(ItemConstants itemConstants) {
        return new ShopItem(getId(itemConstants), getAmount(), price);
    }

}
