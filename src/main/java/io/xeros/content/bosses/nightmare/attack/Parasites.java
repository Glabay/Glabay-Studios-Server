package io.xeros.content.bosses.nightmare.attack;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareAttack;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.projectile.ProjectileBaseBuilder;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.Potions;
import io.xeros.util.Misc;

public class Parasites extends NightmareAttack {

    @Override
    public void tick(Nightmare nightmare) {
        if (getTicks() == 0) {
            nightmare.requestTransform(9426);
            nightmare.startAnimation(8606);
        }

        if (getTicks() == 2) {
            parasites(nightmare);
        }

        if (getTicks() == 5) {
            nightmare.transformToStandard();
            stop();
        }
    }

    private void parasites(Nightmare nightmare) {
        players(nightmare).forEach(player -> {
            new ProjectileBaseBuilder()
                    .setProjectileId(1770)
                    .setStartHeight(90)
                    .createProjectileBase()
                    .createTargetedProjectile(nightmare, player)
                    .send(nightmare.getInstance());
            player.sendMessage("@red@The Nightmare has impregnated you with a deadly parasite!");
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (nightmare == null || !nightmare.checkPlayerState(player)) {
                        container.stop();
                        return;
                    }

                    Position last = new Position(0, 0);
                    for (int index = 0; index < 2; index++) {
                        last = player.getAdjacentPosition(last);
                        Parasite parasite = new Parasite(nightmare, last);
                        nightmare.getInstance().add(parasite);
                    }

                    if (System.currentTimeMillis() - Potions.getSanfewTime(player) >= 16_000) {
                        player.sendMessage("@red@You failed to cleanse the parasite!");
                        player.appendDamage(nightmare, 40 + Misc.trueRand(16), Hitmark.HIT);
                    } else {
                        player.sendMessage("<col=355D27>The parasite within you has been weakened.");
                        player.appendDamage(nightmare, 4 + Misc.trueRand(5), Hitmark.HIT);
                    }
                    container.stop();
                }
            }, 12);
        });
    }

    private List<Player> players(Nightmare nightmare) {
        List<Player> players = Lists.newArrayList();
        List<Player> instance = nightmare.getInstance().getPlayers();
        players.add(instance.get(Misc.trueRand(instance.size())));
        for (Player player : instance) {
            if (!players.contains(player) && Misc.trueRand(5) == 0) {
                players.add(player);
            }
        }
        return players;
    }

    private static class Parasite extends NPC {

        private final Nightmare nightmare;
        private int healTicks;

        public Parasite(Nightmare nightmare, Position position) {
            super(9453, position);
            startAnimation(8561);
            this.nightmare = nightmare;
            getHealth().setMaximumHealth(10);
            getHealth().setCurrentHealth(10);
            getBehaviour().setWalkHome(false);
            getBehaviour().setRespawn(false);
        }

        @Override
        public void process() {
            if (nightmare.distance(getPosition()) > 1.5) {
                NPCDumbPathFinder.walkTowards(this, nightmare.getX(), nightmare.getY());
            } else {
                if (healTicks++ >= 3) {
                    startAnimation(8554);
                    new ProjectileBaseBuilder()
                            .setSendDelay(2)
                            .setSpeed(25)
                            .setDelay(5)
                            .setStartHeight(5)
                            .setEndHeight(60)
                            .setProjectileId(1771)
                            .createProjectileBase()
                            .createTargetedProjectile(this, nightmare)
                            .send(getInstance());
                    if (!nightmare.isOnHealth()) {
                        nightmare.getHealth().increase(8);
                    }
                    healTicks = 0;
                }
            }
            super.process();
            faceNPC(nightmare.getIndex());
        }
    }
}
