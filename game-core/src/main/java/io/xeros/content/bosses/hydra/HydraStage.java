package io.xeros.content.bosses.hydra;

import java.util.Arrays;
import java.util.stream.Stream;
import io.xeros.model.entity.player.Boundary;

public enum HydraStage {
    POISON(8615, 8616, 8237, -1, 1100, new Boundary(1368, 10260, 1374, 10266)),
    LIGHTNING(8619, 8617, 8244, 8238, 825, new Boundary(1368, 10269, 1374, 10275)),
    FLAME(8620, 8618, 8251, 8245, 550, new Boundary(1359, 10269, 1365, 10275)),
    ENRAGED(8621, 8622, 8257, 8252, 275, AlchemicalHydra.AREA);
    private final int npcId;
    private final int health;
    private final int deathID;
    private final int deathAnimation;
    private final int transformation;
    private final Boundary boundary;

    HydraStage(int npcId, int deathID, int deathAnimation, int transformation, int health, Boundary boundary) {
        this.npcId = npcId;
        this.transformation = transformation;
        this.health = health;
        this.boundary = boundary;
        this.deathID = deathID;
        this.deathAnimation = deathAnimation;
    }

    public static Stream<HydraStage> stream() {
        return Stream.of(values());
    }

    public static boolean isHydra(int npcId) {
        return Arrays.stream(values()).anyMatch(hydraStage -> hydraStage.npcId == npcId || hydraStage.deathID == npcId);
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getHealth() {
        return this.health;
    }

    public int getDeathID() {
        return this.deathID;
    }

    public int getDeathAnimation() {
        return this.deathAnimation;
    }

    public int getTransformation() {
        return this.transformation;
    }

    public Boundary getBoundary() {
        return this.boundary;
    }
}
