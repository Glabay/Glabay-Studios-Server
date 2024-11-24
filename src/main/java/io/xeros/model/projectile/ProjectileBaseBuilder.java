package io.xeros.model.projectile;

public class ProjectileBaseBuilder {
    private int projectileId;
    private int delay;
    private int speed = 25;
    private int startHeight = 43;
    private int endHeight = 43;
    private int curve = 16;
    private int scale = 64;
    private int pitch = 16;
    private int sendDelay;

    public ProjectileBaseBuilder setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public ProjectileBaseBuilder setPitch(int p) {
        this.pitch = p;
        return this;
    }

    public ProjectileBaseBuilder setProjectileId(int projectileId) {
        this.projectileId = projectileId;
        return this;
    }

    public ProjectileBaseBuilder setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public ProjectileBaseBuilder setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public ProjectileBaseBuilder setStartHeight(int startHeight) {
        this.startHeight = startHeight;
        return this;
    }

    public ProjectileBaseBuilder setEndHeight(int endHeight) {
        this.endHeight = endHeight;
        return this;
    }

    public ProjectileBaseBuilder setCurve(int curve) {
        this.curve = curve;
        return this;
    }

    public ProjectileBaseBuilder setSendDelay(int sendDelay) {
        this.sendDelay = sendDelay;
        return this;
    }

    public ProjectileBase createProjectileBase() {
        return new ProjectileBase(projectileId, delay, speed, startHeight, endHeight, curve, sendDelay);
    }
}