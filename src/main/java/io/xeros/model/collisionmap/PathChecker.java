package io.xeros.model.collisionmap;

import io.xeros.content.bosses.Skotizo;
import io.xeros.model.Direction;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCClipping;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class PathChecker {

    @Deprecated
    public static boolean isProjectilePathClear(Entity entity, Entity other,
                                                final int x0, final int y0,
                                                final int z, final int x1, final int y1) {

        if (other != null && other instanceof NPC && entity instanceof Player) {
            Player c = (Player) entity;
            NPC theNPC = (NPC) other;
            if (Boundary.isIn(c, Boundary.PEST_CONTROL_AREA) || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_NORTH
                    || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_SOUTH
                    || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_WEST
                    || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_EAST
                    || theNPC.getNpcId() == 7559
                    || theNPC.getNpcId() == 7560
                    || theNPC.getNpcId() == 7706 /* Inferno final boss */) {
                return true;
            }
        }


        int deltaX = x1 - x0;
        int deltaY = y1 - y0;

        double error = 0;
        final double deltaError = Math.abs(
                (deltaY) / (deltaX == 0
                        ? ((double) deltaY)
                        : ((double) deltaX)));

        int x = x0;
        int y = y0;

        int pX = x;
        int pY = y;

        boolean incrX = x0 < x1;
        boolean incrY = y0 < y1;

        while (true) {
            if (x != x1) {
                x += (incrX ? 1 : -1);
            }

            if (y != y1) {
                error += deltaError;

                if (error >= 0.5) {
                    y += (incrY ? 1 : -1);
                    error -= 1;
                }
            }

           /* if (!raycast(entity, entity.getCenterPosition(), new Position(pX, pY, entity.getPosition().getHeight()), true)) {
                return false;
            }*/

            if (!shootable(entity, x, y, z, pX, pY)) {
                return false;
            }

            if (incrX && incrY
                    && x >= x1 && y >= y1) {
                break;
            } else if (!incrX && !incrY
                    && x <= x1 && y <= y1) {
                break;
            } else if (!incrX && incrY
                    && x <= x1 && y >= y1) {
                break;
            } else if (incrX && !incrY
                    && x >= x1 && y <= y1) {
                break;
            }

            pX = x;
            pY = y;
        }

        return true;
    }

    public static boolean isMeleePathClear(Entity entity, Entity other) {
        return isMeleePathClear(entity, entity.getX(), entity.getY(), entity.getHeight(), other.getX(), other.getY());
    }

    public static boolean isMeleePathClear(Entity entity, final int x0, final int y0,
                                           final int z, final int x1, final int y1) {
        int deltaX = x1 - x0;
        int deltaY = y1 - y0;

        double error = 0;
        final double deltaError = Math.abs(
                (deltaY) / (deltaX == 0
                        ? ((double) deltaY)
                        : ((double) deltaX)));

        int x = x0;
        int y = y0;

        int pX = x;
        int pY = y;

        boolean incrX = x0 < x1;
        boolean incrY = y0 < y1;

        while (true) {
            if (x != x1) {
                x += (incrX ? 1 : -1);
            }

            if (y != y1) {
                error += deltaError;

                if (error >= 0.5) {
                    y += (incrY ? 1 : -1);
                    error -= 1;
                }
            }

            if (!canAttackOver(entity, x, y, z, pX, pY)) {
                return false;
            }

            if (incrX && incrY
                    && x >= x1 && y >= y1) {
                break;
            } else if (!incrX && !incrY
                    && x <= x1 && y <= y1) {
                break;
            } else if (!incrX && incrY
                    && x <= x1 && y >= y1) {
                break;
            } else if (incrX && !incrY
                    && x >= x1 && y <= y1) {
                break;
            }

            pX = x;
            pY = y;
        }

        return true;
    }

    public static boolean canAttackOver(Entity entity, int x, int y, int z, int pX, int pY) {
        if (x == pX && y == pY) {
            return true;
        }

        int dir = NPCClipping.getDirection(x, y, pX, pY);
        int dir2 = NPCClipping.getDirection(pX, pY, x, y);

        if (dir == -1 || dir2 == -1) {
            System.out.println("NEGATIVE DIRECTION MELEE CLIP CHECK ERROR");
            return false;
        }

        return entity.getRegionProvider().canMove(x, y, z, dir, entity.isNPC())
                && entity.getRegionProvider().canMove(pX, pY, z, dir2, entity.isNPC());
    }

    @Deprecated
    public static boolean shootable(Entity entity, int x, int y, int z, int pX, int pY) {
        if (x == pX && y == pY) {
            return true;
        }

        int direction = NPCClipping.getDirection(x, y, pX, pY);
        int reverseDirection = NPCClipping.getDirection(pX, pY, x, y);

        if (direction == -1 || reverseDirection == -1) {
            System.out.println("NEGATIVE DIRECTION PROJECTILE ERROR");
            return false;
        }

        if (checkDirectionWithSize(entity, x, y, z, direction, false) && checkDirectionWithSize(entity, pX, pY, z, reverseDirection,
                false)) {
            return true;
        }

        return entity.getRegionProvider().canShoot(x, y, z, direction)
                && entity.getRegionProvider().canShoot(pX, pY, z, reverseDirection);
    }

    public static boolean checkDirectionWithSize(Entity entity, int x, int y, int z, int direction,
                                                 boolean isProjectile) {
        int size = entity.getEntitySize();
        Direction dir = Direction.get(direction);
        switch (dir) {
            case EAST:
                for (int yOffset = entity.getY(); yOffset < entity.getY() + size; yOffset++) {
                        if (!entity.getRegionProvider().canMove(x + size, yOffset, z, direction, entity.isNPC()))
                            return false;
                }
                break;
            case WEST:
                for (int yOffset = entity.getY(); yOffset < entity.getY() + size; yOffset++) {
                        if (!entity.getRegionProvider().canMove(x, yOffset, z, direction, entity.isNPC()))
                            return false;
                }
                break;
            case NORTH:
                for (int xOffset = entity.getX(); xOffset < entity.getX() + size; xOffset++) {
                        if (!entity.getRegionProvider().canMove(xOffset, entity.getY() + size, z, direction,
                                entity.isNPC()))
                            return false;
                }
                break;
            case SOUTH:
                    for (int xOffset = entity.getX(); xOffset < entity.getX() + size; xOffset++) {
                        if (!entity.getRegionProvider().canMove(xOffset, entity.getY(), z, direction, entity.isNPC()))
                            return false;
                    }
                break;
            case NORTH_EAST:
                for (int xOffset = entity.getX(); xOffset < entity.getX() + size; xOffset++) {
                        if (!entity.getRegionProvider().canMove(xOffset, entity.getY() + size, z, direction,
                                entity.isNPC()))
                            return false;
                }
                for (int yOffset = entity.getY(); yOffset < entity.getY() + size; yOffset++) {
                        if (!entity.getRegionProvider().canMove(x + size, yOffset, z, direction, entity.isNPC()))
                            return false;
                }
                break;
            case SOUTH_EAST:
                for (int xOffset = entity.getX(); xOffset < entity.getX() + size; xOffset++) {
                        if (!entity.getRegionProvider().canMove(xOffset, entity.getY(), z, direction, entity.isNPC()))
                            return false;
                }
                for (int yOffset = entity.getY(); yOffset < entity.getY() + size; yOffset++) {
                        if (!entity.getRegionProvider().canMove(x + size, yOffset, z, direction, entity.isNPC())) {
                            return false;
                        }
                }
                break;
            case NORTH_WEST:
                for (int xOffset = entity.getX(); xOffset < entity.getX() + size; xOffset++) {
                        if (!entity.getRegionProvider().canMove(xOffset, entity.getY() + size, z, direction,
                                entity.isNPC()))
                            return false;
                }
                for (int yOffset = entity.getY(); yOffset < entity.getY() + size; yOffset++) {
                        if (!entity.getRegionProvider().canMove(x, yOffset, z, direction, entity.isNPC())) {
                            return false;
                        }
                }
                break;
            case SOUTH_WEST:
                for (int xOffset = entity.getX(); xOffset < entity.getX() + size; xOffset++) {
                    if (!entity.getRegionProvider().canMove(xOffset, entity.getY(), z, direction, entity.isNPC())) {
                        return false;
                    }
                }
                for (int yOffset = entity.getY(); yOffset < entity.getY() + size; yOffset++) {
                    if (!entity.getRegionProvider().canMove(x, yOffset, z, direction, entity.isNPC())) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Casts a line using Bresenham's Line Algorithm with point A [start] and
     * point B [target] being its two points and makes sure that there's no
     * collision flag that can block movement from and to both points.
     *
     * @param source The starting position of the line
     * @param target The ending position of the line
     * @param projectile Determines if the path should be checked for projectiles
     * Projectiles have a higher tolerance for certain objects when the object's
     * metadata explicitly allows them to.
     */
    public static boolean raycast(Position source, Position target, boolean projectile) {
        return raycast(null, source, target, projectile);
    }

    public static boolean raycast(Entity entity, Entity other, boolean projectile) {
        if (other != null && other instanceof NPC && entity instanceof Player) {
            NPC theNPC = (NPC) other;
            Player c = (Player) entity;
            if (Boundary.isIn(c, Boundary.PEST_CONTROL_AREA) || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_NORTH
                    || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_SOUTH
                    || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_WEST
                    || theNPC.getNpcId() == Skotizo.AWAKENED_ALTAR_EAST
                    || theNPC.getNpcId() == 7559
                    || theNPC.getNpcId() == 7560
                    || theNPC.getNpcId() == 7706 /* Inferno final boss */) {
                return true;
            }
        }
        return raycast(entity, entity.getCenterPosition(), other.getCenterPosition(), projectile);
    }

    /**
     * Casts a line using Bresenham's Line Algorithm with point A [start] and
     * point B [target] being its two points and makes sure that there's no
     * collision flag that can block movement from and to both points.
     *
     * @param entity The entity whose RegionProvider we use
     *               If this is set to null the global provider will be used
     * @param source The starting position of the line
     * @param target The ending position of the line
     * @param projectile Determines if the path should be checked for projectiles
     * Projectiles have a higher tolerance for certain objects when the object's
     * metadata explicitly allows them to.
     */
    public static boolean raycast(Entity entity, Position source, Position target, boolean projectile) {
        int x0 = source.getX();
        int y0 = source.getY();

        int x1 = target.getX();
        int y1 = target.getY();

        int height = source.getHeight();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        boolean incrX = x0 < x1;
        boolean incrY = y0 < y1;

        int err = dx - dy;
        int err2;

        Position prev = new Position(x0, y0, height);

        while (x0 != x1 || y0 != y1) {
            err2 = err << 1;

            if (err2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (err2 < dx) {
                err += dx;
                y0 += sy;
            }

            Position next = new Position(x0, y0, height);

            int direction = NPCClipping.getDirection(prev.getX(), prev.getY(), next.getX(), next.getY());
            int reverseDirection = NPCClipping.getDirection(next.getX(), next.getY(), prev.getX(), prev.getY());

            RegionProvider regionProvider = entity == null ? RegionProvider.getGlobal(): entity.getRegionProvider();

            if (projectile) {
                if (!regionProvider.canShoot(prev.getX(), prev.getY(), height, direction)
                    || !regionProvider.canShoot(next.getX(), next.getY(), height, reverseDirection)) {
                    return false;
                }
            } else {
                if (!regionProvider.canMove(prev.getX(), prev.getY(), height, direction, entity.isNPC())
                    || !regionProvider.canMove(next.getX(), next.getY(), height, reverseDirection) ) {
                    return false;
                }
            }

            if (incrX && incrY
                    && x0 >= x1 && y0 >= y1) {
                break;
            } else if (!incrX && !incrY
                    && x0 <= x1 && y0 <= y1) {
                break;
            } else if (!incrX && incrY
                    && x0 <= x1 && y0 >= y1) {
                break;
            } else if (incrX && !incrY
                    && x0 >= x1 && y0 <= y1) {
                break;
            }

            prev = next;
        }
        return true;
    }


}
