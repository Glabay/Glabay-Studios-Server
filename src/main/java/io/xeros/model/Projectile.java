package io.xeros.model;

import io.xeros.Server;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Position;
import lombok.Getter;

@Getter
public class Projectile extends ProjectileBase {


    public static int getLockon(Entity entity) {
        return entity.isPlayer() ? -entity.getIndex() - 1 : entity.getIndex() + 1;
    }

    public static Projectile createTargeted(Entity source, Entity victim, ProjectileBase projectileBase) {
        return new Projectile(source.getCenterPosition(), victim.getCenterPosition(), victim.isPlayer() ? -victim.getIndex() - 1 : victim.getIndex() + 1, projectileBase);
    }

    public static Projectile createTargeted(Position source, int sourceTileSize, Entity victim, ProjectileBase projectileBase) {
        return new Projectile(source.getCenterPosition(sourceTileSize), victim.getCenterPosition(), victim.isPlayer() ? -victim.getIndex() - 1 : victim.getIndex() + 1, projectileBase);
    }

    public static Projectile createTargeted(Entity source, Position position, ProjectileBase projectileBase) {
        return new Projectile(source.getCenterPosition(), position, 0, projectileBase);
    }

    /**
     * The starting position of the projectile.
     */
    private final Position start;

    /**
     * The offset position of the projectile.
     */
    private final Position offset;

    /**
     * The lock on value of the projectile.
     */
    private final int lockon;

    public Projectile(Position start, Position end, int lockon, ProjectileBase projectileBase) {
        super(projectileBase.getProjectileId(),  projectileBase.getDelay(), projectileBase.getSpeed(),
                projectileBase.getStartHeight(), projectileBase.getEndHeight(), projectileBase.getCurve(),
                projectileBase.getScale(), projectileBase.getPitch(), projectileBase.getSendDelay());
        this.start = start;
        int offsetX = ((start.getX() - end.getX()) * -1);
        int offsetY = ((start.getY() - end.getY()) * -1);
        this.offset = new Position(offsetX, offsetY);
        this.lockon = lockon;
    }

    /**
     * Send the projectile to the players
     */
    public void send(InstancedArea instancedArea) {
        if (getSendDelay() > 0) {
            final Projectile projectile = this;
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    Server.playerHandler.sendProjectile(projectile, instancedArea);
                    container.stop();
                }
            }, getSendDelay());
        } else {
            Server.playerHandler.sendProjectile(this, instancedArea);
        }
    }

}
