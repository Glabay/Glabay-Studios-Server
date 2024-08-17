package io.xeros.net.packets;

import io.xeros.model.ContainerAction;
import io.xeros.model.ContainerActionType;
import io.xeros.model.entity.player.Player;

public class ContainerAction6 implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		player.interruptActions();
		int slot = player.getInStream().readUnsignedWordA();
		int component = player.getInStream().readUnsignedWord();
		int item = player.getInStream().readUnsignedWordA();
		int amount = player.getInStream().readInteger();

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_6, component, item, slot, amount);
		
		if (player.debugMessage)
			player.sendMessage("ContainerAction6: interfaceid: "+component+", removeSlot: "+slot+", removeID: " + item);
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		if (amount <= 0)
			return;

		if (player.getBank().withdraw(component, item, amount)) {
			return;
		}
	}

}
