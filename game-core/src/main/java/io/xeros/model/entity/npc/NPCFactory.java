package io.xeros.model.entity.npc;

import java.util.Arrays;

import io.xeros.content.questing.hftd.DagannothMother;
import io.xeros.model.entity.player.Position;

public class NPCFactory {

    public static NPC create(NPC oldInstance, int npcIndex, int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit) {
        NPC npc = oldInstance.provideRespawnInstance();
        if (npc == null) {
            if (Arrays.stream(DagannothMother.DAGANNOTH_MOTHER_TRANSFORMS).anyMatch(dagId -> dagId == npcType)) {
                npc = new DagannothMother(new Position(x, y, heightLevel));
                NPCSpawning.finishNpcConstruction(npc, WalkingType, maxHit);
            } else {
                npc = NPCSpawning.newNPC(npcIndex, npcType, x, y, heightLevel, WalkingType, maxHit);
            }
        } else {
            NPCSpawning.finishNpcConstruction(npc, WalkingType, maxHit);
        }

        return npc;
    }

}
