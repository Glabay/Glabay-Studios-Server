package io.xeros.content.dialogue.impl;

import io.xeros.content.bosses.Hunllef;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.entity.player.Player;

public class CrystalCaveKeyDialogue extends DialogueBuilder {


    public CrystalCaveKeyDialogue(Player player) {
        super(player);
        setNpcId(8761);
                    npc("Nice key, would you like to go straight to Hunllef?")
                    .option(new DialogueOption("Yes, I am ready to fight, and understand I can't teleport away!", p -> startHunllef(player)),
                            new DialogueOption("No, I don't want to fight Hunllef yet.", p -> p.getPA().closeAllWindows()));

    }

    private void startHunllef(Player player) {
        Hunllef.start(player);
    }
}
