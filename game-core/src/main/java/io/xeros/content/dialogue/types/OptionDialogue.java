package io.xeros.content.dialogue.types;

import io.xeros.content.dialogue.DialogueAction;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueObject;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.entity.player.Player;

public class OptionDialogue extends DialogueObject {

    /**
     * This array contains the child id where the dialogue
     * statement starts for option dialogues.
     */
    // Interface id, close sword child id, far sword child id
    private static final int[][] OPTION_DIALOGUE_ID = {
            { 13760, 13764, 13767 },
            { 2461, 2465, 2468 },
            { 2471, 2476, 2479 },
            { 2482, 2488, 2489 },
            { 2494, 2501, 2502 },
    };

    private static final DialogueAction[] OPTIONS = {
            DialogueAction.OPTION_1,
            DialogueAction.OPTION_2,
            DialogueAction.OPTION_3,
            DialogueAction.OPTION_4,
            DialogueAction.OPTION_5,
    };

    protected final String title;
    protected final DialogueOption[] options;
    protected final boolean closeInterfacesWhenOpened;

    public OptionDialogue(DialogueBuilder context, String title, boolean closeInterfacesWhenOpened, DialogueOption...options) {
        super(context, false);
        this.title = title;
        this.options = options;
        this.closeInterfacesWhenOpened = closeInterfacesWhenOpened;
    }

    @Override
    public void send(Player player) {
        int interfaceId = OPTION_DIALOGUE_ID[options.length - 1][0];
        int firstChildId = OPTION_DIALOGUE_ID[options.length - 1][1];
        int secondChildId = OPTION_DIALOGUE_ID[options.length - 1][2];
        boolean farSwords = title.length() > 14;

        if (farSwords) {
            player.getPA().sendInterfaceHidden(firstChildId, true).sendInterfaceHidden(secondChildId, false);
        } else {
            player.getPA().sendInterfaceHidden(firstChildId, false).sendInterfaceHidden(secondChildId, true);
        }

        player.getPA().sendString(interfaceId - 1, title);
        for (int i = 0; i < options.length; i++) {
            player.getPA().sendString(interfaceId + i, options[i].getTitle());
        }
        player.getPA().sendChatboxInterface(interfaceId - 2);

    }

    @Override
    public void handleAction(Player player, DialogueAction action) {
       for (int i = 0; i < OPTIONS.length; i++) {
           if (action == OPTIONS[i]) {
               option(player, i);
           }
       }
    }

    private void option(Player player, int index) {
        if (index >= 0 && index < options.length) {
            options[index].getConsumer().accept(player);
        }
    }

}
