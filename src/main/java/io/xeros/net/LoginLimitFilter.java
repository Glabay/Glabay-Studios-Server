package io.xeros.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.net.login.LoginRequestLimit;
import io.xeros.net.login.RS2LoginProtocol;
import io.xeros.util.logging.global.LoginRequestLog;

public class LoginLimitFilter extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        ChannelHandler.incrementActiveConnections();
        String ipAddress = RS2LoginProtocol.getIP(ctx);
        if (LoginRequestLimit.timedOutInvalidLoginRequest(ipAddress)) {
            ctx.close();
            Server.getLogging().batchWrite(new LoginRequestLog(ipAddress, "Invalid login timeout deny"));
            return;
        }

        if (!Configuration.DISABLE_CONNECTION_REQUEST_LIMIT) {
            if (LoginRequestLimit.rejectConnectionRequest()) {
                Server.getLogging().batchWrite(new LoginRequestLog(ipAddress, "Rate limit deny"));
                ctx.close();
                return;
            }

            LoginRequestLimit.addRequest();
        }
    }

}
