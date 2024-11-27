package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;

/**
 * Packet interface.
 * 
 * @author Graham
 * 
 */
public interface Packet {

	void handlePacket(Player client, int packetType, int packetSize);

}
