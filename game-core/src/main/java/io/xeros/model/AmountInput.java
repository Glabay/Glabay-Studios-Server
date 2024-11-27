package io.xeros.model;

import io.xeros.model.entity.player.Player;

public interface AmountInput {
    void handle(Player player, int amount);
}
