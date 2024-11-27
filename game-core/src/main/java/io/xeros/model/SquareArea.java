package io.xeros.model;

import io.xeros.model.entity.Entity;

/**
 * Represents a square segment of the map.
 *
 * @author Michael Sasse (<a href="https://github.com/mikeysasse/">...</a>)
 */
public record SquareArea(int lowX, int highY, int highX, int lowY) implements Area {
    public boolean inside(Entity entity) {
        return entity.getX() >= lowX && entity.getY() >= lowY && entity.getX() <= highX && entity.getY() <= highY;
    }
}
