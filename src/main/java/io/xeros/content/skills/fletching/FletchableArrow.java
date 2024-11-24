package io.xeros.content.skills.fletching;

import io.xeros.model.definition.Items;

public enum FletchableArrow {
	BRONZE(39, 882, 1, 1.3),
	IRON(40, 884, 15, 2.5),
	STEEL(41, 886, 30, 5),
	MITHRIL(42, 888, 45, 7.5),
	BROAD(Items.BROAD_ARROWHEADS, Items.BROAD_ARROWS, 52, 10),
	ADAMANT(43, 890, 60, 10),
	RUNE(44, 892, 75, 12.5),
	AMETHYST(21350, 21326, 82, 13.6),
	RED_WOOD(19669, 52, 90, 17),
	DRAGON(11237, 11212, 90, 15);

	/**
	 * The id
	 */
	private final int id;
	/**
	 * The reward;
	 */
	private final int reward;
	/**
	 * The level required.
	 */
	private final int levelRequired;
	/**
	 * The experience granted.
	 */
	private final double experience;

	FletchableArrow(int id, int reward, int levelRequired, double experience) {
		this.id = id;
		this.reward = reward;
		this.levelRequired = levelRequired;
		this.experience = experience;
	}

	public double getExperience() {
		return experience;
	}

	public int getId() {
		return id;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public int getReward() {
		return reward;
	}

}