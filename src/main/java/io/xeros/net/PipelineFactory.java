package io.xeros.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.xeros.net.login.RS2Encoder;
import io.xeros.net.login.RS2LoginProtocol;

public class PipelineFactory extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel socketChannel) {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast("filter", new LoginLimitFilter());
		pipeline.addLast("channel_traffic", new ChannelTrafficShapingHandler(0, 1024 * 5, 1000));
		pipeline.addLast("timeout", new IdleStateHandler(10, 0, 0));
		pipeline.addLast("encoder", new RS2Encoder());
		pipeline.addLast("decoder", new RS2LoginProtocol());
		pipeline.addLast("handler", new ChannelHandler());
	}
}
