package io.xeros.model.entity.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.xeros.content.bosses.godwars.Godwars;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.commands.owner.Clipping;
import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.model.Direction;
import io.xeros.model.collisionmap.Tile;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPCClipping;
import lombok.Getter;

@Getter
public final class Position {

    public final int x;
    public final int y;
    public final int height;

    public Position(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public boolean isViewableFrom(Position other) {
        if (this.getHeight() != other.getHeight()) return false;
        Position p = delta(this, other);
        return p.x <= 14 && p.x >= -15 && p.y <= 14 &&
                p.y >= -15;
    }

    public static Position delta(Position a, Position b) {
        return new Position(b.getX() - a.getX(), b.getY() - a.getY());
    }

    public int region() {
        return ((x >> 6) << 8) | (y >> 6);
    }

    private Position() {
        x = y = height = 0;
    }

    public Position(int x, int y) {
        this(x, y, 0);
    }

    public Tile toTile() {
        return new Tile(x, y, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return x == position.x &&
                y == position.y &&
                height == position.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, height);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + height +
                '}';
    }

    public String getFormattedString() {
        return x + ", " + y + ", " + height;
    }

    public Position withHeight(int height) {
        return new Position(x, y, height);
    }

    public Position withX(int x) {
        return new Position(x, y, height);
    }

    public Position withY(int y) {
        return new Position(x, y, height);
    }

    /**
     * Creates a deep copy of this object
     *
     * @return A deep copy of this object
     */
    public Position deepCopy() {
        return new Position(this.x, this.y, height);
    }

    public Position translate(int x, int y) {
        return new Position(this.x + x, this.y + y, height);
    }

    public Position translate(Direction direction) {
        return new Position(this.x + direction.x(), this.y + direction.y(), height);
    }


    /**
     * Checks if the position is within distance of another.
     * @param other The other position.
     * @param distance The distance.
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isWithinDistance(Position other, int distance) {
        if (getHeight() != other.getHeight()) {
            return false;
        }
        int deltaX = Math.abs(x - other.x);
        int deltaY = Math.abs(y - other.y);
        return deltaX <= distance && deltaY <= distance;
    }


    /**
     * Deprecated because it rounds absolute double to integer, use {@link Position#getAbsDistance(Position)}.
     * Use {@link Position#getManhattanDistance(Position)} to get distance in tiles.
     */
    @Deprecated
    public int getDistance(Position dest) {
        return getDistance(this.getX(), this.getY(), dest.getX(), dest.getY());
    }


    /**
     * Deprecated because it rounds absolute double to integer, use {@link Position#getAbsDistance(Position)}.
     * Use {@link Position#getManhattanDistance(Position)} to get distance in tiles.
     */
    @Deprecated
    public int getDistance(int destX, int destY) {
        return getDistance(this.getX(), this.getY(), destX, destY);
    }

    /**
     * Deprecated because it rounds absolute double to integer, use {@link Position#getAbsDistance(Position)}.
     * Use {@link Position#getManhattanDistance(Position)} to get distance in tiles.
     */
    @Deprecated
    public int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public Boundary area(int radius) {
        return new Boundary(x - radius, y - radius, x + radius, y + radius, height);
    }

    public int getClosestX(Entity src, Position target) {
        if (src.getEntitySize() == 1) return src.getX();
        if (target.getX() < src.getX()) return src.getY();
        else if (target.getX() >= src.getY()
                && target.getX() <= src.getX() + src.getEntitySize() - 1) return target.getX();
        else return src.getX() + src.getEntitySize() - 1;
    }

    public int getClosestY(Entity src, Entity target) {
        return getClosestY(src, target.getCenterPosition());
    }

    public int getClosestY(Entity src, Position target) {
        if (src.getEntitySize() == 1) return src.getY();
        if (target.getY() < src.getY()) return src.getY();
        else if (target.getY() >= src.getY()
                && target.getY() <= src.getY() + src.getEntitySize() - 1) return target.getY();
        else return src.getY() + src.getEntitySize() - 1;
    }

    public int getClosestX(Entity src, Entity target) {
        return getClosestX(src, target.getCenterPosition());
    }

    public Position getClosestPosition(Entity src, Entity target) {
        return new Position(getClosestX(src, target), getClosestY(src, target), src.getHeight());
    }

    public int getEffectiveDistance(Entity src, Entity target) {
        Position pos = getClosestPosition(src, target);
        Position pos2 = getClosestPosition(target, src);
        return src.getEntitySize() > 0 ? getDistance(pos, pos2) / 2 : getDistance(pos, pos2);
    }

    public static int getDistanceSqrt(Position start, Position other) {
        return (int) Math.sqrt(Math.pow(start.getX() - other.getX(), 2) + Math.pow(start.getY() - other.getY(), 2) + Math.pow(start.getHeight() - other.getHeight(), 2));
    }

    public int getDistance(Position src, Position dest) {
        return getDistance(src.getX(), src.getY(), dest.getX(), dest.getY());
    }

    public int getManhattanDistance(Position b) {
        return Math.abs(b.getX() - getX()) + Math.abs(b.getY() - getY());
    }

    public double getAbsDistance(Position b) {
        return Math.sqrt(Math.pow(b.getX() - getX(), 2) + Math.pow(b.getY() - getY(), 2));
    }

    public static double getAbsDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Creates a directional position to b in which x and y values are both between -1 and 1 inclusive.
     */
    public Position toDirectional(Position b) {
        int xDiff = b.getX() - x;
        int yDiff = b.getY() - y;
        if (xDiff > 0)
            xDiff = 1;
        if (xDiff < 0)
            xDiff = -1;
        if (yDiff > 0)
            yDiff = 1;
        if (yDiff < 0)
            yDiff = -1;
        return new Position(xDiff, yDiff, getHeight());
    }

    public Position delta(Position b) {
        return new Position(b.getX() - getX(), b.getY() - getY());
    }

    public Position deltaAbsolute(Position b) {
        return new Position(Math.abs(b.getX() - getX()), Math.abs(b.getY() - getY()));
    }

    public Position getCenterPosition(int size) {
        if (size > 2) {
            int deltax = (int) Math.ceil((double) size / 3d);
            int deltay = (int) Math.ceil((double) size / 3d);

            return new Position(getX() + deltax, getY() + deltay, getHeight());
        }
        return this;
    }

    /**
     * Checks if this position is within distance of another position.
     *
     * @param position the position to check the distance for.
     * @param distance the distance to check.
     * @return true if this position is within the distance of another position.
     */
    public boolean withinDistance(Position position, int distance) {
        if (this.height != position.height) return false;
        if (!Clipping.LineValidator.hasLineOfSight(this.getHeight(), this.getX(), this.getY(), position.getX(), position.getY())) return false;
        return Math.abs(position.getX() - this.getX()) <= distance && Math.abs(position.getY() - this.getY()) <= distance;
    }


    @JsonIgnore
    public boolean inWild() {
        if (inClanWars())
            return false;
        if (Boundary.isIn(this, Boundary.BRYOPHYTA_ROOM))
            return false;

        if (Boundary.isIn(this, Boundary.LOBBY)) {
            return false;
        }
        if (Boundary.isIn(this, Boundary.SAFE_ZONE_BLACK_KNIGHTS_FORTRESS)) {
            return false;
        }
        if (Boundary.isIn(this, Boundary.WILDERNESS_UNDERGROUND))
            return true;
        return Boundary.isIn(this, Boundary.WILDERNESS_PARAMETERS);
    }

    @JsonIgnore
    public boolean inBank() {
        return Area(3071, 3110, 3480, 3514) || Area(3089, 3090, 3492, 3498) || Area(3248, 3258, 3413, 3428)
                || Area(3179, 3191, 3432, 3448) || Area(2944, 2948, 3365, 3374) || Area(2942, 2948, 3367, 3374)
                || Area(2944, 2950, 3365, 3370) || Area(3008, 3019, 3352, 3359) || Area(3017, 3022, 3352, 3357)
                || Area(3203, 3213, 3200, 3237) || Area(3212, 3215, 3200, 3235) || Area(3215, 3220, 3202, 3235)
                || Area(3220, 3227, 3202, 3229) || Area(3227, 3230, 3208, 3226) || Area(3226, 3228, 3230, 3211)
                || Area(3227, 3229, 3208, 3226) || Area(3025, 3032, 3374, 3384) || Area(3806, 3820, 2840, 2848);
    }

    @JsonIgnore
    public boolean inOlmRoom() {//checks to see if player is in olm room
        return (getX() > 3200 && getX() < 3260 && getY() > 5710 && getY() < 5770);
    }

    @JsonIgnore
    public boolean inRaidLobby() {//checks to see if player is in the raid lobby
        return Boundary.isIn(this, Boundary.RAIDS_LOBBY);
    }

    @JsonIgnore
    public boolean inPcBoat() {
        return x >= 2660 && x <= 2663 && y >= 2638 && y <= 2643;
    }

    @JsonIgnore
    public boolean inPcGame() {
        return x >= 2624 && x <= 2690 && y >= 2550 && y <= 2619;
    }

    public boolean Area(final int x1, final int x2, final int y1, final int y2) {
        return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
    }

    @JsonIgnore
    public boolean isInJail() {
        return x >= 2066 && x <= 2108 && y >= 4452 && y <= 4478;
    }

    @JsonIgnore
    public boolean inClanWars() {
        return x > 3272 && x < 3391 && y > 4759 && y < 4863;
    }

    @JsonIgnore
    public boolean inClanWarsSafe() {
        return x > 3263 && x < 3390 && y > 4735 && y < 4761;
    }

    @JsonIgnore
    public boolean inEdgeville() {
        return (x > 3040 && x < 3200 && y > 3460 && y < 3519);
    }

    @JsonIgnore
    public boolean inDuelArena() {
        return (x > 3322 && x < 3394 && y > 3195 && y < 3291)
                || (x > 3311 && x < 3323 && y > 3223 && y < 3248);
    }

    @JsonIgnore
    public boolean inGodwars() {
        return Boundary.isIn(this, Godwars.GODWARS_AREA);
    }

    @JsonIgnore
    public boolean inRevs() {
        return (getX() > 3143 && getX() < 3262 && getY() > 10053 && getY() < 10231);
    }

    @JsonIgnore
    public boolean inMulti() {
        if (Boundary.isIn(this, Boundary.ZULRAH)
                || Boundary.isIn(this, Boundary.ABYSSAL_SIRE)
                || Boundary.isIn(this, Boundary.CORPOREAL_BEAST_LAIR)
                || Boundary.isIn(this, Boundary.KRAKEN_CAVE)
                || Boundary.isIn(this, Boundary.SCORPIA_LAIR)
                || Boundary.isIn(this, Boundary.CERBERUS_BOSSROOMS)
                || Boundary.isIn(this, Boundary.INFERNO)
                || Boundary.isIn(this, Boundary.SKOTIZO_BOSSROOM)
                || Boundary.isIn(this, Boundary.LIZARDMAN_CANYON)
                || Boundary.isIn(this, Boundary.BANDIT_CAMP_BOUNDARY)
                || Boundary.isIn(this, Boundary.COMBAT_DUMMY)
                || Boundary.isIn(this, Boundary.TEKTON)
                || Boundary.isIn(this, Boundary.SKELETAL_MYSTICS)
                || Boundary.isIn(this, Boundary.RAIDS)
                || Boundary.isIn(this, Boundary.OLM)
                || Boundary.isIn(this, Boundary.ICE_DEMON)
                || Boundary.isIn(this, Boundary.CATACOMBS)
                || Boundary.isIn(this, Boundary.DZ_BOSS)
                || Boundary.isIn(this, NightmareConstants.BOUNDARY)
                || Boundary.isIn(this, Boundary.OURIANA_ALTAR)
                || Boundary.isIn(this, Boundary.BRYOPHYTA_ROOM)
        ) {
            return true;
        }

        // Tob
        if (Arrays.stream(TobConstants.ALL_BOUNDARIES).anyMatch(boundary -> Boundary.isIn(this, boundary))) {
            return true;
        }

        if (inRevs()) {
            return true;
        }
        if (Boundary.isIn(this, Boundary.KALPHITE_QUEEN) && getHeight() == 0) {
            return true;
        }
        if (Boundary.isIn(this, Boundary.SARACHNIS_LAIR) || Boundary.isIn(this, Boundary.MIMIC_LAIR)
                || Boundary.isIn(this, Boundary.GROTESQUE_LAIR)) {
            return true;
        }
        int absX = getX();
        int absY = getY();
        return (absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607)
                || (absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839)
                || (absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967)
                || (absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967)
                || (absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831)
                || (absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903)
                || (absX >= 2824 && absX <= 2944 && absY >= 5258 && absY <= 5369)
                || (absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711)
                || (absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647)
                || (absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619)
                || (absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117)
                || (absX >= 2896 && absX <= 2927 && absY >= 3595 && absY <= 3630)
                || (absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464)
                || (absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711)
                || (absX >= 2962 && absX <= 3006 && absY >= 3621 && absY <= 3659)
                || (absX >= 3155 && absX <= 3214 && absY >= 3755 && absY <= 3803)
                || (absX >= 1889 && absX <= 1912 && absY >= 4396 && absY <= 4413)
                || (absX >= 3717 && absX <= 3772 && absY >= 5765 && absY <= 5820)
                || (absX >= 3341 && absX <= 3378 && absY >= 4760 && absY <= 4853)
                || (absX >= 2377 && absX <= 2435 && absY >= 9411 && absY <= 9470)
                ;
    }

    public List<Position> getTiles(int size) {
        List<Position> positionList = new ArrayList<>();
        int index = 0;
        for (int i = 1; i < size + 1; i++) {
            for (int k = 0; k < NPCClipping.SIZES[i].length; k++) {
                int x3 = getX() + NPCClipping.SIZES[i][k][0];
                int y3 = getY() + NPCClipping.SIZES[i][k][1];
                positionList.add(new Position(x3, y3, getHeight()));
                index++;
            }
        }
        return positionList;
    }
}
