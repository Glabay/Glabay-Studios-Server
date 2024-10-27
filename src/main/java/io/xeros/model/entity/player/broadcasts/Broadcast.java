package io.xeros.model.entity.player.broadcasts;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.entity.player.Position;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ynneh
 */
public class Broadcast {

    @Getter
    private String url;

    @Getter
    private Position teleport;

    @Getter
    private String message;

    @Getter
    private BroadcastType type;

    @Getter
    @Setter
    public int index;

    public boolean sendChatMessage;

    public Broadcast(String message) {
        this.message = message;
    }

    public Broadcast addLink(String url) {
        this.url = url;
        return this;
    }

    public Broadcast addTeleport(Position teleport) {
        this.teleport = teleport;
        return this;
    }

    public Broadcast copyMessageToChatbox() {
        this.sendChatMessage = true;
        return this;
    }

    public Broadcast addDialogue(Class<DialogueBuilder> dialogue) {
        //TODO
        return this;
    }

    public Broadcast submit() {
        type = teleport != null ? BroadcastType.TELEPORT : url != null ? BroadcastType.LINK : BroadcastType.MESSAGE;
        BroadcastManager.addIndex(this);
        return this;
    }
}
