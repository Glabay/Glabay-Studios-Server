package dev.glabay.rsps.plugin.world.npc.impl.burthorpe.roguesden;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.GRACE_DIALOGUE;
import static io.xeros.model.definition.Npcs.GRACE;
import static io.xeros.model.definition.Shops.GRACE_SHOP;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Grace extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { GRACE };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(GRACE_DIALOGUE, GRACE);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getShops().openShop(GRACE_SHOP);
        return true;
}
}
