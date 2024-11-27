package io.xeros.content.minigames.barrows.brothers;

import java.util.ArrayList;

import io.xeros.content.minigames.barrows.RewardItem;
import io.xeros.content.minigames.barrows.RewardLevel;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Coordinate;
import io.xeros.model.entity.player.Player;

public class Ahrim extends Brother {

	public Ahrim(Player player) {
		super(player);
	}

	@Override
	public int getId() {
		return AHRIM;
	}

	@Override
	public Boundary getMoundBoundary() {
		return new Boundary(3561, 3285, 3568, 3292);
	}

	@Override
	public int getStairsId() {
		return 20667;
	}

	@Override
	public int getFrameId() {
		return 27502;
	}

	@Override
	public Coordinate getStairsLocation() {
		return new Coordinate(3557, 9703, 3);
	}

	@Override
	public int getCoffinId() {
		return 20770;
	}

	@Override
	public String getName() {
		return "Ahrim";
	}

	@Override
	public ArrayList<RewardItem> getRewards() {
		ArrayList<RewardItem> rewards = new ArrayList<>();
		rewards.add(new RewardItem(4708, 1, 1, RewardLevel.RARE));
		rewards.add(new RewardItem(4710, 1, 1, RewardLevel.RARE));
		rewards.add(new RewardItem(4712, 1, 1, RewardLevel.RARE));
		rewards.add(new RewardItem(4714, 1, 1, RewardLevel.RARE));
		return rewards;
	}

	@Override
	public int getHP() {
		return 100;
	}

	@Override
	public int getMaxHit() {
		return 25;
	}

	@Override
	public int getAttack() {
		return 225;
	}

	@Override
	public int getDefense() {
		return 190;
	}

	@Override
	public double getMeleeEffectiveness() {
		return 1.15;
	}

	@Override
	public double getRangeEffectiveness() {
		return 1.25;
	}

	@Override
	public double getMagicEffectiveness() {
		return 0.9;
	}

	@Override
	public Coordinate getSpawn() {
		return new Coordinate(3565, 3288, 0);
	}

}
