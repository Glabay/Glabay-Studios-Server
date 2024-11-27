package io.xeros.content.bosses.nightmare.attack;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareAttack;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.Animation;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class FlowerPower extends NightmareAttack {

    private static final Position FLOWER_ORIGIN = new Position(3872, 9951, 3);
    private static final int LINE_LENGTH = 10;
    private static final int[][] DIR = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static final int TICKS_UNTIL_DAMAGE = 6;
    private static final int TICKS_UNTIL_DISPOSE = 18;

    @Override
    public void tick(Nightmare nightmare) {
        switch (getTicks()) {
            case 0:
                nightmare.startAnimation(new Animation(8607));
                break;
            case 2:
                nightmare.teleport(nightmare.getInstance().resolve(NightmareConstants.NIGHTMARE_SPAWN_POSITION));
                nightmare.startAnimation(new Animation(8609));
                break;
            case 6:
                nightmare.startAnimation(new Animation(8601));
                break;
            case 8:
                flowers(nightmare);
                break;
            case 13:
                nightmare.transformToStandard();
                stop();
                break;
        }
    }

    private void flowers(Nightmare nightmare) {
        List<Flower> flowers = Lists.newArrayList();
        int direction1 = Misc.trueRand(3);
        int direction2 = (direction1 + (Misc.trueRand(2) == 0 ? -1 : 1));
        direction2 = direction2 == -1 ? 3 : direction2 == 4 ? 0 : direction2;
        Position high = FLOWER_ORIGIN.translate(DIR[direction2][0] * LINE_LENGTH, DIR[direction2][1] * LINE_LENGTH)
                .translate(DIR[direction1][0] * LINE_LENGTH, DIR[direction1][1] * LINE_LENGTH);
        Boundary boundary = Boundary.calculateBoundary(FLOWER_ORIGIN, high, nightmare.getPosition().getHeight());

        flowers.add(new Flower(FlowerData.GOOD, FLOWER_ORIGIN));
        for (int index = 0; index < 4; index++) {
            FlowerData flower = index == direction1 || index == direction2 ? FlowerData.GOOD : FlowerData.BAD;

            int x = FLOWER_ORIGIN.getX() + DIR[index][0];
            int y = FLOWER_ORIGIN.getY() + DIR[index][1];

            for (int count = 0; count < LINE_LENGTH; count++) {
                flowers.add(new Flower(flower, new Position(x, y, nightmare.getInstance().getHeight())));
                x += DIR[index][0];
                y += DIR[index][1];
            }
        }

        flowers.forEach(flower -> nightmare.getInstance().getPlayers().forEach(flower::spawn));

        // Activate the flowers, damage players
        CycleEventHandler.getSingleton().addEvent(nightmare, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalTicks() == 1) {
                    flowers.forEach(flower -> nightmare.getInstance().getPlayers().forEach(flower::inactive));
                } else if (container.getTotalTicks() == TICKS_UNTIL_DAMAGE) {
                    flowers.forEach(flower -> nightmare.getInstance().getPlayers().forEach(flower::active));
                } else if (container.getTotalTicks() == TICKS_UNTIL_DISPOSE) {
                    flowers.forEach(flower -> nightmare.getInstance().getPlayers().forEach(flower::dispose));
                    container.stop();
                }

                if (container.getTotalTicks() >= TICKS_UNTIL_DAMAGE && container.getTotalTicks() < TICKS_UNTIL_DISPOSE) {
                    nightmare.getInstance().getPlayers().forEach(player -> {
                        if (!boundary.in(player)) {
                            player.appendDamage(nightmare, 4 + Misc.trueRand(4), Hitmark.HIT);
                        }
                    });
                }
            }
        }, 1);
    }

    private enum FlowerData {
        GOOD(37743, 37744, 8619,
                37745, 8621),
        BAD(37740, 37741, -1,
                37742, 8627)
        ;

        private final int spawningObjectId;
        private final int waitingForActivationObjectId;
        private final int waitingForActivationAnimationId;
        private final int activatedObjectId;
        private final int disposeAnimation;

        FlowerData(int spawningObjectId, int waitingForActivationObjectId,
                   int waitingForActivationAnimationId, int activatedObjectId, int disposeAnimation) {
            this.spawningObjectId = spawningObjectId;
            this.waitingForActivationObjectId = waitingForActivationObjectId;
            this.waitingForActivationAnimationId = waitingForActivationAnimationId;
            this.activatedObjectId = activatedObjectId;
            this.disposeAnimation = disposeAnimation;
        }
    }

    private static class Flower extends GlobalObject {

        private final FlowerData flowerData;

        public Flower(FlowerData flowerData, Position position) {
            super(flowerData.spawningObjectId, position, 0, 10);
            this.flowerData = flowerData;
        }

        void spawn(Player player) {
            player.getPA().object(withId(flowerData.spawningObjectId));
        }

        void inactive(Player player) {
            player.getPA().object(withId(flowerData.waitingForActivationObjectId));
            if (flowerData.waitingForActivationAnimationId > -1) {
                player.getPA().sendPlayerObjectAnimation(this, flowerData.waitingForActivationAnimationId);
            }
        }

        void active(Player player) {
            player.getPA().object(withId(flowerData.activatedObjectId));
        }

        void dispose(Player player) {
            player.getPA().sendPlayerObjectAnimation(this, flowerData.disposeAnimation);
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    player.getPA().object(withId(-1));
                    container.stop();
                }
            }, 1);
        }
    }
}
