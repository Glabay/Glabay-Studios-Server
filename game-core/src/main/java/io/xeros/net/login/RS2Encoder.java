package io.xeros.net.login;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.xeros.net.Packet;

import java.util.List;

public class RS2Encoder extends MessageToMessageEncoder<Packet> {
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {
		list.add(packet.getPayload());
	}
}
