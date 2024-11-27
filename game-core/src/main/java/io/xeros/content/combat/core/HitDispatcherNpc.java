package io.xeros.content.combat.core;

import io.xeros.content.bosses.Hunllef;
import io.xeros.content.bosses.Skotizo;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.AhrimEffect;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.KarilEffect;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.VeracsEffect;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.weapon.WeaponInterface;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.questing.hftd.DagannothMother;
import io.xeros.content.skills.mining.Pickaxe;
import io.xeros.model.entity.CombatType;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.EquipmentSet;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

public class HitDispatcherNpc extends HitDispatcher {

    public HitDispatcherNpc(Player attacker, Entity defender) {
        super(attacker, defender);
    }

    @Override
    public void beforeDamageCalculated(CombatType type) {
        NPC npc = (NPC) defender;
        attacker.getBossTimers().track(npc);
        StyleWarning.styleWarning(attacker, defender, type);
        int npcId = npc.getNpcId();
        if (type == CombatType.MELEE) {
            if (PvpWeapons.activateEffect(attacker, Items.VIGGORAS_CHAINMACE))
                PvpWeapons.degradeWeaponAfterCombat(attacker, false);
        }
        else if (type == CombatType.RANGE)
            // Craws bow
            if (PvpWeapons.activateEffect(attacker, Items.CRAWS_BOW))
                PvpWeapons.degradeWeaponAfterCombat(attacker, false);

            else if (attacker.getSpellId() > -1)
                if (PvpWeapons.activateEffect(attacker, Items.THAMMARONS_SCEPTRE))
                    PvpWeapons.degradeWeaponAfterCombat(attacker, true);

        if (npcId == Npcs.CORPOREAL_BEAST) {
            var weaponInterface = attacker.getCombatConfigs().getWeaponData().getWeaponInterface();
            if (!(type == CombatType.MELEE && (weaponInterface == WeaponInterface.SPEAR || weaponInterface == WeaponInterface.HALBERD))) {
                maximumDamage /= 2;
                attacker.sendMessage("@red@Corporeal beast can only be effectively damaged with spears or halberds!",
                    TimeUnit.MINUTES.toMillis(10));
            }
        }

        // Demonic gorilla
        if (type == CombatType.MELEE && npcId == Npcs.DEMONIC_GORILLA
            || type == CombatType.RANGE && npcId == Npcs.DEMONIC_GORILLA_7145
            || type == CombatType.MAGE && npcId == Npcs.DEMONIC_GORILLA_7146) {
            maximumDamage = 0;
            maximumAccuracy = 0;
        }

        switch (npcId) {
            case 6373:
            case DagannothMother.AIR_PHASE:
                if (!CombatSpellData.airSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            case DagannothMother.WATER_PHASE:
            case 6375:
                if (!CombatSpellData.waterSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            case DagannothMother.FIRE_PHASE:
            case 6376:
                if (!CombatSpellData.fireSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            case DagannothMother.EARTH_PHASE:
            case 6378:
                if (!CombatSpellData.earthSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            case DagannothMother.MELEE_PHASE:
            case 6374:
                if (!attacker.usingMelee) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            case DagannothMother.RANGE_PHASE:
            case 6377:
                if (!attacker.usingBow && !attacker.usingOtherRangeWeapons && !attacker.usingBallista) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;
        }

        if (npcId == Hunllef.MELEE_PROTECT && type == CombatType.MELEE
            || npcId == Hunllef.MAGE_PROTECT && type == CombatType.MAGE
            || npcId == Hunllef.RANGED_PROTECT && type == CombatType.RANGE) {
            maximumDamage = 0;
            maximumAccuracy = 0;
        }
    }

    /**
     * Anything that blocks damage totally should go in the method above and set
     * {@link HitDispatcher#maximumDamage} to zero.
     */
    @Override
    public void afterDamageCalculated(CombatType type, boolean successfulHit) {
        NPC defender = this.defender.asNPC();

        if (successfulHit)
            if (AhrimEffect.INSTANCE.canUseEffect(attacker))
                AhrimEffect.INSTANCE.useEffect(attacker, defender, null);
            else if (KarilEffect.INSTANCE.canUseEffect(attacker))
                KarilEffect.INSTANCE.useEffect(attacker, defender, new Damage(damage));
            else if (VeracsEffect.INSTANCE.canUseEffect(attacker))
                VeracsEffect.INSTANCE.useEffect(attacker, null, null);

        switch (type) {
            case MELEE:
                if (defender.getNpcId() == 6600) {
                    var pickaxe = Pickaxe.getBestPickaxe(attacker);
                    if (pickaxe != null && attacker.getItems().isWearingItem(pickaxe.getItemId()))
                        damage += Misc.random(pickaxe.getPriority() * 4);
                }

                switch (defender.getNpcId()) {
                    case 7930:
                    case 7931:
                    case 7932:
                    case 7933:
                    case 7934:
                    case 7935:
                    case 7936:
                    case 7937:
                    case 7938:
                    case 7939:
                    case 7940:
                        if (attacker.playerEquipment[Player.playerHands] == 21816 && attacker.braceletEtherCount <= 0)
                            attacker.getItems().equipItem(21817, 1, 9);
                        break;
                    case 7413://combat dummy
                        this.maximumAccuracy = 100.0;
                        damage = maximumDamage;
                        break;

                    case 2266:
                    case 2267:
                        damage = 0;
                        break;

                    case 965:
                        if (!EquipmentSet.VERAC.isWearingBarrows(attacker)) damage = 0;
                        break;
                }

                if (defender.getNpcId() == 5666) {
                    damage = damage / 4;
                    if (damage < 0)
                        damage = 0;
                }
                break;
            case RANGE:
                switch (defender.getNpcId()) {
                    case 7413://combat dummy
                        damage = maximumDamage;
                        maximumAccuracy = 100.0;
                        break;

                    case 2265:
                    case 2267:
                        damage = 0;
                        break;

                    case 8781:
                        int health1 = defender.getHealth().getCurrentHealth();
                        if (health1 > 500 && health1 < 600) {
                            defender.forceChat("YOU'LL NEVER TAKE ME DOWN!!");
                            defender.gfx0(1028);
                            defender.gfx0(1009);
                            attacker.attacking.reset();
                        }
                        break;
                    case 5890:
                        if (attacker.getSlayer().getTask().isEmpty()) return;
                        if (attacker.getSlayer().getTask().isEmpty() &&
                            !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal demon") &&
                            !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal sire")
                        ) {
                            attacker.sendMessage("The sire Fdoes not seem interested.");
                            attacker.attacking.reset();
                            return;
                        }
                        int health = defender.getHealth().getCurrentHealth();
                        if (health > 329) if (!CombatSpellData.shadowSpells(attacker)) {
                            attacker.sendMessage("This would not be effective, I should try shadow spells.");
                            attacker.attacking.reset();
                        }
                        break;
                }
                break;
            case MAGE:
                switch (defender.getNpcId()) {
                    case 7413://combat dummy
                        damage = maximumDamage;
                        maximumAccuracy = 100.0;
                        break;

                    case 5890:
                        if (attacker.getSlayer().getTask().isEmpty()) return;
                        if (attacker.getSlayer().getTask().isEmpty() &&
                            !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal demon") &&
                            !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal sire")
                        ) {
                            attacker.sendMessage("The sire does not seem interested.");
                            attacker.attacking.reset();
                            return;
                        }
                        int health = defender.getHealth().getCurrentHealth();
                        if (health > 329) if (!CombatSpellData.shadowSpells(attacker)) {
                            attacker.sendMessage("This would not be effective, I should try shadow spells.");
                            attacker.attacking.reset();
                        }
                        break;
                    case 2265:
                    case 2266:
                        damage = 0;
                        break;
                }
                break;
            default:
                break;
        }

        if (defender.getNpcId() == Skotizo.SKOTIZO_ID) damage = attacker.getSkotizo().calculateSkotizoHit(attacker, damage);

        if (!defender.canBeDamaged(attacker)) {
            damage = 0;
            if (damage2 > 0)
                damage2 = 0;
        }
    }

}
