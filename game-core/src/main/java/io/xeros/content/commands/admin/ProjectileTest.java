package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.projectile.ProjectileEntity;

public class ProjectileTest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Position other = player.getPosition().translate(-1, -1);
        int tileDist = (int) player.getPosition().getAbsDistance(other);
        int duration = 21 + 159 + tileDist;
        ProjectileEntity projectileEntity = new ProjectileEntity(player.getPosition(), other, 0, 1586, duration, 21, 50, 12, 16, 1, 64, 10);
        projectileEntity.send(player.getPosition(), other);
    }
}
