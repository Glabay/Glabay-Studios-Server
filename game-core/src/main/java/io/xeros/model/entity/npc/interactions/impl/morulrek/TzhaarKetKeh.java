package io.xeros.model.entity.npc.interactions.impl.morulrek;

import io.xeros.content.minigames.inferno.Inferno;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import static io.xeros.model.definition.Items.FIRE_CAPE;
import static io.xeros.model.definition.Items.TZREK_JAD;
import static io.xeros.model.definition.Npcs.TZAHAARKETKEH;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class TzhaarKetKeh extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { TZAHAARKETKEH };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        Inferno.startInferno(player, Inferno.getDefaultWave());
        return true;
    }
    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        Inferno.gamble(player);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) { //storing here for now until Inferno/Fight Waves are reworked
        if (player.getItems().playerHasItem(FIRE_CAPE, 1)) {
            int TzrekJadPetChance = Misc.random(200);
            if (TzrekJadPetChance == 0) {
                player.getItems().deleteItem2(FIRE_CAPE, 1);
                player.getItems().addItem(TZREK_JAD, 1);
                player.sendMessage("@red@Congratulations! you have won the Tzrek-Jad pet!");
            } else {
                player.getItems().deleteItem2(FIRE_CAPE, 1);
                player.sendMessage("@red@Unlucky! better luck next time.");
            }
        } else {
            player.sendMessage("@red@You dont have 10 firecapes to gamble.");
        }
        return true;
    }

}
