package io.xeros.content.bosses.zulrah;

import java.util.HashMap;
import java.util.Map;

import io.xeros.Server;
import io.xeros.content.bosses.zulrah.impl.CreateToxicStageOne;
import io.xeros.content.bosses.zulrah.impl.MageStageEight;
import io.xeros.content.bosses.zulrah.impl.MageStageFive;
import io.xeros.content.bosses.zulrah.impl.MageStageThree;
import io.xeros.content.bosses.zulrah.impl.MeleeStageSix;
import io.xeros.content.bosses.zulrah.impl.MeleeStageTen;
import io.xeros.content.bosses.zulrah.impl.MeleeStageTwo;
import io.xeros.content.bosses.zulrah.impl.RangeStageEleven;
import io.xeros.content.bosses.zulrah.impl.RangeStageFour;
import io.xeros.content.bosses.zulrah.impl.RangeStageNine;
import io.xeros.content.bosses.zulrah.impl.RangeStageSeven;
import io.xeros.content.bosses.zulrah.impl.SpawnZulrahStageZero;
import io.xeros.content.commands.owner.Object;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.entity.CombatType;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class Zulrah {

	/**
	 * The minion snake npc id
	 */
	public static final int SNAKELING = 2045;

	public static final int TOXIC_SMOKE = 11700;

	/**
	 * The relative lock for this event
	 */
	private final Object EVENT_LOCK = new Object();

	/**
	 * The player associated with this event
	 */
	private final Player player;

	/**
	 * The single instance of zulrah
	 */
	private LegacySoloPlayerInstance zulrahInstance;

	/**
	 * The zulrah npc
	 */
	private NPC npc;

	/**
	 * The current stage of zulrah
	 */
	private int stage;

	private boolean starting;

	/**
	 * Determines if the npc is transforming or not.
	 */
	private boolean transforming;

	/**
	 * A mapping of all the stages
	 */
	private final Map<Integer, ZulrahStage> stages = new HashMap<>();

	/**
	 * Creates a new Zulrah event for the player
	 * 
	 * @param player the player
	 */
	public Zulrah(Player player) {
		this.player = player;
		stages.put(0, new SpawnZulrahStageZero(this, player));
		stages.put(1, new CreateToxicStageOne(this, player));
		stages.put(2, new MeleeStageTwo(this, player));
		stages.put(3, new MageStageThree(this, player));
		stages.put(4, new RangeStageFour(this, player));
		stages.put(5, new MageStageFive(this, player));
		stages.put(6, new MeleeStageSix(this, player));
		stages.put(7, new RangeStageSeven(this, player));
		stages.put(8, new MageStageEight(this, player));
		stages.put(9, new RangeStageNine(this, player));
		stages.put(10, new MeleeStageTen(this, player));
		stages.put(11, new RangeStageEleven(this, player));
	}

	public void disposeZulrah() {
		if (zulrahInstance != null) {
			zulrahInstance.dispose();
			zulrahInstance = null;
		}
		CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
	}

	public void initialize() {
		setStarting(true);
		disposeZulrah();
		zulrahInstance = new LegacySoloPlayerInstance(player, Boundary.ZULRAH);

		stage = 0;
		player.getPA().removeAllWindows();
		player.getPA().sendScreenFade("Welcome to Zulrah's shrine", 1, 5);
		CycleEventHandler.getSingleton().addEvent(EVENT_LOCK, stages.get(0), 1);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getZulrahEvent().isStarting()) {
					return;
				}
				if (npc != null)
					npc.facePlayer(player.getIndex());
				if (player.getZulrahEvent().getInstancedZulrah() == null || !Boundary.ZULRAH.in(player)) {
					container.stop();
					disposeZulrah();
				}
			}
		}, 1);
	}

	/**
	 * Determines if the player is standing in a toxic location
	 * 
	 * @return true of the player is in a toxic location
	 */
	public boolean isInToxicLocation() {
		for (int x = player.getX() - 1; x < player.getX() + 1; x++) {
			for (int y = player.getY() - 1; y < player.getY() + 1; y++) {
				if (Server.getGlobalObjects().exists(11700, x, y, player.heightLevel)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Stops the zulrah instance and concludes the events
	 */
	public void stop() {
		setStarting(false);
		CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
		zulrahInstance.killNpcs();
		Server.getGlobalObjects().remove(TOXIC_SMOKE, zulrahInstance);
	}

	public void changeStage(int stage, CombatType combatType, ZulrahLocation location) {
		this.stage = stage;
		CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
		CycleEventHandler.getSingleton().addEvent(EVENT_LOCK, stages.get(stage), 1);
		if (stage == 1) {
			return;
		}
		int type = combatType == CombatType.MELEE ? 2043 : combatType == CombatType.MAGE ? 2044 : 2042;
		npc.startAnimation(5072);
		npc.attackTimer = 8;
		transforming = true;
		npc.resetAttack();
		player.attacking.reset();
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				player.attacking.reset();
				if (container.getTotalTicks() == 2) {
					npc.teleport(location.getLocation().x, location.getLocation().y, npc.heightLevel);
				} else if (container.getTotalTicks() == 4) {
					npc.requestTransform(type);
					npc.startAnimation(5071);
					npc.setFacePlayer(true);
					npc.facePlayer(player.getIndex());
				} else if (container.getTotalTicks() == 8) {
					transforming = false;
					container.stop();
				}
			}

		}, 1);
	}

	/**
	 * Determines if any of the events alive contains the event lock
	 * 
	 * @return true if any of the events are active with this as the owner
	 */
	public boolean isActive() {
		return CycleEventHandler.getSingleton().isAlive(EVENT_LOCK);
	}

	/**
	 * The {@link LegacySoloPlayerInstance} object for this class
	 * 
	 * @return the zulrah instance
	 */
	public InstancedArea getInstancedZulrah() {
		return zulrahInstance;
	}

	/**
	 * The reference to zulrah, the npc
	 * 
	 * @return the reference to zulrah
	 */
	public NPC getNpc() {
		return npc;
	}

	/**
	 * The instance of the Zulrah {@link NPC}
	 * 
	 * @param npc the zulrah npc
	 */
	public void setNpc(NPC npc) {
		this.npc = npc;
	}

	/**
	 * The stage of the zulrah event
	 * 
	 * @return the stage
	 */
	public int getStage() {
		return stage;
	}

	/**
	 * Determines if the NPC is transforming or not
	 * 
	 * @return {@code true} if the npc is in a transformation stage
	 */
	public boolean isTransforming() {
		return transforming;
	}

	public void setStarting(boolean starting) {
		this.starting = starting;
	}

	public boolean isStarting() {
		return starting;
	}
}
