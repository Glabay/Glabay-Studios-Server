package io.xeros.content.bosses.wildypursuit;

import io.xeros.Configuration;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.events.monsterhunt.MonsterHunt;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class TheUnbearable {

	public static final int NPC_ID = 1377;

	public static final int KEY = 4185;

	public static void rewardPlayers() {
		MonsterHunt.monsterKilled = System.currentTimeMillis();
		MonsterHunt.spawned = false;
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.WILDERNESS))
		.forEach(p -> {
				if (p.getGlodDamageCounter() >= 80) {
					p.sendMessage("@blu@The wildy boss has been killed!");
					p.sendMessage("@blu@You receive a key for doing enough damage to the boss!");
					if (p.hasFollower && (p.petSummonId == 30123)) {
						if (Misc.random(100) < 25) {
							p.getItems().addItemUnderAnyCircumstance(KEY, 2);
							p.sendMessage("Your pet provided 2 extra keys!");
						}
					}
					p.getItems().addItemUnderAnyCircumstance(KEY, 2);
					if ((Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS)) {
						p.getItems().addItemUnderAnyCircumstance(KEY, 2);
						p.sendMessage("[WOGW] Double drops is activated and you received 2 extra keys!");
					}
					p.getEventCalendar().progress(EventChallenge.OBTAIN_X_WILDY_EVENT_KEYS);
					LeaderboardUtils.addCount(LeaderboardType.WILDY_EVENTS, p, 1);
					Achievements.increase(p, AchievementType.WILDY_EVENT, 1);
					p.setGlodDamageCounter(0);
				} else {
					p.sendMessage("@blu@You didn't do enough damage to the boss to receive a reward.");
					p.setGlodDamageCounter(0);
				}
				

		});
	}
}
