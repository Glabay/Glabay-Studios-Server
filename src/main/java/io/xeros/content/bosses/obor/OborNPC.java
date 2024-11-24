package io.xeros.content.bosses.obor;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.*;
import io.xeros.model.cycleevent.*;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.*;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Optional;

public class OborNPC extends NPC {

    public OborNPC(int npcId, Position position, Player player) {
        super(npcId, position);
        setAttacks();
        this.spawnedBy = player.getIndex();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(5) == 2)//&& !isImmune(attack.getEntity().asPlayer()))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(8)
                        .setHitDelay(2)
                        .setAnimation(new Animation(7183))
                        .setMaxHit(0)
                        .setAttackDelay(2)
                        .setOnAttack(attack -> createGroundEffect(attack.getVictim().asPlayer(), this))
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(1)
                        .setAnimation(new Animation(4666))
                        .setOnHit(attack ->  {
                            if (attack.getCombatHit().missed())
                                return;
                            createMeleeEffect(attack.getVictim().asPlayer(), this);
                         })
                        .setMaxHit(16)
                        .setAttackDelay(6)
                        .createNPCAutoAttack()

        ));
    }

    private void createMeleeEffect(Player player, NPC npc) {
        if (player.prayerActive[18]) {
            /**
             * When melee prayer is active, chance to force animate
             */
            if (Math.random() < .2) {
                player.startAnimation(7210);
            }
            return;
        }
        if (player.lastDamageTaken > 0) {

            player.getPA().shakeScreen(3, 2, 3, 2);

            player.startAnimation(7210);

            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

                @Override
                public void execute(CycleEventContainer container) {
                    player.getPA().resetScreenShake();
                    container.stop();
                }
            }, 2);
        }
    }



    private void createGroundEffect(Player player, NPC npc) {

        int playerX = player.getX();

        int playerY = player.getY();

        List<Position> toCheck = Lists.newArrayList();

        int random = Misc.random(1);

        toCheck.add(player.getPosition());

        toCheck.add(random == 1 ? new Position(playerX - 1, playerY) : new Position(playerX, playerY + 1));

        toCheck.forEach(position -> Server.playerHandler.sendStillGfx(new StillGraphic(60, 0, position), getInstance()));

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

            int tick;

            @Override
            public void execute(CycleEventContainer container) {

                if (player == null || player.isDead() || !player.isOnline()) {
                    container.stop();
                    return;
                }
                if (tick == 2) {
                    Optional<Position> onTile = toCheck.stream().filter(p -> p.equals(player.getPosition())).findAny();
                    if (onTile.isPresent()) {
                        player.appendDamage(player, Misc.random(20), Hitmark.HIT);
                    }
                    container.stop();
                }
                tick++;
            }

        }, 1);

    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    @Override
    public boolean isFreezable() {
        return false;
    }


    //7210 = on sucessful hit with special attack on PLAYER
    //gfx 60
    //7183 = special attack anim on NPC
    //1148 = climbing down rocks



    /**
     *
     *
     *
     */

}
