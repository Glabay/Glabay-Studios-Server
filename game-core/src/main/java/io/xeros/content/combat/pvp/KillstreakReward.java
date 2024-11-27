package io.xeros.content.combat.pvp;

public class KillstreakReward {

	private final int killstreak;
	private final int tokens;

	public KillstreakReward(int killstreak, int tokens) {
		this.killstreak = killstreak;
		this.tokens = tokens;
	}

	public int getKillstreak() {
		return killstreak;
	}

	public int getTokens() {
		return tokens;
	}
}
