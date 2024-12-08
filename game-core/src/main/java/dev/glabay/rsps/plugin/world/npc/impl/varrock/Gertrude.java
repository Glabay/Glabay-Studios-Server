package dev.glabay.rsps.plugin.world.npc.impl.varrock;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.GERTRUDE_DIALOGUE;
import static io.xeros.model.definition.Npcs.GERTRUDE;
import static io.xeros.model.definition.Npcs.GERTRUDE_7723;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Gertrude extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { GERTRUDE, GERTRUDE_7723  };
    }

    @Override
        public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(GERTRUDE_DIALOGUE, GERTRUDE);
            return true;
        }
    }
