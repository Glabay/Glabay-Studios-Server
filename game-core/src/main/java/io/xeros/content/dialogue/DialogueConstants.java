package io.xeros.content.dialogue;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class DialogueConstants {

    public static final List<DialogueActionButton> BUTTONS = Collections.unmodifiableList(Lists.newArrayList(
            new DialogueActionButton(DialogueAction.MAKE_1_SLOT_1, 34245, 34205, 34185, 34170, 10239),
            new DialogueActionButton(DialogueAction.MAKE_5_SLOT_1, 34244, 34204, 34184, 34169, 10238),
            new DialogueActionButton(DialogueAction.MAKE_ALL_SLOT_1, 34242, 34202, 34182, 34167, 6211),
            new DialogueActionButton(DialogueAction.MAKE_X_SLOT_1, 34243, 34203, 34183, 34168, 6212),

            new DialogueActionButton(DialogueAction.MAKE_1_SLOT_2, 34249, 34209, 34189, 34174),
            new DialogueActionButton(DialogueAction.MAKE_5_SLOT_2, 34248, 34208, 34188, 34173),

            new DialogueActionButton(DialogueAction.MAKE_ALL_SLOT_2, 34246, 34206, 34186, 34171),
            new DialogueActionButton(DialogueAction.MAKE_X_SLOT_2, 34247, 34207, 34187, 34172),

            new DialogueActionButton(DialogueAction.MAKE_1_SLOT_3, 34253, 34213, 34193),
            new DialogueActionButton(DialogueAction.MAKE_5_SLOT_3, 34252, 34212, 34192),

            new DialogueActionButton(DialogueAction.MAKE_ALL_SLOT_3, 34250, 34210, 34190),
            new DialogueActionButton(DialogueAction.MAKE_X_SLOT_3, 34251, 34211, 34191),

            new DialogueActionButton(DialogueAction.MAKE_1_SLOT_4, 35001, 34217),
            new DialogueActionButton(DialogueAction.MAKE_5_SLOT_4, 35000, 34216),

            new DialogueActionButton(DialogueAction.MAKE_ALL_SLOT_4, 34254, 34214),
            new DialogueActionButton(DialogueAction.MAKE_X_SLOT_4, 34255, 34215),

            new DialogueActionButton(DialogueAction.MAKE_1_SLOT_5, 35005),
            new DialogueActionButton(DialogueAction.MAKE_5_SLOT_5, 35004),
            new DialogueActionButton(DialogueAction.MAKE_ALL_SLOT_5, 35002),
            new DialogueActionButton(DialogueAction.MAKE_X_SLOT_5, 35003)
    ));

}
