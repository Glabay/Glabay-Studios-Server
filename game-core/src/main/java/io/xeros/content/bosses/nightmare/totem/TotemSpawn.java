package io.xeros.content.bosses.nightmare.totem;

import java.util.Arrays;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Position;

public enum TotemSpawn {
    NORTH_WEST(9440, 9441, 9442, new Position(3863, 9958, 3)),
    NORTH_EAST(9443, 9444, 9445, new Position(3879, 9958, 3)),
    SOUTH_EAST(9437, 9438, 9439, new Position(3879, 9942, 3)),
    SOUTH_WEST(9434, 9435, 9436, new Position(3863, 9942, 3)),
    ;

    private final int attackableNpcId;
    private final int unattackableNpcId;
    private final int chargedNpcId;
    private final Position position;

    TotemSpawn(int unattackableNpcId, int attackableNpcId, int chargedNpcId, Position position) {
        this.unattackableNpcId = unattackableNpcId;
        this.attackableNpcId = attackableNpcId;
        this.position = position;
        this.chargedNpcId = chargedNpcId;
    }

    public int getUnattackableNpcId() {
        return unattackableNpcId;
    }

    public int getAttackableNpcId() {
        return attackableNpcId;
    }

    public int getChargedNpcId() {
        return chargedNpcId;
    }

    public Position getPosition() {
        return position;
    }

    public static boolean isTotem(NPC npc) {
        return Arrays.stream(values()).anyMatch(totemSpawn -> totemSpawn.getChargedNpcId() == npc.getNpcId()
                || totemSpawn.getUnattackableNpcId() == npc.getNpcId() || totemSpawn.getAttackableNpcId() == npc.getNpcId());
    }
}
