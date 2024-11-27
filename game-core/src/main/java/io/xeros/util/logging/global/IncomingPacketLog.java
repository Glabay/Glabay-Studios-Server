package io.xeros.util.logging.global;

import io.netty.channel.Channel;
import io.xeros.net.packets.PacketHandler;
import io.xeros.util.Misc;
import io.xeros.util.logging.GlobalLog;

import java.net.InetSocketAddress;
import java.util.Set;

public class IncomingPacketLog extends GlobalLog {

    private final String ip;
    private final int opcode;
    private final int size;

    public IncomingPacketLog(Channel channel, int opcode, int size) {
        this.ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        this.opcode = opcode;
        this.size = size;
    }

    @Override
    public String getLoggedMessage() {
        return Misc.replaceBracketsWithArguments("opcode={}, size={}, standardSize={}, ip={}", opcode, size, PacketHandler.getPacketSize(opcode), ip);
    }

    @Override
    public Set<String> getFileNames() {
        return Set.of("packets");
    }
}
