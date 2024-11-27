package io.xeros.model.entity.player.mode.group.log;

import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import lombok.Getter;

@Getter
public class GimDropItemLog {

    private final String displayName;
    private final GameItem gameItem;
    private final Position position;

    public GimDropItemLog(String displayName, GameItem gameItem, Position position) {
        this.displayName = displayName;
        this.gameItem = gameItem;
        this.position = position;
    }

    private GimDropItemLog() {
        displayName = null;
        gameItem = null;
        position = null;
    }

    @Override
    public String toString() {
        return getDisplayName() + " dropped " + getGameItem().getFormattedString() + " at " + getPosition().getFormattedString();
    }

}
