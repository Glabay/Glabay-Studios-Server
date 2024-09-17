package io.xeros.content.bosses.nightmare;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.nightmare.attack.SleepWalkers;
import io.xeros.content.bosses.nightmare.attack.StandardMage;
import io.xeros.content.bosses.nightmare.attack.StandardMelee;
import io.xeros.content.bosses.nightmare.attack.StandardRanged;
import io.xeros.content.bosses.nightmare.phase.Phase1;
import io.xeros.content.bosses.nightmare.phase.Phase2;
import io.xeros.content.bosses.nightmare.phase.Phase3;
import io.xeros.content.bosses.nightmare.totem.TotemSpawn;
import io.xeros.content.bosses.nightmare.totem.Totems;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.combat.formula.MeleeMaxHit;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import static io.xeros.content.bosses.nightmare.NightmareConstants.AWAKE_TIMER;
import static io.xeros.content.bosses.nightmare.NightmareConstants.HEALTH;
import static io.xeros.content.bosses.nightmare.NightmareConstants.MAX_SHIELD;
import static io.xeros.content.bosses.nightmare.NightmareConstants.MIN_SHIELD;
import static io.xeros.content.bosses.nightmare.NightmareConstants.NIGHTMARE_ACTIVE_ID;
import static io.xeros.content.bosses.nightmare.NightmareConstants.NIGHTMARE_AWAKE_ANIMATION;
import static io.xeros.content.bosses.nightmare.NightmareConstants.NIGHTMARE_DEATH_ANIMATION;
import static io.xeros.content.bosses.nightmare.NightmareConstants.NIGHTMARE_SLEEPING_ID;
import static io.xeros.content.bosses.nightmare.NightmareConstants.NIGHTMARE_SPAWN_POSITION;
import static io.xeros.content.bosses.nightmare.NightmareConstants.RESPAWN_TIMER;
import static io.xeros.content.bosses.nightmare.NightmareConstants.SHIELD_SCALE_FACTOR;

public class Nightmare extends NPC {

    private NightmareAttack nightmareAttack;
    private int nightmareAttackIndex;
    private int autoAttacks;

    private Totems totems;
    private NightmarePhase phase;
    private int phaseIndex;
    private final List<NightmarePhase> phases = Lists.newArrayList(new Phase1(), new Phase2(), new Phase3());

    private int shields;
    private boolean onHealth;
    private int nightmareHealth;

    private int deathTimer;
    private int respawnTimer;
    private int awakeTimer;
    private boolean useSleepwalkers;

    private List<Player> rareRollPlayers = Lists.newArrayList();

    public Nightmare(InstancedArea instancedArea) {
        super(NIGHTMARE_SLEEPING_ID, instancedArea.resolve(NIGHTMARE_SPAWN_POSITION));
        instancedArea.add(this);
        resetForSpawn();
        getBehaviour().setWalkHome(false);
        getBehaviour().setRespawn(false);
    }

    public void resetForSpawn() {
        rareRollPlayers = Lists.newArrayList();
        nightmareHealth = HEALTH;
        phaseIndex = -1; // set to -1
        phase = null;
        onHealth = false;
        walkingType = 0;
        totems = new Totems(this);
        respawnTimer = RESPAWN_TIMER;
        Preconditions.checkState(nextPhase(), "Cannot phase!");
        setNpcAutoAttacks(Lists.newArrayList(
                new StandardMelee().apply(this),
                new StandardMage().apply(this),
                new StandardRanged().apply(this)
        ));
    }

    @Override
    public boolean susceptibleTo(HealthStatus status) {
        return false;
    }

    @Override
    public void setDead(boolean dead) {
        // Ignored
    }

    @Override
    public boolean hasBlockAnimation() {
        return false;
    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        return isAlive() && !onHealth;
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        return isAlive();
    }

    @Override
    public boolean isAutoRetaliate() {
        return false;
    }

