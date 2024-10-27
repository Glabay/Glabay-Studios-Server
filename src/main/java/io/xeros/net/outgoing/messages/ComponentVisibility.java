package io.xeros.net.outgoing.messages;

import io.xeros.net.outgoing.PacketMessage;
import lombok.Getter;

/**
 * @author Jason MacKeigan
 * @date Nov 15, 2014, 3:14:01 AM
 */
@Getter
public class ComponentVisibility implements PacketMessage<ComponentVisibility> {

	private final int state;

	private final int componentId;

	public ComponentVisibility(int state, int componentId) {
		this.state = state;
		this.componentId = componentId;
	}

    @Override
	public boolean matches(ComponentVisibility message) {
		return message.componentId == componentId;
	}

	@Override
	public boolean requiresUpdate(ComponentVisibility message) {
		return message.state != state;
	}
}
