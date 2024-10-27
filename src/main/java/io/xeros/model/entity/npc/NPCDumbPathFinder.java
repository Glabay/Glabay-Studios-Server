package io.xeros.model.entity.npc;

import com.google.common.base.Preconditions;
import io.xeros.model.Direction;
import io.xeros.model.Npcs;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.collisionmap.Tile;
import io.xeros.model.collisionmap.TileControl;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.Arrays;

public class NPCDumbPathFinder {

private static final int NORTH = 0, EAST = 1,  SOUTH = 2, WEST = 3;

	public static void generateMovement(NPC npc) {
		if (npc.revokeWalkingPrivilege)
			return;
		Position loc = npc.getPosition();
		int dir = -1;
		if (!npc.getRegionProvider().blockedNorth(loc.getX(), loc.getY(), loc.getHeight(), true)) {
			dir = 0;
		} else if (!npc.getRegionProvider().blockedEast(loc.getX(), loc.getY(), loc.getHeight(), true)) {
			dir = 4;
		} else if (!npc.getRegionProvider().blockedSouth(loc.getX(), loc.getY(), loc.getHeight(), true)) {
			dir = 8;
		} else if (!npc.getRegionProvider().blockedWest(loc.getX(), loc.getY(), loc.getHeight(), true)) {
			dir = 12;
		}
		int random = Misc.random(3);
		boolean found = false;
		if (random == 0) {
			if (!npc.getRegionProvider().blockedNorth(loc.getX(), loc.getY(), loc.getHeight(), true)) {
				walkTowards(npc, loc.getX(), loc.getY() + 1);
				found = true;
			}
		} else if (random == 1) {
			if (!npc.getRegionProvider().blockedEast(loc.getX(), loc.getY(), loc.getHeight(), true)) {
				walkTowards(npc, loc.getX() + 1, loc.getY());
				found = true;
			}
		} else if (random == 2) {
			if (!npc.getRegionProvider().blockedSouth(loc.getX(), loc.getY(), loc.getHeight(), true)) {
				walkTowards(npc, loc.getX(), loc.getY() - 1);
				found = true;
			}
		} else if (random == 3) {
			if (!npc.getRegionProvider().blockedWest(loc.getX(), loc.getY(), loc.getHeight(), true)) {
				walkTowards(npc, loc.getX() - 1, loc.getY());
				found = true;
			}
		}
		if (!found) {
			if (dir == 0) {
				walkTowards(npc, loc.getX(), loc.getY() + 1);
			} else if (dir == 4) {
				walkTowards(npc, loc.getX() + 1, loc.getY());
			} else if (dir == 8) {
				walkTowards(npc, loc.getX(), loc.getY() - 1);
			} else if (dir == 12) {
				walkTowards(npc, loc.getX() - 1, loc.getY());
			}
		}
	}

	public static void follow(NPC npc, Entity following) {
		follow(npc, following, false);
	}

