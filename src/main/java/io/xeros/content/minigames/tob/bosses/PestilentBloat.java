package io.xeros.content.minigames.tob.bosses;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobBoss;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.model.Animation;
import io.xeros.model.Direction;
import io.xeros.model.Graphic;
import io.xeros.model.definition.Npcs;
import io.xeros.model.definitions.AnimationLength;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class PestilentBloat extends TobBoss {

    private static final Graphic FLIES_GFX = new Graphic(1568);
    private static final Animation GO_TO_SLEEP_THEN_STOMP = new Animation(8082);
    private static final Animation DEATH = new Animation(8085);

    private static final Position MOVE_WEST_POSITION = new Position(3299, 4440, 0);
    private static final Position MOVE_SOUTH_POSITION = new Position(3299, 4451, 0);
    private static final Position MOVE_EAST_POSITION = new Position(3288, 4451, 0);
    private static final Position MOVE_NORTH_POSITION = new Position(3288, 4440, 0);

    private enum ActionState {
        WALKING,
        SLEEPING
        ;

        ActionState() {
        }

        public int getStateTimer() {
            if (this == WALKING) {
                return 30;
            } else if (this == SLEEPING) {
                return AnimationLength.getFrameLength(GO_TO_SLEEP_THEN_STOMP.getId());
            }

            throw new IllegalStateException("No length for " + this);
        }
    }

    private enum MovementState {
        SOUTH(MOVE_WEST_POSITION, Direction.SOUTH, MovementLos.SOUTH),
        WEST(MOVE_NORTH_POSITION, Direction.WEST, MovementLos.WEST),
        NORTH(MOVE_EAST_POSITION, Direction.NORTH, MovementLos.NORTH),
        EAST(MOVE_SOUTH_POSITION, Direction.EAST, MovementLos.EAST),
        ;

        private final Position end;
        private final Direction direction;
        private final MovementLos movementLos;

        MovementState(Position end, Direction direction, MovementLos movementLos) {
            this.end = end;
            this.direction = direction;
            this.movementLos = movementLos;
        }
    }

    private enum MovementLos {
        SOUTH(new Position(3299, 4440, 0), new Position(3303, 4455, 0)),
        WEST(new Position(3288, 4440, 0), new Position(3303, 4444, 0)),
        NORTH(new Position(3288, 4440, 0), new Position(3292, 4455, 0)),
        EAST(new Position(3288, 4451, 0), new Position(3303, 4455, 0)),
        ;

        private final List<Position> positions;

        MovementLos(Position start, Position end) {
            List<Position> positionList = Lists.newArrayList();
            for (int x = start.getX(); x <= end.getX(); x++) {
                for (int y = start.getY(); y <= end.getY(); y++) {
                    positionList.add(new Position(x, y));
                }
            }
            positions = Collections.unmodifiableList(positionList);
        }
    }

    private ActionState actionState = ActionState.WALKING;
    private MovementState movementState = MovementState.SOUTH;
    private int stateTimer = 0;

    public PestilentBloat(InstancedArea instancedArea) {
        super(Npcs.PESTILENT_BLOAT, new Position(3299, 4451, instancedArea.getHeight()), instancedArea);
    }

    @Override
    public void facePosition(int x, int y) {}

    @Override
    public void facePlayer(int player) {}

    @Override
    public void faceNPC(int index) { }

    @Override
    public int getDeathAnimation() {
        return DEATH.getId();
    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        return actionState == ActionState.SLEEPING;
    }

    @Override
    public void process() {
        if (getHealth().getCurrentHealth() == 0) {      // Process death
            super.process();
            return;
        }

        //fixFace();

        if (actionState == ActionState.WALKING) {
            handleMovement();
            processMovement();
            damagePlayersInLos();
            tickNextState();
        } else if (actionState == ActionState.SLEEPING) {
            tickNextState();
            if (stateTimer == actionState.getStateTimer() - 1) {
                stompPlayers();
            }
        }
    }

    private void tickNextState() {
        if (stateTimer++ >= actionState.getStateTimer()) {
            if (actionState == ActionState.WALKING) {
                actionState = ActionState.SLEEPING;
                startAnimation(GO_TO_SLEEP_THEN_STOMP);
            } else if (actionState == ActionState.SLEEPING) {
                actionState = ActionState.WALKING;
                startAnimation(Animation.RESET_ANIMATION);
            }

            stateTimer = 0;
        }
    }

    private void handleMovement() {
        if (getPosition().equals(movementState.end.withHeight(getInstance().getHeight()))) {
            setNextMovement();
        } else {
            Position nextPositionDelta = movementState.direction.getDeltaPosition();
            Position nextPositionAbsolute = new Position(getX() + nextPositionDelta.getX(), getY() + nextPositionDelta.getY());
            moveTowards(nextPositionAbsolute.getX(), nextPositionAbsolute.getY());
        }
    }

    private void setNextMovement() {
        movementState = MovementState.values()[(movementState.ordinal() + 1) % MovementState.values().length];
    }

    private void damagePlayersInLos() {
        List<Position> positions = movementState.movementLos.positions.stream().map(it -> it.withHeight(getInstance().getHeight())).collect(Collectors.toList());
        getInstance().getPlayers().forEach(plr -> {
            positions.stream().filter(pos -> plr.getPosition().equals(pos)).findFirst().ifPresent(pos -> {
                if (plr.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
                    return;
                plr.startGraphic(FLIES_GFX);
                plr.appendDamage(9 + Misc.random(11), Hitmark.HIT);
            });
        });
    }

    private void stompPlayers() {
        getInstance().getPlayers().forEach(plr -> {
            for (Position position : getTiles()) {
                if (plr.distance(position) <= 1.5) {
                    if (plr.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
                        return;
                    plr.appendDamage(30 + Misc.random(20), Hitmark.HIT);
                    return;
                }
            }
        });
    }

}
