package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface PacketType {
	Logger logger = LoggerFactory.getLogger(PacketType.class);
	void processPacket(Player c, int packetType, int packetSize);
}
