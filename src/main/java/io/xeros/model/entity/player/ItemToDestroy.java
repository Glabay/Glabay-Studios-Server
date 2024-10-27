package io.xeros.model.entity.player;

import org.jetbrains.annotations.NotNull;
public record ItemToDestroy(int itemId, int itemSlot, DestroyType type) {
    public ItemToDestroy(final int itemId, int itemSlot, @NotNull final DestroyType type) {
        this.itemId = itemId;
        this.itemSlot = itemSlot;
        this.type = type;
    }
}
