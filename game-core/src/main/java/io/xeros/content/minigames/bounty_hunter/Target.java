package io.xeros.content.minigames.bounty_hunter;

/**
 * Represents a player target for the bounty hunter minigame.
 * 
 * @author Jason MacKeigan
 * @date Nov 12, 2014, 6:34:06 PM
 */
public class Target {
	/**
	 * The name of the player target.
	 */
	private final String loginName;

	/**
	 * Creates a new target based on the player name
	 * 
	 * @param loginName
	 */
	public Target(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * Returns the name of the player target
	 * 
	 * @return the target name
	 */
	public String getLoginName() {
		return loginName;
	}

}
