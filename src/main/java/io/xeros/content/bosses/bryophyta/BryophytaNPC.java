package io.xeros.content.bosses.bryophyta;

import com.google.common.collect.Lists;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.*;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.projectile.ProjectileBaseBuilder;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ynneh
 */
public class BryophytaNPC extends NPC {

    public static final int GROWTHLING = 8194;

    public BryophytaNPC(int npcId, Position position, Player spawnedBy) {
        super(npcId, position);
        setAttacks();
        this.spawnedBy = spawnedBy.getIndex();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(6) == 2)//&& !isImmune(attack.getEntity().asPlayer()))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(20)
                        .setHitDelay(2)
                        .setAnimation(new Animation(-1))
                        .setMaxHit(0)
                        .setAttackDelay(2)
                        .setOnAttack(attack -> {
                            createGrowthlingAttack(attack.getVictim().asPlayer(), this);
                        })
                        .createNPCAutoAttack(),

                /**
                 * Magic attack
                 */
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(10)
                        .setHitDelay(4)
                        .setMaxHit(16)
                        .setAnimation(new Animation(7173))//magic attack
                        .setAttackDelay(6)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(139).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(140, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(2)
                        .setAnimation(new Animation(4658))
                        .setMaxHit(16)
                        .setAttackDelay(6)
                        .createNPCAutoAttack()

        ));
    }

    private void createGrowthlingAttack(Player player, NPC npc) {

        if (isDead())
            return;

        /**
         * Already summoned growthlings..
         */
        if (isImmune(player)) {
            return;
        }
        List<Position> possible_spawns = Arrays.asList(
                new Position(3218, 9939, getHeight()),
                new Position(3222, 9939, getHeight()),
                new Position(3220, 9937, getHeight()),
                new Position(3217, 9935, getHeight()),
                new Position(3219, 9934, getHeight()),
                new Position(3217, 9932, getHeight()),
                new Position(3220, 9931, getHeight()),
                new Position(3221, 9934, getHeight())
        );
        for (int i = 0; i < 3; i++) {
            NPC growthling = new Growthling(GROWTHLING, possible_spawns.get(Misc.random(possible_spawns.size() - 1)), player);
            player.getInstance().add(growthling);
            growthling.startGraphic(new Graphic(188));
            growthling.attackEntity(player);
        }
    }

    @Override
    public void process() {
        setAttacks();
        super.process();

    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        if (this.spawnedBy != entity.getIndex()) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (p != null)
                    p.sendMessage(this.getName()+" isn't after you.");
            }
            return false;
        }
        return true;
    }

    public boolean isImmune(Player player) {
        List<NPC> npcs_in_area = player.getInstance().getNpcs();
        if (npcs_in_area == null)
            return false;
        return npcs_in_area.stream().filter(n -> n.getNpcId() == GROWTHLING).findAny().isPresent();

    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        if (entity instanceof NPC)
            return false;
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (p != null) {
                if (isImmune(p))
                    return false;

            }
        }
        return true;
    }

    @Override
    public boolean isFreezable() {
        return false;
    }

}

