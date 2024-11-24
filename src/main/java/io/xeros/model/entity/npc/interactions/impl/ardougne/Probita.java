package io.xeros.model.entity.npc.interactions.impl.ardougne;

import io.xeros.content.PetCollector;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.PROBITA;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class Probita extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { PROBITA };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        PetCollector.petCollectorDialogue(player);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        io.xeros.model.entity.npc.pets.Probita.cancellationOfPreviousPet(player);
        return true;
    }

}
