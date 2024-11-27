package io.xeros.content.combat.formula.rework;

import io.xeros.Configuration;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.AhrimEffect;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.commands.owner.SetAccuracyBonus;
import io.xeros.content.commands.owner.SetDefenceBonus;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.Bonus;
import io.xeros.model.entity.CombatType;
import io.xeros.model.definition.Items;
import io.xeros.model.SpellBook;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Arrays;

/**
 * @author Arthur Behesnilian 2:42 PM
 */
public class MagicCombatFormula implements CombatFormula {

    public static MagicCombatFormula STANDARD = new MagicCombatFormula();

    private int maxHit;

    public MagicCombatFormula() {
        this.maxHit = -1;
    }

    public MagicCombatFormula(int maxHit) {
        this.maxHit = maxHit;
    }

    @Override
    public double getAccuracy(Entity attacker, Entity defender, double specialAttackMultiplier,
                              double defenceMultiplier) {

        int attack = getAttackRoll(attacker, defender);
        int defence = (int) ((defender.isPlayer() ? getDefenceRoll(attacker, defender.asPlayer()) :
                getDefenceRoll(defender.asNPC())) * defenceMultiplier);

        double accuracy = 0;
        if (attack > defence) {
            accuracy = 1.0 - (defence + 2.0) / (2.0 * (attack + 1.0));
        } else {
            accuracy = attack / (2.0 * (defence + 1));
        }

        return accuracy;
    }

    private int getAttackRoll(Entity attacker, Entity defender) {
        double effectiveMagicLevel = attacker.isPlayer() ?
                getEffectiveAttackLevel(attacker.asPlayer()) : getEffectiveAttackLevel(attacker.asNPC());

        if (attacker.isPlayer() && defender.isNPC()) {
            effectiveMagicLevel += SetAccuracyBonus.MAGE_ATTACK - 8;
        }

        double equipmentAttackBonus = getEquipmentAttackBonus(attacker);

        double maxRoll = effectiveMagicLevel * (equipmentAttackBonus + 64);
        if (attacker.isPlayer()) {
            maxRoll = applyAttackSpecials(attacker.asPlayer(), maxRoll);
            maxRoll = Math.floor(maxRoll);

            if (defender.isNPC()) {
                maxRoll *= getNpcAttackMultiplier(attacker.asPlayer(), defender.asNPC());
                maxRoll = Math.floor(maxRoll);
            }
        }
        return (int) maxRoll;
    }

