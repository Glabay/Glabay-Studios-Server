package io.xeros.model.entity.player.lock;

import io.xeros.model.entity.player.Player;

/**
 * A lock that will completely prevent the player from doing anything.
 */
public class CompleteLock implements PlayerLock {
    @Override
    public boolean cannotLogout(Player player) {
        return true;
    }

    @Override
    public boolean cannotInteract(Player player) {
        return true;
    }

    @Override
    public boolean cannotClickItem(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean cannotTeleport(Player player) {
        return true;
    }
}
