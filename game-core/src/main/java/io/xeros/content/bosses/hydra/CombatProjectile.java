package io.xeros.content.bosses.hydra;

import io.xeros.Configuration;
import io.xeros.model.collisionmap.doors.Location;

public class CombatProjectile {
    public int angle;
    public int gfx;
    public int startHeight;
    public int endHeight;
    public int delay;
    public int speed;
    public int offset;

    // TODO not sure why offset isn't set here in either constructor, maybe intentional?
    public CombatProjectile(int gfx, int startHeight, int endHeight, int delay, int speed, int offset, int angle) {
        this.gfx = gfx;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.delay = delay;
        this.speed = speed;
        this.angle = angle;
    }

    public CombatProjectile(CombatProjectile projectile) {
        this.gfx = projectile.gfx;
        this.startHeight = projectile.startHeight;
        this.endHeight = projectile.endHeight;
        this.delay = projectile.delay;
        this.speed = projectile.speed;
        this.angle = projectile.angle;
    }

    /**
     * Sets the start height and returns the new instance.
     * @param startHeight
     * @return
     */
    public CombatProjectile setStartHeight(int startHeight) {
        this.startHeight = startHeight;
        return this;
    }

    /**
     * Sets the end height and returns the new instance.
     * @param endHeight
     * @return
     */
    public CombatProjectile setEndHeight(int endHeight) {
        this.endHeight = endHeight;
        return this;
    }

    /**
     * Sets the gfx and returns the new instance.
     * @param gfx
     * @return
     */
    public CombatProjectile setGraphics(int gfx) {
        this.gfx = gfx;
        return this;
    }

    /**
     * Sets the delay and returns the new instance.
     * @param delay
     * @return
     */
    public CombatProjectile setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    /**
     * Sets the speed and returns the new instance.
     * @param speed
     * @return
     */
    public CombatProjectile setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    /**
     * Sets the angle and returns the new instance.
     * @param angle
     * @return
     */
    public CombatProjectile setAngle(int angle) {
        this.angle = angle;
        return this;
    }

    /**
     * Sets the offset and returns the new instance.
     * @param offset
     * @return
     */
    public CombatProjectile setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Gets the time in milliseconds until the projectile hits the target.
     * @param from
     * 			the location from which the projectile originated from.
     * @param to
     * 			the location the projectile is going to.
     * @return
     * 			the time in milliseconds until the projectile hits the target.
     */
    public final long getHitSyncToMillis(Location from, Location to) {
        return getHitSyncToTicks(from, to) * Configuration.CYCLE_TIME;
    }

    /**
     * Gets the time in ticks till the projectile hits the target.
     * @param from
     * 			the location from which the projectile originated from.
     * @param to
     * 			the location the projectile is going to.
     * @return
     * 			the time in game ticks till the projectile hits the target.
     */
    public final int getHitSyncToTicks(Location from, Location to) {
        return (int) Math.floor(from.getDistance(to) / 3) + 1;
    }

    /**
     * Returns a copy of the desired projectile.
     * @param projectile
     * 			the projectile to copy.
     * @return
     */
    public static final CombatProjectile copy(CombatProjectile projectile) {
        return new CombatProjectile(projectile);
    }

    public int getAngle() {
        return this.angle;
    }

    public int getGfx() {
        return this.gfx;
    }

    public int getStartHeight() {
        return this.startHeight;
    }

    public int getEndHeight() {
        return this.endHeight;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getOffset() {
        return this.offset;
    }
}
