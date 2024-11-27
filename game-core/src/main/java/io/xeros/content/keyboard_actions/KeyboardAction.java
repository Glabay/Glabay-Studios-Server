package io.xeros.content.keyboard_actions;

import io.xeros.content.preset.PresetManager;
import io.xeros.model.entity.player.Player;

/**
 * @author Leviticus | www.rune-server.ee/members/leviticus
 * @version 1.0
 */
public enum KeyboardAction implements KeyboardStrategy {

    RELOAD_LAST_PRESET(1, player -> {
            PresetManager.getSingleton().loadLastPreset(player);
    });
    int action;
    private final KeyboardStrategy strategy;

    KeyboardAction(int action, KeyboardStrategy strategy) {
        this.action = action;
        this.strategy = strategy;
    }

    public int getAction() {
        return action;
    }

    @Override
    public void execute(Player player) {
        strategy.execute(player);
    }
}
