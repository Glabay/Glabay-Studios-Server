package io.xeros.content.combat.formula.rework;

import io.xeros.content.combat.CombatConfigs;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.ToragsEffect;
import io.xeros.content.combat.effects.special.impl.ScytheOfVitur;
import io.xeros.content.combat.formula.MeleeMaxHit;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.combat.weapon.AttackStyle;
import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.content.commands.owner.SetAccuracyBonus;
import io.xeros.content.commands.owner.SetDefenceBonus;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.Task;
import io.xeros.model.entity.Bonus;
import io.xeros.model.entity.CombatType;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.EquipmentSet;
import io.xeros.util.Misc;

/**
 * The Combat formula implementation for Melee Combat
 *
 * @author Arthur Behesnilian 12:06 PM
 */
public class MeleeCombatFormula implements CombatFormula {

    public static final MeleeCombatFormula STANDARD = new MeleeCombatFormula();

    public static MeleeCombatFormula get() {
        return MeleeCombatFormula.STANDARD;
    }

    public static int[] BLACK_MASKS = {
            Items.BLACK_MASK, Items.BLACK_MASK_1, Items.BLACK_MASK_2, Items.BLACK_MASK_3, Items.BLACK_MASK_4,
            Items.BLACK_MASK_5, Items.BLACK_MASK_6, Items.BLACK_MASK_7, Items.BLACK_MASK_8, Items.BLACK_MASK_9,
            Items.BLACK_MASK_10
    };

    public static int[] BLACK_MASKS_I = {
            Items.BLACK_MASK_I, Items.BLACK_MASK_1_I, Items.BLACK_MASK_2_I, Items.BLACK_MASK_3_I, Items.BLACK_MASK_4_I,
            Items.BLACK_MASK_5_I, Items.BLACK_MASK_6_I, Items.BLACK_MASK_7_I, Items.BLACK_MASK_8_I,
            Items.BLACK_MASK_9_I, Items.BLACK_MASK_10_I
    };

    @Override
    public double getAccuracy(Entity attacker, Entity defender, double specialAttackMultiplier,
                              double defenceMultiplier) {
        int attack = getAttackRoll(attacker, defender, specialAttackMultiplier);
        int defence = (int) ((double) getDefenceRoll(attacker, defender) * defenceMultiplier);

        double accuracy;
        if (attack > defence) {
            accuracy = 1.0 - (defence + 2.0) / (2.0 * (attack + 1.0));
        } else {
            accuracy = attack / (2.0 * (defence + 1));
        }

        return accuracy;
    }

    @Override
    public int getMaxHit(Entity attacker, Entity defender, double specialAttackMultiplier,
                         double specialPassiveMultiplier) {

        double effectiveStrengthLevel = attacker.isPlayer() ?
                getEffectiveStrengthLevel(attacker.asPlayer()) : getEffectiveStrengthLevel(attacker.asNPC());
        double equipmentStrengthBonus = getEquipmentStrengthBonus(attacker);

        int maxHit = (int) Math.floor(0.5 + effectiveStrengthLevel * (equipmentStrengthBonus + 64) / 640);

        maxHit = applyStrengthSpecials(attacker, defender, maxHit, specialAttackMultiplier, specialPassiveMultiplier);
        maxHit = applyExtras(maxHit, attacker, defender);

        return (int) Math.floor(maxHit);
    }

    public int applyExtras(double maxHit, Entity attacker, Entity defender) {
        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();
            int weapon = player.getItems().getWeapon();

            if (weapon == 13265) {
                maxHit *= 0.90;
            }

            if (defender.isNPC()) {
                NPC npc = defender.asNPC();

            }
        }

