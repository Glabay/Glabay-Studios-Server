package io.xeros.content.dialogue.types;

import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import io.xeros.content.dialogue.DialogueAction;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueObject;
import io.xeros.model.AmountInput;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;

public class MakeItemDialogue extends DialogueObject {

    public static class PlayerMakeItem {
        private final Player player;
        private final int itemId;
        private final int amount;

        public PlayerMakeItem(Player player, int itemId, int amount) {
            this.player = player;
            this.itemId = itemId;
            this.amount = amount;
        }

        public Player getPlayer() {
            return player;
        }

        public int getItemId() {
            return itemId;
        }

        public int getAmount() {
            return amount;
        }
    }

    public static class MakeItem {
        private final String itemHeader;
        private final int itemId;

        public MakeItem(int itemId) {
            this(ItemDef.forId(itemId).getName(), itemId);
        }

        public MakeItem(String itemHeader, int itemId) {
            this.itemHeader = itemHeader;
            this.itemId = itemId;
        }

        public String getItemHeader() {
            return itemHeader;
        }

        public int getItemId() {
            return itemId;
        }
    }

    private final int zoom;
    private final String dialogueHeader;
    private final Consumer<PlayerMakeItem> consumer;
    private final MakeItem[] makeItems;

    public MakeItemDialogue(DialogueBuilder context, int zoom, String dialogueHeader, Consumer<PlayerMakeItem> consumer, MakeItem...makeItems) {
        super(context, false);
        this.zoom = zoom;
        this.dialogueHeader = dialogueHeader;
        this.consumer = consumer;
        this.makeItems = makeItems;
    }

    @Override
    public void send(Player player) {
        switch (makeItems.length) {
            case 1:
                player.getPA().sendString(2799, makeItems[0].itemHeader);
                player.getPA().sendInterfaceModel(1746, makeItems[0].itemId, zoom);
                player.getPA().sendString(2800, dialogueHeader);
                player.getPA().sendChatboxInterface(4429);
                break;
            case 2:
                player.getPA().sendInterfaceModel(8870, makeItems[1].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8869, makeItems[0].getItemId(), zoom);
                player.getPA().sendString(8874, makeItems[0].itemHeader);
                player.getPA().sendString(8878, makeItems[1].itemHeader);
                player.getPA().sendChatboxInterface(8866);
                break;
            case 3:
                player.getPA().sendInterfaceModel(8883, makeItems[0].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8884, makeItems[1].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8885, makeItems[2].getItemId(), zoom);
                for (int i = 0; i < 3; i++) {
                    player.getPA().sendString(8889 + (i * 4), makeItems[i].itemHeader);
                }
                player.getPA().sendChatboxInterface(8880);
                break;
            case 4:
                player.getPA().sendInterfaceModel(8902, makeItems[0].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8903, makeItems[1].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8904, makeItems[2].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8905, makeItems[3].getItemId(), zoom);
                for (int i = 0; i < 4; i++) {
                    player.getPA().sendString(8909 + (i * 4), makeItems[i].itemHeader);
                }
                player.getPA().sendChatboxInterface(8899);
                break;
            case 5:
                player.getPA().sendInterfaceModel(8941, makeItems[0].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8942, makeItems[1].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8943, makeItems[2].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8944, makeItems[3].getItemId(), zoom);
                player.getPA().sendInterfaceModel(8945, makeItems[4].getItemId(), zoom);
                for (int i = 0; i < 5; i++) {
                    player.getPA().sendString(8949 + (i * 4), makeItems[i].getItemHeader());
                }
                player.getPA().sendChatboxInterface(8938);
                break;
            default:
                throw new IllegalArgumentException("Invalid amount of make items: " + makeItems.length);
        }
    }