	public static void follow(NPC npc, Entity following, boolean runTick) {
		if (npc.revokeWalkingPrivilege)
			return;

		if (npc.getNpcId() == Npcs.THE_MAIDEN_OF_SUGADINTI)
			return;

		Preconditions.checkState(!runTick || npc.walkDirection != Direction.NONE, "Can't process run without walk movement");
		Position npcPosition = runTick ? npc.getCenterPosition().translate(npc.walkDirection.x(), npc.walkDirection.y()) : npc.getPosition();

		Tile[] npcTiles = TileControl.getTiles(npc, npcPosition);
		int npcSize = npc.getSize();

		int[] npcLocation = TileControl.currentLocation(npcPosition.toTile());
		int[] followingLocation = TileControl.currentLocation(following);

        boolean[] moves = new boolean[4];
		
		Direction dir = Direction.NONE;
		
		int distance = TileControl.calculateDistance(npc, npcPosition, following);
		
		if (distance > 16) {
			return;
		}
		
		npc.facePlayer(following.getIndex());
		
		if (npc.freezeTimer > 0) {
			return;
		}
		
		if (distance > 1) {

            Arrays.fill(moves, true);

            if (npcLocation[0] < followingLocation[0]) {
				moves[EAST] = true;	
				moves[WEST] = false;
			} else if (npcLocation[0] > followingLocation[0]) {
				moves[WEST] = true;	
				moves[EAST] = false;	
			} else {
				moves[EAST] = false;	
				moves[WEST] = false;
			}
			if (npcLocation[1] > followingLocation[1]) {
				moves[SOUTH] = true;
				moves[NORTH] = false;
			} else if (npcLocation[1] < followingLocation[1]) {
				moves[NORTH] = true;	
				moves[SOUTH] = false;
			} else {
				moves[NORTH] = false;	
				moves[SOUTH] = false;
			}	
			for (Tile tiles : npcTiles) {
				if (tiles.getTile()[0] == following.getX()) { //same x line
					moves[EAST] = false;
					moves[WEST] = false;
				} else if (tiles.getTile()[1] == following.getY()) { //same y line
					moves[NORTH] = false;
					moves[SOUTH] = false;
				}
			}
			boolean[] blocked = new boolean[3];
			
			if (moves[NORTH] && moves[EAST]) {

				// North Check
				for (int x = npc.getX(); x < npc.getX() + npcSize; x++) {
					if (npc.getRegionProvider().blockedNorth(x, npc.getY() + (npcSize - 1), npc.getHeight(), true)) {
						blocked[0] = true;
					}

					if (npc.getRegionProvider().blockedNorthEast(x, npc.getY() + (npcSize - 1), npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				// East Check
				for (int y = npc.getY(); y < npc.getY() + npcSize; y++) {
					if (npc.getRegionProvider().blockedEast(npc.getX() + (npcSize - 1), y, npc.getHeight(), true)) {
						blocked[1] = true;
					}

					if (npc.getRegionProvider().blockedNorthEast(npc.getX() + (npcSize - 1), y, npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				if (!blocked[2] && !blocked[0] && !blocked[1]) {  //northeast
					dir = Direction.NORTH_EAST;
				} else if (!blocked[0]) { //north
					dir = Direction.NORTH;
				} else if (!blocked[1]) { //east
					dir = Direction.EAST;
				}	
				
			} else if (moves[NORTH] && moves[WEST]) {

				// North Check
				for (int x = npc.getX(); x < npc.getX() + npcSize; x++) {
					if (npc.getRegionProvider().blockedNorth(x, npc.getY() + (npcSize - 1), npc.getHeight(), true)) {
						blocked[0] = true;
					}

					if (npc.getRegionProvider().blockedNorthWest(x, npc.getY() + (npcSize - 1), npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				// West Check
				for (int y = npc.getY(); y < npc.getY() + npcSize; y++) {
					if (npc.getRegionProvider().blockedWest(npc.getX(), y, npc.getHeight(), true)) {
						blocked[1] = true;
					}

					if (npc.getRegionProvider().blockedNorthWest(npc.getX(), y, npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				if (!blocked[2] && !blocked[0] && !blocked[1]) { //north-west
					dir = Direction.NORTH_WEST;
				} else if (!blocked[0]) { //north
					dir = Direction.NORTH;
				} else if (!blocked[1]) { //west
					dir = Direction.WEST;
				}	
			} else if (moves[SOUTH] && moves[EAST]) {

				// South Check
				for (int x = npc.getX(); x < npc.getX() + npcSize; x++) {
					if (npc.getRegionProvider().blockedSouth(x, npc.getY(), npc.getHeight(), true)) {
						blocked[0] = true;
					}

					if (npc.getRegionProvider().blockedSouthEast(x, npc.getY(), npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				// East Check
				for (int y = npc.getY(); y < npc.getY() + npcSize; y++) {
					if (npc.getRegionProvider().blockedEast(npc.getX() + (npcSize - 1), y, npc.getHeight(), true)) {
						blocked[1] = true;
					}

					if (npc.getRegionProvider().blockedSouthEast(npc.getX() + (npcSize - 1), y, npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				if (!blocked[2] && !blocked[0] && !blocked[1]) { //south-east
					dir = Direction.SOUTH_EAST;
				} else if (!blocked[0]) { //south
					dir = Direction.SOUTH;
				} else if (!blocked[1]) { //east
					dir = Direction.EAST;
				}	
			} else if (moves[SOUTH] && moves[WEST]) {
				// West Check
				for (int y = npc.getY(); y < npc.getY() + npcSize; y++) {
					if (npc.getRegionProvider().blockedWest(npc.getX(), y, npc.getHeight(), true)) {
						blocked[1] = true;
					}

					if (npc.getRegionProvider().blockedSouthWest(npc.getX(), y, npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}

				// South Check
				for (int x = npc.getX(); x < npc.getX() + npcSize; x++) {
					if (npc.getRegionProvider().blockedSouth(x, npc.getY(), npc.getHeight(), true)) {
						blocked[0] = true;
					}

					if (npc.getRegionProvider().blockedSouthWest(x, npc.getY(), npc.getHeight(), true)) {
						blocked[2] = true;
					}
				}


				if (!blocked[2] && !blocked[0] && !blocked[1]) { //south-west
					dir = Direction.SOUTH_WEST;
				} else if (!blocked[0]) { //south
					dir = Direction.SOUTH;
				} else if (!blocked[1]) { //west
					dir = Direction.WEST;
				}	
				
			} else if (moves[NORTH]) {
				dir = Direction.NORTH;

				for (int x = npc.getX(); x < npc.getX() + npcSize; x++) {
					if (npc.getRegionProvider().blockedNorth(x, npc.getY() + (npcSize - 1), npc.getHeight(), true)) {
						dir = Direction.NONE;
					}
				}
			} else if (moves[EAST]) {
				dir = Direction.EAST;

				for (int y = npc.getY(); y < npc.getY() + npcSize; y++) {
					if (npc.getRegionProvider().blockedEast(npc.getX() + (npcSize - 1), y, npc.getHeight(), true)) {
						dir = Direction.NONE;
					}
				}

			} else if (moves[SOUTH]) {
				dir = Direction.SOUTH;

				for (int x = npc.getX(); x < npc.getX() + npcSize; x++) {
					if (npc.getRegionProvider().blockedSouth(x, npc.getY(), npc.getHeight(), true)) {
						dir = Direction.NONE;
					}
				}
			} else if (moves[WEST]) {
				dir = Direction.WEST;

				for (int y = npc.getY(); y < npc.getY() + npcSize; y++) {
					if (npc.getRegionProvider().blockedWest(npc.getX(), y, npc.getHeight(), true)) {
						dir = Direction.NONE;
					}
				}
			}
		} else if (distance == 0) {
            Arrays.fill(moves, true);
			for (Tile tiles : npcTiles) {
				if (npc.getRegionProvider().blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight(), true)) {
					moves[NORTH] = false;
				}
				if (npc.getRegionProvider().blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight(), true)) {
					moves[EAST] = false;
				}
				if (npc.getRegionProvider().blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight(), true)) {
					moves[SOUTH] = false;
				}
				if (npc.getRegionProvider().blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight(), true)) {
					moves[WEST] = false;
				}
			}

			Direction[] random = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
			Direction randomDir = random[Misc.random(random.length)];
			
			if (moves[randomDir == Direction.NORTH ? NORTH
					: randomDir == Direction.SOUTH ? SOUTH
					: randomDir == Direction.EAST ? EAST
					: WEST
					]) {
				dir = randomDir;
			} else if (moves[NORTH]) {
				dir = Direction.NORTH;
			} else if (moves[EAST]) {
				dir = Direction.EAST;
			} else if (moves[SOUTH])	{
				dir = Direction.SOUTH;
			} else if (moves[WEST]) {	
				dir = Direction.WEST;
			}
		} else {
			int x = 0;
			int y = 0;

			for (int index = 0; index < 2; index++) {
				int moveX = 0;
				int moveY = 0;
				if (index == 0) {
					if (following.getX() > npcPosition.getX() || following.getX() < npcPosition.getX()) {
						moveY = following.getY() > npcPosition.getY() ? 1 : -1;
					}
				} else {
					if (following.getY() > npcPosition.getY() || following.getY() < npcPosition.getY()) {
						moveX = following.getX() > npcPosition.getX() ? 1 : -1;
					}
				}

				int direction = NPCClipping.getDirection(moveX, moveY);
				if (direction != -1 && npc.getRegionProvider().canMove(npcPosition.getX(), npcPosition.getY(), npc.heightLevel, direction, true)) {
					x = moveX;
					y = moveY;
					break;
				}
			}

			if (x != 0 || y != 0) {
				//npc.setMovement(x, y);
				Direction direction = Direction.fromDeltas(x, y);
				if (runTick) {
					npc.setMovement(npc.walkDirection, direction);
				} else {
					npc.setMovement(direction);
				}
			}
			return;
		}
		
		if (dir == Direction.NONE) {
			//npc.setMovement(Direction.NONE);
			return;
		}
		
		//dir >>= 1; // TODO i'm guessing this is because it does the opposite in Npc#processMovement?
		//npc.setMovement(Misc.directionDeltaX[dir], Misc.directionDeltaY[dir]);
		if (runTick) {
			npc.setMovement(npc.walkDirection, dir);
		} else {
			npc.setMovement(dir);
		}

		//if (run && !runTick) {
		//	follow(npc, following, run, true);
		//}
	}

	public static void walkTowards(NPC npc, int waypointx, int waypointy) {
		if (npc.revokeWalkingPrivilege)
			return;

		int x = npc.getX();
		int y = npc.getY();
		
		if (waypointx == x && waypointy == y) {
			return;
		}
		
		int direction = -1;
		final int xDifference = waypointx - x;
		final int yDifference = waypointy - y;

		int toX = 0;
		int toY = 0;

		if (xDifference > 0) {
			toX = 1;
		} else if (xDifference < 0) {
			toX = -1;
		}

		if (yDifference > 0) {
			toY = 1;
		} else if (yDifference < 0) {
			toY = -1;
		}

		int toDir = NPCClipping.getDirection(x, y, x + toX, y + toY);
			if (canMoveTo(npc, toDir)) {
				direction = toDir;
			} else {
				if (toDir == 0) {
					if (canMoveTo(npc, 3)) {
						direction = 3;
					} else if (canMoveTo(npc, 1)) {
						direction = 1;
					}
				} else if (toDir == 2) {
					if (canMoveTo(npc, 1)) {
						direction = 1;
					} else if (canMoveTo(npc, 4)) {
						direction = 4;
					}
				} else if (toDir == 5) {
					if (canMoveTo(npc, 3)) {
						direction = 3;
					} else if (canMoveTo(npc, 6)) {
						direction = 6;
					}
				} else if (toDir == 7) {
					if (canMoveTo(npc, 4)) {
						direction = 4;
					} else if (canMoveTo(npc, 6)) {
						direction = 6;
					}
				}
			}

		if (direction == -1) {
			//npc.setMovement(Direction.NONE);
			return;
		}

		//npc.setMovement(NPCClipping.DIR[direction][0], NPCClipping.DIR[direction][1]);
		npc.setMovement(Direction.get(direction));
	}

	public static boolean canMoveTo(NPC npc, Position source, Direction direction) {
		return canMoveTo(npc, source, direction.toInteger());
	}

	public static boolean canMoveTo(NPC mob, int direction) {
		return canMoveTo(mob, mob.getPosition(), direction);
	}

	public static boolean canMoveTo(NPC mob, Position source, int direction) {
		if (direction == -1) {
			return false;
		}
		if (mob.revokeWalkingPrivilege)
			return false;

        Direction dir = Direction.get(direction);
        Position nextPosition = mob.getCenterPosition().translate(dir.x(), dir.y());
        return PathChecker.raycast(mob, mob.getPosition(), nextPosition, false);

    }

	

}