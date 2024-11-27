package io.xeros.content.achievement_diary;

import com.google.common.collect.Lists;
import io.xeros.content.achievement_diary.impl.ArdougneAchievementDiary;
import io.xeros.content.achievement_diary.impl.DesertAchievementDiary;
import io.xeros.content.achievement_diary.impl.FaladorAchievementDiary;
import io.xeros.content.achievement_diary.impl.FremennikAchievementDiary;
import io.xeros.content.achievement_diary.impl.KandarinAchievementDiary;
import io.xeros.content.achievement_diary.impl.KaramjaAchievementDiary;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorAchievementDiary;
import io.xeros.content.achievement_diary.impl.MorytaniaAchievementDiary;
import io.xeros.content.achievement_diary.impl.VarrockAchievementDiary;
import io.xeros.content.achievement_diary.impl.WesternAchievementDiary;
import io.xeros.content.achievement_diary.impl.WildernessAchievementDiary;
import io.xeros.model.entity.player.Player;

import java.util.List;

public final class AchievementDiaryManager {

	private final Player player;

	private final VarrockAchievementDiary varrockDiary;
	private final ArdougneAchievementDiary ardougneDiary;
	private final FaladorAchievementDiary faladorDiary;
	private final LumbridgeDraynorAchievementDiary lumbridgeDraynorDiary;
	private final KaramjaAchievementDiary karamjaDiary;
	private final WildernessAchievementDiary wildernessDiary;
	private final MorytaniaAchievementDiary morytaniaDiary;
	private final KandarinAchievementDiary kandarinDiary;
	private final FremennikAchievementDiary fremennikDiary;
	private final WesternAchievementDiary westernDiary;
	private final DesertAchievementDiary desertDiary;

	public AchievementDiaryManager(Player player) {
		this.player = player;
		varrockDiary = new VarrockAchievementDiary(player);
		ardougneDiary = new ArdougneAchievementDiary(player);
		faladorDiary = new FaladorAchievementDiary(player);
		lumbridgeDraynorDiary = new LumbridgeDraynorAchievementDiary(player);
		karamjaDiary = new KaramjaAchievementDiary(player);
		wildernessDiary = new WildernessAchievementDiary(player);
		morytaniaDiary = new MorytaniaAchievementDiary(player);
		kandarinDiary = new KandarinAchievementDiary(player);
		fremennikDiary = new FremennikAchievementDiary(player);
		westernDiary = new WesternAchievementDiary(player);
		desertDiary = new DesertAchievementDiary(player);
	}

	public void setDiariesCompleted() {
		if (getVarrockDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d1Complete = true;
		}
		if (getArdougneDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d2Complete = true;
		}
		if (getDesertDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d3Complete = true;
		}
		if (getFaladorDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d4Complete = true;
		}
		if (getFremennikDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d5Complete = true;
		}
		if (getKandarinDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d6Complete = true;
		}
		if (getKaramjaDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d7Complete = true;
		}
		if (getLumbridgeDraynorDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d8Complete = true;
		}
		if (getMorytaniaDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d9Complete = true;
		}
		if (getWesternDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d10Complete = true;
		}
		if (getWildernessDiary().hasDoneAll()) {
			player.diariesCompleted += 1;
			player.d11Complete = true;
		}
	}

	public List<StatefulAchievementDiary<?>> getAll() {
		return Lists.newArrayList(
				varrockDiary, ardougneDiary, desertDiary, faladorDiary, fremennikDiary,
				kandarinDiary, karamjaDiary, lumbridgeDraynorDiary, morytaniaDiary,
				westernDiary, wildernessDiary
		);
	}

	public boolean clickButton(int buttonId) {
		switch (buttonId) {
			case 24601:
				getVarrockDiary().display();
				return true;
			case 24617:
				getArdougneDiary().display();
				return true;
			case 24633:
				getDesertDiary().display();
				return true;
			case 24649:
				getFaladorDiary().display();
				return true;
			case 24665:
				getFremennikDiary().display();
				return true;
			case 24681:
				getKandarinDiary().display();
				return true;
			case 24697:
				getKaramjaDiary().display();
				return true;
			case 24713:
				getLumbridgeDraynorDiary().display();
				return true;
			case 24729:
				getMorytaniaDiary().display();
				return true;
			case 24745:
				getWesternDiary().display();
				return true;
			case 24761:
				getWildernessDiary().display();
				return true;
		}

		return false;
	}

	public VarrockAchievementDiary getVarrockDiary() {
		return varrockDiary;
	}
	
	public ArdougneAchievementDiary getArdougneDiary() {
		return ardougneDiary;
	}
	
	public FaladorAchievementDiary getFaladorDiary() {
		return faladorDiary;
	}

	public LumbridgeDraynorAchievementDiary getLumbridgeDraynorDiary() {
		return lumbridgeDraynorDiary;
	}

	public KaramjaAchievementDiary getKaramjaDiary() {
		return karamjaDiary;
	}

	public WildernessAchievementDiary getWildernessDiary() {
		return wildernessDiary;
	}

	public MorytaniaAchievementDiary getMorytaniaDiary() {
		return morytaniaDiary;
	}

	public KandarinAchievementDiary getKandarinDiary() {
		return kandarinDiary;
	}

	public FremennikAchievementDiary getFremennikDiary() {
		return fremennikDiary;
	}

	public WesternAchievementDiary getWesternDiary() {
		return westernDiary;
	}

	public DesertAchievementDiary getDesertDiary() {
		return desertDiary;
	}

}
