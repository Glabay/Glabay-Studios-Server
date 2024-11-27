package io.xeros.model.items;

import io.xeros.model.definitions.ItemDef;

public interface ItemInterface {

    default ItemDef getDef() {
        return ItemDef.forId(id());
    }

    int id();

    int amount();

}
