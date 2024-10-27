package io.xeros.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.logging.player.ConnectionLog;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger activeConnections = new AtomicInteger();
    private static final Logger logger = LoggerFactory.getLogger(ChannelHandler.class);

    public static int getActiveConnections() {
        return activeConnections.get();
    }

    public static void incrementActiveConnections() {
        activeConnections.getAndIncrement();
    }

    private Session session;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (cause != null) {
//            if (cause.getMessage() != null) {
//                String message = cause.getMessage().toLowerCase();
//                if (message.equals("connection reset by peer") || message.contains("forcibly closed"))
//                    return;
//            }
//
//            if (cause instanceof ReadTimeoutException)
//                return;
//
//            logger.error("Error received in channel", e.getCause());
//        }
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object e) {
        try {
            if (e instanceof Player) {
                session.setClient((Player) e);
                PlayerHandler.addLoginQueue(session.getClient());
            }
            else if (e instanceof Packet) {
                if (session == null)
                    return;

                var client = session.getClient();
                if (client != null) {
                    if (client.getPacketsReceived() >= Configuration.MAX_PACKETS_PROCESSED_PER_CYCLE) {
                        int attempted = client.attemptedPackets.incrementAndGet();
                        if (attempted > Configuration.KICK_PLAYER_AFTER_PACKETS_PER_CYCLE) {
                            logger.info("Disconnecting user: {} for sending {} packets.", client, attempted);
                            client.getSession().disconnect();
                        }
                        return;
                    }

                    Packet message = (Packet) e;
                    int packetOpcode = message.getOpcode();

                    boolean isPriorityPacket = Packet.isPriorityPacket(packetOpcode);
                    client.queueMessage(message, isPriorityPacket);

                    while (client.getPreviousPackets().size() > 50)
                        client.getPreviousPackets().poll();

                    if (message.getOpcode() != 0)
                        client.getPreviousPackets().add(message.getOpcode());
                }
            }
        } catch (Exception ex) {
            logger.error("Exception while receiving message", ex);
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {
        if (session == null) {
            session = new Session(ctx.channel());
        }
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        try {
            if (session != null) {
                Player player = session.getClient();
                if (player != null) {
                    player.setDisconnected();
                }
                session = null;
            }
        } catch (Exception ex) {
            logger.error("Exception during xlog", ex);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        activeConnections.decrementAndGet();
    }
}
