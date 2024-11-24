package io.xeros.content.bosses.araxxor.attack;

import io.xeros.content.bosses.araxxor.Araxxor;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public interface Attack {
    void invoke(Araxxor araxxor, Player target);
}
