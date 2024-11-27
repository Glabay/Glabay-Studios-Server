package io.xeros.content.bosses.araxxor;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.util.ArrayList;
import java.util.List;

import static io.xeros.model.entity.player.Boundary.ARAXXOR_ARENA;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class AraxxorInstance extends InstancedArea {

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
        .setCloseOnPlayersEmpty(true)
        .setRespawnNpcs(true)
        .createInstanceConfiguration();

    final Position entryTile = new Position(3647, 9815, getHeight());

    final List<WorldObject> poolObjects = new ArrayList<>();
    final List<NPC> araxytes = new ArrayList<>();

    public AraxxorInstance() {
        super(CONFIGURATION, ARAXXOR_ARENA);
    }

    public void enter(Player player) {
        player.sendStatement("You crawl through the webbed tunnel.");
        player.moveTo(entryTile);
        add(player);
        var boss = new Araxxor(new Position(3631, 9812, getHeight()));
            boss.startAnimation(11482);
            boss.attackEntity(player);
        add(boss);
    }

    @Override
    public void onDispose() {}
}
