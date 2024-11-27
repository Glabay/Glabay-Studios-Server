package io.xeros.model.items.bank;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

import java.util.Arrays;
import java.util.List;

/**
 * TODO delete this at some point
 */
public class BankPinLoginMessage implements PlayerSaveEntry {

    private static final String ATTR = "sent_bank_pin_message";

    @Override
    public List<String> getKeys(Player player) {
        return List.of("bank_pin_2021_message");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        boolean bool = Boolean.parseBoolean(value);
        player.getAttributes().setBoolean(ATTR, bool);
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return player.getAttributes().getBoolean(ATTR, false) + "";
    }

    @Override
    public void login(Player player) {
        if (!player.getAttributes().getBoolean(ATTR)) {
            player.getAttributes().setBoolean(ATTR, true);

            if (!player.getBankPin().hasBankPin() && player.isCompletedTutorial()) {
                String[] messages = {
                        "Account pins now have rewards, use @blu@::pin @bla@to set one up!",
                        "@red@If you set an account pin you receive one hour of bonus xp!",
                        "You only need to enter them when you login on a different computer."
                };

                Arrays.stream(messages).forEach(player::sendMessage);
                new DialogueBuilder(player).statement(messages).send();
            }
        }
    }
}