    @Override
    public void process() {
        try {
            Preconditions.checkState(getInstance() != null);
            if (deathTimer > 0 && processDeath()) {
               return;
            }

            if (respawnTimer > 0) {
                processRespawn();
            } else if (awakeTimer > 0) {
                processAwaken();
            } else {
                processCombat();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            unregister();
        }
    }

    private void processCombat() {
        if (getPlayerAttackingIndex() <= 0 || Server.getTickCount() % 10 == 0)
            setPlayerAttackingIndex(getTank().getIndex()); // Killer id sets attacking?

        if (!isSleepwalkers()) {
            if (nightmareAttack == null || nightmareAttack.isStopped()) {
                if (useSleepwalkers) {
                    nightmareAttack = new SleepWalkers();
                    useSleepwalkers = false;
                } else {
                    if (autoAttacks >= 1 && attackTimer == 1 && Misc.random(3) == 0) {
                        NightmareAttack[] attacks = phase.getAttacks();
                        NightmareAttack newAttack;
                        int newAttackIndex;
                        do {
                            newAttackIndex = Misc.trueRand(attacks.length);
                            newAttack = attacks[newAttackIndex];
                        } while (nightmareAttackIndex == newAttackIndex && attacks.length > 1);
                        nightmareAttack = newAttack;
                        nightmareAttackIndex = newAttackIndex;
                        attackTimer = 6;
                        autoAttacks = 0;
                    }
                }
            }
        } else if (nightmareAttack.isStopped()) {
            nightmareHealth = getHealth().getCurrentHealth();

            onHealth = false;
            getHealth().setCurrentHealth(shields);
            getHealth().setMaximumHealth(shields);
            Preconditions.checkState(nextPhase(), "Cannot phase!");
        }

        if (nightmareAttack != null && !nightmareAttack.isStopped()) {
            nightmareAttack.tick(this);
            nightmareAttack.ticked();
            facePlayer(0);
        } else {
            if (attackTimer == 1) {
                autoAttacks++;
            }
            super.process();
        }

        if (getHealth().getCurrentHealth() == 0) {
            if (!onHealth && !totems.isActive()) {
                // Shields are down!
                totems.becomeVulnerable();
                onHealth = true;
                getHealth().setCurrentHealth(nightmareHealth);
                getHealth().setMaximumHealth(HEALTH);
            }
        }

        if (totems.isActive()) {
            totems.process();
            if (totems.readyToPhase()) {
                if (onLastPhase()) {
                    kill();
                } else {
                    useSleepwalkers = true;
                }
            }
        }
    }

    public void kill() {
        getInstance().getNpcs().stream().filter(npc -> !npc.equals(this) && !TotemSpawn.isTotem(npc))
                .forEach(npc -> npc.appendDamage(npc, npc.getHealth().getCurrentHealth(), Hitmark.HIT));
        deathTimer = 16;

        startAnimation(NIGHTMARE_DEATH_ANIMATION);
    }

    private boolean processDeath() {
        setPlayerAttackingIndex(0);
        facePlayer(0);
        deathTimer--;

        if (deathTimer == 0) {
            List<Player> players = getInstance().getPlayers().stream().filter(plr ->
                    !plr.isDead && NightmareConstants.BOUNDARY.in(plr)).collect(Collectors.toList());
            rareRollPlayers = Misc.randoms(players, 2);

            players.forEach(plr -> {
                Achievements.increase(plr, AchievementType.NIGHTMARE, 1);
                NPCDeath.dropItemsFor(this, plr, NIGHTMARE_ACTIVE_ID);
                plr.getEventCalendar().progress(EventChallenge.KILL_X_NIGHTMARE);
                LeaderboardUtils.addCount(LeaderboardType.NIGHTMARE, plr, 1);
                plr.getSlayer().killTaskMonster(this);
                plr.getBossTimers().death(this);

            });
            resetForSpawn();
            return false;
        } else if (deathTimer == 8) {
            requestTransform(NIGHTMARE_SLEEPING_ID);
            return true;
        } else {
            return true;
        }
    }

    private void processRespawn() {
        respawnTimer--;
        if (respawnTimer % 16 == 0) {
            int seconds = (respawnTimer / 16) * 10;
            if (seconds > 0) {
                getInstance().getPlayers().forEach(plr -> plr.sendMessage("The Nightmare will awaken in " + seconds + " seconds!"));
                asNPC().increaseDefence(150);
            }
        }

        if (respawnTimer == 0) {
            awakeTimer = AWAKE_TIMER;
        }
    }

    private void processAwaken() {
        awakeTimer--;
        if (awakeTimer == 10) {
            startAnimation(NIGHTMARE_AWAKE_ANIMATION);
            requestTransform(NIGHTMARE_ACTIVE_ID);
        } else if (awakeTimer == 0) {
            determineShieldHealth();
            totems.scaleHealth();
            getInstance().getPlayers().forEach(plr -> plr.sendMessage("@red@The Nightmare has awoken!"));
        }
    }

    private boolean isSleepwalkers() {
        return nightmareAttack != null && nightmareAttack.getClass() == SleepWalkers.class;
    }

    private boolean onLastPhase() {
        return phaseIndex == phases.size() - 1;
    }

    private boolean nextPhase() {
        if (phaseIndex + 1 < phases.size()) {
            asNPC().increaseDefence(150);
            nightmareAttack = null;
            phaseIndex++;
            phase = phases.get(phaseIndex);
            phase.start(this);
            return true;
        } else {
            return false;
        }
    }

    private Player getTank() {
        Preconditions.checkState(!getInstance().getPlayers().isEmpty(), "No players!");
        List<Player> players = getInstance().getPlayers().stream().sorted(Comparator.comparingInt(MeleeMaxHit::bestMeleeDef)).collect(Collectors.toList());

        return players.get(players.size() - 1);
    }

    private void determineShieldHealth() {
        int count = getInstance().getPlayers().size();
        if (count <= 3) {
            shields = MIN_SHIELD;
        } else {
            shields = MIN_SHIELD + ((count - 3) * SHIELD_SCALE_FACTOR);
            if (shields > MAX_SHIELD) {
                shields = MAX_SHIELD;
            }
        }

        getHealth().setMaximumHealth(shields);
        getHealth().setCurrentHealth(shields);
    }

    public boolean checkPlayerState(Player player) {
        return getInstance() != null && getInstance().getPlayers().contains(player);
    }

    public void transformToStandard() {
        requestTransform(NIGHTMARE_ACTIVE_ID);
    }

    public NightmarePhase getPhase() {
        return phase;
    }

    public boolean isJoinable() {
        return respawnTimer != 0;
    }

    public boolean isAlive() {
        return awakeTimer == 0 && respawnTimer == 0 && deathTimer == 0;
    }

    public Totems getTotems() {
        return totems;
    }

    public boolean isOnHealth() {
        return onHealth;
    }

    public List<Player> getRareRollPlayers() {
        return rareRollPlayers;
    }
}
