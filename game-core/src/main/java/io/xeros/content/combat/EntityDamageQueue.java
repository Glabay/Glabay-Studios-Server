package io.xeros.content.combat;

import java.util.LinkedList;
import java.util.Queue;

import io.xeros.content.combat.core.HitExecutor;
import io.xeros.model.entity.player.Player;

/**
 * @author Jason MacKeigan
 * @date Nov 9, 2014, 10:02:13 AM
 */
public class EntityDamageQueue {
    /**
     * The queue containing all of the damage being dealt by the player
     */
    private final Queue<Damage> damageQueue = new LinkedList<>();

    /**
     * The damage dealer, the owner of the queue
     */
    private final Player player;

    /**
     * Creates a new class that will manage all damage dealt by the player
     *
     * @param player the player dealing the damage
     */
    public EntityDamageQueue(Player player) {
        this.player = player;
    }

    /**
     * Adds a damage object to the end of the queued damage list
     *
     * @param damage the damage to be dealt
     */
    public void add(Damage damage) {
        damageQueue.add(damage);
    }

    public void execute() {
        if (damageQueue.isEmpty()) return;
        Damage damage;
        Queue<Damage> updatedQueue = new LinkedList<>();
        while ((damage = damageQueue.poll()) != null) {
            damage.removeTick();
            if (damage.getTicks() == 1) {
                if ((player.alwaysHit && player.getOwnerDamage() != -1)) damage.setAmount(player.getOwnerDamage());
                else if (player.oneHit) damage.setAmount(damage.getTarget().getHealth().getMaximumHealth());
                HitExecutor.getDelayedHit(player, damage.getTarget(), damage).hit();
            } else if (damage.getTicks() > 1) {
                updatedQueue.add(damage);
            }
        }
        damageQueue.addAll(updatedQueue);
    }

    public Queue<Damage> getQueue() {
        return damageQueue;
    }
}
