package io.xeros.model.items;

import io.xeros.util.ItemConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class NamedItem {

    private int id;
    private String name;
    private int amount;

    public GameItem toGameItem(ItemConstants itemConstants) {
        return new GameItem(getId(itemConstants), amount);
    }
    public ImmutableItem toImmutableItem(ItemConstants itemConstants) {
        return new ImmutableItem(getId(itemConstants), amount);
    }

    public int getId(ItemConstants itemConstants) {
        return id != 0 ? id : itemConstants.get(name);
    }
}
