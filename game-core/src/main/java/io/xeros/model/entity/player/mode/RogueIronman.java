package io.xeros.model.entity.player.mode;

public class RogueIronman extends IronmanMode {

    public RogueIronman(ModeType type) {
        super(type);
    }

    @Override
    public double getDropModifier() {
        return -0.07;
    }

    @Override
    public int getTotalLevelNeededForRaids() {
        return 750;
    }

    @Override
    public int getTotalLevelForTob() {
        return 1000;
    }
}
