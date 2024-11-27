package io.xeros.content.dialogue.types;

import io.xeros.content.dialogue.DialogueAction;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueObject;
import io.xeros.model.entity.player.Player;

public class StatementDialogue extends DialogueObject {

    private String[] statement;

    public StatementDialogue(DialogueBuilder context, String...statement) {
        super(context, true);
        this.statement = statement;
    }

    @Override
    public void send(Player player) {
        switch (statement.length) {
            case 1:
                player.getPA().sendString(statement[0], 357);
                player.getPA().sendChatboxInterface(356);
                break;
            case 2:
                player.getPA().sendString(statement[0], 360);
                player.getPA().sendString(statement[1], 361);
                player.getPA().sendChatboxInterface(359);
                break;
            case 3:
                player.getPA().sendString(statement[0], 364);
                player.getPA().sendString(statement[1], 365);
                player.getPA().sendString(statement[2], 366);
                player.getPA().sendChatboxInterface(363);
                break;
            case 4:
                player.getPA().sendString(statement[0], 369);
                player.getPA().sendString(statement[1], 370);
                player.getPA().sendString(statement[2], 371);
                player.getPA().sendString(statement[3], 372);
                player.getPA().sendChatboxInterface(368);
                break;
            case 5:
                player.getPA().sendString(statement[0], 375);
                player.getPA().sendString(statement[1], 376);
                player.getPA().sendString(statement[2], 377);
                player.getPA().sendString(statement[3], 378);
                player.getPA().sendString(statement[4], 379);
                player.getPA().sendChatboxInterface(374);
                break;
            default:
                throw new IllegalArgumentException("Invalid length: " + statement.length);
        }
    }

    @Override
    public void handleAction(Player player, DialogueAction action) {
        if (action == DialogueAction.CLICK_TO_CONTINUE) {
            getContext().sendNextDialogue();
        }
    }
}
