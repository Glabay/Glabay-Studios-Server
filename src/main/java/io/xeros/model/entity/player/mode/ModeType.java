package io.xeros.model.entity.player.mode;

import io.xeros.content.skills.Skill;

public enum ModeType {
	STANDARD,
	IRON_MAN,
	ULTIMATE_IRON_MAN,
	OSRS,
	HC_IRON_MAN,
	ROGUE,
	ROGUE_HARDCORE_IRONMAN,
	ROGUE_IRONMAN,
	GROUP_IRONMAN,
	EVENT_MAN;
	;

	public double getExperienceRate(Skill skill) {
        return switch (this) {
            case STANDARD, IRON_MAN, ULTIMATE_IRON_MAN, HC_IRON_MAN, GROUP_IRONMAN -> skill.getExperienceRate();
            case OSRS -> 1d;
            case ROGUE, ROGUE_HARDCORE_IRONMAN, ROGUE_IRONMAN -> 5d;
            default -> throw new IllegalStateException("No xp rate defined for " + toString());
        };
	}
	public boolean isStandardRate(Skill skill) {
        return switch (this) {
            case STANDARD, IRON_MAN, ULTIMATE_IRON_MAN, HC_IRON_MAN, GROUP_IRONMAN -> true;
            default -> false;
        };
	}
	public String getFormattedName() {
        return switch (this) {
            case STANDARD -> "Standard";
            case IRON_MAN -> "Ironman";
            case ULTIMATE_IRON_MAN -> "Ultimate Ironman";
            case OSRS -> "OSRS";
            case HC_IRON_MAN -> "Hardcore Ironman";
            case ROGUE -> "Rogue";
            case ROGUE_HARDCORE_IRONMAN -> "Rogue Hardcore Ironman";
            case ROGUE_IRONMAN -> "Rogue Ironman";
            case GROUP_IRONMAN -> "Group Ironman";
            default -> throw new IllegalStateException("No format option for: " + this);
        };
	}
}
