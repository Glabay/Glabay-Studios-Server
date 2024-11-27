package io.xeros.content.dialogue;

import java.util.function.Consumer;

import io.xeros.content.dialogue.types.ItemStatementDialogue;
import io.xeros.content.dialogue.types.MakeItemDialogue;
import io.xeros.content.dialogue.types.NpcDialogue;
import io.xeros.content.dialogue.types.OptionDialogue;
import io.xeros.content.dialogue.types.PlayerDialogue;
import io.xeros.content.dialogue.types.StatementDialogue;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;


/**
 * Builds a list of DialogueObjects.
 */
public class DialogueBuilder {

    public static final DialogueOption EXIT_DIALOGUE_OPTION = new DialogueOption("Exit", plr -> plr.getPA().closeAllWindows());

    private final Player player;
    private DialogueObject root;
    private DialogueObject current;
    private int npcId = -1;
    private DialogueExpression expression;
    private DialogueBuilder nextDialogue;

    public DialogueBuilder(Player player) {
        this.player = player;
    }

    /**
     * Send the first dialogue.
     */
    public void initialise() {
        if (root == null) throw new IllegalStateException("No dialogues!");
        current = root;
        beginNextDialogue();
    }

    /**
     * Remove all dialogue chains.
     */
    public DialogueBuilder reset() {
        root = null;
        current = null;
        expression = null;
        nextDialogue = null;
        return this;
    }

    /**
     * Close the dialogue.
     */
    public void end() {
        player.getPA().closeAllWindows();
        player.setDialogueBuilder(null);
    }

    public DialogueBuilder send() {
        player.start(this);
        return this;
    }

    /**
     * Sends the next dialogue if there is another dialogue,
     * if there's no dialogue it will exit.
     */
    public void sendNextDialogue() {
        if (!hasNext()) {
            if (player.getDialogueBuilder() == this) {
                if (nextDialogue == null) {
                    end();
                }

                if (current != null) {
                    current.acceptExitConsumers(player);
                }

                if (nextDialogue != null) { // Send after to prevent exit consumers from closing it
                    player.start(nextDialogue);
                }
            }
        } else {
            if (current != null) {
                current.acceptExitConsumers(player);
            }
            next();
            beginNextDialogue();
        }
    }

    /**
     * Return <code>true</code> if there is another dialogue.
     * @return
     *      <code>true</code> if there is another dialogue.
     */
    public boolean hasNext() {
        if (current == null) throw new IllegalStateException("Not started!");
        return current.getChild() != null;
    }

    /**
     * Queues up the next dialogue.
     * @return
     *      the next dialogue
     */
    private DialogueObject next() {
        if (!hasNext()) throw new IllegalStateException("No more dialogues to send!");
        current = current.getChild();
        return current;
    }

    private void beginNextDialogue() {
        current.acceptEntranceConsumers(player);
        current.send(player);
    }

    /**
     * Set a {@link DialogueBuilder} to be send next, this will prevent any
     * close interfaces packets to send at the end of this dialogue chain,
     * making the transition more smooth.
     * @param nextDialogue The next {@link DialogueBuilder} to start.
     * @return the current {@link DialogueBuilder}
     */
    public DialogueBuilder next(DialogueBuilder nextDialogue) {
        this.nextDialogue = nextDialogue;
        return this;
    }

    public void dispatchAction(DialogueAction action) {
        if (current != null) {
            current.handleAction(player, action);
        }
    }

    private DialogueObject atIndex(int index) {
        if (root == null) return null;

        DialogueObject it = null;
        for (int i = 0; i <= index; i++) {
            it = it == null ? root : it.getChild();
            if (it == null) throw new IllegalStateException(String.format("Invalid index %s", index));
            if (i == index) {
                return it;
            }
        }

        return null;
    }

    private DialogueObject getLast() {
        if (root == null) return null;
        DialogueObject it = root;
        while (it.getChild() != null) { it = it.getChild(); }
        return it;
    }

    /**
     * Inserts the next DialogueObject.
     * @param object
     *          the DialogueObject
     */
    public void add(DialogueObject object) {
        if (root == null) {
            root = object;
        } else {
            getLast().setChild(object);
        }
    }

    /**
     * Adds a consumer to the last created DialogueObject to be called when the dialogue is sent.
     * @param consumer
     *          the player consumer
     */
    public DialogueBuilder action(Consumer<Player> consumer) {
        DialogueObject last = getLast();
        if (last == null) throw new NullPointerException("No dialogue added yet.");
        last.addEntranceConsumer(consumer);
        return this;
    }

    /**
     * Set an action to be performed after a dialogue
     * @param consumer the consumer
     * @return the DialogueBuilder
     */
    public DialogueBuilder exit(Consumer<Player> consumer) {
        DialogueObject last = getLast();
        if (last == null) throw new NullPointerException("No dialogue added yet.");
        last.addExitConsumer(consumer);
        return this;
    }

