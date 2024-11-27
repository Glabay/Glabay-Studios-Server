package io.xeros.content.minigames.tob.bosses;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobBoss;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.model.Animation;
import io.xeros.model.definition.Npcs;
import io.xeros.model.projectile.ProjectileBaseBuilder;
import io.xeros.model.StillGraphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class Xarpus extends TobBoss {

    private static final int[] GREEN_SPLAT_STILL_GFX = {1654, 1655, 1656, 1657, 1658, 1659, 1660, 1661};

    public Xarpus(InstancedArea instancedArea) {
        super(Npcs.XARPUS_8340, new Position(3168, 4385, instancedArea.getHeight() + 1), instancedArea);
        setNpcAutoAttacks(Lists.newArrayList(
            new NPCAutoAttackBuilder()
                    .setAnimation(new Animation(8059))
                    .setMaxHit(0)
                    .setAttackDamagesPlayer(false)
                    .setDistanceRequiredForAttack(18)
                    .setOnAttack(this::sendSplatProjectile)
                    .createNPCAutoAttack()
        ));
    }

    private void sendSplatProjectile(NPCCombatAttack npcCombatAttack) {
        Position position = npcCombatAttack.getVictim().getPosition();
        new ProjectileBaseBuilder().setProjectileId(1644).setSendDelay(1).createProjectileBase()
                .createTargetedProjectile(npcCombatAttack.getNpc(), position).send(getInstance());

        // Cycle event to handle pool damage
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (isDead() || !isRegistered()) {
                    container.stop();
                    return;
                }

                if (container.getTotalExecutions() == 2) {
                    Server.playerHandler.sendStillGfx(new StillGraphic(GREEN_SPLAT_STILL_GFX[Misc.trueRand(GREEN_SPLAT_STILL_GFX.length)], 0, position), getInstance());
                }

                if (container.getTotalExecutions() == 18) {
                    container.stop();
                } else if (container.getTotalExecutions() >= 2) {
                    getInstance().getPlayers().stream().filter(plr -> plr.getPosition().equals(position)).forEach(plr ->
                                    plr.appendDamage(6 + Misc.random(10), Hitmark.POISON));
                }
            }
        }, 1);
    }

    @Override
    public int getDeathAnimation() {
        return 8063;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        TobInstance instance = (TobInstance) super.getInstance();
        instance.setLastRoom(true);
    }

}
