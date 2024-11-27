package io.xeros.content.minigames.bounty_hunter.events;

import io.xeros.content.combat.pvp.WildAntiFarm;
import io.xeros.content.minigames.bounty_hunter.BountyHunter;
import io.xeros.content.minigames.bounty_hunter.Target;
import io.xeros.content.minigames.bounty_hunter.TargetEvent;
import io.xeros.content.minigames.bounty_hunter.TargetState;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 
 * @author Jason MacKeigan
 * @date Nov 13, 2014, 2:27:05 PM
 */
public class TargetSelector extends TargetEvent {

	public TargetSelector(BountyHunter bountyHunter) {
		super(bountyHunter);
	}

	@Override
	public void execute(CycleEventContainer container) {
		BountyHunter bh = super.bountyHunter;
		Player player = bh.getPlayer();
		if (!isExecutable()) {
			container.stop();
			return;
		}
		Predicate<Player> viableTarget = t -> t != null 
				&& t != player //player isnt null
				&& t.getPosition().inWild() //player must be in wild
				&& !t.getBH().hasTarget() //player doesnt have target already
				&& t.wildLevel > 0 //above 0 wild
				&& !t.getBH().getTargetState().hasKilledRecently() //player killed recently (gives them 1 minute before new target)
				&& WildAntiFarm.canReceiveRewards(player, t) // Check if they can receive rewards from killing this player
				&& !t.isInvisible() //wont assign invisible people
				&& !t.isSameComputer(player) //same ip
				&& !t.getPosition().inClanWars();//not in clan wars
		List<Player> possibleTargets = new ArrayList<>(1);
		for (int levelOffset = 0; levelOffset < player.wildLevel + 1; levelOffset++) {
			//player.sendMessage("" + levelOffset);
			final int level = levelOffset;
			possibleTargets = PlayerHandler.nonNullStream()
					.filter(viableTarget
							.and(t -> Misc.combatDifference(player, t) <= level)
							.and(t -> !t.isInIronmanGroupWith(player)))
					.collect(Collectors.toList());

			if (possibleTargets.size() > 0) {
				break;
			}
		}
		if (possibleTargets.size() <= 0) {
			return;
		}
		Optional<Player> randomTarget = Optional.of(possibleTargets.get(Misc.random(possibleTargets.size() - 1)));
		randomTarget.ifPresent(target -> {
			assignTarget(player, target);
			assignTarget(target, player);
			container.stop();
			return;
		});
	}

	@Override
	public void onStopped() {
		if (Objects.nonNull(bountyHunter.getPlayer())) {
			bountyHunter.setTargetState(TargetState.NONE);
		}
	}

	/**
	 * Determines if the selection event should be executed based on some conditions.
	 * 
	 * @return if true, the event will start. Otherwise, the event should come to a halt.
	 */
	public boolean isExecutable() {
		BountyHunter bh = super.bountyHunter;
		Player player = bh.getPlayer();
		if (Objects.isNull(player) || player.isDisconnected()) {
			return false;
		}
		return !bh.getTargetState().hasKilledRecently() && !bh.getTargetState().isPenalized() && !player.isInvisible()
				&& player.getPosition().inWild() && !player.getPosition().inClanWars() && !bh.hasTarget();
	}

	private void assignTarget(Player player, Player target) {
		player.getBH().setTargetState(TargetState.SELECTED);
		player.getBH().setTarget(new Target(target.getLoginName()));
		player.getBH().updateTargetUI();
		player.sendMessage("<col=FF0000>You've been assigned a target: " + target.getDisplayName() + "</col>");
		player.getPA().createPlayerHints(10, target.getIndex());
	}
}
