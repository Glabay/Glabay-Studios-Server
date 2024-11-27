package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 1:05 PM
 */
public class SetAccuracyBonus extends Command {

    public static int MELEE_ATTACK = 64;
    public static int RANGE_ATTACK = 64;
    public static int MAGE_ATTACK = 64;
    public static int SCYTHE_ATTACK_BONUS = 130;

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        String type = parts[0];
        int attack = Integer.parseInt(parts[1]);

        switch (type) {
            case "melee":
                MELEE_ATTACK = attack;
                break;
            case "range":
                RANGE_ATTACK = attack;
                break;
            case "mage":
                MAGE_ATTACK = attack;
                break;
            case "scythe":
                SCYTHE_ATTACK_BONUS = attack;
            default:
                player.sendMessage("No type: " + type + ", [melee, range, mage, scythe].");
                return;
        }

        player.sendMessage("You set the " + type + " attack level bonus to " + attack);
    }

}
