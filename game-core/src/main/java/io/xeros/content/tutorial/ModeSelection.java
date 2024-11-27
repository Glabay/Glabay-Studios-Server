package io.xeros.content.tutorial;

import java.util.Arrays;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ModeType;

public class ModeSelection {

    private static final int CONFIRM_BUTTON = 94_242;
    private static final int SCROLLABLE_CONTAINER = 24_308;
    public static final int INTERFACE_ID = 24303;
    private static final int GAME_MODE_SETUP_SELECTION_CONFIG = 1372;

    private final Player player;
    private ModeType modeType = ModeType.STANDARD;


    public ModeSelection(Player player) {
        this.player = player;
    }

    public void openInterface() {
        modeType = ModeType.STANDARD;
        player.getPA().sendConfig(GAME_MODE_SETUP_SELECTION_CONFIG, 0);
        player.getPA().resetScrollBar(SCROLLABLE_CONTAINER);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public boolean clickButton(int buttonId) {
        if (!player.isInterfaceOpen(INTERFACE_ID))
            return false;
        if (buttonId == CONFIRM_BUTTON) {
            TutorialDialogue.selectedMode(player, modeType);
            return true;
        }

        Optional<Button> button = Arrays.stream(Button.values()).filter(button2 -> button2.buttonId == buttonId).findFirst();
        button.ifPresent(this::select);
        return button.isPresent();
    }

    private void select(Button button) {
        player.getPA().sendConfig(GAME_MODE_SETUP_SELECTION_CONFIG, button.ordinal());
        modeType = button.type;
    }

    private enum Button {
        STANDARD(ModeType.STANDARD, 94_245),
        ROGUE(ModeType.ROGUE, 94_248),
        IRONMAN(ModeType.IRON_MAN, 94_251),
        HARDCORE_IRONMAN(ModeType.HC_IRON_MAN, 94_254),
        ULTIMATE_IRONMAN(ModeType.ULTIMATE_IRON_MAN, 95_001),
        ROGUE_HARDCORE_IRONMAN(ModeType.ROGUE_HARDCORE_IRONMAN, 95_004),
        GROUP_IRONMAN(ModeType.GROUP_IRONMAN, 95_007),
        ;

        private final ModeType type;
        private final int buttonId;

        Button(ModeType type, int buttonId) {
            this.type = type;
            this.buttonId = buttonId;
        }
    }
}
