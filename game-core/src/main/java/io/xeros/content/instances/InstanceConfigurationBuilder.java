package io.xeros.content.instances;

public class InstanceConfigurationBuilder {

    private boolean closeOnPlayersEmpty;
    private boolean respawnNpcs;
    private int relativeHeight;

    public InstanceConfigurationBuilder setCloseOnPlayersEmpty(boolean closeOnPlayersEmpty) {
        this.closeOnPlayersEmpty = closeOnPlayersEmpty;
        return this;
    }

    public InstanceConfigurationBuilder setRespawnNpcs(boolean respawnNpcs) {
        this.respawnNpcs = respawnNpcs;
        return this;
    }

    public InstanceConfigurationBuilder setRelativeHeight(int relativeHeight) {
        this.relativeHeight = relativeHeight;
        return this;
    }

    public InstanceConfiguration createInstanceConfiguration() {
        return new InstanceConfiguration(closeOnPlayersEmpty, respawnNpcs, relativeHeight);
    }
}