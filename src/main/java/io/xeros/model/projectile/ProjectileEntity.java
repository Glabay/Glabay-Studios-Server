package io.xeros.model.projectile;

import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import lombok.Getter;
import lombok.val;

@Getter
public class ProjectileEntity {

    private final Position start, target, offset;
    private final int creatorSize, startDistanceOffset, lockon, projectileId, delay, speed, startHeight, endHeight, slope, stepMultiplier;
    public static final float TICK = 600F, CLIENT_CYCLE = 20F, CYCLES_PER_TICK = TICK / CLIENT_CYCLE;
    public ProjectileEntity(Position start, Position end, int lockon,
                            int projectileId, int speed, int delay, int startHeight, int endHeight,
                            int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this.start = start;
        this.target = end;
        int offX = (start.getY() - end.getY()) * -1;
        int offY = (start.getX() - end.getX()) * -1;
        this.offset = new Position(offX, offY, start.getHeight());
        this.creatorSize = creatorSize;
        this.startDistanceOffset = startDistanceOffset;
        this.lockon = lockon;
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.slope = curve;
        this.stepMultiplier = stepMultiplier;
    }

    public ProjectileEntity(Position source, Position victim, int lockon, int projectileId, int startDuration, int travelTime, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source, victim, lockon, projectileId, travelTime, startDuration, startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    public ProjectileEntity(Entity source, Entity victim, int projectileId, int startDuration, int travelTime, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier, boolean lockToTarget) {
        this(source.getCenterPosition(), victim.getCenterPosition(), lockToTarget ? victim.getProjectileLockonIndex() : -1, projectileId, travelTime, startDuration, startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    public ProjectileEntity(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int creatorSize, int stepMultiplier, boolean lockToTarget) {
        this(source.getCenterPosition(), victim.getCenterPosition(), lockToTarget ? victim.getProjectileLockonIndex() : -1, projectileId, speed, delay, startHeight, endHeight, 16, creatorSize, 64, stepMultiplier);
    }

    public ProjectileEntity(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this(source.getCenterPosition(), victim.getCenterPosition(), victim.getProjectileLockonIndex(), projectileId, speed, delay, startHeight, endHeight, curve, creatorSize, startDistanceOffset, stepMultiplier);
    }

    public void sendProjectile() {
        for (var p : PlayerHandler.players) {
            if (p == null) continue;
            if (!p.getPosition().isViewableFrom(start)) continue;
            if (p.getPosition().getHeight() != start.getHeight()) continue;
            p.getPA().sendProjectile(start.getX(), start.getY(), offset.getX(), offset.getY(), 0, speed, projectileId, startHeight, endHeight, lockon, delay, slope, creatorSize, startDistanceOffset);
        }
    }

    public int send(Position src, Position dest) {
        return send(src.getX(), src.getY(), dest.getX(), dest.getY(), src.getHeight());
    }

    public int send(int startX, int startY, int destX, int destY, int z) {
        ProjectileEntity projectileEntity = new ProjectileEntity(new Position(startX, startY, z), new Position(destX, destY, z), this.lockon, this.projectileId, this.speed, this.delay, this.startHeight, this.endHeight, this.slope, this.creatorSize, this.startDistanceOffset, this.stepMultiplier);
        projectileEntity.sendProjectile();
        return projectileEntity.getTime(projectileEntity.getStart(), projectileEntity.getTarget());
    }

    public int getTime(final Position from, final Position to) {
        float duration = getProjectileDuration(from, to) / CYCLES_PER_TICK;
        if (duration - (int) duration > 0.5F) duration++;
        return Math.max(0, (int) duration - 1);
    }

    public int getProjectileDuration(final Position from, final Position to) {
        val flightDuration = Math.max(Math.abs(from.getX() - to.getX()), Math.abs(from.getY() - to.getY()));
        return delay + speed + flightDuration;
    }

    @Override
    public String toString() {
        return "Projectile{" +
                "start=" + start +
                ", target=" + target +
                ", offset=" + offset +
                ", creatorSize=" + creatorSize +
                ", startDistanceOffset=" + startDistanceOffset +
                ", lockon=" + lockon +
                ", projectileId=" + projectileId +
                ", delay=" + delay +
                ", speed=" + speed +
                ", startHeight=" + startHeight +
                ", endHeight=" + endHeight +
                ", slope=" + slope +
                ", stepMultiplier=" + stepMultiplier +
                '}';
    }
}
