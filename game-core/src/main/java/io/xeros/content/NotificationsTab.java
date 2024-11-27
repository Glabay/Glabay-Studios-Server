package io.xeros.content;

import io.xeros.model.entity.player.Player;

public class NotificationsTab {

    private static final int INTERFACE_ID = 42_658;

    private final Player player;

    public NotificationsTab(Player player) {
        this.player = player;
    }

    private void openTab() {
        player.getPA().sendTabAreaOverlayInterface(INTERFACE_ID);
    }

    public boolean clickButton(int buttonId) {
        if (buttonId == 162_070) {
            openTab();
        }
        return false;
    }

}
