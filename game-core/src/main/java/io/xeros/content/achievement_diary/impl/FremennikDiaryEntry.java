package io.xeros.content.achievement_diary.impl;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public enum FremennikDiaryEntry {
	//Easy
	KILL_ROCK_CRAB("Kill some rock crabs: %totalstage", 80),
	FILL_BUCKET("Fill some buckets with water at the rellekka well: %totalstage", 10),
	CHOP_OAK_FREM("Cut some oak logs within fremennik province: %totalstage", 20),
	TRAVEL_NEITIZNOT("Have the sailor bring you to neitiznot"),
	TRAVEL_JATIZSOT("Have the sailor bring you to jatizsot"),
	//Medium
	MINE_COAL_FREM("Mine some coal in rellekka: %totalstage", 85),
	KILL_DAGANNOTH_KINGS("Kill 3 of the dagganoth kings of your choice.", 3),
	KILL_SPIRITUAL_MAGE("Kill a spiritual mage within god wars dungeon"),
	
	//Hard
	PRAY_AT_ALTAR("Teleport to Xeric's Shrine and pray at the altar."),
	MIX_SUPER_DEFENCE("Mix super defence potions within fremennik province: %totalstage", 35),
	KILL_KURASK("Kill 200 kurasks in the fremennik dungeon %totalstage", 200),
	DAGANNOTH_KINGS("Kill some dagannoth kings: %totalstage", 30),

	//Elite
	KILL_BANDOS("Kill bandos god wars boss: %totalstage", 10),
	KILL_ARMADYL("Kill armadyl god wars boss: %totalstage", 10),
	KILL_ZAMORAK("Kill zamorak god wars boss: %totalstage", 10),
	KILL_SARADOMIN("Kill saradomin god wars boss: %totalstage", 10);
	
	private final String description;
	
	private final int maximumStages;
	
	public static final Set<FremennikDiaryEntry> SET = EnumSet.allOf(FremennikDiaryEntry.class);
	
	FremennikDiaryEntry(String description) {
		this(description, -1);
	}
	
	FremennikDiaryEntry(String description, int maximumStages) {
		this.description = description;
		this.maximumStages = maximumStages;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public static final Optional<FremennikDiaryEntry> fromName(String name) {
		return SET.stream().filter(entry -> entry.name().equalsIgnoreCase(name)).findAny();
	}

	public int getMaximumStages() {
		return maximumStages;
	}
}
