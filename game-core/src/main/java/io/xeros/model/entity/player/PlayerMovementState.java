package io.xeros.model.entity.player;

public record PlayerMovementState(boolean allowClickToMove, boolean runningEnabled, boolean locked) {
	public static PlayerMovementState getDefault() {
		return new PlayerMovementStateBuilder().createPlayerMovementState();
	}
}








