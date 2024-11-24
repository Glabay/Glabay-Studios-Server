package io.xeros.content.skills.slayer;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.player.Player;

public class KonarDialogue extends DialogueBuilder {

    private final Task task;

    public KonarDialogue(Player player, Task task) {
        super(player);
        this.task = task;
        KonarSlayer.assignKonarSlayer(player, task);
        setNpcId(Npcs.KONAR_QUO_MATEN);
        npc("Your are to bring balance to ", player.getSlayer().getTaskAmount() + " " + task.getFormattedName() + " in the " + player.getKonarSlayerLocation() + ".")
                .exit(plr -> { plr.getPA().closeAllWindows(); }
                );
    }

}
