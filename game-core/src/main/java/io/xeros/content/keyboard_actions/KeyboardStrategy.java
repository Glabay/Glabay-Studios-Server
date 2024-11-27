package io.xeros.content.keyboard_actions;

import io.xeros.model.entity.player.Player;

@FunctionalInterface
public interface KeyboardStrategy {
    void execute(Player player);
}
