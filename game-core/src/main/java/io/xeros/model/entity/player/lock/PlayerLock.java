package io.xeros.model.entity.player.lock;

import io.xeros.model.entity.player.Player;

public interface PlayerLock {

    boolean cannotLogout(Player player);

    /**
     * Can the player not interact with the game world.
     * This includes walking, clicking objects/players/npcs.
     */
    boolean cannotInteract(Player player);

    boolean cannotClickItem(Player player, int itemId);

    boolean cannotTeleport(Player player);
}
