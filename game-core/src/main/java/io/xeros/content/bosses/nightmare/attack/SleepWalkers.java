package io.xeros.content.bosses.nightmare.attack;

import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareAttack;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.Animation;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class SleepWalkers extends NightmareAttack {

    // TODO
    // sleepwalker npc 9448, 9449, 9450, 9451, death anim 8570
    // sleepwalker anim 8571 when absorbed
    // transform 9431 after sleep walkers are absorbed, nm anim 8604, gfx on player immediately 1782
    // player takes damage before gfx on player is over
    // nm anim 8572

    private static final String ABSORB_KEY = "nightmare_absorbed_sleepwalkers";

    private static final Position[][] SPAWNS = {
            {new Position(3878, 9942, 3), new Position(3877, 9942, 3), new Position(3876, 9942, 3)},
            {new Position(3866, 9942, 3), new Position(3867, 9942, 3), new Position(3869, 9942, 3)},
            {new Position(3863, 9945, 3), new Position(3863, 9946, 3), new Position(3863, 9947, 3)},
            {new Position(3863, 9957, 3), new Position(3863, 9956, 3), new Position(3863, 9955, 3)},
            {new Position(3866, 9960, 3), new Position(3867, 9960, 3), new Position(3868, 9960, 3)},
            {new Position(3878, 9960, 3), new Position(3877, 9960, 3), new Position(3876, 9960, 3)},
            {new Position(3881, 9957, 3), new Position(3881, 9956, 3), new Position(3881, 9955, 3)},
            {new Position(3881, 9945, 3), new Position(3881, 9946, 3), new Position(3881, 9947, 3)},
    };

    public static void absorb(Nightmare nightmare) {
        nightmare.getAttributes().setInt(ABSORB_KEY, nightmare.getAttributes().getInt(ABSORB_KEY, 0) + 1);
    }

    public static int getAbsorbs(Nightmare nightmare) {
        return nightmare.getAttributes().getInt(ABSORB_KEY, 0);
    }

    @Override
    public void tick(Nightmare nightmare) {
        switch (getTicks()) {
            case 0:
                nightmare.getAttributes().removeInt(ABSORB_KEY);
                nightmare.startAnimation(new Animation(8607));
                break;
            case 2:
                nightmare.teleport(nightmare.getInstance().resolve(NightmareConstants.NIGHTMARE_SPAWN_POSITION));
                nightmare.startAnimation(new Animation(8609));
                break;

            case 6:
                nightmare.requestTransform(9431);
                break;
            case 8:
                spawn(nightmare);
                break;
            case 18:
                nightmare.requestTransform(9431);
                nightmare.startAnimation(new Animation(8604));
                break;
            case 20:
                nightmare.getInstance().getPlayers().forEach(plr -> plr.startGraphic(new Graphic(1782)));
                break;
            case 22:
                nightmare.getInstance().getPlayers().forEach(plr -> {
                    plr.appendDamage(nightmare, 4 + (getAbsorbs(nightmare) * 5), Hitmark.HIT);
                });
                break;
            case 28:
                nightmare.transformToStandard();
                stop();
                break;
        }
    }

    private void spawn(Nightmare nightmare) {
        int amount = 4 + (nightmare.getInstance().getPlayers().size() / 4);
        if (amount > 24)
            amount = 24;
        int index = 0;
        int index2 = 0;
        for (int count = 0; count < amount; count++) {
            Sleepwalker sleepwalker = new Sleepwalker(nightmare, nightmare.getInstance().resolve(SPAWNS[index][index2++]));
            nightmare.getInstance().add(sleepwalker);
            if (index2 == 1 && amount <= 8 || index == 2 && amount <= 16 || index2 == 3) {
                index++;
                index2 = 0;
            }
        }
    }

    public static class Sleepwalker extends NPC {

        private static final int[] IDS = {9448, 9449, 9450, 9451};
        private final Nightmare nightmare;
        private boolean absorbed;
        private int walkTick;

        public Sleepwalker(Nightmare nightmare, Position position) {
            super(IDS[Misc.trueRand(IDS.length)], position);
            this.nightmare = nightmare;
            getHealth().setMaximumHealth(10);
            getHealth().reset();
            getBehaviour().setWalkHome(false);
            getBehaviour().setRespawn(false);
        }

        @Override
        public boolean isAutoRetaliate() {
            return false;
        }

        @Override
        public int getDeathAnimation() {
            return 8570;
        }

        @Override
        public int modifyDamage(Player player, int damage) {
            return 10;
        }

        @Override
        public void process() {
            if (!absorbed) {
                if (nightmare.distance(getPosition()) <= 1.5 && !isDead) {
                    absorb(nightmare);
                    startAnimation(8571);
                    absorbed = true;
                    CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            unregister();
                            container.stop();
                        }
                    }, 2);
                } else {
                    if (walkTick++ >= 4){
                        freezeTimer = 0;
                        faceNPC(nightmare.getIndex());
                        NPCDumbPathFinder.walkTowards(this, nightmare.getX(), nightmare.getY());
                    }
                    super.process();
                }
            }
        }
    }
}
