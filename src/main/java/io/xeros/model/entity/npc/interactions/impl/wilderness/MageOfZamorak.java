package io.xeros.model.entity.npc.interactions.impl.wilderness;

import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.MAGE_OF_ZAMORAK_DIALOGUE;
import static io.xeros.model.definition.Npcs.MAGE_OF_ZAMORAK;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class MageOfZamorak extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MAGE_OF_ZAMORAK };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(MAGE_OF_ZAMORAK_DIALOGUE, MAGE_OF_ZAMORAK);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getPA().startTeleport(3039, 4835, 0, "modern", false);
        player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.ABYSS_TELEPORT);
        player.dialogueAction = -1;
        player.teleAction = -1;
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY))
            player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TELEPORT_ESSENCE_VAR);
        if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY))
            player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.TELEPORT_ESSENCE_ARD);
        if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY))
            player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.TELEPORT_ESSENCE_FAL);
        player.getPA().startTeleport(2929, 4813, 0, "modern", false);
        return true;
    }

    @Override
    public Boolean handleActionFour(Player player, NPC npc) {
        player.getPA().startTeleport(3039, 4788, 0, "modern", false);
        player.teleAction = -1;
        return true;
    }
}
