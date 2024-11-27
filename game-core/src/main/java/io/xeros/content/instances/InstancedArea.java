package io.xeros.content.instances;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.collisionmap.Region;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.grounditem.GroundItemManager;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instanced area.
 */
public abstract class InstancedArea extends RegionProvider {
    // TODO need a way to have a relative height level
    // for instance, nightmare is on height level 3, so if we get a random height
    // from InstanceHeight we need to add 3 to it to get the proper height level
    // otherwise everything will be on the wrong height level
    // for now, nightmare is a single instance so height is fine.


    private static final Logger logger = LoggerFactory.getLogger(InstancedArea.class);

    /**
     * The {@link Player}s contained in this instance.
     */
    private final List<Player> players = Lists.newArrayList();

    /**
     * The {@link NPC}s contained in this instance.
     */
    private final List<NPC> npcs = Lists.newArrayList();

    /**
     * Has the instance been disposed? A dispose instance has all it's
     * objects, npcs, ground items removed from the world. Players are removed
     * from the instance but not from the world.
     */
    private boolean disposed;

    /**
     * The boundaries for this instanced area.
     * When a player leaves these boundaries they are removed from the instance.
     */
    private final List<Boundary> boundaries = Lists.newArrayList();

    /**
     * The height level that the instance takes place in.
     */
    private final int height;

    /**
     * Determines if the {@link InstancedArea#height} should be automatically freed when the
     * instance is disposed.
     */
    private boolean freeHeightLevel;

    /**
     * Contains configuration fields for this instance.
     */
    private final InstanceConfiguration configuration;

    /**
     * Actions to take when an instance is disposed. This should not include removing
     * npcs, players, ground items or game objects as this happens automatically when an instance
     * is disposed. This will be called before all entities that reside in this instance are removed.
     */
    public abstract void onDispose();

    /**
     * Creates a {@link InstancedArea}.
     *
     * This will create a new instance and also reserve a free height level using {@link InstanceHeight}.
     * You can choose your own height level using the other constructor but it's recommended you use
     * this constructor because it will automatically handle the height level reserving and freeing.
     *
     * @param configuration the {@link InstanceConfiguration}.
     * @param boundaries a varargs of {@link Boundary} which will contain the players.
     *                   If a player leaves this area they are automatically removed
     *                   from the instance.
     */
    public InstancedArea(InstanceConfiguration configuration, Boundary... boundaries) {
        this(configuration, InstanceHeight.getFreeAndReserve(), boundaries);
        this.freeHeightLevel = true;
    }

    /**
     * Creates a {@link InstancedArea}.
     *
     * @param configuration the {@link InstanceConfiguration}.
     * @param height The height level this instance takes place on.
     * @param boundaries a varargs of {@link Boundary} which will contain the players.
     *                   If a player leaves this area they are automatically removed
     *                   from the instance.
     */
    public InstancedArea(InstanceConfiguration configuration, int height, Boundary... boundaries) {
        this.configuration = configuration;
        this.boundaries.addAll(Arrays.stream(boundaries).collect(Collectors.toList()));
        this.height = height;
        this.freeHeightLevel = false;
    }

    @Override
    public String toString() {
        return "InstancedArea{" +
                "class=" + getClass() +
                ", players=" + players.size() +
                ", npcs=" + npcs.size() +
                ", disposed=" + disposed +
                ", boundaries=" + boundaries +
                ", height=" + height +
                ", configuration=" + configuration +
                '}';
    }

    @Override
    public Region get(int x, int y) {
        if (contains(x, y)) {
            return super.get(x, y);
        } else {
            Region region = RegionProvider.getGlobal().get(x, y).clone(this);
            add(region);
            return region;
        }
    }

    /**
     * Kill all npcs inside this instance.
     */
    public void killNpcs() {
        npcs.forEach(it -> it.appendDamage(it.getHealth().getCurrentHealth(), Hitmark.HIT));
    }

    /**
     * Add an {@link NPC} to the instance and calls {@link Entity#setInstance(InstancedArea)} on the npc.
     * This is the method that's called when you spawn an npc to place into the instance.
     */
    public void add(NPC npc) {
        if (disposed) {
            logger.debug("Attempting to add npc to instance after diposed {} {}", npc, this);
            return;
        }

        npcs.add(npc);
        npc.setInstance(this);
        logger.debug("Add to instance npc={}, instance={}", npc, this);
    }

    /**
     * Remove an {@link NPC} to the instance and calls {@link Entity#setInstance(InstancedArea)} on the npc.
     */
    public void remove(NPC npc) {
        npcs.remove(npc);
        npc.setInstance(null);
        logger.debug("Remove from instance npc={}, instance={}", npc, this);
    }

