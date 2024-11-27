package io.xeros.content.titles;

import io.xeros.model.entity.player.Player;

/**
 * Titles commonly have requirements for purchase or for display. Any title that implements this interface will more than likely have a requirement or standard that the player must
 * meet.
 * 
 * @author Jason MacKeigan
 * @date Jan 22, 2015, 3:35:04 PM
 */
public interface TitleRequirement {

	/**
	 * Determines if the player meets the specific requirement of a certain title.
	 * 
	 * @param player The player
	 * @return By default the function returns true unless defined otherwise.
	 */
	default boolean meetsStandard(Player player) {
		return true;
	}

}
