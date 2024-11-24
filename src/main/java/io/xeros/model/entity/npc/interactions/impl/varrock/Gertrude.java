package io.xeros.model.entity.npc.interactions.impl.varrock;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.GERTRUDE_DIALOGUE;
import static io.xeros.model.definition.Npcs.*;

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
