package io.xeros.content.dialogue;

import java.util.ArrayList;
import java.util.function.Consumer;

import io.xeros.model.entity.player.Player;

/**
 * A piece of dialogue.
 */
public abstract class DialogueObject {

    /**
     * The DialogueBuilder context.
     */
    private final DialogueBuilder context;

    /**
     * The child.
     */
    private DialogueObject child;

    /**
     * The actions called when entering the dialogue.
     */
    private ArrayList<Consumer<Player>> entranceConsumers = new ArrayList<>();

    /**
     * The actions called when entering the dialogue.
     */
    private ArrayList<Consumer<Player>> exitConsumers = new ArrayList<>();

    private final boolean continuable;

    /**
     * Creates a DialogueObject.
     */
    public DialogueObject(DialogueBuilder context, boolean continuable) {
        this.context = context;
        this.continuable = continuable;
    }

    /**
     * Send the dialogue display.
     */
    public abstract void send(Player player);

    /**
     * Handle the player's action on the dialogue.
     * @param player
     *          the player
     * @param action
     *          the action
     */
    public abstract void handleAction(Player player, DialogueAction action);

    /**
     * Adds a consumer to be called when the dialogue is sent.
     * @param consumer
     *          the Player consumer
     * @return
     *          this object
     */
    public DialogueObject addEntranceConsumer(Consumer<Player> consumer) {
        entranceConsumers.add(consumer);
        return this;
    }

    /**
     * Called when the dialogue is sent.
     * @param player
     *          the player
     */
    protected void acceptEntranceConsumers(Player player) {
        for (Consumer<Player> consumer : entranceConsumers) {
            consumer.accept(player);
        }
    }

    /**
     * Adds a consumer to be called when the dialogue is continued.
     * @param consumer
     *          the Player consumer
     * @return
     *          this object
     */
    public DialogueObject addExitConsumer(Consumer<Player> consumer) {
        exitConsumers.add(consumer);
        return this;
    }

    /**
     * Called when the dialogue is continued.
     * @param player
     *          the player
     */
    protected void acceptExitConsumers(Player player) {
        for (Consumer<Player> consumer : exitConsumers) {
            consumer.accept(player);
        }
    }

    /**
     * The DialogueBuilder context.
     * @return
     *      DialogueBuilder context
     */
    public DialogueBuilder getContext() {
        return context;
    }

    /**
     * Gets the child object.
     * @return
     *      child DialogueObject
     */
    public DialogueObject getChild() {
        return child;
    }

    /**
     * Set the child object.
     * @param child
     *          child DialogueObject
     */
    protected void setChild(DialogueObject child) {
        this.child = child;
    }

    public boolean isContinuable() {
        return continuable;
    }
}
