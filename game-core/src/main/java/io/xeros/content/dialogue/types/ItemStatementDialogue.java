package io.xeros.content.dialogue.types;

import com.google.common.base.Preconditions;
import io.xeros.content.dialogue.DialogueAction;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueObject;
import io.xeros.model.entity.player.Player;

public class ItemStatementDialogue extends DialogueObject {

    /**
     * This array contains the child id where the dialogue
     * statement starts for npc and item dialogues.
     */
    private static final int[] NPC_DIALOGUE_ID = {
            4885,
            4890,
            4896,
            4903
    };

    private final int itemId;
    private final int itemZoom;
    private final String itemName;
    private final String[] statement;
    private final boolean closeOtherInterfaces;

    public ItemStatementDialogue(DialogueBuilder context, int itemId, int itemZoom, String itemName, String[] statement, boolean closeOtherInterfaces) {
        super(context, true);
        this.itemId = itemId;
        this.itemZoom = itemZoom;
        this.itemName = itemName;
        this.statement = statement;
        this.closeOtherInterfaces = closeOtherInterfaces;
        Preconditions.checkArgument(statement.length <= 2, "You can only have up to two lines of dialogue on an item statement.");
    }

    @Override
    public void send(Player player) {
        if (statement.length == 2) {
            player.getPA().sendString(6232, statement[0]);
            player.getPA().sendString(6233, statement.length > 1 ? statement[1] : "");
            player.getPA().sendItemOnInterface2(6235, itemZoom, 65_535);
            player.getPA().sendItemOnInterface2(6236, itemZoom, itemId);
            player.getPA().sendChatboxInterface(6231);
        } else {
            player.getPA().sendString(308, statement[0]);
            player.getPA().sendItemOnInterface2(307, itemZoom, itemId);
            player.getPA().sendChatboxInterface(306);
        }
    }

    @Override
    public void handleAction(Player player, DialogueAction action) {
        if (action == DialogueAction.CLICK_TO_CONTINUE) {
            getContext().sendNextDialogue();
        }
    }
}
