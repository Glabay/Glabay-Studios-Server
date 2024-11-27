package io.xeros.model.entity.player.mode;

public class UltimateIronmanMode extends IronmanMode {
    public UltimateIronmanMode(ModeType type) {
        super(type);
    }

    @Override
    public boolean isBankingPermitted() {
        return false;
    }
}
