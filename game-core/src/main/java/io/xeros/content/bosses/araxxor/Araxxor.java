package io.xeros.content.bosses.araxxor;

import io.xeros.content.bosses.araxxor.attack.basic.CleaveAttack;
import io.xeros.content.bosses.araxxor.attack.basic.MagicAttack;
import io.xeros.content.bosses.araxxor.attack.basic.MeleeAttack;
import io.xeros.content.bosses.araxxor.attack.basic.RangedAttack;
import io.xeros.model.entity.Bonus;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.combat.CombatMethod;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class Araxxor extends NPC implements CombatMethod {

    private boolean isEnraged = false;
    private boolean isHatchTick = false;
    private int attackCounter = 0;
    private int minionIndex = 0;

    // TODO PrimaryAraxyte


    public Araxxor(Position position) {
        super(Npcs.ARAXXOR, position);
    }

    @Override
    public void process() {
        super.process();
        var targetHp = 0.25 * health.getMaximumHealth();
        if (health.getCurrentHealth() < targetHp && !isEnraged) {
            isEnraged = true;
            startAnimation(11488);
            // TODO: assume npc switches a 'varbit' to change legs to envenomed legs
        }
    }

    private Boolean attackingWithRange(Player target) {
        var mageDef = target.getBonus(Bonus.DEFENCE_MAGIC);
        var rangedDef = target.getBonus(Bonus.DEFENCE_RANGED);
        return rangedDef > mageDef;
    }

    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        if (isReachable(entity, target)) {
            if (isEnraged) // Cleave Attack
                new CleaveAttack().invoke(this, target);
            else // Melee attack
                new MeleeAttack().invoke(this, target);
        }
        else {
            // Distance Attack
            if (attackingWithRange(target))
                new RangedAttack().invoke(this, target);
            else
                new MagicAttack().invoke(this, target);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return isEnraged ? 4 : 6;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}
