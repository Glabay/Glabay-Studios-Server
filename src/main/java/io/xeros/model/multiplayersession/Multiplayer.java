package io.xeros.model.multiplayersession;

import io.xeros.model.entity.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Jason MacKeigan
 * @since Oct 19, 2014, 8:03:05 PM
 */
public abstract class Multiplayer {
	@Setter
    @Getter
    protected long lastAccept;

	/**
	 * Player associated with trading operations
	 */
	protected Player player;

	/**
	 * Constructs a new class for managing trade operations
	 *
     */
	public Multiplayer(Player player) {
		this.player = player;
	}

	public abstract boolean requestable(Player request);

	public abstract void request(Player requested);

}
