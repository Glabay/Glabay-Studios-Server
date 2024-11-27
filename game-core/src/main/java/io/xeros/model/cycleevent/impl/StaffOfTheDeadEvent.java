package io.xeros.model.cycleevent.impl;

import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;

public class StaffOfTheDeadEvent extends Event<Player> {

	public StaffOfTheDeadEvent(Player attachment) {
		super("staff_of_the_dead", attachment, 1);
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.isDisconnected()) {
			super.stop();
			return;
		}
		if (attachment.playerEquipment[Player.playerWeapon] != 11791 && attachment.playerEquipment[Player.playerWeapon] != 12904 && attachment.playerEquipment[Player.playerWeapon] != 22296) {
			super.stop();
			return;
		}
		if (super.getElapsedTicks() > 100) {
			super.stop();
        }
	}

}
