package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 1:05 PM
 */
public class SetDefenceBonus extends Command {

    public static int MELEE_DEFENCE = 24;
    public static int RANGE_DEFENCE = 24;
    public static int MAGE_DEFENCE = 24;

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        String type = parts[0];
        int attack = Integer.parseInt(parts[1]);

        switch (type) {
            case "melee":
                MELEE_DEFENCE = attack;
                break;
            case "range":
                RANGE_DEFENCE = attack;
                break;
            case "mage":
                MAGE_DEFENCE = attack;
                break;
            default:
                player.sendMessage("No type: " + type + ", [melee, range, mage].");
                return;
        }

        player.sendMessage("You set the " + type + " defence level bonus to " + attack);
    }

}
