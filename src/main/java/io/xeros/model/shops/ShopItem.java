package io.xeros.model.shops;

import io.xeros.model.items.GameItem;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShopItem extends GameItem {

    private int price;

    public ShopItem(int id, int amount, int price) {
        super(id, amount);
        this.price = price;
    }
}
