package io.xeros.net.packets;

import io.xeros.model.entity.grounditem.GroundItemManager;
import io.xeros.model.entity.player.Player;

public class MapRegionChange implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		GroundItemManager.INSTANCE.onRegionChange(c);
	}

}
