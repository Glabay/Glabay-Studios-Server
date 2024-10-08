package io.xeros.content.combat.core;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.hydra.HydraStage;
import io.xeros.content.bosses.nightmare.attack.Spores;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.MagicRequirements;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.combat.magic.data.AncientSpells;
import io.xeros.content.combat.magic.data.ModernSpells;
import io.xeros.content.combat.magic.data.PoweredStaffSpellData;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.combat.range.ArrowDrawBack;
import io.xeros.content.combat.range.Bow;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.combat.specials.Specials;
import io.xeros.content.combat.weapon.AttackStyle;
import io.xeros.content.combat.weapon.RangedWeaponType;
import io.xeros.content.items.Degrade;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.skills.herblore.PoisonedWeapon;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.model.*;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.AnimationLength;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ItemStats;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.EntityReference;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCClipping;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.*;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;
import io.xeros.model.projectile.ProjectileEntity;
import io.xeros.util.Misc;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static io.xeros.content.combat.Hitmark.HIT;

public class AttackEntity {

    private static final Logger log = LoggerFactory.getLogger(AttackEntity.class);
    private final Player attacker;
    private Position src;
    private Position dest;

    public AttackEntity(Player attacker) {
        this.attacker = attacker;
    }

    public int getAttackDelay() {
        int weapon = attacker.playerEquipment[Player.playerWeapon];
        if (attacker.usingMagic) {
            switch (attacker.getSpellId()) {
                case 98://Sanguinisti Staff
                case 52://Trident
                case 53://Trident
                    return 4;
            }
            switch (attacker.playerEquipment[Player.playerWeapon]) {
                case 24423://Harmonized Staff
                    return 4;
            }

            return 5;
        }

        int attackSpeed = ItemStats.forId(weapon).getEquipment().getAttackSpeed();
        if (attacker.playerAttackingIndex > 0 && attacker.playerEquipment[Player.playerWeapon] == 12926) {
            return attackSpeed + 1;
        }
        if (weapon == -1 || attackSpeed <= 0) {
            return 4;
        } else {
            return attackSpeed;
        }
    }

    public CombatType getCombatType() {
        if (attacker.usingMagic) {
            return CombatType.MAGE;
        } else if (attacker.usingBow || attacker.usingOtherRangeWeapons || attacker.usingCross || attacker.usingBallista) {
            return CombatType.RANGE;
        } else {
            return CombatType.MELEE;
        }
    }

    public void reset() {
        attacker.playerAttackingIndex = 0;
        attacker.npcAttackingIndex = 0;
        //System.err.println("changing magic to false...222 " + new Throwable().getStackTrace()[1].toString());
        //attacker.usingMagic = false;
        attacker.usingClickCast = false;
        attacker.faceUpdate(0);
        attacker.getPA().resetFollow();
        attacker.getPA().resetClickCast();
        // attacker.setTargeted(null);
        // attacker.getPA().sendEntityTarget(0, null);
    }

    private void resetStyle() {
        attacker.usingMagic = false;
        attacker.usingMelee = false;
        attacker.usingRangeWeapon = false;
        attacker.usingArrows = false;
        attacker.usingOtherRangeWeapons = false;
        attacker.usingCross = false;
        attacker.usingBallista = false;
        attacker.usingBow = false;
    }

    private boolean determineCombatStyle() {
        resetStyle();
        if (!attacker.usingClickCast && attacker.autocasting) {
            attacker.setSpellId(attacker.autocastId);
            attacker.usingMagic = true;
        } else if (attacker.getCombatItems().usingNightmareStaffSpecial()
                || attacker.getSpellId() > -1
                || attacker.getCombatItems().usingEldritchStaffSpecial()
        ) {
            attacker.usingMagic = true;
        }

        if (!determineWeaponStyle()) {
            return false;
        }

        attacker.usingMelee = getCombatType() == CombatType.MELEE;
        return true;
    }

    public boolean hasDistanceAndPathToAttack(Entity entity, boolean followingLogic) {
        if (!determineCombatStyle()) {
            return false;
        }

        if (attacker.attacking.checkAttackDistance(entity, followingLogic, attacker)) {
            boolean projectile = getCombatType() != CombatType.MELEE;
            int dir = NPCClipping.getDirection(attacker.getX(), attacker.getY(), entity.getX(), entity.getY());

            return !projectile && entity.getRegionProvider().canMove(attacker.getX(), attacker.getY(), attacker.getHeight(), NPCClipping.getDirection(attacker.getX(), attacker.getY(), entity.getX(), entity.getY()), entity.isNPC())
                    || PathChecker.raycast(attacker, entity, true)
                    || PathChecker.raycast(entity, attacker, true);
        }
        return false;
    }

    public void stopCombatMovement() {
        Entity entity = null;
        if (attacker.npcAttackingIndex > 0) {
            entity = NPCHandler.npcs[attacker.npcAttackingIndex];
        } else if (attacker.playerAttackingIndex > 0) {
            entity = PlayerHandler.players[attacker.playerAttackingIndex];
        }

        if (entity != null && hasDistanceAndPathToAttack(entity, true)) {
            attacker.resetWalkingQueue();
        }
    }

    public boolean attackEntityCheck(Entity targetEntity, boolean sendMessages) {
        determineCombatStyle();
        if (targetEntity.isNPC()) {
            return AttackNpcCheck.check(attacker, targetEntity, sendMessages);
        } else {
            return AttackPlayerCheck.check(attacker, targetEntity, sendMessages);
        }
    }

