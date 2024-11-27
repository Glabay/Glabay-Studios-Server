package io.xeros.content.dialogue;

public class DialogueActionButton {

    private final DialogueAction action;
    private final int[] buttonIds;

    public DialogueActionButton(DialogueAction action, int...buttonIds) {
        this.action = action;
        this.buttonIds = buttonIds;
    }

    public DialogueAction getAction() {
        return action;
    }

    public int[] getButtonIds() {
        return buttonIds.clone();
    }
}
