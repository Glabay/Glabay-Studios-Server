package io.xeros.content.commands.admin;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.StillGraphic;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

/**
 * @author Arthur Behesnilian 2:53 AM
 */
public class CoolGfx extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] split = input.split(" ");
        int graphic = Integer.parseInt(split[0]);
        int amount = Integer.parseInt(split[0]);

        for (int x = 0; x < amount; x++) {
            for (int y = 0; y < amount; y++) {
                Position position = new Position(player.getX() + x, player.getY() + y, player.getHeight());
                Position negPosition = new Position(player.getX() - x, player.getY() - y, player.getHeight());
                Position position2 = new Position(player.getX() + x, player.getY() - y, player.getHeight());
                Position neg2Position = new Position(player.getX() - x, player.getY() + y, player.getHeight());

                Server.playerHandler.sendStillGfx(new StillGraphic(graphic, position), player.getInstance());
                Server.playerHandler.sendStillGfx(new StillGraphic(graphic, negPosition), player.getInstance());
                Server.playerHandler.sendStillGfx(new StillGraphic(graphic, position2), player.getInstance());
                Server.playerHandler.sendStillGfx(new StillGraphic(graphic, neg2Position), player.getInstance());
            }
        }

    }

}