    /**
     * Get an unmodifiable list that contains the {@link NPC}s inside this instance.
     */
    public List<NPC> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    /**
     * Add a {@link Player} to the instance and calls {@link Entity#setInstance(InstancedArea)} on the player.
     * This should not be called manually from any instance code, this is called by the {@link NPC} deregistration.
     */
    public void add(Player player) {
        if (disposed) {
            logger.debug("Attempting to add player to instance after diposed {} {}", player, this);
            return;
        }

        if (!players.contains(player)) {
            players.add(player);
        }
        player.setInstance(this);
        logger.debug("Add to instance player={}, instance={}", player, this);
    }

    /**
     * Remove an {@link Player} to the instance and calls {@link Entity#setInstance(InstancedArea)} on the player.
     */
    public void remove(Player player) {
        players.remove(player);
        player.setInstance(null);
        GroundItemManager.INSTANCE.onRegionChange(player);
        logger.debug("Remove from instance player={}, instance={}", player, this);

        if (!disposed && players.isEmpty() && configuration.isCloseOnPlayersEmpty()) {
            logger.debug("Players list is empty, closing instance {}", this);
            dispose();
        }
    }

    /**
     * Get an unmodifiable list that contains the {@link Player}s inside this instance.
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Set {@link InstancedArea#disposed} to true. Unregister all npcs from the game and
     * remove all players from the instance. Ground items and objects contained in this instance
     * will be removed in the next ground item/object cycle. Regions instanced will be automatically
     * removed because the instance will be freed from memory by the JVM.
     */
    public void dispose() {
        if (disposed) {
            logger.debug("Trying to dispose instance that is already disposed {}", this);
            return;
        }

        logger.debug("Disposing instance {}", this);
        disposed = true;
        onDispose();

        if (freeHeightLevel) {
            InstanceHeight.free(height);
        }

        getNpcs().forEach(npc -> {
            logger.debug("Disposed instance, unregister npc={}, instance={}", npc, this);
            npc.unregister();
        });

        Lists.newArrayList(getPlayers()).forEach(this::remove);
    }

    /**
	 * Get the {@link InstancedArea#height} and adds the {@link InstanceConfiguration#getRelativeHeight()} to it.
     *
     * Use {@link InstancedArea#getReservedHeight} to get the height level reserved from {@link InstanceHeight}
     * (or you're entered height if you used the {@link InstancedArea#InstancedArea(InstanceConfiguration, int, Boundary...)}
     * constructor).)
     *
     * @return the height that all actions inside this instance will take place in
     */
    public int getHeight() {
        return height + configuration.getRelativeHeight();
    }

    /**
     * If you used the {@link InstancedArea#InstancedArea(InstanceConfiguration, int, Boundary...)}
     * constructor this will return the height you supplied to that constructor. If you used the
     * {@link InstancedArea#InstancedArea(InstanceConfiguration, Boundary...)} constructor it will
     * return the height level reserved by {@link InstanceHeight}.
     * @return the height level.
     */
    public int getReservedHeight() {
        return height;
    }

    /**
     * Resolves a relative height level for this instance.
     *
     * Takes the {@param height} and adds {@link InstancedArea#getHeight()}} to it,
     * giving us a relative height level for the instance.
     *
     * @param height the absolute height (0-3).
     * @return the relative height.
     */
    public int resolveHeight(int height) {
        return height + getHeight();
    }

    /**
     * Resolve a position to be at the correct coordinates for this instance.
     *
     * It takes the {@param position} and returns a new position with that
     * positions height, plus the value of {@link InstancedArea#getHeight()},
     * giving a relative position for this instance. Does not change the X/Y positions.
     *
     * @param position The coordinates without any instance height adjusted (meaning 0 through 3).
     * @return {@link Position} with the height adjusted to be inside the instance.
     */
    public Position resolve(Position position) {
        return position.withHeight(position.getHeight() + getHeight());
    }

    /**
     * The boundary or location of this instanced area
     * 
     * @return the boundary
     */
    public List<Boundary> getBoundaries() {
        return boundaries;
    }

    /**
     * Check if an instance is disposed.
     * @return <code>true</code> if the instance is disposed.
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Get the {@link InstanceConfiguration}.
     * @return the {@link InstanceConfiguration}
     */
    public InstanceConfiguration getConfiguration() {
        return configuration;
    }

    // Random methods below here, how can we make this fit?

    public void tick(Entity entity) { }

    public boolean handleInterfaceUpdating(Player player) {
        return false;
    }

    public boolean handleClickObject(Player player, WorldObject object, int option) {
        return false;
    }

    public boolean handleDeath(Player player) {
        return false;
    }
}
