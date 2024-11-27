package io.xeros.model.world.objects.actions.handlers.areas.LithkrenVault;

import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.definition.Items.*;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/2/2024
 */
public class Machinery extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {return new Integer[] {32161}; }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.getItems().playerHasItem(HYDRA_LEATHER) && player.getItems().playerHasItem(HAMMER)) {
            player.startAnimation(898);
            player.getItems().deleteItem(HYDRA_LEATHER, 1); //leather
            player.getItems().addItem(FEROCIOUS_GLOVES, 1); //ads forocious gloves
            player.sendMessage("You place your leather into the machine, and with your hammer you manage to create some forocious gloves.");
    } else if (player.getItems().playerHasItem(FEROCIOUS_GLOVES) && player.getItems().playerHasItem(HAMMER)) {
            player.getItems().deleteItem(FEROCIOUS_GLOVES, 1); //gloves
            player.getItems().addItem(HYDRA_LEATHER, 1); // leather
            player.sendMessage("By feeding the gloves through the machine, you manage to revert them into leather.");
        }
        return true;
}

}
