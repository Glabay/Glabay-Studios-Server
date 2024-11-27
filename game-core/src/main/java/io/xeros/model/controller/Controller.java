package io.xeros.model.controller;

import com.google.common.base.Preconditions;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Set;

/**
 * A controller specifies what the player is allowed to do
 * while in the controlled state. Feel free to add new functions
 * for what you need.
 */
public interface Controller {

    default boolean isDefault() {
        return getKey().equals(ControllerRepository.DEFAULT_CONTROLLER_KEY);
    }

    /**
     * Check if a player is inside this controller's set of boundaries.
     * @return true if boundaries are unset or if player is inside the declared boundaries.
     */
    default boolean inBoundary(Player player) {
        Preconditions.checkState(getBoundaries() != null, "Controller has no boundaries.");
        return getBoundaries().stream().anyMatch(boundary -> boundary.in(player));
    }

    /**
     * Check if a player is inside the specified boundaries if they exist, otherwise return true.
     * @return true if player inside controller boundaries or if controller has no boundaries.
     */
    default boolean inBoundaryOrNoBoundaries(Player player) {
        if (getBoundaries() == null)
            return true;
        return inBoundary(player);
    }

    /**
     * Controllers allow switching if it's the default controller or if the controller has a set
     * of defined boundaries, which allow it to set and unset itself depending on coordinates.
     * In other cases the controller must be set/unset manually and cannot be automatically switched.
     */
    default boolean allowSwitch() {
        return this == ControllerRepository.getDefault() || getBoundaries() != null && !getBoundaries().isEmpty();
    }

    /**
     * Key is used to serialize the player's current controller and set it when they log back in.
     * @return A unique string that identifies this controller implementation.
     */
    String getKey();

    /**
     * A set of {@link Boundary} where if the {@link Player} enters a boundary
     * in this set their controller will be set to this one, and when they reach a point
     * where they're not in any of the boundaries the controller be reset either to {@link DefaultController}
     * or a controller in which they are in the specified boundary.
     */
    Set<Boundary> getBoundaries();

    /**
     * Player was set to this controller.
     */
    void added(Player player);

    /**
     * Player was unset from this controller.
     */
    void removed(Player player);

    /**
     * Called when a player has clicked another.
     * @return true if player option has been handled and shouldn't be processed further.
     */
    boolean onPlayerOption(Player player, Player clicked, String option);

    boolean canMagicTeleport(Player player);

    /**
     * Called when the player logins in after the controller is loaded via.
     */
    void onLogin(Player player);

    void onLogout(Player player);
}
