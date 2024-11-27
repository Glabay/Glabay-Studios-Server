package io.xeros.model;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import lombok.Getter;

/**
 * Force movement wrapper. Only supports npcs cause I don't have time to rewrite a bunch of shit.
 */
public class ForceMovement extends CycleEvent {

    private final NPC npc;
    private final int ticksToFinish;
    private final Position start;
    private final Position end;
    @Getter
    private final int moveCycleStart;
    @Getter
    private final int moveCycleEnd;
    @Getter
    private final int moveDirection;
    private final List<Consumer<NPC>> everyTick = Lists.newArrayList();
    private CycleEventContainer container;

    public ForceMovement(NPC npc, int ticksToFinish, Position start, Position end, int moveCycleStart, int moveCycleEnd) {
        this.npc = npc;
        this.ticksToFinish = ticksToFinish;
        this.start = start;
        this.end = end;
        this.moveCycleStart = moveCycleStart;
        this.moveCycleEnd = moveCycleEnd;
        Direction direction = Direction.fromDeltas(start, end);
        switch (direction) {
            case NORTH:
                this.moveDirection = 0;
                break;
            case EAST:
                this.moveDirection = 1;
                break;
            case SOUTH:
                this.moveDirection = 2;
                break;
            case WEST:
                this.moveDirection = 3;
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid force movement direction=%s", direction));
        }
    }

    @Override
    public void execute(CycleEventContainer container) {
        everyTick.forEach(consumer -> consumer.accept(npc));
        if (container.getTotalTicks() == ticksToFinish) {
            npc.teleport(end);
            npc.facePosition(npc.getPosition().translate(start.delta(end).getX(), start.delta(end).getY()));
            container.stop();
            npc.getAttributes().setBoolean("force_movement", false);
        }
    }

    public void startForceMovement() {
        Preconditions.checkState(!npc.getAttributes().getBoolean("force_movement"), "Force movement already active!");
        npc.getAttributes().setBoolean("force_movement", true);
        CycleEventHandler.getSingleton().addEvent(container = new CycleEventContainer(-1, npc, this, ticksToFinish));
        npc.sendForceMovement(this);
        npc.facePosition(end);
    }

    public void addConsumer(Consumer<NPC> consumer) {
        everyTick.add(consumer);
    }

    public Position getStart(Player player) {
        int dx = start.getX() - (player.mapRegionX * 8);
        int dy = start.getY() - (player.mapRegionY * 8);
        return new Position(dx, dy, start.getHeight());
    }

    public Position getEnd(Player player) {
        int dx = end.getX() - (player.mapRegionX * 8);
        int dy = end.getY() - (player.mapRegionY * 8);
        return new Position(dx, dy, end.getHeight());
    }

    public boolean isActive() {
        return container.isRunning();
    }

}
