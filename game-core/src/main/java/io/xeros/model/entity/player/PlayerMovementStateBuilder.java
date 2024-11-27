package io.xeros.model.entity.player;

public class PlayerMovementStateBuilder {
    private boolean allowClickToMove = true;
    private boolean runningEnabled = true;
    private boolean locked = false;

    public PlayerMovementStateBuilder setAllowClickToMove(boolean allowClickToMove) {
        this.allowClickToMove = allowClickToMove;
        return this;
    }

    public PlayerMovementStateBuilder setRunningEnabled(boolean runningEnabled) {
        this.runningEnabled = runningEnabled;
        return this;
    }

    public PlayerMovementStateBuilder setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public PlayerMovementState createPlayerMovementState() {
        return new PlayerMovementState(allowClickToMove, runningEnabled, locked);
    }
}