package io.xeros.content.dwarfmulticannon;

import io.xeros.model.Direction;

/**
 * The eight directions a cannon can face, in chronological order (note: do
 * not change order, order is crucial!).
 */
enum CannonRotationState {
    NORTH(Direction.NORTH, 516),
    NORTH_EAST(Direction.NORTH_EAST, 517),
    EAST(Direction.EAST, 518),
    SOUTH_EAST(Direction.SOUTH_EAST, 519),
    SOUTH(Direction.SOUTH, 520),
    SOUTH_WEST(Direction.SOUTH_WEST, 521),
    WEST(Direction.WEST, 514),
    NORTH_WEST(Direction.NORTH_WEST, 515);

    private final Direction direction;
    private final int animationId;


    CannonRotationState(Direction direction, int animationId) {
        this.direction = direction;
        this.animationId = animationId;
    }

    public int getAnimationId() {
        return animationId;
    }
}

