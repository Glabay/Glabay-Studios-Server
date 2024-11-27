package io.xeros.content.bosses.nightmare;

import com.google.common.base.Preconditions;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class NightmareInstance extends InstancedArea {

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRelativeHeight(0)
            .setRespawnNpcs(false)
            .setCloseOnPlayersEmpty(true)
            .createInstanceConfiguration();

    private final boolean publicInstance;
    private Nightmare nightmare;
    private final NightmareInterface nightmareInterface = new NightmareInterface();
    private boolean started;

    /**
     * Creates the {@link NightmareInstance}
     */
    public NightmareInstance(boolean publicInstance) {
        super(CONFIGURATION, NightmareConstants.BOUNDARY);
        this.publicInstance = publicInstance;
    }

    @Override
    public void tick(Entity entity) { }

    @Override
    public void add(Player player) {
        player.getPA().sendScreenFade("The Nightmare pulls you into her dream as you approach her.", 1, 1);
        player.moveTo(resolve(NightmareConstants.NIGHTMARE_PLAYER_SPAWN_POSITION));
        super.add(player);
    }

    @Override
    public boolean handleInterfaceUpdating(Player player) {
        nightmareInterface.update(player, nightmare);
        return true;
    }

    public void countdown() {
        nightmare = new Nightmare(this);
        started = true;
        final InstancedArea instancedArea = this;

        if (publicInstance)
            NightmareStatusNPC.update(this, nightmare);

        // Begin the countdown
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            int ticks;
            @Override
            public void execute(CycleEventContainer container) {
                if (getPlayers().isEmpty() || PlayerHandler.getPlayers().stream().noneMatch(p -> p != null && Boundary.isIn(p, NightmareConstants.BOUNDARY))) {
                    end();
                    container.stop();
                } else if (++ticks == (50)) {
                    startFight();
                    container.stop();

                    if (publicInstance)
                        NightmareStatusNPC.update(instancedArea, nightmare);
                }
            }
        }, 1);
    }

    private void startFight() {
        final InstancedArea instancedArea = this;

        // Track the status of the nightmare fight to update the status npc
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            final int deathTime = -1;
            @Override
            public void execute(CycleEventContainer container) {
                Preconditions.checkState(nightmare != null);

                if (publicInstance)
                    NightmareStatusNPC.update(instancedArea, nightmare);

                if (getPlayers().isEmpty() || PlayerHandler.getPlayers().stream().noneMatch(p -> p != null && Boundary.isIn(p, NightmareConstants.BOUNDARY))) {
                    container.stop();
                    end();
                }
            }
        }, 1);
    }

    private void end() {
        if (nightmare != null) {
            nightmare.unregister();
            nightmare = null;
        }
        dispose();

        if (publicInstance) {
            NightmareStatusNPC.update(null, null);
            NightmareInstanceManager.getSingleton().setNightmareInstance(null);
        }
    }

    @Override
    public void onDispose() { }

    public Nightmare getNightmare() {
        return nightmare;
    }

    public boolean isStarted() {
        return started;
    }
}
