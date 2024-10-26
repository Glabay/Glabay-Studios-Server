package io.xeros.content.commands.owner;

import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ContainerUpdate;

import java.util.Optional;

/**
 * @author Arthur Behesnilian 5:53 PM
 */
public class CopyPlayer extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
        if (optionalPlayer.isPresent()) {
            Player other = optionalPlayer.get();

            for (int i = 0; i < other.playerEquipment.length; i++) {
                player.playerEquipment[i] = other.playerEquipment[i];
            }

            for (int i = 0; i < other.playerEquipmentN.length; i++) {
                player.playerEquipmentN[i] = other.playerEquipmentN[i];
            }

            other.getItems().getInventoryItems().forEach(item -> {
                player.getItems().setInventoryItemSlot(item.slot(), item.id(), item.amount());
            });

            for (int i = 0; i < other.playerLevel.length; i++) {
                player.playerLevel[i] = other.playerLevel[i];
            }

            for (int i = 0; i < other.playerXP.length; i++) {
                player.playerXP[i] = other.playerXP[i];
            }

            player.getPA().refreshSkills();
            player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
            player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
            player.getItems().calculateBonuses();
            player.getPA().requestUpdates();
            MeleeData.setWeaponAnimations(player);
            player.getItems().calculateBonuses();
            player.getItems().sendEquipmentContainer();
            player.sendMessage("You copy " + input + "s loadout.");

        } else {
            player.sendMessage("There was no user with the name " + input);
        }
    }

}
