package io.xeros.content.dialogue;

import java.util.function.Consumer;

import io.xeros.model.entity.player.Player;

public class DialogueOption {

    public static DialogueOption nevermind() {
        return new DialogueOption("Never mind.", plr -> plr.getPA().closeAllWindows());
    }

    private final String title;
    private final Consumer<Player> consumer;

    public DialogueOption(String title, Consumer<Player> consumer) {
        this.title = title;
        this.consumer = consumer;
    }

    public String getTitle() {
        return title;
    }

    public Consumer<Player> getConsumer() {
        return consumer;
    }
}
