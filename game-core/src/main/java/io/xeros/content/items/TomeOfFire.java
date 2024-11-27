package io.xeros.content.items;

import io.xeros.model.entity.player.Player;

public class TomeOfFire {

    public static final int TOME_EMPTY = 20716;
    public static final int TOME = 20714;
    public static final int PAGE = 20718;

    public static void store(Player player) {
        int pageAmount = player.getItems().getItemAmount(PAGE);

        if (pageAmount == 0) {
            player.sendMessage("You don't have any pages to store.");
            player.getPA().removeAllWindows();
            return;
        }

        int space = 1000 - player.getTomeOfFire().getPages();

        if (space == 0) {
            player.sendMessage("@blu@You have reached the maximum charges in the tome of fire.");
            player.getPA().removeAllWindows();
            return;
        }

        int pagesToAdd = Math.min(space, pageAmount);
        int chargesToAdd = pagesToAdd * 20;

        player.sendMessage("@blu@You stored @red@" + chargesToAdd + "@blu@ charges in your tome of fire.");
        player.getTomeOfFire().addPages(pagesToAdd);
        player.getItems().deleteItem(PAGE, pagesToAdd);

        if (player.getItems().playerHasItem(TOME_EMPTY)) {
            player.getItems().deleteItem(TOME_EMPTY, 1);
            player.getItems().addItem(TOME, 1);
        }
    }

    public static void remove(Player c) {
        if (c.getItems().freeSlots() < 1) {
            c.sendMessage("You need at least 1 inventory space to do this.");
            c.getPA().removeAllWindows();
            return;
        }

        int pages = c.getTomeOfFire().getPages();

        if (pages > 0) {
            c.getItems().addItem(PAGE, pages);
            c.sendMessage("@blu@You have removed all of your pages from your tome of fire.");
            c.getTomeOfFire().reset();
        }

        if (c.getItems().playerHasItem(TOME)) {
            c.getItems().deleteItem(TOME, 1);
            c.getItems().addItem(TOME_EMPTY, 1);
        }
        c.getPA().removeAllWindows();
    }

}
