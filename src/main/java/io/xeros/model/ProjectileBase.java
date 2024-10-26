package io.xeros.model;

import com.google.common.base.Preconditions;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Position;
import lombok.Getter;

@Getter
public class ProjectileBase {

    /**
     * The id of the projectile.
     */
    private final int projectileId;

    /**
     * The delay of the projectile.
     */
    private final int delay;

    /**
     * The speed of the projectile.
     */
    private final int speed;

    /**
     * The starting height of the projectile.
     */
    private final int startHeight;

    /**
     * The ending height of the projectile.
     */
    private final int endHeight;

    /**
     * The curve angle of the projectile.
     */
    private final int curve;

    /**
     * Ticks until the projectile is sent.
     */
    private final int sendDelay;

    private int scale = 64;
    private int pitch = 16;

    public ProjectileBase(int projectileId, int delay, int speed, int startHeight, int endHeight, int curve, int sendDelay) {
        Preconditions.checkArgument(delay <= 25, "Delay can't be more than 25!");
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.curve = curve;
        this.sendDelay = sendDelay;
    }

    public ProjectileBase(int projectileId, int delay, int speed, int startHeight, int endHeight, int curve, int scale, int pitch, int sendDelay) {
        Preconditions.checkArgument(delay <= 25, "Delay can't be more than 25!");
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.curve = curve;
        this.sendDelay = sendDelay;
        this.scale = scale;
        this.pitch = pitch;
    }

    public Projectile createTargetedProjectile(Entity attacker, Entity defender) {
        return Projectile.createTargeted(attacker, defender, this);
    }

    public Projectile createTargetedProjectile(Entity attacker, Position position) {
        return Projectile.createTargeted(attacker, position, this);
    }

}
