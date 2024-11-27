package io.xeros.net;

import io.netty.channel.Channel;
import io.xeros.model.entity.player.Player;
import lombok.Getter;
import lombok.Setter;

public class Session {

	private final Channel channel;

	@Setter
    @Getter
    private Player client;

	public Session(Channel channel) {
		this.channel = channel;
	}

}