    /**
     * Adds a consumer to the last created DialogueObject to be called when the dialogue is continued.
     * @param consumer
     *          the player consumer
     */
    public DialogueBuilder continueAction(Consumer<Player> consumer) {
        DialogueObject last = getLast();
        if (last == null) throw new NullPointerException("No dialogue added yet.");
        last.addExitConsumer(consumer);
        return this;
    }

    public DialogueBuilder jumpTo(int index) {
        DialogueObject atIndex = atIndex(index);
        if (atIndex == null)
            throw new NullPointerException();
        add(atIndex(index));
        return this;
    }

    public DialogueBuilder player(String...text) {
        return player(DialogueExpression.CALM, text);
    }

    public DialogueBuilder player(DialogueExpression expression, String...text) {
        PlayerDialogue dialogue = new PlayerDialogue(this, expression, text);
        add(dialogue);
        return this;
    }

    public DialogueBuilder npc(int npcId, DialogueExpression expression, String...text) {
        NpcDialogue dialogue = new NpcDialogue(this, npcId, expression, text);
        add(dialogue);
        return this;
    }

    public DialogueBuilder npc(DialogueExpression expression, String...text) {
        return npc(npcId, expression, text);
    }

    public DialogueBuilder npc(int npcId, String...text) {
        return npc(npcId, expression != null ? expression : DialogueExpression.SPEAKING_CALMLY, text);
    }

    public DialogueBuilder npc(String...text) {
        if (npcId == -1) throw new IllegalStateException("Npc id isn't set.");
       return npc(npcId, text);
    }

    public DialogueBuilder option(boolean closeOtherInterfaces, String title, DialogueOption...options) {
        OptionDialogue dialogue = new OptionDialogue(this, title, closeOtherInterfaces, options);
        add(dialogue);
        return this;
    }

    public DialogueBuilder option(String title, DialogueOption...options) {
       return option(true, title, options);
    }

    public DialogueBuilder option(DialogueOption...options) {
        return option("Select Option", options);
    }

    public DialogueBuilder confirmOption(boolean closeOtherInterfaces, String title, String confirm, Consumer<Player> acceptConsumer, Consumer<Player> declineConsumer) {
        return option(closeOtherInterfaces, title, new DialogueOption(confirm, acceptConsumer), new DialogueOption("Cancel", p -> { if (declineConsumer == null) end(); else declineConsumer.accept(p); }));
    }

    public DialogueBuilder confirmOption(String title, String confirm, Consumer<Player> acceptConsumer) {
        return confirmOption(true, title, confirm, acceptConsumer, null);
    }

    public DialogueBuilder statement(String...statement) {
        StatementDialogue dialogue = new StatementDialogue(this, statement);
        add(dialogue);
        return this;
    }

    public DialogueBuilder itemStatement(boolean closeOtherInterfaces, int itemId, int itemZoom, String header, String...text) {
        ItemStatementDialogue dialogue = new ItemStatementDialogue(this, itemId, itemZoom, header, text, closeOtherInterfaces);
        add(dialogue);
        return this;
    }

    public DialogueBuilder itemStatement(int itemId, int itemZoom, String header, String...text) {
        return itemStatement(true, itemId, itemZoom, header, text);
    }

    public DialogueBuilder itemStatement(String header, int itemId, String...text) {
        return itemStatement(true, itemId, 150, header, text);
    }

    public DialogueBuilder itemStatement(int itemId, String...text) {
        return itemStatement(true, itemId, 150, ItemDef.forId(itemId).getName(), text);
    }

    public DialogueBuilder makeItems(Consumer<MakeItemDialogue.PlayerMakeItem> consumer, MakeItemDialogue.MakeItem...makeItems) {
        return makeItems(150, consumer, makeItems);
    }

    public DialogueBuilder makeItems(int itemZoom, Consumer<MakeItemDialogue.PlayerMakeItem> consumer, MakeItemDialogue.MakeItem...makeItems) {
        return makeItems("How many would you like to make?", itemZoom, consumer, makeItems);
    }

    public DialogueBuilder makeItems(String dialogueHeader, int itemZoom, Consumer<MakeItemDialogue.PlayerMakeItem> consumer, MakeItemDialogue.MakeItem...makeItems) {
        MakeItemDialogue dialogue = new MakeItemDialogue(this, itemZoom, dialogueHeader, consumer, makeItems);
        add(dialogue);
        return this;
    }

    /**
     * Sets the npc id to be used for {@link DialogueBuilder#npc(String...)}.
     * @param npcId
     *          the npc id to set.
     * @return
     *      the DialogueBuilder
     */
    public DialogueBuilder setNpcId(int npcId) {
        this.npcId = npcId;
        return this;
    }

    /**
     * Sets the default {@link DialogueExpression} to be used.
     * @param expression
     *          the expression to be used
     * @return
     *      the DialogueBuilder
     */
    public DialogueBuilder setExpression(DialogueExpression expression) {
        this.expression = expression;
        return this;
    }

    /**
     * Gets the current DialogueObject.
     * @return
     *      the current DialogueObject.
     */
    public DialogueObject getCurrent() {
        return current;
    }

    public Player getPlayer() {
        return player;
    }
}