    public void attackEntity(Entity targetEntity) {
        if (targetEntity == null || !attackEntityCheck(targetEntity, true) || attacker.teleTimer > 0 || attacker.respawnTimer > 0
                || targetEntity.getHealth().getMaximumHealth() <= 0 || targetEntity.isDead) {
            //attacker.stopMovement();
            reset();
            return;
        }
        if (Boundary.isIn(attacker, Boundary.HESPORI) && Hespori.TOTAL_ESSENCE_BURNED < Hespori.ESSENCE_REQUIRED) {
            attacker.sendMessage("Hespori requires " + (Hespori.ESSENCE_REQUIRED - Hespori.TOTAL_ESSENCE_BURNED) + " " +
                    "more essence to be burned.");
            attacker.stopMovement();
            reset();
            return;
        }

        if (Boundary.isIn(attacker, Boundary.HESPORI)) {
            int amount = attacker.getItems().getItemAmount(9699);
            int scaleAmount = Boundary.getPlayersInBoundary(Boundary.HESPORI) / 3;
            int setBurnAmount = Misc.random((Hespori.TOXIC_GEM_AMOUNT * amount) / scaleAmount); //player max hit is equal to amount of used toxic gems * 3
            if (attacker.getItems().playerHasItem(9699, 1)) {
                attacker.setHesporiDamageCounter(attacker.getHesporiDamageCounter() + (5 * amount));
                attacker.getItems().deleteItem2(9699, amount);
                attacker.startAnimation(929);
                if (!attacker.getMode().isOsrs() && !attacker.getMode().is5x()) {
                    attacker.getPA().addSkillXP(1000 * amount, 20, true);
                    attacker.getPA().addSkillXP(2400 * amount, 13, true);
                    attacker.getPA().addSkillXP(2400 * amount, 14, true);
                } else {
                    attacker.getPA().addSkillXP(20 * amount, 20, true);//runecrafting
                    attacker.getPA().addSkillXP(60 * amount, 13, true);//smithing
                    attacker.getPA().addSkillXP(60 * amount, 14, true);//mining
                }
                if (Hespori.TOXIC_GEM_AMOUNT < 100) {
                    attacker.sendMessage("@blu@Hesporis defence is still very high!");
                    targetEntity.appendDamage(attacker, Misc.random(5 * amount), HIT);
                } else {
                    targetEntity.appendDamage(attacker, setBurnAmount, HIT);
                    attacker.sendMessage("@blu@Your burning runes vanish as they damages Hespori.");

                }
            } else if (attacker.getItems().playerHasItem(23783, 1)) {
                Hespori.useToxicGem(attacker);
            } else {
                attacker.sendMessage("@blu@You have no burning runes or toxic gems to use on Hespori.");
            }
            attacker.stopMovement();
            reset();
            return;
        }
        // Dragon spear delay?
        if (System.currentTimeMillis() - attacker.lastSpear < 3000) {
            attacker.attacking.reset();
            return;
        }

        if (attacker.getInterfaceEvent().isActive()) {
            attacker.sendMessage("Please finish what you're doing.");
            attacker.stopMovement();
            reset();
            return;
        }

        if (attacker.isInvisible() && !attacker.getRights().isOrInherits(Right.OWNER)) {
            attacker.sendMessage("You cannot attack npcs while being invisible.");
            attacker.stopMovement();
            reset();
            return;
        }
        if (attacker.isNpc == true) {
            attacker.sendMessage("You cannot attack npcs in this form.");
            attacker.stopMovement();
            reset();
            return;
        }
        attacker.getPA().setFollowingEntity(targetEntity, true);

        if (targetEntity.isNPC() && !attacker.usingClickCast) {
            if (attacker.isWearingWeapon(Items.TRIDENT_OF_THE_SEAS)) {
                attacker.usingMagic = true;
                attacker.autocasting = true;
                attacker.setSpellId(52);
            }
            if (attacker.isWearingWeapon(Items.TRIDENT_OF_THE_SWAMP)) {
                attacker.usingMagic = true;
                attacker.autocasting = true;
                attacker.setSpellId(53);
            }
            if (attacker.isWearingWeapon(Items.SANGUINESTI_STAFF)) {
                attacker.usingMagic = true;
                attacker.autocasting = true;
                attacker.setSpellId(SanguinestiStaff.COMBAT_SPELL_INDEX);
            }
            if (attacker.isWearingWeapon(27275)) {
                attacker.usingMagic = true;
                attacker.autocasting = true;
                attacker.setSpellId(99);
            }
        }

        if (!hasDistanceAndPathToAttack(targetEntity, false)) {
            return;
        }

        if (attacker.attackTimer > 0) {
            return;
        }

        if (getCombatType() == CombatType.MAGE && !MagicRequirements.checkMagicReqs(attacker, attacker.getSpellId(),
                true)) {
            reset();
            return;
        }
        if (targetEntity.getHealth().getCurrentHealth() <= 0) {
            return;
        }
        // Successful attack starts here
        attacker.weaponUsedOnAttack = attacker.playerEquipment[Player.playerWeapon];
        attacker.arrowUsedOnAttack = attacker.playerEquipment[Player.playerArrows];

        handleItemChangesOnAttack(targetEntity);
        attacker.attackTimer = getAttackDelay() + (Spores.isInfected(attacker) ? 1 : 0);
        attacker.hitDelay = MeleeData.getHitDelay(attacker);
        attacker.faceEntity(targetEntity);
        attacker.lastAttackedEntity = EntityReference.getReference(targetEntity);
        attacker.logoutDelay = System.currentTimeMillis();

        if (targetEntity.isPlayer()) {
            // Player specific actions
            Player targetPlayer = targetEntity.asPlayer();
            targetPlayer.underAttackByPlayer = attacker.getIndex();
            targetPlayer.logoutDelay = System.currentTimeMillis();
            targetPlayer.singleCombatDelay = System.currentTimeMillis();
            targetPlayer.killerId = attacker.getIndex();

            if (!Boundary.isIn(attacker, Boundary.DUEL_ARENA) && !TourneyManager.getSingleton().isInArena(attacker)) {
                if (!attacker.attackedPlayers.contains(attacker.playerAttackingIndex) && !PlayerHandler.players[attacker.playerAttackingIndex].attackedPlayers.contains(attacker.getIndex())) {
                    attacker.attackedPlayers.add(attacker.playerAttackingIndex);
                    attacker.isSkulled = true;
                    attacker.skullTimer = Configuration.SKULL_TIMER;
                    attacker.headIconPk = 0;
                    attacker.getPA().requestUpdates();
                }
            }
        } else if (targetEntity.isNPC()) {
            // NPC specific actions
            NPC targetNpc = targetEntity.asNPC();
            targetNpc.underAttackBy = attacker.getIndex();
            targetNpc.lastDamageTaken = System.currentTimeMillis();
            targetNpc.underAttack = true;

            if (NPCHandler.transformOnAttack(targetNpc)) {
                return;
            }

            if (getCombatType() == CombatType.MELEE && (targetNpc.getNpcId() == 3162 || targetNpc.getNpcId() == 3169
                    || targetNpc.getNpcId() == 3164 || targetNpc.getNpcId() == 3165 || targetNpc.getNpcId() == 3163)) {
                return;
            }

        }

        // Adds the PvP HP overlay when attacking targets
        if (attacker.getTargeted() == null || !attacker.getTargeted().equals(targetEntity)) {
            boolean addOverlay = true;
            if (targetEntity.isNPC()) {
                if (targetEntity.asNPC().getNpcId() == Npcs.UNDEAD_COMBAT_DUMMY) {
                    addOverlay = false;
                }
            }

            if (addOverlay) {
                attacker.setTargeted(targetEntity);
                int targetState = targetEntity.isNPC() ? 1 : targetEntity.isPlayer() ? 2 : 0;
                attacker.getPA().sendEntityTarget(targetState, targetEntity);
            }
        }

        // Special attack
        Special special = null;
        if (attacker.usingSpecial
                && (!attacker.usingMagic
                || attacker.getCombatItems().usingNightmareStaffSpecial()
                || attacker.getCombatItems().usingEldritchStaffSpecial())) {
            if (Boundary.isIn(attacker, Boundary.DUEL_ARENA)) {
                DuelSession session =
                        (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(attacker,
                                MultiplayerSessionType.DUEL);
                if (Objects.nonNull(session)) {
                    if (session.getRules().contains(DuelSessionRules.Rule.NO_SPECIAL_ATTACK)) {
                        attacker.sendMessage("Special attacks have been disabled during this duel!");
                        attacker.usingSpecial = false;
                        attacker.getItems().updateSpecialBar();
                        attacker.attacking.reset();
                        return;
                    }

                    Optional<Integer> optional =
                            PoisonedWeapon.getOriginal(attacker.playerEquipment[Player.playerWeapon]);
                    if (optional.isPresent()) {
                        int item = optional.get();
                        if (item == 1249) {
                            attacker.sendMessage("You cannot use this special attack whilst in the duel arena.");
                            attacker.usingSpecial = false;
                            attacker.getItems().updateSpecialBar();
                            attacker.attacking.reset();
                            return;
                        }
                    }
                }
            }

            special = Specials.forWeaponId(attacker.playerEquipment[Player.playerWeapon]);
            if (special == null) {
                return;
            }

            if (special.getRequiredCost() > attacker.specAmount) {
                attacker.sendMessage("You don't have enough power left.");
                attacker.usingSpecial = false;
                attacker.getItems().updateSpecialBar();
                attacker.getItems().addSpecialBar(attacker.playerEquipment[Player.playerWeapon]);
                attacker.npcAttackingIndex = 0;
                attacker.attacking.reset();
                return;
            }

            attacker.specAmount -= special.getRequiredCost();
            HitDispatcher.getHitEntity(attacker, targetEntity).playerHitEntity(getCombatType(), special);

            attacker.usingSpecial = false;
            attacker.getItems().updateSpecialBar();
            attacker.getItems().addSpecialBar(attacker.playerEquipment[Player.playerWeapon]);
        } else {
            // Standard auto attacks
            if (getCombatType() == CombatType.MAGE) {

                int spellId = attacker.getSpellId();

                if (!Boundary.isInMageArena(attacker) && attacker.usingGodSpell() && !attacker.getRights().isOrInherits(Right.OWNER)) {
                    if (!attacker.hasUnlockedGodSpell(spellId)) {
                        attacker.sendMessage("You are not yet experienced enough to use this spell outside the Mage " +
                                "Arena.");
                        attacker.attacking.reset();
                        return;
                    }
                }
                if (Boundary.isInMageArena(attacker) && attacker.usingGodSpell()) {
                    String spellName = null;
                    if (spellId == 30) {
                        if (++attacker.flamesOfZamorakCasts == 100)
                            spellName = "Flames of Zamorak";
                    } else if (spellId == 29) {
                        if (++attacker.clawsOfGuthixCasts == 100)
                            spellName = "Claws of Guthix";
                    } else if (spellId == 28) {
                        if (++attacker.saradominStrikeCasts == 100)
                            spellName = "Saradomin Strike";
                    }
                    if (spellName != null) {
                        attacker.sendMessage("You can now cast " + spellName + " outside the Arena.");
                    }
                }

                attacker.oldSpellId = attacker.getSpellId();
                sendMagicProjectileEntity(targetEntity);
            } else {
                int animationId = MeleeData.getWepAnim(attacker);
                int animationDelay = AnimationLength.getFrameLength(animationId);
                attacker.getAnimationTimer().setDuration(animationDelay);
                attacker.startAnimation(new Animation(animationId, 0, AnimationPriority.HIGH));
                // BandAid TODO: Fix Client side, new Animation system seems to continuously repeat itself with some animations
                CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (container.getTotalTicks() == animationDelay -1) {
                            attacker.startAnimation(Animation.RESET_ANIMATION);
                            container.stop();
                        }
                    }
                }, 1);

                if (getCombatType() == CombatType.RANGE) {
                    if (attacker.usingOtherRangeWeapons || attacker.usingBow || attacker.usingCross || attacker.usingBallista) {
                        if (attacker.getCombatConfigs().getWeaponMode().getAttackStyle() == AttackStyle.AGGRESSIVE) {
                            attacker.attackTimer--;
                        }
                    }

                    fireRangeProjectile(targetEntity);

                    if (attacker.playerEquipment[Player.playerWeapon] >= 4212 && attacker.playerEquipment[Player.playerWeapon] <= 4223) {
                        attacker.crystalBowArrowCount++;
                    } else {
                        if (attacker.usingOtherRangeWeapons) {
                            attacker.getItems().deleteEquipment();
                        } else {
                            attacker.getItems().deleteArrow();
                        }

                        // Delete another arrow for double shot weapons!
                        if (attacker.playerEquipment[3] == 11235 || attacker.playerEquipment[3] == 12765
                                || attacker.playerEquipment[3] == 12766 || attacker.playerEquipment[3] == 12767
                                || attacker.playerEquipment[3] == 12768) {
                            attacker.getItems().deleteArrow();
                        }
                    }
                }
            }

