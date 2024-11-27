package io.xeros.content.bosses.nightmare.attack;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareAttack;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class Spores extends NightmareAttack {

    private static final int SPORE_COUNT = 16;
    private static final int SPORE_SPAWN_ID = 37738;
    private static final int SPORE_ACTIVE_ID = 37739;
    private static final String INFECTED_KEY = "nightmare_spore_infected";

    public static boolean isInfected(Player player) {
        return player.getAttributes().getBoolean(INFECTED_KEY);
    }

    @Override
    public void tick(Nightmare nightmare) {
        if (getTicks() == 0) {
            nightmare.requestTransform(9427);
            nightmare.startAnimation(8600);
        }

        if (getTicks() == 2) {
            spores(nightmare);
        }

        if (getTicks() == 3) {
            nightmare.transformToStandard();
            stop();
        }
    }

    private void spores(Nightmare nightmare) {
        getSporePositions(nightmare).forEach(position -> {
            Spore spore = new Spore(position);
            spore.sendSpawnId(nightmare);
            CycleEventHandler.getSingleton().addEvent(spore, new CycleEvent() {
                int burstTick = -1;
                @Override
                public void execute(CycleEventContainer container) {
                    if (!nightmare.isAlive() || !nightmare.isRegistered()) {
                        spore.remove(nightmare);
                        container.stop();
                        return;
                    }

                    if (container.getTotalTicks() == 1) {
                        spore.sendActiveId(nightmare);
                    }

                    if (!spore.burst) {
                        if (container.getTotalTicks() >= 16) {
                            spore.burst(nightmare);
                        } else {
                            spore.attemptBurst(nightmare);
                        }
                    }
                    if (spore.burst) {
                        if (burstTick == -1) {
                            burstTick = container.getTotalTicks();
                        }

                        if (container.getTotalTicks() - burstTick >= 2) {
                            spore.remove(nightmare);
                            container.stop();
                        }
                    }
                }
            }, 1);
        });
    }

    private List<Position> getSporePositions(Nightmare nightmare) {
        List<Position> spores = Lists.newArrayList();

        for (int count = 0; count < SPORE_COUNT; count++) {
            Position position;

            main: while (true) {
                position = nightmare.getInstance().resolve(GraspingClaws.random());
                final Position position1 = position;

                if (spores.contains(position1) || nightmare.insideOf(position1))
                    continue;
                if (nightmare.getTotems().getTotems().stream().anyMatch(t -> t.insideOf(position1)))
                    continue;
                if (spores.stream().map(position1::deltaAbsolute).anyMatch(p -> p.getX() <= 2 && p.getY() <= 2)) {
                    continue;
                }

                break;
            }

            spores.add(position);
        }

        return spores;
    }

    private static class Spore extends GlobalObject {

        private boolean burst;

        public Spore(Position position) {
            super(SPORE_SPAWN_ID, position, 0, 10);
        }

        public void sendSpawnId(Nightmare nightmare) {
            nightmare.getInstance().getPlayers().forEach(p -> p.getPA().object(this));
        }

        public void sendActiveId(Nightmare nightmare) {
            nightmare.getInstance().getPlayers().forEach(p -> p.getPA().object(withId(SPORE_ACTIVE_ID)));
        }

        public void remove(Nightmare nightmare) {
            nightmare.getInstance().getPlayers().forEach(p -> p.getPA().object(withId(-1)));
        }

        public void attemptBurst(Nightmare nightmare) {
            List<Player> playerList = nightmare.getInstance().getPlayers().stream().filter(p -> p.distance(getPosition()) <= 1.5
                    && !isInfected(p)).collect(Collectors.toList());
            if (!playerList.isEmpty()) {
                playerList.forEach(this::infect);
                burst(nightmare);
            }
        }

        public void burst(Nightmare nightmare) {
            nightmare.getInstance().getPlayers().forEach(p -> p.getPA().sendPlayerObjectAnimation(this, 8632));
            burst = true;
        }

        public void infect(Player player) {
            if (!isInfected(player)) {
                player.sendMessage("@red@You have been infected by poisonous spores!");
                player.getAttributes().setBoolean(INFECTED_KEY, true);
                player.addTickable((container, player1) -> {
                    if (isInfected(player1)) {
                        player1.updateRunningToggled(false);
                    } else {
                        player1.updateRunningToggled(true);
                        container.stop();
                    }
                });
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (!NightmareConstants.BOUNDARY.in(player) || player.getInstance() == null || !isInfected(player)
                                || container.getTotalExecutions() >= 4) {
                            container.stop();
                            player.sendMessage("<col=355D27>The spores effect has wore off.");
                        } else {
                            player.forcedChat("*cough*");
                            player.getInstance().getPlayers().stream().filter(p -> p != player && p.distance(player.getPosition()) <= 1.5
                                    && !p.getAttributes().getBoolean(INFECTED_KEY)).forEach(player1 -> {
                                infect(player1);
                            });
                        }
                    }

                    @Override
                    public void onStopped() {
                        player.getAttributes().removeBoolean(INFECTED_KEY);
                    }

                }, 5);
            }
        }
    }
}