    @Override
    public void handleAction(Player player, DialogueAction action) {
        Preconditions.checkState(consumer != null, new IllegalStateException());

        switch (action) {
            case MAKE_1_SLOT_1:
            case MAKE_1_SLOT_2:
            case MAKE_1_SLOT_3:
            case MAKE_1_SLOT_4:
            case MAKE_1_SLOT_5:
                handleMakeItem(player,
                        action == DialogueAction.MAKE_1_SLOT_1 ? 0
                        : action == DialogueAction.MAKE_1_SLOT_2 ? 1
                        : action == DialogueAction.MAKE_1_SLOT_3 ? 2
                        : action == DialogueAction.MAKE_1_SLOT_4 ? 3
                        : action == DialogueAction.MAKE_1_SLOT_5 ? 4 : -1 , 1);
                break;
            case MAKE_5_SLOT_1:
            case MAKE_5_SLOT_2:
            case MAKE_5_SLOT_3:
            case MAKE_5_SLOT_4:
            case MAKE_5_SLOT_5:
                handleMakeItem(player,
                        action == DialogueAction.MAKE_5_SLOT_1 ? 0
                                : action == DialogueAction.MAKE_5_SLOT_2 ? 1
                                : action == DialogueAction.MAKE_5_SLOT_3 ? 2
                                : action == DialogueAction.MAKE_5_SLOT_4 ? 3
                                : action == DialogueAction.MAKE_5_SLOT_5 ? 4 : -1 , 5);
                break;
            case MAKE_10_SLOT_1:
            case MAKE_10_SLOT_2:
            case MAKE_10_SLOT_3:
            case MAKE_10_SLOT_4:
            case MAKE_10_SLOT_5:
                handleMakeItem(player,
                        action == DialogueAction.MAKE_10_SLOT_1 ? 0
                                : action == DialogueAction.MAKE_10_SLOT_2 ? 1
                                : action == DialogueAction.MAKE_10_SLOT_3 ? 2
                                : action == DialogueAction.MAKE_10_SLOT_4 ? 3
                                : action == DialogueAction.MAKE_10_SLOT_5 ? 4 : -1 , 10);
                break;
            case MAKE_X_SLOT_1:
            case MAKE_X_SLOT_2:
            case MAKE_X_SLOT_3:
            case MAKE_X_SLOT_4:
            case MAKE_X_SLOT_5:
                handleMakeItem(player,
                        action == DialogueAction.MAKE_X_SLOT_1 ? 0
                                : action == DialogueAction.MAKE_X_SLOT_2 ? 1
                                : action == DialogueAction.MAKE_X_SLOT_3 ? 2
                                : action == DialogueAction.MAKE_X_SLOT_4 ? 3
                                : action == DialogueAction.MAKE_X_SLOT_5 ? 4 : -1 , -1);
                break;
            case MAKE_ALL_SLOT_1:
            case MAKE_ALL_SLOT_2:
            case MAKE_ALL_SLOT_3:
            case MAKE_ALL_SLOT_4:
            case MAKE_ALL_SLOT_5:
                handleMakeItem(player,
                        action == DialogueAction.MAKE_ALL_SLOT_1 ? 0
                                : action == DialogueAction.MAKE_ALL_SLOT_2 ? 1
                                : action == DialogueAction.MAKE_ALL_SLOT_3 ? 2
                                : action == DialogueAction.MAKE_ALL_SLOT_4 ? 3
                                : action == DialogueAction.MAKE_ALL_SLOT_5 ? 4 : -1 , Integer.MAX_VALUE);
                break;
            default:
                break;
        }
    }

    private void handleMakeItem(Player player, int slot, int amount) {
        if (amount == -1) {
            player.getPA().sendEnterAmount(makeItems[0].getItemId());
            player.amountInputHandler = new AmountInput() {
                @Override
                public void handle(Player player, int amount) {
                    consumer.accept(new PlayerMakeItem(player, makeItems[slot].getItemId(), amount));
                }
            };
        } else {
            consumer.accept(new PlayerMakeItem(player, makeItems[slot].getItemId(), amount));
        }
    }
}