            // Queue hit
            HitDispatcher.getHitEntity(attacker, targetEntity).playerHitEntity(getCombatType(), null);
        }

        if (attacker.usingOtherRangeWeapons || attacker.usingBow) {
            if (attacker.getCombatConfigs().getWeaponMode().getAttackStyle() == AttackStyle.AGGRESSIVE) {
                attacker.attackTimer--;
            }
        }

        if (targetEntity.getAnimationTimer().isFinished()) {
            if (targetEntity.hasBlockAnimation())
                targetEntity.startAnimation(targetEntity.getBlockAnimation());
        }

        // Reset spell id after attack and end attacking if click casting
        if (special == null && getCombatType() == CombatType.MAGE) {
            attacker.setSpellId(-1);
            if (attacker.usingClickCast) {
                attacker.usingClickCast = false;
                if (!attacker.autocasting)
                    reset();
                attacker.faceEntity(targetEntity);
                attacker.stopMovement();
            }
        }

        // Rapid ranged

        if (attacker.bowSpecShot <= 0) {
            attacker.weaponUsedOnAttack = 0;
            attacker.bowSpecShot = 0;
        }
        if (attacker.bowSpecShot != 0) {
            attacker.bowSpecShot = 0;
        }

    }

    public RangedWeaponType getRangedWeaponType() {
        if (getCombatType() == CombatType.RANGE) {
            int weapon = attacker.playerEquipment[3];
            if (attacker.usingOtherRangeWeapons) {
                return RangedWeaponType.THROWN;
            } else if (Arrays.stream(RangeData.NO_ARROW_DROP).anyMatch(id -> id == weapon)) {
                return RangedWeaponType.NO_ARROWS;
            } else if (Arrays.stream(RangeData.DOUBLE_SHOT_BOWS).anyMatch(id -> id == weapon)) {
                return RangedWeaponType.DOUBLE_SHOT;
            }
        }

        return RangedWeaponType.SHOT;
    }

    /**
     * Called when {@link AttackEntity#attacker} hits {@param targetEntity}.
     */
    private void handleItemChangesOnAttack(Entity targetEntity) {
        Degrade.degrade(attacker, getCombatType(), true);
        attacker.getCombatItems().checkVenomousItems(targetEntity);
        attacker.getCombatItems().checkDemonItems();

        if (!attacker.usingMagic) {
            attacker.getCombatItems().checkBlowpipe();

            PvpWeapons.degradeWeaponAfterCombat(attacker, false);

        } else {
            if (targetEntity.isNPC()) {
                if (attacker.playerEquipment[Player.playerWeapon] == Items.TRIDENT_OF_THE_SEAS) {
                    attacker.setTridentCharge(attacker.getTridentCharge() - 1);
                } else if (attacker.playerEquipment[Player.playerWeapon] == Items.TRIDENT_OF_THE_SWAMP) {
                    attacker.setToxicTridentCharge(attacker.getToxicTridentCharge() - 1);
                } else if (attacker.playerEquipment[Player.playerWeapon] == Items.SANGUINESTI_STAFF) {
                    attacker.setSangStaffCharge(attacker.getSangStaffCharge() - 3);
                    if (attacker.getSangStaffCharge() < 0) {
                        attacker.setSangStaffCharge(0);
                    }
                }
            }

            PvpWeapons.degradeWeaponAfterCombat(attacker, true);
        }

        // crystal bow
        if (attacker.usingBow && attacker.usingBow && Configuration.CRYSTAL_BOW_DEGRADES) {
            // degrading
            if (attacker.playerEquipment[Player.playerWeapon] == 4212) { // new
                attacker.getItems().equipItem(4214, 1, 3);
            }
            if (attacker.crystalBowArrowCount >= 250) {
                switch (attacker.playerEquipment[Player.playerWeapon]) {

                    case 4223: // 1/10 bow
                        attacker.getItems().equipItem(-1, 1, 3);
                        attacker.sendMessage("Your crystal bow has fully degraded.");
                        attacker.getItems().addItemUnderAnyCircumstance(4207, 1);
                        attacker.crystalBowArrowCount = 0;
                        break;

                    default:
                        attacker.getItems().equipItem(++attacker.playerEquipment[Player.playerWeapon], 1, 3);
                        attacker.sendMessage("Your crystal bow degrades.");
                        attacker.crystalBowArrowCount = 0;
                        break;
                }
            }
        }
    }

    public void sendMagicProjectileEntity(Entity target) {
        if (attacker.getSpellId() > -1) {
            Graphic.GraphicHeight startGraphicHeight = Graphic.GraphicHeight.HIGH;
            Graphic.GraphicHeight endGraphicHeight = Graphic.GraphicHeight.HIGH;
            ModernSpells findModernSpellData = ModernSpells.findSpellProjectileData(attacker.getSpellId(), endGraphicHeight);
            AncientSpells findAncientSpellData = AncientSpells.findSpellProjectileData(attacker.getSpellId(), startGraphicHeight, endGraphicHeight);
            PoweredStaffSpellData findPoweredStaffSpellData = PoweredStaffSpellData.findSpellProjectileData(attacker.getSpellId(), endGraphicHeight);
            ProjectileEntity projectile = null;
            if (findModernSpellData != null && attacker.getSpellId() == findModernSpellData.spellID) {
                int distance = (int) attacker.getCenterPosition().getAbsDistance(target.getCenterPosition());
                int duration = findModernSpellData.startSpeed + -5 + (findModernSpellData.stepMultiplier * distance);
                int animationDelay = AnimationLength.getFrameLength(findModernSpellData.castAnimation) + 1;
                attacker.getAnimationTimer().setDuration(animationDelay);
                attacker.startAnimation(findModernSpellData.castAnimation);
                attacker.gfx100(findModernSpellData.startGraphic);
                projectile = new ProjectileEntity(attacker, target, findModernSpellData.projectile, findModernSpellData.startSpeed, duration, findModernSpellData.startHeight, findModernSpellData.endHeight, 1, findModernSpellData.stepMultiplier, true);
                target.startGraphic(new Graphic(findModernSpellData.endGraphic, projectile.getSpeed()));
            } else if (findAncientSpellData != null && attacker.getSpellId() == findAncientSpellData.spellID) {
                int distance = (int) attacker.getCenterPosition().getAbsDistance(target.getCenterPosition());
                int duration = findAncientSpellData.startSpeed + -5 + (findAncientSpellData.stepMultiplier * distance);
                int animationDelay = AnimationLength.getFrameLength(findAncientSpellData.castAnimation) + 1;
                attacker.getAnimationTimer().setDuration(animationDelay);
                attacker.startAnimation(findAncientSpellData.castAnimation);
                attacker.gfx100(findAncientSpellData.startGraphic);
                projectile = new ProjectileEntity(attacker, target, findAncientSpellData.projectile, findAncientSpellData.startSpeed, duration, findAncientSpellData.startHeight, findAncientSpellData.endHeight, 1, findAncientSpellData.stepMultiplier, true);
                target.startGraphic(new Graphic(findAncientSpellData.endGraphic, projectile.getSpeed()));
            } else if (findPoweredStaffSpellData != null && attacker.getSpellId() == findPoweredStaffSpellData.spellID) {
                int distance = (int) attacker.getCenterPosition().getAbsDistance(target.getCenterPosition());
                int duration = findPoweredStaffSpellData.startSpeed + -5 + (findPoweredStaffSpellData.stepMultiplier * distance);
                int animationDelay = AnimationLength.getFrameLength(findPoweredStaffSpellData.castAnimation) + 1;
                attacker.getAnimationTimer().setDuration(animationDelay);
                attacker.startAnimation(findPoweredStaffSpellData.castAnimation);
                attacker.gfx100(findPoweredStaffSpellData.startGraphic);
                projectile = new ProjectileEntity(attacker, target, findPoweredStaffSpellData.projectile, findPoweredStaffSpellData.startSpeed, duration, findPoweredStaffSpellData.startHeight, findPoweredStaffSpellData.endHeight, 1, findPoweredStaffSpellData.stepMultiplier, true);
                target.startGraphic(new Graphic(findPoweredStaffSpellData.endGraphic, projectile.getSpeed()));
            }
            if (projectile != null) {
                projectile.sendProjectile();
            }
        }
    }

    public void fireRangeProjectile(@NotNull Entity entity) {
        int pX = attacker.getX();
        int pY = attacker.getY();
        int nX = entity.getX();
        int nY = entity.getY();
        int offX = (pY - nY) * -1;
        int offY = (pX - nX) * -1;
        int ammoId = attacker.playerEquipment[Player.playerArrows];
        int weaponId = attacker.playerEquipment[Player.playerWeapon];
        var drawbackBow = ArrowDrawBack.find(weaponId, ammoId);
        int distance = (int) attacker.getCenterPosition().getAbsDistance(entity.getCenterPosition());
        if (attacker.weaponUsedOnAttack == 12926) {
            attacker.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, RangeData.getProjectileSpeed(attacker), RangeData.getRangeProjectileGFX(attacker), 35, 35, Projectile.getLockon(entity), 45);
        } else {
            if (drawbackBow != null) {
                attacker.gfx100(drawbackBow.gfx);
                int duration = drawbackBow.startSpeed + 5 + (drawbackBow.stepMultiplier * distance);
                ProjectileEntity projectile = new ProjectileEntity(attacker, entity, drawbackBow.projectile, drawbackBow.startSpeed, duration, drawbackBow.startHeight, drawbackBow.endHeight, 1, drawbackBow.stepMultiplier, true);
                projectile.sendProjectile();
            }
        }
        if (attacker.getCombatItems().usingDbow()) {
            attacker.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, RangeData.getProjectileSpeed(attacker), RangeData.getRangeProjectileGFX(attacker), 60, 31, Projectile.getLockon(entity), CombatSpellData.getStartDelay(attacker), 35);
        }
    }

    private boolean determineWeaponStyle() {
        // Magic checks first
        switch (attacker.playerEquipment[Player.playerWeapon]) {
            case 27275:
                if (!attacker.usingClickCast) {
                    attacker.usingMagic = true;
                    attacker.autocasting = true;
                    attacker.autocastId = 99;
                }
                return true;
            case Items.SANGUINESTI_STAFF:
                if (attacker.getSangStaffCharge() <= 0) {
                    attacker.sendMessage("Your Sanguinesti staff has no more charges.");
                    attacker.attacking.reset();
                    return false;
                }
                if (!attacker.usingClickCast) {
                    attacker.usingMagic = true;
                    attacker.autocasting = true;
                    attacker.autocastId = SanguinestiStaff.COMBAT_SPELL_INDEX;
                }
                return true;
            case Items.TRIDENT_OF_THE_SEAS:
                if (attacker.getTridentCharge() <= 0) {
                    attacker.sendMessage("Your trident of the seas has no more charges.");
                    attacker.attacking.reset();
                    return false;
                }
                if (!attacker.usingClickCast) {
                    attacker.usingMagic = true;
                    attacker.autocasting = true;
                    attacker.autocastId = 52;
                }
                return true;

            case Items.TRIDENT_OF_THE_SWAMP:
                if (attacker.getToxicTridentCharge() <= 0) {
                    attacker.sendMessage("Your trident of the swamp has no more charges.");
                    attacker.attacking.reset();
                    return false;
                }
                if (!attacker.usingClickCast) {
                    attacker.usingMagic = true;
                    attacker.autocasting = true;
                    attacker.autocastId = 53;
                }
                return true;
        }

        if (attacker.playerEquipment[Player.playerWeapon] == 4734 && attacker.playerEquipment[Player.playerArrows] != 4740) {
            attacker.sendMessage("You must use bolt racks with the karil's crossbow.");
            reset();
            return false;
        }

        int weapon = attacker.getItems().getWeapon();

        // Then equipped weapon checks
        if (!attacker.usingMagic) {
            // Disable attack if pvp weapon out of charge
            if (weapon == Items.CRAWS_BOW || weapon == Items.CRAWS_BOW_U
                    || weapon == Items.VIGGORAS_CHAINMACE || weapon == Items.VIGGORAS_CHAINMACE_U) {
                if (attacker.getPvpWeapons().getCharges(weapon) <= 0) {
                    attacker.stopMovement();
                    attacker.sendMessage("Your weapon needs charges to function properly.");
                    attacker.attacking.reset();
                    return false;
                }
            }

            attacker.usingCross =
                    attacker.playerEquipment[Player.playerWeapon] == 4734 || attacker.playerEquipment[Player.playerWeapon] == 9185 || attacker.playerEquipment[Player.playerWeapon] == 11785 || attacker.playerEquipment[Player.playerWeapon] == 21012 || attacker.playerEquipment[Player.playerWeapon] == 21902;
            attacker.usingBallista =
                    attacker.playerEquipment[Player.playerWeapon] == 19481 || attacker.playerEquipment[Player.playerWeapon] == 19478;

            /**
             * Throwing snowballs
             */
            if (attacker.getItems().isWearingItem(Items.SNOWBALL, 3)) {
                if (System.currentTimeMillis() - attacker.getLastContainerSearch() < 2000) {
                    attacker.attacking.reset();
                    return false;
                }
                reset();
                attacker.setLastContainerSearch(System.currentTimeMillis());
                return false;
            }


            for (int bowId : RangeData.BOWS) {
                if (attacker.playerEquipment[Player.playerWeapon] == bowId && System.currentTimeMillis() - attacker.switchDelay >= 600) {
                    attacker.usingBow = true;
                    if (bowId == Items.HEAVY_BALLISTA || bowId == Items.LIGHT_BALLISTA) {
                        attacker.usingBow = false;
                        attacker.usingBallista = true;
                    }
                    for (int arrowId : RangeData.ARROWS) {
                        if (attacker.playerEquipment[Player.playerArrows] == arrowId) {
                            attacker.usingArrows = true;
                        }
                    }
                }
            }

            for (int otherRangeId : RangeData.OTHER_RANGE_WEAPONS) {
                if (attacker.playerEquipment[Player.playerWeapon] == otherRangeId) {
                    attacker.usingOtherRangeWeapons = true;
                }
            }

            if (attacker.getItems().isWearingItem(12926)) {
                if (attacker.getToxicBlowpipeAmmo() == 0 || attacker.getToxicBlowpipeAmmoAmount() == 0 || attacker.getToxicBlowpipeCharge() == 0) {
                    attacker.sendMessage("Your blowpipe is out of ammo or charge.");
                    attacker.attacking.reset();
                    return false;
                }
                attacker.usingBow = true;
                attacker.usingArrows = true;
            }

            if (attacker.playerEquipment[Player.playerWeapon] == 9703) {
                attacker.sendMessage("The training sword is only meant as a cosmetic.");
                return false;
            }
            if (attacker.getItems().isWearingAnyItem() && attacker.playerEquipment[Player.playerArrows] > -1) {
                attacker.sendMessage("You cannot use any arrows with this bow.");
                attacker.attacking.reset();
                return false;
            }

            if (!attacker.usingBallista && !attacker.usingCross && !attacker.usingArrows && attacker.usingBow && (((attacker.playerEquipment[Player.playerWeapon] < 4212) || (attacker.playerEquipment[Player.playerWeapon] > 4223)))
                    && !(attacker.playerEquipment[Player.playerWeapon] == 22550)) {
                attacker.sendMessage("You have run out of arrows!");
                reset();
                return false;
            }

            if (!Bow.canUseArrow(attacker) && Configuration.CORRECT_ARROWS && attacker.usingBow && !attacker.getCombatItems().usingCrystalBow() && !(attacker.playerEquipment[Player.playerWeapon] == 22550) && attacker.playerEquipment[Player.playerWeapon] != 9185
                    && attacker.playerEquipment[Player.playerWeapon] != 4734 && attacker.playerEquipment[Player.playerWeapon] != 11785 && attacker.playerEquipment[Player.playerWeapon] != 21012 && attacker.playerEquipment[Player.playerWeapon] != 12926 && attacker.playerEquipment[Player.playerWeapon] != 19478 && attacker.playerEquipment[Player.playerWeapon] != 19481 && attacker.playerEquipment[Player.playerWeapon] != 21902) {
                attacker.sendMessage("You can't use " + ItemAssistant.getItemName(attacker.playerEquipment[Player.playerArrows]).toLowerCase() + "'s with a "
                        + ItemAssistant.getItemName(attacker.playerEquipment[Player.playerWeapon]).toLowerCase() + ".");
                return false;
            }

            if (attacker.playerEquipment[Player.playerWeapon] == 9185 && !attacker.getCombatItems().properBolts() || attacker.playerEquipment[Player.playerWeapon] == 11785 && !attacker.getCombatItems().properBolts() || attacker.playerEquipment[Player.playerWeapon] == 21012 && !attacker.getCombatItems().properBolts() || attacker.playerEquipment[Player.playerWeapon] == 21902 && !attacker.getCombatItems().properBolts()) {
                attacker.sendMessage("You must use bolts with a crossbow.");
                return false;
            }

            if (attacker.playerEquipment[Player.playerWeapon] == 19478 && !attacker.getCombatItems().usingJavelins(attacker.playerEquipment[Player.playerArrows]) || attacker.playerEquipment[Player.playerWeapon] == 19481 && !attacker.getCombatItems().usingJavelins(attacker.playerEquipment[Player.playerArrows])) {
                attacker.sendMessage("You must use javelins with a ballista.");
                return false;
            }

        } else {
            // Disable attack if pvp weapon out of charge
            if (weapon == Items.THAMMARONS_SCEPTRE || weapon == Items.THAMMARONS_SCEPTRE_U) {
                if (attacker.getPvpWeapons().getCharges(weapon) <= 0) {
                    attacker.stopMovement();
                    attacker.sendMessage("Your weapon needs charges to function properly.");
                    attacker.attacking.reset();
                    return false;
                }
            }
        }

        if (attacker.getItems().isWearingItem(12931) || attacker.getItems().isWearingItem(13197) || attacker.getItems().isWearingItem(13199)) {
            if (attacker.getSerpentineHelmCharge() <= 0) {
                attacker.sendMessage("Your serpentine helm has no charge, you need to recharge it.");
                return false;
            }
        }

        return true;
    }

    public int getRequiredDistance() {
        if (attacker.playerFollowingIndex > 0 && attacker.freezeTimer <= 0 && !attacker.isMoving)
            return 2;
        else if (attacker.playerFollowingIndex > 0 && attacker.freezeTimer <= 0 && attacker.isMoving) {
            return 4;
        } else {
            return 1;
        }
    }

    public int getDistanceRequired(CombatType combatType, Player attacker) {
        if (combatType == CombatType.MAGE) {
            return 10;
        } else if (getCombatType() == CombatType.RANGE) {
            return getRangeDistance(attacker);
        } else {
            if (MeleeData.usingHally(attacker)) {
                return 2;
            }
            return 1;
        }
    }

    public static int getRangeDistance(Player player) {
        ItemDef definition = ItemDef.forId(player.getItems().getWeapon());
        if (definition == null)
            return 5;

        String name = definition.getName().toLowerCase();
        boolean isLongRange = player.getCombatConfigs().getAttackStyle() == 2;

        if (name.contains("twisted bow"))
            return 10;
        else if (name.contains("bow of faerdhinen"))
            return 10;
        else if (name.contains("dark bow"))
            return 10;
        else if (name.contains("crystal bow"))
            return 10;
        else if (name.contains("composite"))
            return 10;
        else if (name.contains("ogre bow"))
            return 10;
        else if (name.contains("longbow"))
            return 10;
        else if (name.contains("craw"))
            return isLongRange ? 10 : 9;
        else if (name.contains("3rd age"))
            return isLongRange ? 10 : 9;
        else if (name.contains("chinchompa"))
            return isLongRange ? 10 : 9;
        else if (name.contains("ballista"))
            return isLongRange ? 10 : 9;
        else if (name.contains("seercull"))
            return isLongRange ? 10 : 8;
        else if (name.contains("karil"))
            return isLongRange ? 10 : 8;
        else if (name.contains("armadyl"))
            return isLongRange ? 10 : 8;
        else if (name.contains("shortbow"))
            return isLongRange ? 9 : 7;
        else if (name.contains("crossbow"))
            return isLongRange ? 9 : 7;
        else if (name.contains("dorgeshuun"))
            return isLongRange ? 8 : 6;
        else if (name.contains("blowpipe"))
            return isLongRange ? 7 : 5;
        else if (name.contains("comp ogre"))
            return isLongRange ? 7 : 5;
        else if (name.contains("throwing axe"))
            return isLongRange ? 6 : 4;
        else if (name.contains("knife"))
            return isLongRange ? 6 : 4;
        else if (name.contains("dart"))
            return isLongRange ? 5 : 3;
        else if (name.contains("salamander"))
            return 3;

        return 5;
    }

    public boolean checkNpcAttackDistance(NPC npc, Player attacker) {
        int attackDistanceRequired =
                getDistanceRequired(getCombatType(), attacker) + npc.getAttackDistanceModifier(attacker,
                        getCombatType());

        if (getCombatType() != CombatType.MELEE) {
            if (npc.getNpcId() == 7706) {
                attackDistanceRequired = 18;
            }
            if (npc.getNpcId() == 8031 || npc.getNpcId() == 8030 || npc.getNpcId() == 8781) {
                attackDistanceRequired = 5;
            }
            if (HydraStage.isHydra(npc.getNpcId())) {
                attackDistanceRequired = 1;
            }
        }

        return npc.getDistance(attacker.absX, attacker.absY) <= attackDistanceRequired;
    }

    /**
     * Checks the combat distance to see if the player is in an appropriate location
     * based on the attack style.
     *
     * @param target
     * @param followingLogic TODO
     * @param player
     * @return
     */
    public boolean checkPlayerAttackDistance(Player target, boolean followingLogic, Player player) {
        double totalDistance = target.getDistance(attacker.absX, attacker.absY);
        double required_distance = getDistanceRequired(getCombatType(), player);

        if (!followingLogic && player.isRunning() && target.isRunning()) {
            required_distance += 2;
        }

        // This fixes an issue with diagonal attacking with melee weapons
        if (required_distance == 1 && totalDistance < 2 && totalDistance > 1)
            return false;

        // Floor the total distance so that the decimal values don't interfere with pathing
        return Math.floor(totalDistance) <= required_distance && totalDistance != 0;
    }

    public boolean checkAttackDistance(Entity target, boolean followingLogic, Player player) {
        if (target.isNPC()) {
            return checkNpcAttackDistance(target.asNPC(), player);
        } else {
            return checkPlayerAttackDistance(target.asPlayer(), followingLogic, player);
        }
    }

    public int getFightModeAttackBonus() {
        switch (attacker.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case ACCURATE:
                return 9;
            case CONTROLLED:
                return 3;
            default:
                return 0;
        }
    }

    public int getFightModeStrengthBonus() {
        switch (attacker.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case AGGRESSIVE:
                return 3;
            case CONTROLLED:
                return 1;
            default:
                return 0;
        }
    }

    public boolean clickWeaponTabButton(int buttonId) {
        switch (buttonId) {
            // 1
            case 9125: // Accurate
            case 6221: // range accurate
            case 48010: // flick (whip)
            case 21200: // spike (pickaxe)
            case 1080: // bash (staff)
            case 6168: // chop (axe)
            case 6236: // accurate (long bow)
            case 17102: // accurate (darts)
            case 8234: // 2h stab
            case 22228: // punch
            case 18077: // lunge (spear)
            case 18103:
            case 33018: // jab (hally)
            case 1177:
            case 30088:
            case 14218:
            case 3014:
                attacker.getCombatConfigs().setAttackStyle(0);

                if (attacker.autocasting) {
                    attacker.getPA().resetAutocast();
                }
                return true;

            // 2
            case 9128: // Aggressive
            case 6220: // range rapid
            case 21203: // impale (pickaxe)
            case 21202: // smash (pickaxe)
            case 1079: // pound (staff)
            case 6171: // hack (axe)
            case 33020: // swipe (hally)
            case 6235: // rapid (long bow)
            case 17101: // repid (darts)
            case 22230: // kick
            case 1176: // aggressive maul
            case 18080: // swipe (spear)
            case 18106:
            case 48009: // lash (whip)
            case 8237:
            case 30091:
            case 14221:
            case 3016:
                attacker.getCombatConfigs().setAttackStyle(1);
                if (attacker.autocasting) {
                    attacker.getPA().resetAutocast();
                }
                return true;

            // 3
            case 22229: // unarmed
            case 6234: // longrange (long bow)
            case 6219: // longrange
            case 17100: // longrange (darts)
            case 48008: // deflect (whip)
            case 18079: // pound (spear)
            case 33019: // fend (hally)
            case 1078: // focus - block (staff)
            case 18105:
            case 6170: // smash (axe)
            case 8236:
            case 1175:
            case 30090:
            case 14220:
            case 9127:
            case 3017:
                attacker.getCombatConfigs().setAttackStyle(2);
                if (attacker.autocasting) {
                    attacker.getPA().resetAutocast();
                }
                return true;

            // 4
            case 9126: // Defensive
            case 21201: // block (pickaxe)
            case 6169: // block (axe)
            case 18078: // block (spear)
            case 8235: // block 2h
            case 18104:
            case 30089:
            case 14219:
            case 3015:
                attacker.getCombatConfigs().setAttackStyle(3);

                if (attacker.autocasting) {
                    attacker.getPA().resetAutocast();
                }
                return true;

        }

        return false;
    }

}
