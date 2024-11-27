package io.xeros.content.dialogue.types;

import io.xeros.content.dialogue.DialogueAction;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.dialogue.DialogueObject;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.Player;

public class NpcDialogueNoContinue extends DialogueObject {

    private final int npcId;
    private final String[] text;
    private final DialogueExpression expression;

    public NpcDialogueNoContinue(DialogueBuilder context, int npcId, DialogueExpression expression, String[] text) {
        super(context, false);
        this.npcId = npcId;
        this.text = text;
        this.expression = expression;
    }

    @Override
    public void send(Player player) {
        switch (text.length) {
            case 2:
                player.getPA().sendNpcHeadOnInterface(npcId, 12379);
                player.getPA().sendInterfaceAnimation(12379, expression.getAnimation());
                player.getPA().sendString(12380,  NpcDef.forId(npcId).getName());
                player.getPA().sendString(12381, text[0]);
                player.getPA().sendString(12382, text[1]);
                player.getPA().sendChatboxInterface(12378);
                break;
            case 3:
                player.getPA().sendNpcHeadOnInterface(npcId, 12384);
                player.getPA().sendInterfaceAnimation(12384, expression.getAnimation());
                player.getPA().sendString(12385, NpcDef.forId(npcId).getName());
                player.getPA().sendString(12386, text[0]);
                player.getPA().sendString(12387, text[1]);
                player.getPA().sendString(12388, text[2]);
                player.getPA().sendChatboxInterface(12383);
                break;
            case 4:
                player.getPA().sendNpcHeadOnInterface(npcId, 11892);
                player.getPA().sendInterfaceAnimation(11892, expression.getAnimation());
                player.getPA().sendString(11893, NpcDef.forId(npcId).getName());
                player.getPA().sendString(11894, text[0]);
                player.getPA().sendString(11895, text[1]);
                player.getPA().sendString(11896, text[2]);
                player.getPA().sendString(11897, text[3]);
                player.getPA().sendChatboxInterface(11891);
        }
    }

    @Override
    public void handleAction(Player player, DialogueAction action) {
        if (action == DialogueAction.CLICK_TO_CONTINUE) {
            getContext().sendNextDialogue();
        }
    }

}
