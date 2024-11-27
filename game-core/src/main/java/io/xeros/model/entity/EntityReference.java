package io.xeros.model.entity;

import java.util.Objects;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import lombok.Getter;

@Getter
public class EntityReference {

    public static EntityReference getReference(Entity entity) {
        if (entity == null) {
            return new EntityReference(0, true, 0);
        }

        long id;
        if (entity.isPlayer()) {
            id = entity.asPlayer().getNameAsLong();
        } else {
            id = hashNpc(entity.asNPC());
        }
        return new EntityReference(id, entity.isNPC(), entity.getIndex());
    }

    private static long hashNpc(NPC npc) {
        return Objects.hash(npc.getNpcId(), npc.getDefinition().getName());
    }

    private final boolean npc;
    private final int index;
    private final long id;

    private EntityReference(long id, boolean npc, int index) {
        this.id = id;
        this.npc = npc;
        this.index = index;
    }

    public Entity get() {
        if (npc) {
            NPC npc = NPCHandler.npcs[index];
            if (npc == null) {
                return null;
            } else {
                return hashNpc(npc) == id ? npc : null;
            }
        } else {
            Player player = PlayerHandler.players[index];
            if (player == null) {
                return null;
            } else {
                return player.getNameAsLong() == id ? player : null;
            }
        }
    }

}
