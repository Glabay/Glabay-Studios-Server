package io.xeros.content.bosses.zulrah;

import java.awt.Point;

public enum ZulrahLocation {
	NORTH(new Point(2266, 3072)),
	SOUTH(new Point(2266, 3062)),
	WEST(new Point(2257, 3071)),
	EAST(new Point(2275, 3072));

	private final Point location;

	ZulrahLocation(Point location) {
		this.location = location;
	}

	public Point getLocation() {
		return location;
	}

}
