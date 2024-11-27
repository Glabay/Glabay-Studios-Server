package io.xeros.content.bosses.nightmare;

import io.xeros.model.entity.player.Player;

public class NightmareInstanceManager {

    private static final NightmareInstanceManager SINGLETON = new NightmareInstanceManager();

    public static NightmareInstanceManager getSingleton() {
        return SINGLETON;
    }

    private NightmareInstance nightmareInstance;

    public boolean canJoin() {
        return nightmareInstance == null || nightmareInstance.getNightmare().isJoinable();
    }

    public boolean join(Player player) {
        if (canJoin()) {
            if (nightmareInstance == null) {
                setNightmareInstance(new NightmareInstance(true));
                nightmareInstance.countdown();
            }

            //player.moveTo(NightmareConstants.NIGHTMARE_PLAYER_SPAWN_POSITION);
            nightmareInstance.add(player);
            return true;
        } else {
            player.sendMessage("The fight cannot be joined, you must wait until it's over.");
            return false;
        }
    }

    void setNightmareInstance(NightmareInstance nightmareInstance) {
        this.nightmareInstance = nightmareInstance;
    }
}
