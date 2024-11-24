package io.xeros.content.bosses.mimic;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.definition.Npcs;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class MimicInstance extends InstancedArea {

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRelativeHeight(1)
            .createInstanceConfiguration();

    public MimicInstance() {
        super(CONFIGURATION, Boundary.MIMIC_LAIR);
    }

    public void enter(Player plr) {
        add(plr);
        plr.moveTo(new Position(2720, 4314, getHeight()));
        MimicNpc mimic = new MimicNpc(Npcs.THE_MIMIC_8633, new Position(2720, 4319,getHeight()));
        //mimic.requestTransform(Npcs.THE_MIMIC);
        this.add(mimic);
        plr.getPA().closeAllWindows();
    }

    public void unlockFight() {
        this.getNpcs().get(0).getBehaviour().setAggressive(true);
        this.getNpcs().get(0).requestTransform(Npcs.THE_MIMIC_8633);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {

        return false;
    }

    @Override
    public void onDispose() {
        getPlayers().stream().forEach(plr -> {
            remove(plr);
        });
    }
}
