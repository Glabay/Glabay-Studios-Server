package io.xeros.content.leaderboards;

public class RewardEntry {
	private final LeaderboardPeriodicity period;
	private final LeaderboardEntry entry;
	private final int place;

	public RewardEntry(LeaderboardPeriodicity period, LeaderboardEntry entry, int place) {
		this.period = period;
		this.entry = entry;
		this.place = place;
	}

	@Override
	public String toString() {
		return "RewardEntry{" +
				"period=" + period +
				", entry=" + entry +
				", place=" + place +
				'}';
	}

	public LeaderboardPeriodicity getPeriod() {
		return period;
	}

	public LeaderboardEntry getEntry() {
		return entry;
	}

	public int getPlace() {
		return place;
	}
}