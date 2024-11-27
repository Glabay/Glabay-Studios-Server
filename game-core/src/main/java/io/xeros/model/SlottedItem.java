package io.xeros.model;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemInterface;
import io.xeros.util.Misc;
import lombok.Getter;

public record SlottedItem(int id, int amount, @Getter int slot) implements ItemInterface {

    public GameItem toGameItem() {
        return new GameItem(id, amount);
    }

    @Override
    public String toString() {
        ItemDef definition = ItemDef.forId(id);
        String name = definition == null ? "null" : definition.getName();
        return "SlottedItem{" +
            "name=" + name +
            ", id=" + id +
            ", amount=" + Misc.insertCommas(String.valueOf(amount)) +
            '}';
    }

}
