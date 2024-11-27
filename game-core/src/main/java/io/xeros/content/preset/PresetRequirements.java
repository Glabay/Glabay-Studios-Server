package io.xeros.content.preset;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 *
 */
public class PresetRequirements {

	private final int levelId;
	private final int level;
	
	public PresetRequirements(int levelId, int level) {
		this.levelId = levelId;
		this.level = level;
	}

	public int getLevelId() {
		return levelId;
	}

	public int getLevel() {
		return level;
	}
}
