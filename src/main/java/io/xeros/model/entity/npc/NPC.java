package io.xeros.model.entity.npc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.bosses.Skotizo;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.combat.CombatHit;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.combat.npc.NPCCombatAttackHit;
import io.xeros.content.minigames.pest_control.PestControl;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.*;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.HitMark;
import io.xeros.model.entity.healthbar.StaticHealthBarUpdate;
import io.xeros.model.entity.npc.actions.NPCHitPlayer;
import io.xeros.model.entity.npc.combat.CombatMethod;
import io.xeros.model.entity.npc.combat.CommonCombatMethod;
import io.xeros.model.entity.npc.data.BlockAnimation;
import io.xeros.model.entity.npc.data.DeathAnimation;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.EquipmentSet;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import io.xeros.util.Stream;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NPC extends Entity {

	private static final Logger logger = LoggerFactory.getLogger(NPC.class);
    public int parentIndex;

    @Getter
    @Setter
    private int npcId;
	public int summonedBy;
	public int absX, absY;
	public int heightLevel;
	@Getter
    @Setter
    private boolean unregister;

	public boolean isPet;

	public int makeX;
	public int makeY;
	public int maxHit;
	@Getter
    @Setter
    public Direction walkDirection = Direction.NONE;
	@Getter
    @Setter
    public Direction runDirection = Direction.NONE;
	public int walkingType;

	public int lastX, lastY;
	public boolean summoner;
	public boolean teleporting;

	public int parentNpc = -1;
	public List<Integer> children;

	public long lastRandomlySelectedPlayer = 0;

	private boolean transformUpdateRequired;
	public int transformId;
	public Location3D targetedLocation;

	/**
	 * attackType: 0 = melee, 1 = range, 2 = mage
	 */
	public long lastSpecialAttack;

	public boolean spawnedMinions;

	@Getter
    @Setter
    private CombatType attackType;
	@Getter
	private CombatMethod combatMethod;
	public void setCombatMethod(CombatMethod combatMethod) {
		this.combatMethod = combatMethod;
		if (combatMethod instanceof CommonCombatMethod ccm) {
            ccm.set(this, null);
			ccm.init(this);
		}
	}

	public int projectileId, endGfx, spawnedBy, hitDelayTimer, hitDiff, actionTimer;
	public boolean applyDead;
	public boolean needRespawn;
	public boolean walkingHome, underAttack;
	@Getter
    private int playerAttackingIndex;
	public int killedBy;
	public int oldIndex;
	public int underAttackBy;
	public long lastDamageTaken;
	public boolean randomWalk;
	public boolean faceEntityUpdateRequired;
	public boolean hitUpdateRequired;
	public boolean forcedChatRequired;
	private boolean forceMovementUpdateRequired;
	private ForceMovement forceMovement;
	public String forcedText;
	public int FocusPointX = -1, FocusPointY = -1;
	public int face;
	public int totalAttacks;
    /**
     * -- SETTER --
     *  Makes the npcs either able or unable to face other players
     *
     * @param facePlayer
     *            {@code true} if the npc can face players
     */
    @Setter
    private boolean facePlayer = true;
	@Getter
    @Setter
    private int projectileDelay;

	@Getter
    private final NPCBehaviour behaviour = new NPCBehaviour();
    /**
     * -- GETTER --
     *  An object containing specific information about the NPC such as the combat
     *  level, default maximum health, the name, etcetera.
     *
     * @return the {@link NpcDef} object associated with this NPC
     */
    @Getter
    private final NpcDef definition;

	@Getter
    @Setter
    private long lastRandomWalk;
	private long lastRandomWalkHome;

	@Getter
    @Setter
    private long randomWalkDelay;
	private long randomStopDelay;

	@Getter
    private NpcStats defaultNpcStats;
	@Getter
    @Setter
    private NpcStats npcStats;

	@Setter
    private List<NPCAutoAttack> npcAutoAttacks = Lists.newArrayList();
	@Getter
    private NPCAutoAttack currentAttack;

	private NpcCombatDefinition npcCombatDefinition;

	public NPC(int npcId, Position position) {
		super();
		setNpcId(npcId);
		this.definition = NpcDef.forId(npcId);
		Preconditions.checkState(definition != null, "NPCDefinition cannot be null!");
		absX = makeX = position.getX();
		absY = makeY = position.getY();
		heightLevel = position.getHeight();
		setup();
		register();
		clearUpdateFlags();
		fetchDefaultNpcStats();
		setNpcCombatDefinition();
		if (this.getNpcStats() != null && this.getNpcStats().scripts != null && this.getNpcStats().scripts.combat_ != null) {
			this.setCombatMethod(this.getNpcStats().scripts.newCombatInstance());
		}
	}

	public NPC(int index, int npcId, NpcDef definition, NpcStats defaultNpcStats) {
		super(index);
		this.definition = definition;
		setNpcId(npcId);
		walkDirection = Direction.NONE;
		runDirection = Direction.NONE;
		setup();
		setDefaultNpcStats(defaultNpcStats);
		setNpcCombatDefinition();
		if (this.getNpcStats() != null && this.getNpcStats().scripts != null && this.getNpcStats().scripts.combat_ != null) {
			this.setCombatMethod(this.getNpcStats().scripts.newCombatInstance());
		}
	}

	public NPC(int index, int npcId, NpcDef definition) {
		super(index);
		this.definition = definition;
		setNpcId(npcId);
		walkDirection = Direction.NONE;
		runDirection = Direction.NONE;
		setup();
		fetchDefaultNpcStats();
		setNpcCombatDefinition();
		if (this.getNpcStats() != null && this.getNpcStats().scripts != null && this.getNpcStats().scripts.combat_ != null) {
			this.setCombatMethod(this.getNpcStats().scripts.newCombatInstance());
		}
	}

	private void setup() {
		setDead(false);
		applyDead = false;
		actionTimer = 0;
		randomWalk = true;
		if (definition.isRunnable()) {
			getBehaviour().setRunnable(true);
		}
	}

	public void setNpcCombatDefinition() {
		NpcCombatDefinition definition = NpcCombatDefinition.definitions.get(this.npcId);
		if (definition == null) {
			this.npcCombatDefinition = new NpcCombatDefinition(this);
		} else {
			this.npcCombatDefinition = new NpcCombatDefinition(definition);
		}
	}

	public NPC provideRespawnInstance() {
		return null;
	}

	private void fetchDefaultNpcStats() {
		if (definition.getCombatLevel() > 0 || NpcStats.forId(npcId).getHitpoints() > 0) {
			setDefaultNpcStats(NpcStats.forId(npcId));
		} else {
			setDefaultNpcStats(NpcStats.builder().setAttackSpeed(4).createNpcStats());
		}
	}

	public void setDefaultNpcStats(NpcStats defaultNpcStats) {
		this.npcStats = defaultNpcStats;
		this.defaultNpcStats = defaultNpcStats;
		//this.defence = defaultNpcStats.getDefenceLevel();
		getHealth().setMaximumHealth(getNpcStats().getHitpoints());
		getHealth().reset();
	}

    public boolean canBeAttacked(Entity entity) {
		return true;
	}

	public boolean canBeDamaged(Entity entity) {
		return true;
	}

	@Override
	public boolean isAutoRetaliate() {
		return true;
	}

	public void onDeath() {}

	public void afterDeath() {}

	public int getDeathAnimation() {
		return DeathAnimation.handleEmote(getNpcId());
	}

	public int modifyDamage(Player player, int damage) {
		return damage;
	}

	public int getAttackDistanceModifier(Player player, CombatType combatType) {
		return 0;
	}

	@Override
	public boolean hasBlockAnimation() {
		return Arrays.stream(PestControl.PORTAL_DATA).noneMatch(data -> data[0] == getNpcId()) && getNpcId() != 2042 && getNpcId() != 2043 & getNpcId() != 2044
				&& getNpcId() != 3127 && getNpcId() != 319;
	}

	@Override
	public Animation getBlockAnimation() {
		return new Animation(BlockAnimation.getAnimation(getNpcId()));
	}

	@Override
	public void attackEntity(Entity entity) {
		if (entity.isPlayer()) {
			setPlayerAttackingIndex(entity.getIndex());
		}
	}

	@Override
	public boolean isNPC() {
		return true;
	}

	@Override
	public String toString() {
		return "NPC{" +
				"npcId=" + npcId +
				", absX=" + absX +
				", absY=" + absY +
				", spawnX=" + makeX +
				", spawnY=" + makeY +
				", name='" + getName() + '\'' +
				", index=" + getIndex() +
				", instance=" + getInstance() +
				'}';
	}

	@Override
	public int getX() {
		return absX;
	}
	@Override
	public int getY() {
		return absY;
	}
	@Override
	public void setX(int x) {
		this.absX = x;
	}

	@Override
	public void setY(int y) {
		this.absY = y;
	}

	@Override
	public int getHeight() {
		return heightLevel;
	}

	@Override
	public void setHeight(int height) {
		this.heightLevel = height;
	}

	@Override
	public int getDefenceLevel() {
		return this.npcCombatDefinition.getLevel(NpcCombatSkill.DEFENCE);
	}

	@Override
	public int getDefenceBonus(CombatType type, Entity attacker) {
		// WARNING: the returned value can't be negative for magic combat formula
		// and other combat formulas if they ever get corrected to osrs
		if (type.equals(CombatType.MELEE)) {
			switch (getNpcId()) {
				case 965://kalphite queen
					return attacker.isPlayer() ? (EquipmentSet.VERAC.isWearing(attacker.asPlayer()) ? +500 : 5000) : 5000;
				case 9021://hunllef melee
				case 9022://hunllef range
				case 9023://hunllef mage
					return 100;
			}
		} else if (type.equals(CombatType.MAGE)) {
			switch (getNpcId()) {
				//case 2042://green zulrah
				//	return -150;
				case 319://corp
					return +80;
				//case 2044://blue zulrah
				//	return 1550;
				case 963://kalhpite queen
					return +7000;
				case 965://kalhpite queen part 2
				case 5890://abyssal sire
					return 100;
			}
		} else if (type.equals(CombatType.RANGE)) {
			switch (getNpcId()) {
				case 2042://green zulrah
				case 2043://red zulrah
				case 5890://abyssale sire
					return 0;
				case 2044://blue zulrah
					return -150;
				case 963://kalphite queen
					return +7000;
				case 965://kalphite queen part 2
					return 300;

			}
		}

        return switch (type) {
            case MELEE -> {
                if (attacker.isPlayer()) {
                    switch (attacker.asPlayer().getCombatConfigs().getWeaponMode().getCombatStyle()) {
                        case STAB:
                            yield getNpcStats().getStabDef();
                        case SLASH:
                            yield getNpcStats().getSlashDef();
                        case CRUSH:
                            yield getNpcStats().getCrushDef();
                    }
                }
                yield getNpcStats().getSlashDef();
            }
            case RANGE -> getNpcStats().getRangeDef();
            case MAGE -> getNpcStats().getMagicDef();
            default -> 0;
        };

    }

	@Override
	public void removeFromInstance() {
		if (getInstance() != null) {
			getInstance().remove(this);
		}
	}

	@Override
	public int getEntitySize() {
		return getSize();
	}

	public void register() {
		Preconditions.checkState(getIndex() == 0, "Already registered!");
		int index = Server.npcHandler.register(this);
		Preconditions.checkState(index != -1, "Cannot register npc!");
		setIndex(index);
		this.getRegionProvider().addNpcClipping(this);
	}

	/**
	 * You should avoid using this unless you need instant deregistration.
	 * If deregistration deffered to the next npc process with due, you should simply
	 * call {@link NPC#unregister}. Calling this will cause issues, especially
	 * if you're calling from inside the npc process, as that will still complete
	 * after the npc has been unregistered.
	 */
	public void unregisterInstant() {
		unregister();
		processDeregistration();
	}

	/**
	 * Set the NPC to unregister on the next process.
	 * If you need instant deregistration call this and {@link NPC#processDeregistration()}.
	 */
	public void unregister() {
		setUnregister(true);
		this.getRegionProvider().removeNpcClipping(this);
	}

	/**
	 * Called by the internal npc processing to finish deregistration of an NPC.
	 * This is to prevent npcs from being unregistered while they are still processing.
	 * You shouldn't call this method to remove an npc, use {@link NPC#unregister()}.
	 */
	public boolean processDeregistration() {
		if (isUnregister() && getIndex() > 0) {
			CycleEventHandler.getSingleton().stopEvents(this);
			NPCHandler.npcs[getIndex()] = null;
			setIndex(0);
			if (getInstance() != null) {
				getInstance().remove(this);
			}
			logger.debug("Unregistered {}", this);
			return true;
		}

		return false;
	}

	public void resetAttack() {
		setPlayerAttackingIndex(0);
		facePlayer(0);
		underAttack = false;
		randomWalk = true;
	}

	public void addChild(NPC npc) {
		Preconditions.checkState(npc.parentNpc == -1);
		npc.parentNpc = getIndex();
		if (children == null) {
			children = Lists.newArrayList(npc.getIndex());
		} else {
			children.add(npc.getIndex());
		}
	}

	public NPC getParent() {
		if (parentNpc != -1) {
			return NPCHandler.npcs[parentNpc];
		} else {
			return null;
		}
	}

	public List<NPC> getChildren() {
		List<NPC> childrenNpcs = Lists.newArrayList();
		if (children != null) {
			children.forEach(child -> {
				NPC npc = NPCHandler.npcs[child];
				Objects.requireNonNull(npc);
				childrenNpcs.add(npc);
			});
		}
		return childrenNpcs;
	}

	public void process() {
		Server.npcHandler.getNpcProcess().process(getIndex());
		healthBarQueue.clear();
	}

	public void selectAutoAttack(Entity entity) {
		Preconditions.checkState(!npcAutoAttacks.isEmpty(), "No auto attacks present!");
		List<NPCAutoAttack> viable = npcAutoAttacks.stream().filter(autoAttack -> autoAttack.getSelectAutoAttack() == null
				|| autoAttack.getSelectAutoAttack().apply(new NPCCombatAttack(this, entity))).toList();
		Preconditions.checkState(!viable.isEmpty(), "No viable attacks can be found, npc: " + this);
		currentAttack = viable.get(Misc.trueRand(viable.size()));
	}

	public void attack(Player c, NPCAutoAttack npcAutoAttack) {
		if (lastX != getX() || lastY != getY()) {
			return;
		} else if (c.isInvisible()  || isDead() || c.respawnTimer > 0) {
			return;
		} else if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		} else if (!getPosition().inMulti() && underAttackBy > 0 && underAttackBy != c.getIndex()) {
			resetAttack();
			return;
		} else if (!getPosition().inMulti() && ((c.underAttackByPlayer > 0 && c.underAttackByNpc != getIndex())
				|| (c.underAttackByNpc > 0 && c.underAttackByNpc != getIndex()))) {
			resetAttack();
			return;
		} else if (heightLevel != c.heightLevel) {
			resetAttack();
			return;
		}

		facePlayer(c.getIndex());

		if (getDistance(c.getX(), c.getY()) > ((double) npcAutoAttack.getDistanceRequiredForAttack()) + (getSize() > 1 ? 0.5 : 0.0)) {
			return;
		}

        if (NPCHandler.projectileClipping && !npcAutoAttack.isIgnoreProjectileClipping()) {
			if (getAttackType() == null || getAttackType() == CombatType.MAGE || getAttackType() == CombatType.RANGE) {
				int x1 = absX;
				int y1 = absY;
				int z = heightLevel;
                if (!PathChecker.raycast(this, c, true)
					&& !PathChecker.raycast(c, this, true))
					return;
			}
		}

		attackTimer = npcAutoAttack.getAttackDelay();
		setAttackType(npcAutoAttack.getCombatType());
		oldIndex = c.getIndex();

		if (npcAutoAttack.getAnimation() != null)
			startAnimation(npcAutoAttack.getAnimation());
		if (npcAutoAttack.getStartGraphic() != null)
			startGraphic(npcAutoAttack.getStartGraphic());

		if (npcAutoAttack.isMultiAttack()) {
			Preconditions.checkState(npcAutoAttack.getSelectPlayersForMultiAttack() != null, "You must define a NPCAutoAttack#selectPlayersForMultiAttack");
			for (Player player : npcAutoAttack.getSelectPlayersForMultiAttack().apply(new NPCCombatAttack(this, c))) {
				finishAutoAttack(player, npcAutoAttack);
			}
		} else {
			finishAutoAttack(c, npcAutoAttack);
		}
	}

	private void finishAutoAttack(Player c, NPCAutoAttack npcAutoAttack) {
		c.underAttackByNpc = getIndex();
		c.singleCombatDelay2 = System.currentTimeMillis();
		c.getPA().removeAllWindows();

		NPCCombatAttack npcCombatAttack = new NPCCombatAttack(this, c);

		if (npcAutoAttack.getOnAttack() !=  null)
			npcAutoAttack.getOnAttack().accept(npcCombatAttack);
		if (npcAutoAttack.getProjectile() != null)
			npcAutoAttack.getProjectile().createTargetedProjectile(this, c).send(getInstance());

		if (getAttackType() == CombatType.DRAGON_FIRE) {
			hitDelayTimer += 2;
			c.getCombatItems().absorbDragonfireDamage();
		}

		Preconditions.checkState(npcAutoAttack.getHitDelay() > 0, "Hit delay is zero!");
		final NPC npc = this;
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				CombatHit hit = NPCHitPlayer.applyAutoAttackDamage(npc, c, npcAutoAttack);
				if (npcAutoAttack.getOnHit() != null)
					npcAutoAttack.getOnHit().accept(NPCCombatAttackHit.of(npcCombatAttack, hit));
				container.stop();
			}
		}, npcAutoAttack.getHitDelay());
	}

	public Position getFollowPosition() {
        return switch (getNpcId()) {
            case Skotizo.AWAKENED_ALTAR_EAST -> new Position(1713, 9888);
            case Skotizo.AWAKENED_ALTAR_NORTH -> new Position(1694, 9903);
            case Skotizo.AWAKENED_ALTAR_SOUTH -> new Position(1696, 9872);
            case Skotizo.AWAKENED_ALTAR_WEST -> new Position(1679, 9888);
            default -> null;
        };
    }

	public Position getPosition() {
		return new Position(absX, absY, heightLevel);
	}

	public Position getCenterPosition() {
		Position position = getPosition();
		Position position2 = getPosition().translate(getSize() - 1, getSize() - 1);
		return new Position((position.getX() + position2.getX()) / 2, (position.getY() + position2.getY()) / 2, position.getHeight());
	}

	public void startAnimation(int animationId) {
		startAnimation(new Animation(animationId));
	}

	public void startAnimation(int animationId, AnimationPriority animationPriority) {
		startAnimation(new Animation(animationId, 0, animationPriority));
	}

	public void sendForceMovement(ForceMovement forceMovement) {
		this.forceMovement = forceMovement;
		forceMovementUpdateRequired = true;
		setUpdateRequired(true);
	}

	/**
	 * Teleport
	 *
     */
	public void teleport(int x, int y, int z) {
		teleport(new Position(x, y, z));
	}

	public void teleport(Position position) {
		teleporting = true;
		setUpdateRequired(true);

		if (!this.isPet) {
			this.getRegionProvider().removeNpcClipping(this);
		}

		absX = position.getX();
		absY = position.getY();
		heightLevel = position.getHeight();

		if (!this.isPet) {
			this.getRegionProvider().addNpcClipping(this);
		}
	}

    /**
	 * Sends the request to a client that the npc should be transformed into
	 * another.
	 */
	public void requestTransform(int id) {
		if (npcId != id) {
			transformId = id;
			setNpcId(id);
			transformUpdateRequired = true;
			setUpdateRequired(true);
			this.setNpcCombatDefinition();
		}
	}

	public void appendTransformUpdate(Stream str) {
		str.writeWordBigEndianA(transformId);
	}

	public void updateNPCMovement(Stream str) {
		if (walkDirection == Direction.NONE) {
			if (isUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);

			if (runDirection == Direction.NONE) {
				str.writeBits(2, 1);
				str.writeBits(3, walkDirection.toInteger());
			} else {
				str.writeBits(2, 2);
				str.writeBits(3, walkDirection.toInteger());
				str.writeBits(3, runDirection.toInteger());
			}

			if (isUpdateRequired()) {
				str.writeBits(1, 1);
			} else {
				str.writeBits(1, 0);
			}
		}
	}
	
	/**
	 * Text update
	 **/

	public void forceChat(String text) {
		forcedText = text;
		forcedChatRequired = true;
		setUpdateRequired(true);
	}

	public void appendMask80Update(Stream str) {
		str.writeByte(graphics.size());
		int index = 0;
		for (Graphic graphic : graphics) {
			str.writeByte(index);
			str.writeUShort(graphic.getId());
			str.writeDWord(graphic.getHeight() + (graphic.getDelay() & 0xffff));
			index++;
		}
	}

	public void gfx100(int gfx) {
		startGraphic(new Graphic(gfx, 0, 6553600));
	}
	public void gfx100(int gfx, int height) {
		startGraphic(new Graphic(gfx, 0, 65536 * height));
	}
	public void gfx0(int gfx) {
		startGraphic(new Graphic(gfx, 0, 0));
	}

	@Override
	public boolean isFreezable() {
        return switch (getNpcId()) {
            case 2042, 2043, 2044, 7544, 5129, FragmentOfSeren.NPC_ID, 2205, 3129, 2215, 3162 -> false;
            default -> true;
        };
    }

	public void appendAnimUpdate(Stream str) {
		str.writeWordBigEndian(getAnimation().getId());
		str.writeByte(getAnimation().getDelay());
	}

	private void appendSetFocusDestination(Stream str) {
		str.writeWordBigEndian(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	public void facePosition(Position position) {
		facePosition(position.getX(), position.getY());
	}

	public void facePosition(int x, int y) {
		FocusPointX = 2 * x + 1;
		FocusPointY = 2 * y + 1;
		setUpdateRequired(true);
	}

	public void appendFaceEntity(Stream str) {
		str.writeUShort(face);
	}

	public void facePlayer(int player) {
		if (getNpcId() == Npcs.UNDEAD_COMBAT_DUMMY) {
			return;
		}

		if (!facePlayer) {
			if (face == -1) {
				return;
			}
			face = -1;
		} else {
			face = player + 32768;
		}
		faceEntityUpdateRequired = true;
		setUpdateRequired(true);
	}

	public void faceNPC(int index) {
		face = index;
		faceEntityUpdateRequired = true;
		setUpdateRequired(true);
	}

	public void appendForcedMovementUpdate(Player player, Stream str) {
		str.writeByte(forceMovement.getStart(player).getX());
		str.writeByte(forceMovement.getStart(player).getY());
		str.writeByte(forceMovement.getEnd(player).getX());
		str.writeByte(forceMovement.getEnd(player).getY());
		str.writeUShort(forceMovement.getMoveCycleStart());
		str.writeUShort(forceMovement.getMoveCycleEnd());
		str.writeByte(forceMovement.getMoveDirection());
	}

	public void appendNPCUpdateBlock(Player player, Stream str) {
		if (!isUpdateRequired())
			return;
		int updateMask = 0;
		if (forceMovementUpdateRequired)
			updateMask |= 0x100;
		if (isAnimationUpdateRequired())
			updateMask |= 0x10;
		if (hitUpdateRequired2)
			updateMask |= 8;
		if (isGfxUpdateRequired())
			updateMask |= 0x80;
		if (faceEntityUpdateRequired)
			updateMask |= 0x20;
		if (forcedChatRequired)
			updateMask |= 1;
		if (hitUpdateRequired)
			updateMask |= 0x40;
		if (transformUpdateRequired)
			updateMask |= 2;
		if (FocusPointX != -1)
			updateMask |= 4;

		str.writeUShort(updateMask);

		if (forceMovementUpdateRequired)
			appendForcedMovementUpdate(player, str);
		if (isAnimationUpdateRequired())
			appendAnimUpdate(str);
		if (hitUpdateRequired2)
			appendHitUpdate(str);
		if (isGfxUpdateRequired())
			appendMask80Update(str);
		if (faceEntityUpdateRequired)
			appendFaceEntity(str);
		if (forcedChatRequired) {
			str.writeString(forcedText);
		}
		if (hitUpdateRequired)
			appendHitUpdate(str);
		if (transformUpdateRequired)
			appendTransformUpdate(str);
		if (FocusPointX != -1)
			appendSetFocusDestination(str);
	}

	public void clearUpdateFlags() {
		setUpdateRequired(false);
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		faceEntityUpdateRequired = false;
		transformUpdateRequired = false;
		forcedText = null;
		walkDirection = Direction.NONE;
		runDirection = Direction.NONE;
		FocusPointX = -1;
		FocusPointY = -1;
		teleporting = false;
		forceMovementUpdateRequired = false;
		resetAfterUpdate();
	}

	@Override
	public void resetWalkingQueue() {
		setMovement(Direction.NONE);
	}

	public void setMovement(Direction walkDirection) {
		setMovement(walkDirection, Direction.NONE);
	}

	public void setMovement(Direction walkDirection, Direction runDirection) {
		setWalkDirection(walkDirection);
		setRunDirection(runDirection);
	}

	public void moveTowards(int x, int y) {
		moveTowards(x, y, false, false);
	}

	public void moveTowards(int x, int y, boolean checkClipping) {
		moveTowards(x, y, false, checkClipping);
	}

	public void moveTowards(int x, int y, boolean run, boolean checkClipping) {
		resetWalkingQueue();
		Position moveTowards = new Position(x, y, heightLevel);
		Direction dir1 = Direction.fromDeltas(getPosition(), moveTowards);
		if (!checkClipping || NPCDumbPathFinder.canMoveTo(this, getPosition(), dir1)) {
			setWalkDirection(dir1);
		} else {
			resetWalkingQueue();
			return;
		}

		Position walked = new Position(absX + walkDirection.x(), absY + walkDirection.y());

		if (walked.equals(moveTowards)) {
			return;
		}

		if ((behaviour.isRunnable() || run) && (!checkClipping || NPCDumbPathFinder.canMoveTo(this, walked, runDirection))) {
			setRunDirection(Direction.fromDeltas(walked, moveTowards));
		}
	}

	public boolean revokeWalkingPrivilege;

	public void processMovement() {
		if (revokeWalkingPrivilege)
			return;
		if (walkDirection != Direction.NONE) {
			if (freezeTimer != 0 || (forceMovement != null && !forceMovement.isActive()) || teleporting) {
				resetWalkingQueue();
			} else {
				if (!this.isPet)
					this.getRegionProvider().removeNpcClipping(this);

				absX += walkDirection.x() + runDirection.x();
				absY += walkDirection.y() + runDirection.y();

				if (!this.isPet)
					this.getRegionProvider().addNpcClipping(this);

				setUpdateRequired(true);
			}
		} else {
			Preconditions.checkState(runDirection == Direction.NONE, "How can you walk before you run?");
		}
		//dir >>= 1; // TODO is this needed, commented out when adding npc running?
	}

	@Override
	public void appendHitUpdate(Stream str) {
		if (getHealth().getCurrentHealth() <= 0) {
			setDead(true);
		}

		str.writeUShort(health.getCurrentHealth());
		str.writeUShort(health.getMaximumHealth());
		this.appendHitMarkUpdate(str);
		this.appendHealthBarUpdate(str);
	}

	public int hitDiff2;
	public boolean hitUpdateRequired2;


	@Override
	public void appendHitUpdate2(Stream str) {
		if (getHealth().getCurrentHealth() <= 0) {
			setDead(true);
		}
		str.writeUShort(health.getCurrentHealth());
		str.writeUShort(health.getMaximumHealth());
		this.appendHitMarkUpdate(str);
		this.appendHealthBarUpdate(str);
	}

	public void lowerDefence(double percent) {
		Preconditions.checkArgument(percent > 0 && percent <= 1, "Percent out of bounds.");
		int defence = getDefence();
		int loweredDefence = (int) (defence * (1.0 - percent));
		if (loweredDefence < 0) {
			loweredDefence = 0;
		}
		this.npcCombatDefinition.setLevel(NpcCombatSkill.DEFENCE, loweredDefence);
	}

	public void increaseDefence(int newDefence) {
		this.npcCombatDefinition.setLevel(NpcCombatSkill.DEFENCE, newDefence);
	}

	public int getDefence() {
		return this.npcCombatDefinition.getLevel(NpcCombatSkill.DEFENCE);
	}

	public boolean inRaids() {
		return (getX() > 3210 && getX() < 3368 && getY() > 5137 && getY() < 5759);
	}
	public boolean inXeric() {
		return (getX() > 2685 && getX() < 2743 && getY() > 5440 && getY() < 5495);
	}

	public int getSize() {
		if (definition == null)
			return 1;
		return definition.getSize();
	}

    public String getName() {
		return getDefinition().getName();
	}

    @Override
	public void appendHeal(int amount, Hitmark hitmark) {
		getHealth().increase(amount);
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = amount;
			hitmark1 = hitmark;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = amount;
			hitmark2 = hitmark;
		}
		updateHealthBar(new StaticHealthBarUpdate(this));
		setUpdateRequired(true);
	}

	@Override
	public void appendDamage(Entity entity, int damage, Hitmark hitmark) {
		if (entity != null && entity.isPlayer()) {

			Player player = (Player) entity;

			damage = modifyDamage(player, damage);

			if (!canBeDamaged(player)) {
				damage = 0;
			}

			if (damage > 0 && hitmark == Hitmark.MISS) {
				hitmark = Hitmark.HIT;
			}

			addDamageTaken(player, damage);

			if (player.getRaidsInstance() != null && Boundary.isIn(player, Boundary.FULL_RAIDS)) {
				Raids.damage(player, damage);
			}
		}

		if (damage < 0) {
			damage = 0;
			hitmark = Hitmark.MISS;
		}
		if (getHealth().getCurrentHealth() - damage < 0) {
			damage = getHealth().getCurrentHealth();
		}
		if (getNpcId() != Npcs.UNDEAD_COMBAT_DUMMY) {
			getHealth().reduce(damage);
			if (getHealth().getCurrentHealth() <= 0) {
				setDead(true);
			}
		}
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			updateHitMark(new HitMark(hitmark,damage,0));
        } else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			updateHitMark(new HitMark(hitmark,damage,0));
        }
		updateHealthBar(new StaticHealthBarUpdate(this));
		setUpdateRequired(true);
	}

	@Override
	public boolean susceptibleTo(HealthStatus status) {
		switch (getNpcId()) {
		case 2042:
		case 2043:
		case 2044:
		case 6720:
		case 7413:
		case 7544:
		case 5129:
		case FragmentOfSeren.NPC_ID:
		case 7604:
		case 7605:
		case 7606:
			return false;
		}

		if (status == HealthStatus.POISON) {
			return !defaultNpcStats.isPoisonImmune();
		} else if (status == HealthStatus.VENOM) {
			return !defaultNpcStats.isVenomImmune();
		}

		return true;
	}

    public List<NPCAutoAttack> getNpcAutoAttacks() {
		return Collections.unmodifiableList(npcAutoAttacks);
	}

    public boolean isDeadOrDying() {
		return isDead() || needRespawn || getHealth().getCurrentHealth() == 0;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

    public boolean isAggro() {
		return playerAttackingIndex > 1;
	}

	public void setPlayerAttackingIndex(int playerAttackingIndex) {
		if (this.playerAttackingIndex != playerAttackingIndex) {
			this.playerAttackingIndex = playerAttackingIndex;
		}
	}

    public NpcCombatDefinition getCombatDefinition() {
		return this.npcCombatDefinition;
	}

	public boolean isDemon() {
		return Misc.linearSearch(Configuration.DEMON_IDS, this.npcId) != -1;
	}

	public boolean isUndead() {
		return Misc.linearSearch(Configuration.UNDEAD_NPCS, this.npcId) != -1;
	}

	public boolean isDragon() {
		return Misc.linearSearch(Configuration.DRAG_IDS, this.npcId) != -1;
	}

	public boolean isLeafy() {
		return Misc.linearSearch(Configuration.LEAF_IDS, this.npcId) != -1;
	}

	@Override
	public int getBonus(Bonus bonus) {
		NpcCombatDefinition definition = this.getCombatDefinition();
		if (definition == null) {
			return 0;
		}

        return switch (bonus) {
            case ATTACK_STAB, ATTACK_SLASH, ATTACK_CRUSH -> definition.getAttackBonus(NpcBonus.ATTACK_BONUS);
            case ATTACK_MAGIC -> definition.getAttackBonus(NpcBonus.MAGIC_BONUS);
            case ATTACK_RANGED -> definition.getAttackBonus(NpcBonus.RANGE_BONUS);
            case DEFENCE_STAB -> definition.getDefenceBonus(NpcBonus.STAB_BONUS);
            case DEFENCE_SLASH -> definition.getDefenceBonus(NpcBonus.SLASH_BONUS);
            case DEFENCE_CRUSH -> definition.getDefenceBonus(NpcBonus.CRUSH_BONUS);
            case DEFENCE_MAGIC -> definition.getDefenceBonus(NpcBonus.MAGIC_BONUS);
            case DEFENCE_RANGED -> definition.getDefenceBonus(NpcBonus.RANGE_BONUS);
            case MAGIC_DMG -> definition.getAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS);
            default -> 0;
        };

    }

}
