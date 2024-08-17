package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;

public interface PacketType {
	void processPacket(Player c, int packetType, int packetSize);
}
