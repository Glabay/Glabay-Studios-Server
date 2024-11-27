package io.xeros.content.combat.npc;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Lists;
import io.xeros.model.Animation;
import io.xeros.model.entity.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.projectile.ProjectileBase;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class NPCAutoAttack {

    /**
     * Default select players for multi attack
     */
    public static Function<NPCCombatAttack, List<Player>> getDefaultSelectPlayersForAttack() {
        return npcCombatAttack -> {
            NPC npc = npcCombatAttack.getNpc();
            List<Player> players = Lists.newArrayList();
            for (Player player : PlayerHandler.getPlayers()) {
                if (player != null && player.getInstance() == npc.getInstance()
                        && npc.getDistance(player.getX(), player.getY()) <= 16) {
                    players.add(player);
                }
            }
            return players;
        };
    }

    public static List<Player> getPlayers(NPC npc) {
        List<Player> players = Lists.newArrayList();
        for (Player player : PlayerHandler.getPlayers()) {
            if (player != null && player.getInstance() == npc.getInstance()
                    && npc.getDistance(player.getX(), player.getY()) <= 16) {
                players.add(player);
            }
        }
        return players;
    }

    private final Animation animation;
    private final ProjectileBase projectile;
    private final Graphic startGraphic;
    private final Graphic endGraphic;
    private final CombatType combatType;
    private final int hitDelay;
    private final int attackDelay;
    private final int maxHit;
    private final int poisonDamage;
    private final int distanceRequiredForAttack;
    private final boolean ignoreProjectileClipping;
    private final boolean multiAttack;

    /**
     * Called on the same tick that the npc does the attack animation and queues the hit.
     * {@link NPCAutoAttack#onHit} comes when the damage occurs.
     */
    private final Consumer<NPCCombatAttack> onAttack;

    /**
     * Called when the victim is hit, i.e. when damage is done to the victim.
     */
    private final Consumer<NPCCombatAttackHit> onHit;

    /**
     * Accuracy bonus. Added to 1.0. This defines the bonus (return 0.3 would be 1.3).
     */
    private final Function<NPCCombatAttack, Double> accuracyBonus;

    /**
     * Max hit bonus. Added to 1.0. This defines the bonus (return 0.3 would be 1.3).
     */
    private final Function<NPCCombatAttack, Double> maxHitBonus;

    /**
     * Prayer protection percentage, 0 (default) would block all damage, 1.0 would allow all damage).
     */
    private final Function<NPCCombatAttack, Double> prayerProtectionPercentage;

    private final Function<NPCCombatAttack, List<Player>> selectPlayersForMultiAttack;
    private final Function<NPCCombatAttack, Boolean> selectAutoAttack;

    private final boolean attackDamagesPlayer;

    private final Function<NPCAutoAttackDamage, Integer> modifyDamage;

    public NPCAutoAttack(Animation animation, ProjectileBase projectile, Graphic startGraphic, Graphic endGraphic, CombatType combatType,
                         int hitDelay, int attackDelay, int maxHit, int poisonDamage, int distanceRequiredForAttack, boolean ignoreProjectileClipping,
                         boolean multiAttack, Consumer<NPCCombatAttack> onAttack, Consumer<NPCCombatAttackHit> onHit, Function<NPCCombatAttack,
            Double> accuracyBonus, Function<NPCCombatAttack, Double> maxHitBonus, Function<NPCCombatAttack, Double> prayerProtectionPercentage,
                         Function<NPCCombatAttack, List<Player>> selectPlayersForMultiAttack, Function<NPCCombatAttack, Boolean> selectAutoAttack, boolean attackDamagesPlayer,
                         Function<NPCAutoAttackDamage, Integer> modifyDamage) {
        this.animation = animation;
        this.projectile = projectile;
        this.startGraphic = startGraphic;
        this.endGraphic = endGraphic;
        this.combatType = combatType;
        this.hitDelay = hitDelay;
        this.attackDelay = attackDelay;
        this.maxHit = maxHit;
        this.poisonDamage = poisonDamage;
        this.distanceRequiredForAttack = distanceRequiredForAttack;
        this.ignoreProjectileClipping = ignoreProjectileClipping;
        this.multiAttack = multiAttack;
        this.onAttack = onAttack;
        this.onHit = onHit;
        this.accuracyBonus = accuracyBonus;
        this.maxHitBonus = maxHitBonus;
        this.prayerProtectionPercentage = prayerProtectionPercentage;
        this.selectPlayersForMultiAttack = selectPlayersForMultiAttack;
        this.selectAutoAttack = selectAutoAttack;
        this.attackDamagesPlayer = attackDamagesPlayer;
        this.modifyDamage = modifyDamage;
    }

    public Animation getAnimation() {
        return animation;
    }

    public ProjectileBase getProjectile() {
        return projectile;
    }

    public Graphic getStartGraphic() {
        return startGraphic;
    }

    public Graphic getEndGraphic() {
        return endGraphic;
    }

    public CombatType getCombatType() {
        return combatType;
    }

    public int getHitDelay() {
        return hitDelay;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public int getMaxHit() {
        return maxHit;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public int getDistanceRequiredForAttack() {
        return distanceRequiredForAttack;
    }

    public boolean isIgnoreProjectileClipping() {
        return ignoreProjectileClipping;
    }

    public boolean isMultiAttack() {
        return multiAttack;
    }

    public Consumer<NPCCombatAttack> getOnAttack() {
        return onAttack;
    }

    public Consumer<NPCCombatAttackHit> getOnHit() {
        return onHit;
    }

    public Function<NPCCombatAttack, Double> getAccuracyBonus() {
        return accuracyBonus;
    }

    public Function<NPCCombatAttack, Double> getMaxHitBonus() {
        return maxHitBonus;
    }

    public Function<NPCCombatAttack, Double> getPrayerProtectionPercentage() {
        return prayerProtectionPercentage;
    }

    public Function<NPCCombatAttack, List<Player>> getSelectPlayersForMultiAttack() {
        return selectPlayersForMultiAttack;
    }

    public Function<NPCCombatAttack, Boolean> getSelectAutoAttack() {
        return selectAutoAttack;
    }

    public boolean isAttackDamagesPlayer() {
        return attackDamagesPlayer;
    }

    public Function<NPCAutoAttackDamage, Integer> getModifyDamage() {
        return modifyDamage;
    }
}