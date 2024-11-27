package io.xeros.content.item.lootable;

import java.util.List;
import java.util.Random;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public abstract class MysteryBoxLootable implements Lootable {

    public abstract int getItemId();

    /**
     * The player object that will be triggering this event
     */
    private final Player player;

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MysteryBoxLootable(Player player) {
        this.player = player;
    }

    /**
     * Can the player open the mystery box
     */
    public boolean canMysteryBox = true;

    /**
     * The prize received
     */
    private int mysteryPrize;

    private int mysteryAmount;

    private int spinNum;

    /**
     * The chance to obtain the item
     */
    private int random;
    private boolean active;
    private final int INTERFACE_ID = 47000;
    private final int ITEM_FRAME = 47101;

    public boolean isActive() {
        return active;
    }

    public void draw() {
        openInterface();
        if (spinNum == 0) {
            for (int i=0; i<66; i++){
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                GameItem NotPrize = Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity()));
                final int NOT_PRIZE_ID = NotPrize.id();
                sendItem(i, 55, mysteryPrize, NOT_PRIZE_ID,1);
            }
        } else {
            for (int i=spinNum*50 + 16; i<spinNum*50 + 66; i++){
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                final int NOT_PRIZE_ID = Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity())).id();
                sendItem(i, (spinNum+1)*50 + 5, mysteryPrize, NOT_PRIZE_ID, mysteryAmount);
            }
        }
        spinNum++;
    }

    public void spin() {

        // Server side checks for spin
        if (!canMysteryBox) {
            player.sendMessage("Please finish your current spin.");
            return;
        }
        if (!player.getItems().playerHasItem(getItemId())) {
            player.sendMessage("You require a mystery box to do this.");
            return;
        }

        // Delete box
        player.getItems().deleteItem(getItemId(), 1);
        // Initiate spin
        player.sendMessage(":resetBox");
        for (int i=0; i<66; i++){
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }
        spinNum = 0;
        player.sendMessage(":spin");
        process();
    }

    public void process() {
        mysteryPrize = -1;
        mysteryAmount = -1;
        canMysteryBox = false;
        active = true;
        setMysteryPrize();

        // Send items to interface
        // Move non-prize items client side if you would like to reduce server load
        if (spinNum == 0) {
            for (int i=0; i<66; i++){
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                GameItem NotPrize =Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity()));
                final int NOT_PRIZE_ID = NotPrize.id();
                sendItem(i, 55, mysteryPrize, NOT_PRIZE_ID,1);
            }
        } else {
            for (int i=spinNum*50 + 16; i<spinNum*50 + 66; i++){
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                final int NOT_PRIZE_ID = Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity())).id();
                sendItem(i, (spinNum+1)*50 + 5, mysteryPrize, NOT_PRIZE_ID, mysteryAmount);
            }
        }

        spinNum++;
    }

    public void setMysteryPrize() {
        random = Misc.random(100);
        List<GameItem> itemList = random < 50 ? getLoot().get(MysteryBoxRarity.COMMON.getLootRarity()) : random >= 50
                && random <= 85 ? getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity())
                : getLoot().get(MysteryBoxRarity.RARE.getLootRarity());
        GameItem item = Misc.getRandomItem(itemList);
        mysteryPrize = item.id();
        mysteryAmount = item.amount();

    }

    public void sendItem(int i, int prizeSlot, int PRIZE_ID, int NOT_PRIZE_ID, int amount) {
        if (i == prizeSlot) {
            player.getPA().mysteryBoxItemOnInterface(PRIZE_ID, amount, ITEM_FRAME, i);
        }
        else {
            player.getPA().mysteryBoxItemOnInterface(NOT_PRIZE_ID, amount, ITEM_FRAME, i);
        }
    }

    public void openInterface() {
        player.boxCurrentlyUsing = getItemId();
        for (int i = 0; i < 66; i++){
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }
        spinNum = 0;
        player.getPA().sendString(ItemDef.forId(getItemId()).getName(), 47002);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public void canMysteryBox() {
        canMysteryBox = true;

    }

    public void quickOpen() {

        if (player.getUltraInterface().isActive() || player.getSuperBoxInterface().isActive() || player.getNormalBoxInterface().isActive() || player.getFoeInterface().isActive()) {
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");

            return;
        }
        if (!(player.getSuperMysteryBox().canMysteryBox) || !(player.getNormalMysteryBox().canMysteryBox) ||
                !(player.getUltraMysteryBox().canMysteryBox) || !(player.getFoeMysteryBox().canMysteryBox) ||
                !(player.getYoutubeMysteryBox().canMysteryBox)
        ) {
            player.getPA().showInterface(47000);
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
            return;
        }
        if (player.getItems().playerHasItem(getItemId(), 1)) {
            player.getItems().deleteItem(getItemId(), 1);
            setMysteryPrize();
            roll(player);
        } else {
            player.sendMessage("@blu@You have used your last mystery box.");
        }
    }

    @Override
    public void roll(Player player) {
        if (mysteryPrize == -1) {
            canMysteryBox = true;
            player.getNormalMysteryBox().canMysteryBox();
            player.getUltraMysteryBox().canMysteryBox();
            player.getSuperMysteryBox().canMysteryBox();
            player.getFoeMysteryBox().canMysteryBox();
            player.getYoutubeMysteryBox().canMysteryBox();
            return;
        }

        player.getItems().addItemUnderAnyCircumstance(mysteryPrize, mysteryAmount);
        if (random > 85) {
            String name = ItemDef.forId(mysteryPrize).getName();
            String itemName = ItemDef.forId(getItemId()).getName();
            PlayerHandler.executeGlobalMessage("[<col=CC0000>" + itemName + "</col>] <col=255>"
                    + player.getDisplayName()
                    + "</col> hit the jackpot and got a <col=CC0000>"+name+"</col>!");
        }
        active = false;
        player.inDonatorBox = true;

        // Reward message


        // Can now spin again
        canMysteryBox = true;
        player.getNormalMysteryBox().canMysteryBox();
        player.getUltraMysteryBox().canMysteryBox();
        player.getSuperMysteryBox().canMysteryBox();
        player.getFoeMysteryBox().canMysteryBox();
        player.getYoutubeMysteryBox().canMysteryBox();
    }
}
