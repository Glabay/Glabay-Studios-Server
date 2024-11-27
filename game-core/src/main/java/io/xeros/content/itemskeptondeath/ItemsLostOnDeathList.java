package io.xeros.content.itemskeptondeath;

import io.xeros.model.items.GameItem;

import java.util.List;

public class ItemsLostOnDeathList {

    private final List<GameItem> kept;
    private final List<GameItem> lost;

    public ItemsLostOnDeathList(List<GameItem> kept, List<GameItem> lost) {
        this.kept = kept;
        this.lost = lost;
    }

    public List<GameItem> getKept() {
        return kept;
    }

    public List<GameItem> getLost() {
        return lost;
    }
}
