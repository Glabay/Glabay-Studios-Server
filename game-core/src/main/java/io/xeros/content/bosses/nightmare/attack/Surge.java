package io.xeros.content.bosses.nightmare.attack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareAttack;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.Animation;
import io.xeros.model.Direction;
import io.xeros.model.ForceMovement;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class Surge extends NightmareAttack {

    private static final Position[][] STARTS = {
            {new Position(3863, 9949, 3), new Position(3878, 9949, 3), new Position(0, 1)}, // West
            {new Position(3878, 9949, 3), new Position(3863, 9949, 3), new Position(0, 1)}, // East
            {new Position(3870, 9958, 3), new Position(3870, 9941, 3), new Position(1, 0)}, // North
            {new Position(3870, 9941, 3), new Position(3870, 9958, 3), new Position(1, 0)}, // South
    };

    /**
     * The position that Nightmare moves to in order to dash
     */
    private Position dashPosition;
    /**
     * The position that Nightmare faces and moves to after completing a dash
     */
    private Position endPositioon;

    @Override
    public void tick(Nightmare nightmare) {
        switch (getTicks()) {
            case 0:
                nightmare.requestTransform(9427);
                nightmare.startAnimation(new Animation(8607, 5));
                break;
            case 2:
                this.choosePosition(nightmare);
                nightmare.teleport(dashPosition);
                nightmare.startAnimation(new Animation(8609));
                nightmare.facePosition(endPositioon);
                break;
            case 6:
                move(nightmare);
                break;
            case 7:
                nightmare.getInstance().getPlayers().stream().filter(inside(nightmare))
                        .forEach(player -> player.appendDamage(nightmare, 35 + Misc.random(30), Hitmark.HIT));
                break;
        }

        if (getTicks() >= 12) {
            stop();
        }
    }

    private Predicate<Player> inside(Nightmare nightmare) {
        List<Position> hitBoxes = Lists.newArrayList();
        Position delta = dashPosition.deltaAbsolute(endPositioon);
        Direction direction = Direction.fromDeltas(dashPosition, endPositioon);
        Position hitBox = dashPosition;
        int distance = Math.max(delta.getX(), delta.getY());
        for (int index = 0; index < distance; index++) {
            hitBoxes.addAll(Arrays.asList(nightmare.getTiles(hitBox, nightmare.getSize())));
            hitBox = hitBox.translate(direction.getDelta()[0], direction.getDelta()[1]);
        }
        List<Position> boxes = hitBoxes.stream().distinct().collect(Collectors.toList());
        return player -> boxes.stream().anyMatch(position -> player.distance(position) <= 1.5);
    }

    /**
     * Selects a random position for Nightmare to move to
     * @param nightmare The Nightmare boss
     */
    private void choosePosition(Nightmare nightmare) {
        // Select a random position index
        int randomPositionIndex = Misc.trueRand(STARTS.length);

        // Check if the random position is the same as the current position
        if (STARTS[randomPositionIndex][0].equals(nightmare.getPosition())) {
            choosePosition(nightmare);
            return;
        }

        // Determine to new position to move Nightmare to
        dashPosition = nightmare.getInstance().resolve(STARTS[randomPositionIndex][0]);
        endPositioon = nightmare.getInstance().resolve(STARTS[randomPositionIndex][1]);

        // Randomize the new position and face position
        randomizePositions(randomPositionIndex);
    }

    /**
     * Randomizes the new positions offsets
     * @param randomPositionIndex The random position index selected
     */
    private void randomizePositions(int randomPositionIndex) {
        // Create backup positions in case the new tile is not clipped
        Position backupDashPosition = dashPosition.deepCopy();
        Position backupEndPosition = endPositioon.deepCopy();

        // Roll to determine offsets
        int roll = Misc.random(3);

        // If roll is 1 or 2 add offsets
        if (roll == 1 || roll == 2) {
            // Determine if the new position is a vertical position (N, S)
            boolean isVerticalPosition = STARTS[randomPositionIndex][2].getX() > 0;

            int offset = (roll == 1 ? 1 : -1) * 3;
            backupDashPosition = dashPosition.translate(isVerticalPosition ? offset : 0, !isVerticalPosition ? offset : 0);
            backupEndPosition = endPositioon.translate(isVerticalPosition ? offset : 0, !isVerticalPosition ? offset : 0);
        }
        // Determine if this tile is clipped or not and rerun if not
        boolean isBlocked =
                RegionProvider.getGlobal().isBlocked(backupEndPosition.getX(), backupEndPosition.getY(), backupEndPosition.getHeight())
                || RegionProvider.getGlobal().isBlocked(backupDashPosition.getX(), backupDashPosition.getY(), backupDashPosition.getHeight());
        if (isBlocked) {

            randomizePositions(randomPositionIndex);
        } else {
            // If the tile is not blocked set new positions
            dashPosition = backupDashPosition;
            endPositioon = backupEndPosition;
        }
    }

    private void move(Nightmare nightmare) {
        ForceMovement forceMovement = new ForceMovement(nightmare, 2, dashPosition, endPositioon, 15, 30);
        forceMovement.startForceMovement();
        nightmare.startAnimation(8597);
    }

}