        return (int) Math.floor(maxHit);
    }

    private int applyStrengthSpecials(Entity attacker, Entity defender, int base, double specialAttackMultiplier,
                                      double specialPassiveMultiplier) {

        double hit = (double) base;

        Player player = attacker.isPlayer() ? attacker.asPlayer() : null;

        if (player != null) {
            hit *= getEquipmentMultiplier(player);
            hit = Math.floor(hit);
        }

        hit *= specialAttackMultiplier;
        hit = Math.floor(hit);

        if (defender.isPlayer()) {
            if (CombatPrayer.isPrayerOn(defender.asPlayer(), CombatPrayer.PROTECT_FROM_MELEE)) {
                hit *= 0.6;
                hit = Math.floor(hit);
            }
        }

        if (specialAttackMultiplier == 1.0 && player != null) {
            hit = applyPassiveMultiplier(attacker, defender, hit);
            hit = Math.floor(hit);
        } else {
            hit *= specialPassiveMultiplier;
            hit = Math.floor(hit);
        }

        if (player != null) {
            hit *= getDamageDealMultiplier(player);
            hit = Math.floor(hit);
        }

        hit *= getDamageTakenMultiplier(defender);
        hit = Math.floor(hit);

        if (attacker.isPlayer() && defender.isNPC()) {
            hit *= getNpcMultipliers(attacker.asPlayer(), defender.asNPC());
            hit = Math.floor(hit);
        }

        return (int) hit;
    }

    private double getNpcMultipliers(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Helmet boost
        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.MELEE)) {
            multiplier += 0.1666;
        } else {
            // Salve Amulet boosts
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }

        // Arclight
        if (attacker.getCombatItems().hasArcLight())
            if (defender.isDemon()) multiplier += 0.7;

        // Dragon Hunter lance boost
        if (attacker.getItems().isWearingItem(Items.DRAGON_HUNTER_LANCE, Player.playerWeapon))
            if (defender.isDragon()) multiplier += 0.60;

        // Leaf-bladed battle axe boost
        if (attacker.getItems().isWearingItem(Items.LEAF_BLADED_BATTLEAXE, Player.playerWeapon))
            if (defender.isLeafy()) multiplier += 0.18;

        // Slayer Task boosts
        if (attacker.getSlayer().isTaskNpc(defender)) {
            Task task = attacker.getSlayer().getTask().get();

            if (task.isCrystalline())
                if (attacker.getItems().isWearingItem(Items.CRYSTALLINE_SIGNET, Player.playerRing))
                    multiplier += 0.10;

            // Black Mask boosts
            if (attacker.getItems().hasAnyItem(Player.playerHat, BLACK_MASKS))
                multiplier += 0.1666;
            else if (attacker.getItems().hasAnyItem(Player.playerHat, BLACK_MASKS_I))
                multiplier += 0.1666;
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.5;
            }
        }

        // Melee pet boosts
        boolean hasDarkMeleePet = PetHandler.hasDarkMeleePet(attacker);
        boolean hasMeleePet = PetHandler.hasMeleePet(attacker);

        if (hasDarkMeleePet)
            multiplier += 0.10;
        else if(hasMeleePet && Misc.isLucky(50))
            multiplier += 0.10;

        return multiplier;
    }

    private double getDamageTakenMultiplier(Entity defender) {
        if (defender.isPlayer()) {
            Player player = defender.asPlayer();

            if (player.getCombatItems().elyProc())
                return 0.75;

        }
        return 1.0;
    }

    private double getDamageDealMultiplier(Player player) {
        return 1.0;
    }

    private double applyPassiveMultiplier(Entity attacker, Entity defender, double base) {
        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();
            double multiplier = 1.0;

            if (EquipmentSet.DHAROK.isWearing(player)) {
                double lost = (player.getHealth().getMaximumHealth() - player.getHealth().getCurrentHealth()) / 100.0;
                double max = player.getHealth().getMaximumHealth() / 100.0;
                multiplier = 1.0 + (lost * max);
            } else if (EquipmentSet.VERAC.isWearingBarrows(player)) {
                return base + 1;
            }

            return base * multiplier;
        }
        return base;
    }

    private double getEquipmentStrengthBonus(Entity attacker) {
        if (attacker.isPlayer()) {
            return attacker.asPlayer().getBonus(Bonus.STRENGTH);
        } else {
            NPC npc = attacker.asNPC();
            NpcCombatDefinition definition = npc.getCombatDefinition();
            return definition == null ? 0 : definition.getAttackBonus(NpcBonus.STRENGTH_BONUS);
        }
    }

    private double getEffectiveStrengthLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.STRENGTH.getId()]) *
                getPrayerStrengthMultiplier(player);

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case AGGRESSIVE:
                effectiveLevel += 3.0;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }

        effectiveLevel += 8.0;

        if (player.fullVoidMelee()) {
            effectiveLevel *= 1.1;
        }

        if (MeleeMaxHit.hasObsidianEffect(player))
            effectiveLevel *= 1.2;

        // Inquisitor Set Effect
        if (player.getCombatItems().getInquisitorBonus() > 1.0) {
            effectiveLevel *= player.getCombatItems().getInquisitorBonus();
            effectiveLevel = Math.floor(effectiveLevel);
        }

        return Math.floor(effectiveLevel);
    }


    private double getEffectiveStrengthLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.STRENGTH) + 8;
    }

    private int getAttackRoll(Entity attacker, Entity defender, double specialAttackMultiplier) {
        int effectiveAttackLevel = attacker.isPlayer() ?
                getEffectiveAttackLevel(attacker.asPlayer()) : getEffectiveAttackLevel(attacker.asNPC());

        if (attacker.isPlayer() && defender.isNPC()) {
            effectiveAttackLevel += SetAccuracyBonus.MELEE_ATTACK - 8;

            if (ScytheOfVitur.usingScythe(attacker.asPlayer())) {
                effectiveAttackLevel += SetAccuracyBonus.SCYTHE_ATTACK_BONUS;
            }
        }

        double equipmentAttackBonus = getEquipmentAttackBonus(attacker);

        double maxRoll = effectiveAttackLevel * (equipmentAttackBonus + 64.0);
        if (attacker.isPlayer()) {
            maxRoll = applyAttackSpecials(attacker.asPlayer(), defender, maxRoll, specialAttackMultiplier);
        }

        return (int) ((int) maxRoll);
    }

    private int getDefenceRoll(Entity attacker, Entity defender) {
        double effectiveDefenceLevel = defender.isPlayer() ?
                getEffectiveDefenceLevel(defender.asPlayer()) : getEffectiveDefenceLevel(defender.asNPC());
        double equipmentDefenceBonus = getEquipmentDefenceBonus(attacker, defender);

        if (attacker.isNPC() && defender.isPlayer()) {
            effectiveDefenceLevel += SetDefenceBonus.MELEE_DEFENCE - 8;
        }

        double maxRoll = effectiveDefenceLevel * (equipmentDefenceBonus + 64.0);
        maxRoll = applyDefenceSpecials(defender, maxRoll);

        return (int) maxRoll;
    }

    private double applyDefenceSpecials(Entity defender, double base) {
        double hit = base;

        if (defender.isPlayer()) {
            Player player = defender.asPlayer();
            if (EquipmentSet.TORAG.isWearingBarrows(player) && EquipmentSet.AMULET_OF_THE_DAMNED.isWearing(player)) {
                hit *= ToragsEffect.modifyDefenceLevel(player);
                hit = Math.floor(hit);
            }
        }

        return hit;
    }

    private double getEquipmentDefenceBonus(Entity attacker, Entity defender) {
        CombatStyle combatStyle = CombatConfigs.getCombatStyle(attacker);

        Bonus bonus = Bonus.DEFENCE_STAB;
        switch (combatStyle) {
            case STAB:
                bonus = Bonus.DEFENCE_STAB;
                break;
            case SLASH:
                bonus = Bonus.DEFENCE_SLASH;
                break;
            case CRUSH:
                bonus = Bonus.DEFENCE_CRUSH;
                break;
        }

        return defender.getBonus(bonus);
    }

    private double getEffectiveDefenceLevel(Player player) {
        double effectiveLevel =
                Math.floor(player.playerLevel[Skill.DEFENCE.getId()]) * getPrayerDefenceMultiplier(player);

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case DEFENSIVE:
                effectiveLevel += 3;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }

        effectiveLevel += 8;

        return Math.floor(effectiveLevel);
    }

    private double getPrayerStrengthMultiplier(Player player) {
        if (CombatPrayer.isPrayerOn(player, CombatPrayer.BURST_OF_STRENGTH))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.SUPERHUMAN_STRENGTH))
            return 1.10;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.ULTIMATE_STRENGTH))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.CHIVALRY))
            return 1.18;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.PIETY))
            return 1.25;
        else
            return 1.0;
    }

    private double getPrayerDefenceMultiplier(Player player) {
        if (CombatPrayer.isPrayerOn(player, CombatPrayer.THICK_SKIN))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.ROCK_SKIN))
            return 1.10;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.STEEL_SKIN))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.CHIVALRY))
            return 1.20;
        else if (CombatPrayer.isPrayerOn(
                player,
                CombatPrayer.PIETY, CombatPrayer.RIGOUR, CombatPrayer.AUGURY
        ))
            return 1.25;
        else
            return 1.0;
    }

    private double getEffectiveDefenceLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.DEFENCE) + 8;
    }

    private int getEffectiveAttackLevel(Player attacker) {
        double effectiveLevel =
                Math.floor(attacker.playerLevel[Skill.ATTACK.getId()] * getPrayerAttackMultiplier(attacker));

        final int attackStyle = attacker.getCombatConfigs().getAttackStyle();

        switch (AttackStyle.forStyle(attackStyle)) {
            case ACCURATE:
                effectiveLevel += 3.0;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }

        effectiveLevel += 8.0;

        if (attacker.fullVoidMelee()) {
            effectiveLevel *= 1.1;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        // Inquisitor Set Effect
        if (attacker.getCombatItems().getInquisitorBonus() > 1.0) {
            effectiveLevel *= attacker.getCombatItems().getInquisitorBonus();
            effectiveLevel = Math.floor(effectiveLevel);
        }

        return (int) effectiveLevel;
    }

    private int getEffectiveAttackLevel(NPC attacker) {
        NpcCombatDefinition definition = attacker.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.ATTACK) + 8;
    }

    private double getPrayerAttackMultiplier(Player attacker) {
        if (CombatPrayer.isPrayerOn(attacker, CombatPrayer.CLARITY_OF_THOUGHT))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(attacker, CombatPrayer.IMPROVED_REFLEXES))
            return 1.1;
        else if (CombatPrayer.isPrayerOn(attacker, CombatPrayer.INCREDIBLE_REFLEXES))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(attacker, CombatPrayer.CHIVALRY))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(attacker, CombatPrayer.PIETY))
            return 1.20;
        else
            return 1.0;
    }

    private double getEquipmentAttackBonus(Entity attacker) {
        CombatStyle combatStyle = CombatConfigs.getCombatStyle(attacker);
        Bonus bonus = Bonus.attackBonusForCombatStyle(combatStyle);

        return attacker.getBonus(bonus);
    }


    private double applyAttackSpecials(Player attacker, Entity defender, double base, double specialAttackMultiplier) {
        double hit = base;

        hit *= getEquipmentMultiplier(attacker);
        hit = Math.floor(hit);

        hit *= specialAttackMultiplier;
        hit = Math.floor(hit);

        if (defender.isNPC()) {
            hit *= specialNpcAttackMultiplier(attacker, defender.asNPC());
            hit = Math.floor(hit);
        }

        return hit;
    }

    private double specialNpcAttackMultiplier(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Helmet boost
        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.MELEE)) {
            multiplier += 0.1666;
        } else {
            // Salve Amulet boosts
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }

        // Arclight
        if (attacker.getCombatItems().hasArcLight())
            if (defender.isDemon()) multiplier += 0.7;

        // Dragon Hunter lance boost
        if (attacker.getItems().isWearingItem(Items.DRAGON_HUNTER_LANCE, Player.playerWeapon))
            if (defender.isDragon()) multiplier += 0.60;

        if (attacker.getSlayer().isTaskNpc(defender)) {
            Task task = attacker.getSlayer().getTask().get();

            if (attacker.getSlayer().getTask().isPresent()) {
                // Black Mask boosts
                if (attacker.getItems().hasAnyItem(Player.playerHat, BLACK_MASKS))
                    multiplier += 0.1666;
                else if (attacker.getItems().hasAnyItem(Player.playerHat, BLACK_MASKS_I))
                    multiplier += 0.1666;
            }
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.5;
            }
        }

        return multiplier;
    }

    private double getEquipmentMultiplier(Player attacker) {
        return 1.0;
    }

}
