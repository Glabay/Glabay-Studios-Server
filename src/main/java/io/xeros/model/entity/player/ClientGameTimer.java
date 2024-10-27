package io.xeros.model.entity.player;

import io.xeros.model.Items;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientGameTimer {
	VENGEANCE(0, true, false),
	OVERLOAD(1, true, false),
	ANTIFIRE(2, true, false),
	ANTIVENOM(3, true, false),
	ANTIPOISON(4, true, false),
	TELEBLOCK(5, true, false),
	STAMINA(6, true, false),
	FARMING(7, false, false),
	EXPERIENCE(8, false, false),
	PEST_CONTROL(9, false, false),
	DROPS(10, false, false),
	FREEZE(11, true, false),
	DIVINE_SUPER_COMBAT(Items.DIVINE_SUPER_COMBAT_POTION4, true, true),
	DIVINE_RANGE(Items.DIVINE_RANGING_POTION4, true, true),
	DIVINE_MAGIC(Items.DIVINE_MAGIC_POTION4, true, true),
	BONUS_XP(12, false, false),
	BONUS_SKILLING_PET_RATE(13, false, false),
	BONUS_CLUES(2722, false, true),
	;

	private final int timerId;
	private final boolean resetOnDeath;
	private final boolean item;
}
