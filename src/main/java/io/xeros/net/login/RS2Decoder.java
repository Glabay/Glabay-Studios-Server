package io.xeros.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.net.packets.PacketHandler;
import io.xeros.net.Packet;
import io.xeros.util.ISAACCipher;
import io.xeros.util.logging.global.IncomingPacketLog;

import java.util.List;

public class RS2Decoder extends ByteToMessageDecoder {

	private final ISAACCipher cipher;

	private int opcode = -1;
	private int size = -1;

	public RS2Decoder(ISAACCipher cipher) {
		this.cipher = cipher;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
		if (opcode == -1) {
			if (buffer.readableBytes() >= 1) {
				opcode = buffer.readByte() & 0xFF;
				opcode = (opcode - cipher.getNextValue()) & 0xFF;

				//size = PacketHandler.PACKET_SIZES[opcode];
				size = PacketHandler.getPacketSize(opcode);
				if (size == PacketHandler.OPCODE_OUT_OF_RANGE_SIZE) {
					opcode = -1;
					size = -1;
					return;
				}
			} else {
				return;
			}
		}
		if (size == -1) {
			if (buffer.readableBytes() >= 1) {
				size = buffer.readByte() & 0xFF;
			} else {
				return;
			}
		}
		if (buffer.readableBytes() >= size) {
			final byte[] data = new byte[size];
			buffer.readBytes(data);
			final ByteBuf payload = Unpooled.buffer(size);
			payload.writeBytes(data);
			try {
				if (!Configuration.DISABLE_PACKET_LOG)
					Server.getLogging().batchWrite(new IncomingPacketLog(ctx.channel(), opcode, size));
				list.add(new Packet(opcode, Packet.Type.FIXED, payload));
				return;
			} finally {
				opcode = -1;
				size = -1;
			}
		}
		return;
	}
}
