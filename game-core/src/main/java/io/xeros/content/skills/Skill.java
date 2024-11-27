package io.xeros.content.skills;

import java.util.stream.Stream;

import io.xeros.Configuration;
import io.xeros.util.Misc;

public enum Skill {
	ATTACK(0, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	DEFENCE(1, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	STRENGTH(2, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	HITPOINTS(3, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	RANGED(4, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	PRAYER(5, 50),
	MAGIC(6, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	COOKING(7, 40),
	WOODCUTTING(8, 55),
	FLETCHING(9, 40),
	FISHING(10, 55),
	FIREMAKING(11, 50),
	CRAFTING(12, 45),
	SMITHING(13, 70),
	MINING(14, 55),
	HERBLORE(15, 60),
	AGILITY(16, 70),
	THIEVING(17, 80),
	SLAYER(18, 70),
	FARMING(19, 35),
	RUNECRAFTING(20, 75),
	HUNTER(21, 60);

	public static final int MAX_EXP = 200_000_000;

	public static int iconForSkill(Skill skill) {
		switch (skill) {
			case ATTACK:
				return 0;
			case STRENGTH:
				return 1;
			case DEFENCE:
				return 2;
			case RANGED:
				return 3;
			case PRAYER:
				return 4;
			case MAGIC:
				return 5;
			case RUNECRAFTING:
				return 6;
			case HITPOINTS:
				return 7;
			case AGILITY:
				return 8;
			case HERBLORE:
				return 9;
			case THIEVING:
				return 10;
			case CRAFTING:
				return 11;
			case FLETCHING:
				return 12;
			case MINING:
				return 13;
			case SMITHING:
				return 14;
			case FISHING:
				return 15;
			case COOKING:
				return 16;
			case FIREMAKING:
				return 17;
			case WOODCUTTING:
				return 18;
			case SLAYER:
				return 19;
			case FARMING:
				return 20;
			case HUNTER:
				return 22;
			default:
				return 0;
		}
	}

	public static Skill forId(int id) {
		return Stream.of(values()).filter(s -> s.id == id).findFirst().orElse(null);
	}

	public static int getIconId(Skill skill) {
		switch (skill) {
			case ATTACK:
				return 134;
			case STRENGTH:
				return 135;
			case DEFENCE:
				return 136;
			case RANGED:
				return 137;
			case PRAYER:
				return 138;
			case MAGIC:
				return 139;
			case RUNECRAFTING:
				return 140;
			case HITPOINTS:
				return 141;
			case AGILITY:
				return 142;
			case HERBLORE:
				return 143;
			case THIEVING:
				return 144;
			case CRAFTING:
				return 145;
			case FLETCHING:
				return 146;
			case MINING:
				return 147;
			case SMITHING:
				return 148;
			case FISHING:
				return 14;
			case COOKING:
				return 150;
			case FIREMAKING:
				return 151;
			case WOODCUTTING:
				return 152;
			case SLAYER:
				return 153;
			case FARMING:
				return 154;
			case HUNTER:
				return 155;
			default:
				return -1;
		}
	}

	public static Skill[] getCombatSkills() {
		return Stream.of(values()).filter(skill -> skill.getId() <= 6).toArray(Skill[]::new);
	}

	public static Skill[] getNonCombatSkills() {
		return Stream.of(values()).filter(skill -> skill.getId() > 6).toArray(Skill[]::new);
	}

	public static final int MAXIMUM_SKILL_ID = 21;

	public static Stream<Skill> stream() {
		return Stream.of(values());
	}

	public static int length() {
		return values().length;
	}

	private final int id;
	private final int experienceRate;

	Skill(int id) {
		this(id, 1);
	}

	Skill(int id, int experienceRate) {
		this.id = id;
		this.experienceRate = experienceRate;
	}

	public int getId() {
		return id;
	}

	public double getExperienceRate() {
		return experienceRate;
	}

	@Override
	public String toString() {
		String name = name().toLowerCase();
		return Misc.capitalize(name);
	}
}