    private double getNpcAttackMultiplier(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Helmet boost
        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.MELEE)) {
            multiplier += 0.1666;
        } else {
            // Salve Amulet boosts
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 1.0;
            }
        }

        return multiplier;
    }

    private double applyAttackSpecials(Player attacker, double base) {
        double hit = base;

        hit *= getEquipmentMultiplier(attacker);
        hit = Math.floor(hit);

        if (attacker.getItems().isWearingItem(Items.MYSTIC_SMOKE_STAFF)) {
            hit *= 1.1;
            hit = Math.floor(hit);
        }

        return hit;
    }

    private double getEquipmentMultiplier(Player attacker) {
        return 1.0;
    }

    private double getEquipmentAttackBonus(Entity attacker) {
        return attacker.getBonus(Bonus.ATTACK_MAGIC);
    }

    private static int[] TRIDENTS = {
            Items.TRIDENT_OF_THE_SEAS, Items.TRIDENT_OF_THE_SEAS_E,
            Items.TRIDENT_OF_THE_SEAS_FULL, Items.TRIDENT_OF_THE_SEAS_FULL_2,
            Items.TRIDENT_OF_THE_SWAMP, Items.TRIDENT_OF_THE_SWAMP_E,
            Items.TRIDENT_OF_THE_SWAMP_E_2
    };

    private double getEffectiveAttackLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.MAGIC.getId()] * getPrayerAttackMultiplier(player));

        if (player.getItems().hasAnyItem(Player.playerWeapon, TRIDENTS)) {
            switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
                case ACCURATE:
                    effectiveLevel += 3.0;
                    break;
                case CONTROLLED:
                    effectiveLevel += 1.0;
                    break;
            }
        }

        effectiveLevel += 8.0;

        if (player.fullVoidMage()) {
            effectiveLevel *= 1.45;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        return Math.floor(effectiveLevel);
    }

    private double getPrayerAttackMultiplier(Player player) {
        if (CombatPrayer.isPrayerOn(player, CombatPrayer.MYSTIC_WILL))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.MYSTIC_LORE))
            return 1.1;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.MYSTIC_MIGHT))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.AUGURY))
            return 1.25;
        else
            return 1.0;
    }

    private double getEffectiveAttackLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.MAGIC) + 8;
    }

    private double getDefenceRoll(Entity attacker, Player player) {
        double effectiveDefenceLevel = getEffectiveDefenceLevel(player);

        if (attacker.isNPC()) {
            effectiveDefenceLevel += SetDefenceBonus.MAGE_DEFENCE - 8;
        }

        effectiveDefenceLevel *= 0.3;
        effectiveDefenceLevel = Math.floor(effectiveDefenceLevel);

        double magicLevel = player.playerLevel[Skill.MAGIC.getId()];
        magicLevel *= getPrayerAttackMultiplier(player);
        magicLevel = Math.floor(magicLevel);

        magicLevel *= 0.7;
        magicLevel = Math.floor(magicLevel);

        double effectiveMagicLevel = Math.floor(effectiveDefenceLevel + magicLevel + 8);
        double equipmentDefenceBonus = getEffectiveDefenceBonus(player);

        double maxRoll = effectiveMagicLevel * (equipmentDefenceBonus + 64);
        return maxRoll;
    }

    private double getEffectiveDefenceLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.DEFENCE.getId()] *
                getPrayerDefenceMultiplier(player));

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case DEFENSIVE:
                effectiveLevel += 3.0;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }

        effectiveLevel += 8.0;
        return Math.floor(effectiveLevel);
    }

    private double getPrayerDefenceMultiplier(Player player) {
        if (CombatPrayer.isPrayerOn(player, CombatPrayer.THICK_SKIN))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.ROCK_SKIN))
            return 1.1;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.STEEL_SKIN))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.CHIVALRY))
            return 1.2;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.PIETY))
            return 1.25;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.RIGOUR))
            return 1.25;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.AUGURY))
            return 1.25;
        else
            return 1.0;
    }

    private int getDefenceRoll(NPC npc) {
        double effectiveDefenceLevel = getEffectiveDefenceLevel(npc);
        double equipmentDefenceBonus = getEffectiveDefenceBonus(npc);

        double maxRoll = effectiveDefenceLevel * (equipmentDefenceBonus + 64);

        return (int) maxRoll;
    }

    public int getEffectiveDefenceLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.MAGIC) + 9;
    }

    private double getEffectiveDefenceBonus(Entity entity) {
        return entity.getBonus(Bonus.DEFENCE_MAGIC);
    }

    @Override
    public int getMaxHit(Entity attacker, Entity defender, double specialAttackMultiplier,
                         double specialPassiveMultiplier) {
        int[] spellData = null;
        double hit = 0;
        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();
            if (player.getSpellId() > -1 && !player.usingSpecial) {
                spellData = CombatSpellData.MAGIC_SPELLS[player.getSpellId()];
            } else {
                // Nightmare staff max hit
                int magicLevel = player.playerLevel[Skill.MAGIC.getId()];
                if (player.getCombatItems().usingNightmareStaffSpecial())
                    hit = MagicMaxHit.getNightmareSpecialMaxHit(magicLevel, 44);
                else if(player.getCombatItems().usingEldritchStaffSpecial())
                    hit = MagicMaxHit.getNightmareSpecialMaxHit(magicLevel, 39);
                else if (this.hasSanguinestiCharges(player)) {
                    hit = this.getSanguinestuMaxHit(player);
                } else if (this.hasTridentCharges(player)) {
                    hit = this.getTridentMaxHit(player);
                }
            }
        }
        if (this.maxHit != -1) {
            hit = this.maxHit;
        } else if (spellData != null && hit == 0) {
            hit = CombatSpellData.getBaseDamage(spellData);
        } else if (attacker.isNPC()) {
            NPC npc = attacker.asNPC();
            hit = npc.maxHit;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();
            int magicLevel = player.playerLevel[Skill.MAGIC.getId()];

            if (player.getItems().hasAnyItem(Player.playerWeapon, Items.TRIDENT_OF_THE_SEAS,
                    Items.TRIDENT_OF_THE_SEAS_E, Items.TRIDENT_OF_THE_SEAS_FULL)) {
                hit = Math.floor(magicLevel / 3.0) - 5.0;
            } else if (player.getItems().hasAnyItem(Player.playerWeapon, Items.TRIDENT_OF_THE_SWAMP,
                    Items.TRIDENT_OF_THE_SWAMP_E)) {
                hit = Math.floor(magicLevel / 3.0) - 2.0;
            }

            // God Spell bonus
            if (CombatSpellData.godSpells(player)) {
                if (System.currentTimeMillis() - player.godSpellDelay < Configuration.GOD_SPELL_CHARGE) {
                    hit += 10;
                }
            }

            if (spellData != null) {
                if (player.getItems().isWearingItem(Player.playerHands, Items.CHAOS_GAUNTLETS)) {
                    boolean isBoltSpell = CombatSpellData.isBoltSpell(spellData[0]);
                    if (isBoltSpell)
                        hit += 3;
                }
            }

            int magicDamageBonus = attacker.getBonus(Bonus.MAGIC_DMG);
            double multiplier = 1.0 + (magicDamageBonus / 100.0);

            // Tome of Fire bonus
            if (player.getCombatItems().hasTomeOfFire()) {
                boolean usingNightMareStaffSpecial = player.getCombatItems().usingNightmareStaffSpecial();
                if (CombatSpellData.fireSpells(player) && spellData != null) {
                    multiplier += 0.5;
                    player.getTomeOfFire().decrCharge();
                }
            }

            if (player.getItems().hasAnyItem(Player.playerAmulet, Items.AMULET_OF_THE_DAMNED_FULL,
                    Items.AMULET_OF_THE_DAMNED)) {
                boolean hasExtra = AhrimEffect.INSTANCE.hasExtraRequirement(player);
                if (hasExtra && Misc.random(4) == 1) {
                    multiplier += 0.3;
                }
            }

            if (player.getItems().isWearingItem(Player.playerWeapon, Items.MYSTIC_SMOKE_STAFF)
                    && player.getSpellBook() == SpellBook.MODERN) {
                multiplier += 0.1;
            }

            if (player.fullEliteVoidMage()) {
                multiplier += 0.025;
            }

            hit *= multiplier;
            hit = Math.floor(hit);

        } else if (attacker.isNPC()) {
            double multiplier = 1.0 + (attacker.getBonus(Bonus.MAGIC_DMG) / 100.0);
            hit += multiplier;
            hit = Math.floor(hit);
        }

        if (defender.isPlayer()) {
            Player player = defender.asPlayer();
            if (CombatPrayer.isPrayerOn(player, CombatPrayer.PROTECT_FROM_MAGIC)) {
                hit *= 0.6D;
                hit = Math.floor(hit);
            }
        }

        hit *= getDamageDealMultiplier(attacker);
        hit = Math.floor(hit);

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
        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.MAGE)) {
            multiplier += 0.1666;
        } else {
            // Salve Amulet boosts
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.25;
            }
        }

        // Pet boosts
        boolean hasDarkMagePet = PetHandler.hasDarkMagePet(attacker);
        boolean hasMagePet = PetHandler.hasMagePet(attacker);
        if (hasDarkMagePet)
            multiplier += 0.10;
        else if(hasMagePet && Misc.isLucky(50))
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

    private double getDamageDealMultiplier(Entity attacker) {
        return 1.0;
    }

    public boolean hasTridentCharges(Player player) {
        if (player.getItems().hasAnyItem(Player.playerWeapon, TRIDENTS)) {
            return player.getToxicTridentCharge() > 0 || player.getTridentCharge() > 0;
        }
        return false;
    }

    private static int[] TOXIC_TRIDENTS = {
            Items.TRIDENT_OF_THE_SWAMP, Items.TRIDENT_OF_THE_SWAMP_E, Items.TRIDENT_OF_THE_SEAS_FULL
    };

    private boolean isToxic(int weaponId) {
        return Arrays.stream(TOXIC_TRIDENTS).anyMatch(id -> id == weaponId);
    }

    public int getTridentMaxHit(Player player) {
        int weaponId = player.getItems().getWeapon();
        if (weaponId == -1)
            return 0;

        boolean isToxic = isToxic(weaponId);
        int baseHit = isToxic ? 23 : 20;
        int magicLevel = Math.max(75, player.getLevelForXP(player.playerXP[Skill.MAGIC.getId()]));
        int extraHits = (int) ((double) (magicLevel - 75) / 3);
        return baseHit + extraHits;
    }

    public boolean hasSanguinestiCharges(Player player) {
        if (player.getItems().hasAnyItem(Player.playerWeapon, Items.SANGUINESTI_STAFF,
                Items.SANGUINESTI_STAFF_UNCHARGED)) {
            return player.getSangStaffCharge() > 0;
        }
        return false;
    }

    public int getSanguinestuMaxHit(Player player) {
        int baseHit = 24;
        int magicLevel = Math.max(player.playerLevel[Skill.MAGIC.getId()], 75);
        int extraHits = (int) ((double) (magicLevel - 75) / 3);
        return baseHit + extraHits;
    }

}
