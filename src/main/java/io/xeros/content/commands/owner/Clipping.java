package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.Direction;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.grounditem.GroundItem;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;

import java.lang.reflect.Field;
import java.util.*;

import static io.xeros.content.commands.owner.Clipping.ClippingUtils.*;
import static java.lang.Math.abs;

/**
 * @author Arthur Behesnilian 7:14 PM
 */
public class Clipping extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        List<GroundItem> items = new ArrayList<>();
        int clipping = 0;
        Player owner = PlayerHandler.players[player.getIndex()];
        var area = player.getPosition().area(8);
        int maxX = area.getMaximumX();
        int maxY = area.getMaximumY();
        int minX = area.getMinimumX();
        int minY = area.getMinimumY();
        GroundItem groundItem;
        for (int index = 0; index < Direction.values().length - 1; index++) {
            System.out.println("dir: " + index + " " + Direction.get(index));
        }
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                var provider = RegionProvider.getGlobal().get(x, y);
                var clip = provider.getClip(x, y, player.getHeight());
                for (var f : ClippingUtils.projectile_flags) {
                    if (Clipping.LineValidator.isFlagged(x, y, player.getHeight(), f)) {
                        groundItem = new GroundItem(new GameItem(4151,1), Optional.of(player),new Position(x, y, player.getHeight()));
                        player.getItems().createGroundItem(groundItem);
                        items.add(groundItem);
                        player.sendMessage("<shad=0>@blu@[ProjectileClipAT:</shad> <shad=0>@bla@Tile[X:" + x + " Y:" + y + "]</shad> <shad=0>@bla@[FLAG:" + clip + "]</shad>");

                    }
                }
                if (Clipping.LineValidator.isFlagged(x, y, player.getHeight(), clip)) {
                    groundItem = new GroundItem(new GameItem(995,1), Optional.of(player),new Position(x, y, player.getHeight()));
                    player.getItems().createGroundItem(groundItem);
                    items.add(groundItem);
                    player.sendMessage("<shad=0>@red@[TileClipAT:</shad> <shad=0>@bla@Tile[X:" + x + " Y:" + y + "]</shad> <shad=0>@bla@[FLAG:" + clip + "]</shad>");
                }
            }
        }
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for (var i : items) {
                    player.getItems().removeGroundItem(i);
                }
                container.stop();
            }
        }, 15);
        player.sendMessage("Clipping for tile: " + player.getPosition() + "=" + clipping);
    }

    static class ClippingUtils {
        public static final int PROJECTILE_NORTH_WEST_BLOCKED = 0x200;
        public static final int PROJECTILE_NORTH_BLOCKED = 0x400;
        public static final int PROJECTILE_NORTH_EAST_BLOCKED = 0x800;
        public static final int PROJECTILE_EAST_BLOCKED = 0x1000;
        public static final int PROJECTILE_SOUTH_EAST_BLOCKED = 0x2000;
        public static final int PROJECTILE_SOUTH_BLOCKED = 0x4000;
        public static final int PROJECTILE_SOUTH_WEST_BLOCKED = 0x8000;
        public static final int PROJECTILE_WEST_BLOCKED = 0x10000;
        public static final int PROJECTILE_TILE_BLOCKED = 0x20000;
        public static final int UNKNOWN = 0x80000;
        public static final int BLOCKED_TILE = 0x200000;
        public static final int UNLOADED_TILE = 0x1000000;
        public static final int OCEAN_TILE = 2097152;
        public static final int[] projectile_flags = new int[]{PROJECTILE_NORTH_WEST_BLOCKED, PROJECTILE_NORTH_BLOCKED, PROJECTILE_NORTH_EAST_BLOCKED, PROJECTILE_EAST_BLOCKED, PROJECTILE_SOUTH_EAST_BLOCKED, PROJECTILE_SOUTH_BLOCKED, PROJECTILE_SOUTH_WEST_BLOCKED, PROJECTILE_WEST_BLOCKED, PROJECTILE_TILE_BLOCKED};

    }

    public static class LineValidator {

        public static boolean hasLineOfSight(
                int level,
                int srcX,
                int srcY,
                int destX,
                int destY,
                int srcSize,
                int destWidth,
                int destHeight
        ) {
            return rayCast(
                    level,
                    srcX,
                    srcY,
                    destX,
                    destY,
                    srcSize,
                    destWidth,
                    destHeight,
                    SIGHT_BLOCKED_WEST,
                    SIGHT_BLOCKED_EAST,
                    SIGHT_BLOCKED_SOUTH,
                    SIGHT_BLOCKED_NORTH,
                    true
            );
        }

        public static boolean hasLineOfSight(int level, int srcX, int srcY, int destX, int destY) {
            return hasLineOfSight(level, srcX, srcY, destX, destY, 1, 0, 0);
        }

        public static boolean hasLineOfWalk(
                int level,
                int srcX,
                int srcY,
                int destX,
                int destY,
                int srcSize,
                int destWidth,
                int destHeight
        ) {
            return rayCast(
                    level,
                    srcX,
                    srcY,
                    destX,
                    destY,
                    srcSize,
                    destWidth,
                    destHeight,
                    WALK_BLOCKED_WEST,
                    WALK_BLOCKED_EAST,
                    WALK_BLOCKED_SOUTH,
                    WALK_BLOCKED_NORTH,
                    false
            );
        }

        public static boolean hasLineOfWalk(
                int level,
                int srcX,
                int srcY,
                int destX,
                int destY
        ) {
            return hasLineOfWalk(level, srcX, srcY, destX, destY, 1, 0, 0);
        }

        private static boolean rayCast(
                int level,
                int srcX,
                int srcY,
                int destX,
                int destY,
                int srcSize,
                int destWidth,
                int destHeight,
                int flagWest,
                int flagEast,
                int flagSouth,
                int flagNorth,
                boolean los
        ) {
            int startX = coordinate(srcX, destX, srcSize);
            int startY = coordinate(srcY, destY, srcSize);

            if (los && isFlagged( startX, startY, level, Flags.OBJECT)) {
                return false;
            }

            int endX = coordinate(destX, srcX, destWidth);
            int endY = coordinate(destY, srcY, destHeight);

            if (startX == endX && startY == endY) {
                return true;
            }

            int deltaX = endX - startX;
            int deltaY = endY - startY;

            boolean travelEast = deltaX >= 0;
            boolean travelNorth = deltaY >= 0;

            int xFlags = travelEast ? flagWest : flagEast;
            int yFlags = travelNorth ? flagSouth : flagNorth;

            if (abs(deltaX) > abs(deltaY)) {
                int offsetX = travelEast ? 1 : -1;
                int offsetY = travelNorth ? 0 : -1;

                int scaledY = scaleUp(startY) + HALF_TILE + offsetY;
                int tangent = scaleUp(deltaY) / abs(deltaX);

                int currX = startX;
                while (currX != endX) {
                    currX += offsetX;
                    int currY = scaleDown(scaledY);

                    if (los && currX == endX && currY == endY) xFlags = xFlags & ~PROJECTILE_TILE_BLOCKED;
                    if (isFlagged( currX, currY, level, xFlags)) {
                        return false;
                    }

                    scaledY += tangent;

                    int nextY = scaleDown(scaledY);
                    if (los && currX == endX && nextY == endY) yFlags = yFlags & ~PROJECTILE_TILE_BLOCKED;
                    if (nextY != currY && isFlagged(currX, currY, level, yFlags)) {
                        return false;
                    }
                }
            } else {
                int offsetX = travelEast ? 0 : -1;
                int offsetY = travelNorth ? 1 : -1;

                int scaledX = scaleUp(startX) + HALF_TILE + offsetX;
                int tangent = scaleUp(deltaX) / abs(deltaY);

                int currY = startY;
                while (currY != endY) {
                    currY += offsetY;
                    int currX = scaleDown(scaledX);
                    if (los && currX == endX && currY == endY) yFlags = yFlags & ~PROJECTILE_TILE_BLOCKED;
                    if (isFlagged( currX, currY, level, yFlags)) {
                        return false;
                    }

                    scaledX += tangent;

                    int nextX = scaleDown(scaledX);
                    if (los && nextX == endX && currY == endY) xFlags = xFlags & ~PROJECTILE_TILE_BLOCKED;
                    if (nextX != currX && isFlagged( nextX, currY, level, xFlags)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private static int coordinate(int a, int b, int size) {
            if (a >= b) {
                return a;
            } else if (a + size - 1 <= b) {
                return a + size - 1;
            } else {
                return b;
            }
        }

        public static boolean isFlagged(int x, int y, int level, int testFlags) {
            return (RegionProvider.getGlobal().get(x, y).getClip(x,y,level) & testFlags) != 0;
        }

        private static final int SIGHT_BLOCKED_NORTH = PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED;
        private static final int SIGHT_BLOCKED_EAST = PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED;
        private static final int SIGHT_BLOCKED_SOUTH = PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED;
        private static final int SIGHT_BLOCKED_WEST = PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED;

        public static final int WALK_BLOCKED_NORTH =
                Flags.WALL_NORTH | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
        public static final int WALK_BLOCKED_EAST =
                Flags.WALL_EAST | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
        public static final int WALK_BLOCKED_SOUTH =
                Flags.WALL_SOUTH | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
        public static final int WALK_BLOCKED_WEST =
                Flags.WALL_WEST | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
        private static final int SCALE = 16;
        private static final int HALF_TILE = scaleUp(1) / 2;

        private static int scaleUp(int tiles) {
            return tiles << SCALE;
        }

        private static int scaleDown(int tiles) {
            return tiles >>> SCALE;
        }
    }

    public class Flags {

        /**
         * The flag which denotes a normal tile, no flag.
         */
        public static final int NONE = 0x0;

        /**
         * The flag which denotes a bridge tile.
         */
        public static final int FLOOR_DECORATION = 0x40000;

        /**
         * The flag which denotes a blocked tile.
         */
        public static final int BLOCKED = 0x200000;

        /**
         * The flag for a north facing wall.
         */
        public static final int WALL_NORTH = 0x2;

        /**
         * The flag for a south facing wall.
         */
        public static final int WALL_SOUTH = 0x20;

        /**
         * Object
         */
        public static final int OBJECT = 0x100;

        /**
         * The flag for a east facing wall.
         */
        public static final int WALL_EAST = 0x8;

        /**
         * The flag for a west facing wall.
         */
        public static final int WALL_WEST = 0x80;

        /**
         * The flag for a north east facing wall.
         */
        public static final int WALL_NORTH_EAST = 0x4;

        /**
         * The flag for a north west facing wall.
         */
        public static final int WALL_NORTH_WEST = 0x1;

        /**
         * The flag for a south east facing wall.
         */
        public static final int WALL_SOUTH_EAST = 0x10;

        /**
         * The flag for a south west facing wall.
         */
        public static final int WALL_SOUTH_WEST = 0x40;

        /**
         * The flag for an object occupant, which is impenetrable.
         */
        public static final int FLOOR = 0x20000;

        /**
         * The flag for a impenetrable north facing wall.
         */
        public static final int IMPENETRABLE_WALL_NORTH = 0x400;

        /**
         * The flag for a impenetrable south facing wall.
         */
        public static final int IMPENETRABLE_WALL_SOUTH = 0x4000;

        /**
         * The flag for a impenetrable east facing wall.
         */
        public static final int IMPENETRABLE_WALL_EAST = 0x1000;

        /**
         * The flag for a impenetrable west facing wall.
         */
        public static final int IMPENETRABLE_WALL_WEST = 0x10000;

        /**
         * The flag for a impenetrable north east facing wall.
         */
        public static final int IMPENETRABLE_WALL_NORTH_EAST = 0x800;

        /**
         * The flag for a impenetrable north west facing wall.
         */
        public static final int IMPENETRABLE_WALL_NORTH_WEST = 0x200;

        /**
         * The flag for a impenetrable south east facing wall.
         */
        public static final int IMPENETRABLE_WALL_SOUTH_EAST = 0x2000;

        /**
         * The flag for a impenetrable south west facing wall.
         */
        public static final int IMPENETRABLE_WALL_SOUTH_WEST = 0x8000;

        public Map<String, Integer> getMASKS() {
            boolean empty = MASKS.isEmpty();
            for (Field declaredField : Flags.class.getDeclaredFields()) {
                if (declaredField.getType() != Integer.class && declaredField.getType() != int.class)
                    continue;
                try {
                    MASKS.put(declaredField.getName(), declaredField.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace(System.err);
                }
            }
            if (empty)
                System.out.println("bruh "+ Arrays.toString(MASKS.entrySet().stream().map(e->e.getKey()+":"+e.getValue()+",").toArray()));
            return MASKS;
        }

        private final Map<String,Integer> MASKS = new HashMap<>();

    }
}
