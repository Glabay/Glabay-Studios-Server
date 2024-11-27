package io.xeros.content.instances;

/**
 * Holds instance configuration.
 */
public class InstanceConfiguration {

    /**
     * Instance that closes when all players have left and where npcs will not respawn.
     */
    public static final InstanceConfiguration CLOSE_ON_EMPTY = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .createInstanceConfiguration();

    /**
     * Instance that closes when all players have left and where npcs will respawn.
     */
    public static final InstanceConfiguration CLOSE_ON_EMPTY_RESPAWN = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    /**
     * Will the instance automatically dispose when the
     * player list reaches zero?
     */
    private final boolean closeOnPlayersEmpty;

    /**
     * Will the intsance also npcs to respawn after death?
     * Otherwise the npcs are unregistered from the game.
     */
    private final boolean respawnNpcs;

    /**
     * Relative height is the height that the instance would be if the base height level was zero.
     *
     * For instance, {@link io.xeros.content.bosses.nightmare.Nightmare} takes place on height
     * level three. But when we get a height from {@link InstanceHeight} it returns a height
     * level that will render as level zero. This "relative height" will offset that height
     * level when you call {@link InstancedArea#getHeight()}. So if you set this to 3, the
     * height level returned when you call {@link InstancedArea#getHeight()} will be the
     * one reserved by {@link InstanceHeight} plus 3. Giving you a free height level but
     * also setting the correct height level for the map you're using.
     */
    private final int relativeHeight;

    /**
     * Create an {@link InstanceConfiguration}.
     */
    public InstanceConfiguration(boolean closeOnPlayersEmpty, boolean respawnNpcs, int relativeHeight) {
        this.closeOnPlayersEmpty = closeOnPlayersEmpty;
        this.respawnNpcs = respawnNpcs;
        this.relativeHeight = relativeHeight;
    }

    @Override
    public String toString() {
        return "InstanceConfiguration{" +
                "closeOnPlayersEmpty=" + closeOnPlayersEmpty +
                ", respawnNpcs=" + respawnNpcs +
                ", relativeHeight=" + relativeHeight +
                '}';
    }

    public boolean isCloseOnPlayersEmpty() {
        return closeOnPlayersEmpty;
    }

    public boolean isRespawnNpcs() {
        return respawnNpcs;
    }

    public int getRelativeHeight() {
        return relativeHeight;
    }
}
