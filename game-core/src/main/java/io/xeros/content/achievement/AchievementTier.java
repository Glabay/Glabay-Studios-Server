package io.xeros.content.achievement;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 26, 2014
 */
public enum AchievementTier {

	TIER_1(0, "Beginner", "I"),
	TIER_2(1, "Intermediate", "II"),
	TIER_3(2, "Expert", "III"),
	TIER_4(3, "Legendary", "IV"),
	STARTER(4, "Starter", "")
	;

	private final int id;
	private final String name;
	private final String tier;

	AchievementTier(int id, String name, String tier) {
		this.id = id;
		this.name = name;
		this.tier = tier;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTierText() {
		return tier;
	}
}
