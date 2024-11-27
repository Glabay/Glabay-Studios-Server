package io.xeros.content.skills.fletching;

import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class FletchLogEvent extends Event<Player> {

	private final FletchableLog fletch;

	private int amount;

	public FletchLogEvent(Player attachment, int ticks, FletchableLog fletch, int amount) {
		super("skilling", attachment, ticks);
		this.fletch = fletch;
		this.amount = amount;
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			stop();
			return;
		}
		if (attachment.playerLevel[Skill.FLETCHING.getId()] < fletch.getLevel()) {
			attachment.sendMessage("You need a fletching level of " + fletch.getLevel() + " to do this.");
			stop();
			return;
		}
		if (!attachment.getItems().playerHasItem(fletch.getItemId())) {
			attachment.sendMessage("You have run out of logs.");
			stop();
			return;
		}
		amount--;
		attachment.startAnimation(1248);
		Achievements.increase(attachment, AchievementType.FLETCH, 1);
		attachment.getItems().deleteItem2(fletch.getItemId(), 1);
		switch (fletch.getProduct()) {
		case 70:
			if (Boundary.isIn(attachment, Boundary.CATHERBY_BOUNDARY)) {
				attachment.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.FLETCH_MAGIC_BOW);
			}
			break;
			
		case 54:
			if (Boundary.isIn(attachment, Boundary.GNOME_STRONGHOLD_BOUNDARY)) {
				attachment.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.FLETCH_OAK_SHORT_WEST);
			}
			break;
		}
		if (fletch.getProduct() == 52) {
			int amount = 15;
			if (fletch.getItemId() == 1521) {
				amount*= 2;
			}
			if (fletch.getItemId() == 1519) {
				amount*= 3;
			}
			if (fletch.getItemId() == 1517) {
				amount*= 4;
			}
			if (fletch.getItemId() == 1515) {
				amount*= 5;
			}
			if (fletch.getItemId() == 1513) {
				amount*= 6;
			}
			if (fletch.getItemId() == 19669) {
				amount*= 15;
			}
			if (SkillcapePerks.FLETCHING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment)) {
				amount+= 10;
			}
			attachment.getItems().addItem(fletch.getProduct(), amount);
		} else {
			attachment.getItems().addItem(fletch.getProduct(), 1);
		}

		attachment.getPA().addSkillXPMultiplied(fletch.getExperience(), Skill.FLETCHING.getId(), true);
		if (amount <= 0) {
			stop();
			return;
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment != null && !attachment.isDisconnected() && attachment.getSession() != null) {
			attachment.stopAnimation();
		}
	}
}
