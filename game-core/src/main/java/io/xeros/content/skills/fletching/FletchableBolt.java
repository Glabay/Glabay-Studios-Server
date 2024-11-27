package io.xeros.content.skills.fletching;

public enum FletchableBolt {
	SAPPHIRE(9142, 9189, 9337, 54, 5), 
	EMERALD(9142, 9190, 9338, 61, 7), 
	RUBY(9143, 9191, 9339, 63, 8), 
	DIAMOND(9143, 9192, 9340, 65, 9), 
	DRAGON(9144, 9193, 9341, 71, 10), 
	ONYX(9144, 9194, 9342, 73, 12),
	BROAD_AMETHYST(11875, 21338, 21316, 76, 11),
	OPAL_DRAGON(21905, 45, 21955, 76, 2),
	JADE_DRAGON(21905,9187,21957, 76, 2),
	PEARL_DRAGON(21905,46,21959, 76, 3),
	TOPAZ_DRAGON(21905,9188,21961, 76, 4),
	SAPPHITRE_DRAGON(21905,9189, 21963, 76, 5),
	EMERALD_DRAGON(21905,9190, 21965, 76, 6),
	RUBY_DRAGON(21905,9191, 21967, 76, 6),
	DIAMOND_DRAGON(21905,9192,21969, 76, 7),
	DRAGONSTONE_DRAGON(21905,9193,21971, 76, 8),
	ONYX_DRAGON(21905,9194,21973, 76, 10),
	;
	private final int unfinished, tip, bolt, level, experience;

	FletchableBolt(int unfinished, int tip, int bolt, int level, int experience) {
		this.unfinished = unfinished;
		this.tip = tip;
		this.bolt = bolt;
		this.level = level;
		this.experience = experience;
	}

	public int getUnfinished() {
		return unfinished;
	}

	public int getTip() {
		return tip;
	}

	public int getBolt() {
		return bolt;
	}

	public int getLevel() {
		return level;
	}

	public int getExperience() {
		return experience;
	}

}
