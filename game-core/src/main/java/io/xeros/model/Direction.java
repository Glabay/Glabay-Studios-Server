package io.xeros.model;

import com.google.common.base.Preconditions;
import io.xeros.model.entity.npc.NPCClipping;
import io.xeros.model.entity.player.Position;

import java.util.Arrays;

/**
 * Represents a single movement direction.
 * 
 * @author Graham
 */
public enum Direction {

	/**
	 * North movement.
	 */
	NORTH(1, 0, 1),

	/**
	 * North east movement.
	 */
	NORTH_EAST(2, 1, 1),

	/**
	 * East movement.
	 */
	EAST(4, 1, 0),

	/**
	 * South east movement.
	 */
	SOUTH_EAST(7, 1, -1),

	/**
	 * South movement.
	 */
	SOUTH(6, 0, -1),

	/**
	 * South west movement.
	 */
	SOUTH_WEST(5, -1, -1),

	/**
	 * West movement.
	 */
	WEST(3, -1, 0),

	/**
	 * North west movement.
	 */
	NORTH_WEST(0, -1, 1),

	/**
	 * No movement.
	 */
	NONE(-1, 0, 0);

	private static final Direction[] VALUES = Direction.values();

	public static Direction get(int intValue) {
		return Arrays.stream(VALUES).filter(it -> it.intValue == intValue).findFirst()
				.orElseThrow(() -> new IllegalStateException("No direction for value: " + intValue));
	}

	public static Direction fromDeltas(Position a, Position b) {
		Position position = a.toDirectional(b);
		return fromDeltas(position.getX(), position.getY());
	}

	/**
	 * Get a Direction for the provided directional position.
	 * @param position A directional position, meaning x/y are between -1 and 1 inclusive.
	 */
	public static Direction fromDirectional(Position position) {
		return fromDeltas(position.getX(), position.getY());
	}

	/**
	 * Creates a direction from the differences between X and Y.
	 * 
	 * @param deltaX
	 *            The difference between two X coordinates.
	 * @param deltaY
	 *            The difference between two Y coordinates.
	 * @return The direction.
	 */
	public static Direction fromDeltas(int deltaX, int deltaY) {
		if (deltaY == 1) {
			if (deltaX == 1)
				return NORTH_EAST;
			else if (deltaX == 0)
				return NORTH;
			else
				return NORTH_WEST;
		} else if (deltaY == -1) {
			if (deltaX == 1)
				return SOUTH_EAST;
			else if (deltaX == 0)
				return SOUTH;
			else
				return SOUTH_WEST;
		} else if (deltaX == 1)
			return EAST;
		else if (deltaX == -1)
			return WEST;

		if (deltaX == 0 && deltaY == 0) {
			return Direction.NONE;
		}
		throw new IllegalArgumentException("Deltas not supported: " + deltaX + " " + deltaY);
	}

	/**
	 * Checks if the direction represented by the two delta values can connect
	 * two points together in a single direction.
	 * 
	 * @param deltaX
	 *            The difference in X coordinates.
	 * @param deltaY
	 *            The difference in X coordinates.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public static boolean isConnectable(int deltaX, int deltaY) {
		return Math.abs(deltaX) == Math.abs(deltaY) || deltaX == 0
				|| deltaY == 0;
	}

	public static int getReverseDirection(int direction) {
		return getReverseDirection(Direction.get(direction));
	}

	public static int getReverseDirection(Direction direction) {
		switch (direction) {
			case EAST:
				return WEST.intValue;
			case WEST:
				return EAST.intValue;
			case NORTH:
				return SOUTH.intValue;
			case SOUTH:
				return NORTH.intValue;
			case NORTH_EAST:
				return SOUTH_WEST.intValue;
			case SOUTH_EAST:
				return NORTH_WEST.intValue;
			case NORTH_WEST:
				return SOUTH_EAST.intValue;
			case SOUTH_WEST:
				return NORTH_EAST.intValue;
			default:
				return -1;
		}
	}

	/**
	 * The direction as an integer.
	 */
	private final int intValue;
	private final int x;
	private final int y;

	/**
	 * Creates the direction.
	 * 
	 * @param intValue
	 *            The direction as an integer.
	 */
    Direction(int intValue, int x, int y) {
		this.intValue = intValue;
		this.x = x;
		this.y = y;

		if (intValue != -1) {
			int[] delta = NPCClipping.DIR[intValue];
			Preconditions.checkState(x == delta[0] && y == delta[1]);

			//int delta2x = Misc.directionDeltaX[intValue];
			//int delta2y = Misc.directionDeltaY[intValue];
			//Preconditions.checkState(x == delta2x && y == delta2y, String.format("%s - %d != %d OR %d != %d", name(), delta2x, x, delta2y, y));
		}
	}

	public int x() {
    	return getDelta()[0];
	}

	public int y() {
    	return getDelta()[1];
	}

	public Position getDeltaPosition() {
		return new Position(getDelta()[0], getDelta()[1]);
	}

	public int[] getDelta() {
    	return new int[] {x, y};
	}

	/**
	 * Gets the direction as an integer which the client can understand.
	 * 
	 * @return The movement as an integer.
	 */
	public int toInteger() {
		return intValue;
	}

}