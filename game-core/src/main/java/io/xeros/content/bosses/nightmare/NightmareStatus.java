package io.xeros.content.bosses.nightmare;

import java.util.Arrays;

public enum NightmareStatus {
    IDLE(9460),
    STARTING(9461),
    PHASE_1(9462),
    PHASE_2(9463),
    PHASE_3(9464)
    ;

    public static boolean isStatusNpc(int npcId) {
        return Arrays.stream(values()).anyMatch(status -> status.npcId == npcId);
    }

    private final int npcId;

    NightmareStatus(int npcId) {
        this.npcId = npcId;
    }

    public int getNpcId() {
        return npcId;
    }
}
