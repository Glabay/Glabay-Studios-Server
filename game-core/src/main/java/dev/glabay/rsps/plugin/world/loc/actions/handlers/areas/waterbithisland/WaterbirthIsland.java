package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.waterbithisland;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class WaterbirthIsland extends WorldObjectAction {

    @Override
    public Integer[] getIds() {
        return new Integer[]{ 8929 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return EnterDungeon(player, object);
    }

    private Boolean EnterDungeon(Player player, GlobalObject object) {
        player.objectDistance = 3;
        AgilityHandler.delayEmote(player, "CRAWL", 2394, 10300, 1, 2);
        //c.getDH().sendDialogues(792, 1158);
        return true;
    }
}
