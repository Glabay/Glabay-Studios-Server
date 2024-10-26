package io.xeros.model.items.interactions.impl.offhand;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.WorldItemAction;

import static io.xeros.model.Items.AVERNIC_DEFENDER_HILT;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */

public class AvernicDefender extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { AVERNIC_DEFENDER_HILT };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        player.sendMessage("Attach it onto a Dragon Defender to make an Avernic Defender.");
        return true;
    }
}
