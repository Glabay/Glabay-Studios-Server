package io.xeros.model.world.objects.actions.areas.fossilisland.home;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanBank;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import java.util.Optional;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class GIMBankChest extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[]{ GroupIronmanBank.OBJECT_ID };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return openGIMBankChest(player, object);
    }

    private Boolean openGIMBankChest(Player player, GlobalObject object) {
        Optional<GroupIronmanGroup> group = GroupIronmanRepository.getGroupForOnline(player);
        group.ifPresentOrElse(it -> it.getBank().open(player),
                () -> player.sendMessage("This chest is only for group ironmen!"));
        return true;
    }
}