package io.xeros.content.minigames.barrows.brothers;

import java.util.ArrayList;

import io.xeros.content.minigames.barrows.RewardItem;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Coordinate;
import io.xeros.model.entity.player.Player;

public abstract class Brother {

	public static final int AHRIM = 1672;
	public static final int DHAROK = 1673;
	public static final int GUTHAN = 1674;
	public static final int KARIL = 1675;
	public static final int TORAG = 1676;
	public static final int VERAC = 1677;

	protected Player player;

	private boolean active;
	private boolean defeated;
	private boolean finalBrother;

	private NPC npc;

	public Brother(Player player) {
		this.player = player;
	}

	public void handleSpawn() {
		if (finalBrother) {
			player.getDH().sendDialogues(2900, 2026);
		} else {
			spawnBrother();
		}
	}

	public void spawnBrother() {
		if (defeated) {
			if (finalBrother) {
				player.sendMessage("Something went wrong with the final Barrows brother. Please report this issue on the forums.");
				return;
			} else {
				player.sendMessage("You have already searched this sarcophagus.");
				return;
			}
		}
		if (active) {
			player.sendMessage("You are already fighting a brother.");
			return;
		}
		active = true;
		if (finalBrother) {
			npc = NPCSpawning.spawnNpcOld(player, getId(), 3565, 3289, 0, 0, getHP(), getMaxHit(), getAttack(), getDefense(), true, true);
		} else {
			npc = NPCSpawning.spawnNpcOld(player, getId(), getSpawn().getX(), getSpawn().getY(), player.getHeight(), 0, getHP(), getMaxHit(), getAttack(), getDefense(), true,
					true);
		}
		if (npc != null) {
			npc.forceChat("You dare disturb my rest!");
		}
	}

	public boolean digDown() {
		if (Boundary.isIn(player, getMoundBoundary())) {
			player.getPA().movePlayer(getStairsLocation());
			return true;
		}
		return false;
	}

	public void moveUpStairs() {
		player.getPA().movePlayer(Boundary.centre(getMoundBoundary()));
	}

	public boolean isActive() {
		return active;
	}

	public boolean isDefeated() {
		return defeated;
	}

	public boolean isFinal() {
		return finalBrother;
	}

	public void setFinalBrother(boolean finalBrother) {
		this.finalBrother = finalBrother;
	}

	public abstract int getId();

	public abstract Boundary getMoundBoundary();

	public abstract int getStairsId();

	public abstract int getFrameId();

	public abstract Coordinate getStairsLocation();

	public abstract int getCoffinId();

	public abstract Coordinate getSpawn();

	public abstract String getName();

	public abstract ArrayList<RewardItem> getRewards();

	public abstract int getHP();

	public abstract int getMaxHit();

	public abstract int getAttack();

	public abstract int getDefense();

	public abstract double getMeleeEffectiveness();

	public abstract double getRangeEffectiveness();

	public abstract double getMagicEffectiveness();

}
