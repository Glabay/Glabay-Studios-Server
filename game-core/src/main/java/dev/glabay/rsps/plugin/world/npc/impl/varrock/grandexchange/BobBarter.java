package dev.glabay.rsps.plugin.world.npc.impl.varrock.grandexchange;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.skills.herblore.PotionDecanting;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.BOB_BARTER_HERBS;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class BobBarter extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] {  BOB_BARTER_HERBS };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendNpcChat1("I can decant potions for you.", BOB_BARTER_HERBS, "Bob Barter");
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        PotionDecanting.decantInventory(player);
        player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.POTION_DECANT);
        return true;
    }
}
