package io.xeros.model.entity.player.mode.group.log;

import io.xeros.model.items.GameItem;
import lombok.Getter;

@Getter
public class GimWithdrawItemLog {

    private final String displayName;
    private final GameItem gameItem;

    public GimWithdrawItemLog(String displayName, GameItem gameItem) {
        this.displayName = displayName;
        this.gameItem = gameItem;
    }

    @Override
    public String toString() {
        return getDisplayName() + " withdraw " + getGameItem().getFormattedString();
    }

    public GimWithdrawItemLog() {
        displayName = null;
        gameItem = null;
    }

}
