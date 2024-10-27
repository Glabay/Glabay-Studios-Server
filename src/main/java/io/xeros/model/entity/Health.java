package io.xeros.model.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import io.xeros.Server;
import io.xeros.model.cycleevent.impl.PoisonEvent;
import io.xeros.model.cycleevent.impl.PoisonResistanceEvent;
import io.xeros.model.cycleevent.impl.VenomEvent;
import io.xeros.model.cycleevent.impl.VenomResistanceEvent;
import io.xeros.model.entity.player.Player;
import lombok.Getter;
import lombok.Setter;

public final class Health {

	/**
	 * The entity this health was created for
	 */
	private final Entity entity;

	/**
	 * The amount of health the entity has
     * -- GETTER --
     *  The amount of health the entity has
     *

     */
	@Getter
    private int currentHealth;

	/**
	 * The maximum amount of health the entity has access to
     * -- GETTER --
     *  The maximum health the entity can have
     * <p>
     *
     * -- SETTER --
     *  The absolute maximum health of this npc
     *
     */
	@Setter
    @Getter
    private int maximumHealth;

	/**
	 * The status of the health
     * -- GETTER --
     *  Retrieves the current status of the entities health
     *

     */
	@Getter
    private HealthStatus status = HealthStatus.NORMAL;

	/**
	 * A {@link Collection} of statuses that the entity is not susceptible to
	 */
	private final Collection<HealthStatus> nonsusceptibles = new ArrayList<>();

	/**
	 * A new {@link Health} object to manage health operations
	 * 
	 * @param entity the single entity this is created for
	 */
	public Health(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Determines if the health of the entity is not susceptible to a possible status change
	 * 
	 * @param status the possible new status change
	 * @return {@code true} if the entity is not susceptible to the status
	 */
	public boolean isNotSusceptibleTo(HealthStatus status) {
		return nonsusceptibles.contains(status);
	}

	/**
	 * Proposes that the health of the entity be changed to another
	 * 
	 * @param status the potential new status of the entity
	 */
	public void proposeStatus(HealthStatus status, int damage, Optional<Entity> inflictor) {
		if (!entity.susceptibleTo(status)) {
			return;
		}

		if (isNotSusceptibleTo(status)) {
			return;
		}

		if (this.status == status) {
			return;
		}

		if (this.status.isVenomed() && status.isPoisoned()) {
			return;
		}

		if (Server.getEventHandler().isRunning(entity, "health_status")) {
			Server.getEventHandler().stop(entity, "health_status");
		}

		if (status.isVenomed()) {
			Server.getEventHandler().submit(new VenomEvent(entity, damage, inflictor));
		}

		if (status.isPoisoned()) {
			Server.getEventHandler().submit(new PoisonEvent(entity, damage, inflictor));
		}

		this.status = status;

		if (entity instanceof Player player) {
            player.sendMessage("You have been effected by " + status + ".");
			player.getPA().requestUpdates();
			player.getPA().updatePoisonStatus();
		}
	}

	/**
	 * Attempts to resolve the status specified. If the entity is already not
	 * 
	 * @param status the status we're trying to resolve
	 * @param resistanceDuration the duration of the resistance if provided
	 */
	public void resolveStatus(HealthStatus status, int resistanceDuration) {
		if (!nonsusceptibles.contains(status)) {
			nonsusceptibles.add(status);
		}

		if (status == HealthStatus.POISON) {
			Server.getEventHandler().stop(entity, "poison_resistance_event");
			Server.getEventHandler().submit(new PoisonResistanceEvent(entity, resistanceDuration));
		}

		if (status == HealthStatus.VENOM) {
			Server.getEventHandler().stop(entity, "venom_resistance_event");
			Server.getEventHandler().submit(new VenomResistanceEvent(entity, resistanceDuration));
		}

		this.status = HealthStatus.NORMAL;
		updateStatus();
	}

	private void updateStatus() {
		if (entity instanceof Player player) {
            player.getPA().updatePoisonStatus();
		}
	}

	/**
	 * Removes and resolves all status effects such as venom or poison.
	 */
	public void removeAllStatuses() {
		status = HealthStatus.NORMAL;
		Server.getEventHandler().stop(entity, "health_status");
		updateStatus();
	}

	/**
	 * Removes all immunities the player may have such as being immune to venom or poison.
	 */
	public void removeAllImmunities() {
		Server.getEventHandler().stop(entity, "poison_resistance_event");
		Server.getEventHandler().stop(entity, "venom_resistance_event");
		updateStatus();
	}

	/**
	 * Allows us to remove the status from the list of non-susceptible.
	 *
	 * @param status the status to be removed
	 */
	public void removeNonsusceptible(HealthStatus status) {
		nonsusceptibles.remove(status);
	}

	/**
	 * Requests that the health of the entity update some attribute of the player. In this case, the {@link Player} would have his or her health value sent to the client to show in
	 * the orb and on the statistic interface.
	 */
	public void requestUpdate() {
		if (entity instanceof Player player) {
            if (player.initialized) // Fix for writing packets from outside main
				player.getPA().refreshSkill(Player.playerHitpoints);
		}
	}

    /**
	 * Attempts to reduce the entities health amount by a specified amount
	 * 
	 * @param amount the amount the health amount is to be reduced by
	 */
	public void reduce(int amount) {
		if (amount <= 0) {
			return;
		}
		if (amount > this.currentHealth) {
			amount = this.currentHealth;
		}
		this.currentHealth -= amount;
		requestUpdate();
	}

	/**
	 * Attempts to increase the entities health by a specified amount
	 * 
	 * @param amount the amount the health will be increased by
	 */
	public void increase(int amount) {
	    increase(amount, maximumHealth);
	}

	/**
	 * Attempts to increase the entities health by a specified amount
	 *
	 * @param amount the amount the health will be increased by
	 */
	public void increase(int amount, int maxHealth) {
		if (currentHealth + amount >= maxHealth) {
			if (currentHealth <= maxHealth) {
				currentHealth = maxHealth;
				requestUpdate();
			}
			return;
		}

		this.currentHealth += amount;
		requestUpdate();
	}

	public void tick(int amount) {
		if (currentHealth > maximumHealth) {
			currentHealth--;
			requestUpdate();
		} else {
			increase(amount);
		}
	}

	public boolean isMaximum() {
		return currentHealth == maximumHealth;
	}

	/**
	 * Modifies the current amount of health to the amount specified in the parameter. <b> It is worth noting that the amount can exceed that of the {@code maximum}. </b>
	 * 
	 * @param currentHealth the new amount of health
	 */
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
		requestUpdate();
	}

	/**
	 * Sets the value of {@code #amount} to that of {@code #maximum} in an effort to revitalize the health of the entity.
	 */
	public void reset() {
		currentHealth = maximumHealth;
		requestUpdate();
	}
}
