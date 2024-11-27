package io.xeros.content.minigames.raids;

import java.util.Arrays;
import java.util.Optional;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public enum RaidMonsters {
	
	LIZARD_SHAMAN	(new Boundary(3299, 5251, 3324, 5278), 7573),
	VASA_NISTIRIO	(new Boundary(3299, 5279, 3326, 5309), 7566),
	VANGUARD		(new Boundary(3304, 5320, 3319, 5340), 7527, 7528, 7529),
	ICE_DEMON		(new Boundary(3303, 5356, 3318, 5369), 7585),
	SKELETAL_MYSTIC	(new Boundary(3298, 5248, 3325, 5275), 7604, 7605, 7606),
	TEKTON			(new Boundary(3301, 5283, 3322, 5305), 7544),
	MUTTADILE		(new Boundary(3297, 5316, 3325, 5339), 7563),
	DEATHLY_RANGER	(new Boundary(3298, 5350, 3325, 5372), 7559),
	DEATHLY_MAGER	(new Boundary(3298, 5350, 3325, 5372), 7560),
	lEFT_CLAW		(new Boundary(3226, 5729, 3247, 5749), 7553),
	RIGHT_CLAW		(new Boundary(3226, 5729, 3247, 5749), 7555),
	GREAT_OLM		(new Boundary(3226, 5729, 3247, 5749), 7554);
	
	private final int[] possibleIds;
	private final Boundary bounds;
	
	RaidMonsters(Boundary boundary, int...possibleIds) {
		this.possibleIds = possibleIds;
		this.bounds = boundary;
	}
	
	public Boundary getBoundary() {
		return bounds;
	}
	
	public boolean isMatch(int npcId) {
		return Arrays.stream(possibleIds).filter(id -> npcId == id).findFirst().isPresent();
	}
	
	public int[] getIds() {
		return possibleIds;
	}
	
	public boolean isInBounds(Player p) {
		return p.getX() >= bounds.getMinimumX()
					&& p.getY() >= bounds.getMinimumY()
					&& p.getX() <= bounds.getMaximumX()
					&& p.getY() <= bounds.getMaximumY();
	}
	
	public static RaidMonsters getMonster(Player player) {
		Optional<RaidMonsters> monster = Arrays.stream(values()).filter(rm -> rm.isInBounds(player)).findFirst();
		return monster.isPresent() ? monster.get() : null;
	}
	
	public static RaidMonsters getMonsterById(int npcId) {
		Optional<RaidMonsters> monster = Arrays.stream(values()).filter(rm -> rm.isMatch(npcId)).findFirst();
		return monster.isPresent() ? monster.get() : null;
	}
}