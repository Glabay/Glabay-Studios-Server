package io.xeros.model.multiplayersession.duel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com.mchange.v1.util.SimpleMapEntry;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.grounditem.GroundItemManager;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.multiplayersession.MultiplayerSession;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSessionRules.Rule;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ItemStakeLog;

public class DuelSession extends MultiplayerSession {

    /**
     * The point in where you spawn in a duel. Do not change this.
     */
    public static final int RANDOM_DUELING_RESPAWN = 0;
    static final Boundary NO_OBSTACLE_ARENA = Boundary.DUEL_ARENA[0];

	static final Boundary OBSTACLE_ARENA = Boundary.DUEL_ARENA[1];

	DuelSessionRules rules = new DuelSessionRules();

	private Optional<Player> winner = Optional.empty();

	private Boundary arenaBoundary;

	private boolean attackingOperationable;

	private long lastRuleModification;

	public DuelSession(List<Player> players, MultiplayerSessionType type) {
		super(players, type);
	}

	@Override
	public void accept(Player player, Player recipient, int stageId) {
		switch (stageId) {
		case MultiplayerSessionStage.OFFER_ITEMS:
			if (System.currentTimeMillis() - lastRuleModification < 5_000) {
				player.sendMessage("<col=CC0000>A rule was changed in the last 5 seconds, you cannot accept yet.");
				player.getPA().sendString("A rule was changed in recently, you cannot accept yet.", 6684);
				return;
			}
			if (recipient.getItems().freeSlots() < getItems(player).size()) {
				player.getPA().sendString("You have offered more items than " + recipient.getDisplayName() + " has free space.", 6684);
				recipient.getPA().sendString("You do not have enough inventory space to continue.", 6684);
				return;
			}
			if (player.hasFollower || recipient.hasFollower) {
				player.getPA().sendString("You or your opponent has a pet spawned, pick it up.", 6684);
				recipient.getPA().sendString("You or your opponent has a pet spawned, pick it up.", 6684);
				return;
			}
			if (player.getItems().isWearingItem(10501, 3) || recipient.getItems().isWearingItem(10501, 3)) {
				player.getPA().sendString("You or your opponent is wearing a faulty weapon, take it off.", 6684);
				recipient.getPA().sendString("You or your opponent is wearing a faulty weapon, take it off.", 6684);
				return;
			}
			if (recipient.getItems().freeSlots() < getDisabledEquipmentCount(recipient)) {
				player.getPA().sendString("Player doesn't have enough space to unequip the disabled items.", 6684);
				recipient.getPA().sendString("Not enough space to remove the disabled equipped items.", 6684);
				return;
			}
			if (rules.contains(Rule.NO_MELEE) && rules.contains(Rule.NO_MAGE) && rules.contains(Rule.NO_WEAPON)) {
				player.getPA().sendString("You cannot have no melee, no mage and no weapon selected.", 6684);
				recipient.getPA().sendString("You cannot have no melee, no mage and no weapon selected.", 6684);
				return;
			}
			for (Player p : players) {
				GameItem overlap = getOverlappedItem(p);
				if (overlap != null) {
					p.getPA().sendString("Too many of one item! The other player has " + Misc.getValueRepresentation(overlap.amount()) + " "
							+ ItemAssistant.getItemName(overlap.id()) + " in their inventory.", 6684);
					getOther(p).getPA().sendString("The other player has offered too many of one item, they must remove some.", 6684);
					return;
				}
			}
			if (stage.hasAttachment() && stage.getAttachment() != player) {
				stage.setStage(MultiplayerSessionStage.CONFIRM_DECISION);
				stage.setAttachment(null);
				updateMainComponent();
				return;
			}
			player.getPA().sendString("Waiting for other player...", 6684);
			stage.setAttachment(player);
			recipient.getPA().sendString("Other player has accepted", 6684);
			break;

		case MultiplayerSessionStage.CONFIRM_DECISION:
			if (stage.hasAttachment() && stage.getAttachment() != player) {
				stage.setStage(MultiplayerSessionStage.FURTHER_INTERATION);
				Player opponent = getOther(player);
				clearPlayerAttributes(player);
				clearPlayerAttributes(opponent);
				arenaBoundary = rules.contains(Rule.OBSTACLES) ? OBSTACLE_ARENA : NO_OBSTACLE_ARENA;
				int teleportX = arenaBoundary.getMinimumX() + 6 + Misc.random(12);
				int teleportY = arenaBoundary.getMinimumY() + 1 + Misc.random(11);
				player.getPA().movePlayerDuel(teleportX, teleportY, 0);
				opponent.getPA().movePlayerDuel(teleportX, teleportY - 1, 0);
				player.getPA().createPlayerHints(10, opponent.getIndex());
				opponent.getPA().createPlayerHints(10, player.getIndex());
				player.getPA().removeAllWindows();
				opponent.getPA().removeAllWindows();
				removeDisabledEquipment(player);
				removeDisabledEquipment(opponent);
				CycleEventHandler.getSingleton().addEvent(this, new AttackingOperation(), 2);
				return;
			}
			stage.setAttachment(player);
			player.getPA().sendString("Waiting for other player...", 6571);
			recipient.getPA().sendString("Other player has accepted", 6571);
			break;

		default:
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			break;
		}
	}

	@Override
	public void updateOfferComponents() {
		for (Player player : items.keySet()) {
			Player recipient = getOther(player);
			player.getItems().sendInventoryInterface(3322);
			refreshItemContainer(player, player, 6669);
			refreshItemContainer(player, getOther(player), 6670);
			player.getPA().sendString("", 6684);
			player.getPA().sendString("Dueling with: " + recipient.getDisplayName() + " (level-" + recipient.combatLevel + ")", 6671);
		}
	}

	@Override
	public boolean itemAddable(Player player, GameItem item) {
		if (item.id() == 12006) {
			player.sendMessage("You cannot stake this item, it is deemed as untradable.");
			return false;
		}
		if (!player.getItems().isTradable(item.id())) {
			player.sendMessage("You cannot stake this item, it is deemed as untradable.");
			return false;
		}
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return true;
	}

	@Override
	public boolean itemRemovable(Player player, GameItem item) {
		if (!Server.getMultiplayerSessionListener().inAnySession(player)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return stage.getStage() == MultiplayerSessionStage.OFFER_ITEMS;
	}

	@Override
	public void updateMainComponent() {
		if (stage.getStage() == MultiplayerSessionStage.OFFER_ITEMS) {
			for (Player player : players) {
				Player recipient = getOther(player);
				player.setTrading(true);
				player.getItems().sendInventoryInterface(3322);
				refreshItemContainer(player, player, 6669);
				refreshItemContainer(player, player, 6670);
				player.getPA().sendString("Dueling with: " + recipient.getDisplayName() + " (level-" + recipient.combatLevel + ")", 6671);
				player.getPA().sendString("Whip/DDS", 669);
				player.getPA().sendString("", 6684);
				player.getPA().sendFrame248(6575, 3321);
				player.getPA().sendFrame87(286, 0);
			}
		} else if (stage.getStage() == MultiplayerSessionStage.CONFIRM_DECISION) {
			for (Player player : players) {
				Player recipient = getOther(player);
				player.getItems().sendInventoryInterface(3214);
				StringBuilder itemList = new StringBuilder();
				List<GameItem> items = getItems(player);
				for (GameItem item : items) {
					if (item.id() > 0 && item.amount() > 0) {
						itemList.append(ItemAssistant.getItemName(item.id()) + " x " + Misc.getValueRepresentation(item.amount()) + "\\n");
					}
				}
				player.getPA().sendString(itemList.toString(), 6516);
				itemList = new StringBuilder();
				items = getItems(recipient);
				for (GameItem item : items) {
					if (item.id() > 0 && item.amount() > 0) {
						itemList.append(ItemAssistant.getItemName(item.id()) + " x " + Misc.getValueRepresentation(item.amount()) + "\\n");
					}
				}
				player.getPA().sendString(itemList.toString(), 6517);
				player.getPA().sendString("", 8242);
				for (int i = 8238; i <= 8253; i++) {
					player.getPA().sendString("", i);
				}
				player.getPA().sendString("Hitpoints will be restored.", 8250);
				player.getPA().sendString("Boosted stats will be reset.", 8238);
				int offset = 0;
				for (Rule rule : rules.rules) {
					if (!rule.getDetails().isEmpty()) {
						player.getPA().sendString(rule.getDetails(), 8242 + offset);
						offset++;
					}
				}
				player.getPA().sendString("", 6571);
				player.getPA().sendFrame248(6412, 197);
			}
		}
	}

	@Override
	public void give() {
		if (!winner.isPresent()) {
			players.forEach((plr) -> {
				plr.sendMessage("The duel was a tie.");
				moveAndClearAttributes(plr);
				List<GameItem> refundItems = items.get(plr);
				refundItems.forEach(item -> plr.getItems().addItem(item.id(), item.amount()));
				items.remove(plr);
			});
			return;
		}
		players.forEach(this::moveAndClearAttributes);
		if (!Objects.equals(getOther(winner.get()), winner.get())) {
			Player other = getOther(winner.get());
			items.get(winner.get()).addAll(items.get(other));
			if (items.get(winner.get()).size() > 0) {
				for (GameItem item : items.get(winner.get())) {
					long totalSum = (long) winner.get().getItems().getItemAmount(item.id()) + item.amount();
					
					if (winner.get().getItems().freeSlots() == 0 || winner.get().getItems().playerHasItem(item.id()) && totalSum > Integer.MAX_VALUE) {
						winner.get().getItems().sendItemToAnyTabOrDrop(new BankItem(item.id(), item.amount()),
								Configuration.DUELING_RESPAWN_X + (Misc.random(RANDOM_DUELING_RESPAWN)), Configuration.DUELING_RESPAWN_Y + (Misc.random(RANDOM_DUELING_RESPAWN)));
					} else {
						winner.get().getItems().addItem(item.id(), item.amount());
					}
				}

				Server.getLogging().write(new ItemStakeLog(winner.get(), other.getLoginName(), items.get(winner.get()), items.get(other)));
				Server.getLogging().write(new ItemStakeLog(other, winner.get().getLoginName(), items.get(other), items.get(winner.get())));
			}
			showRewardComponent(winner.get());
			winner.get().setDuelWinsCounter(winner.get().getDuelWinsCounter() + 1);
			winner.get().sendMessage("You have now won a total of @blu@" + winner.get().getDuelWinsCounter() + "@bla@ duels.");
		} else {
			winner.get().sendMessage("You cannot be the winner and the loser of a duel.");
		}
		GroundItemManager.INSTANCE.onRegionChange(winner.get());
		items.clear();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void withdraw() {
		for (Player player : items.keySet()) {
			if (Objects.isNull(player)) {
				continue;
			}
			if (items.get(player).size() <= 0) {
				continue;
			}
			for (GameItem item : items.get(player)) {
				player.getItems().addItem(item.id(), item.amount());
			}
		}
	}

	public void toggleRule(Player player, Rule rule) {
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			player.sendMessage("You cannot change rules whilst on the second interface.");
			return;
		}
		if (rule.equals(Rule.NO_MELEE) || rule.equals(Rule.NO_RANGE) || rule.equals(Rule.NO_MAGE)) {
			long count = rules.rules.stream().filter(r -> r.equals(Rule.NO_MELEE) || r.equals(Rule.NO_RANGE) || r.equals(Rule.NO_MAGE)).count();
			if (count >= 2 && !rules.contains(rule)) {
				player.getPA().sendString("You must fight with at least one combat style.", 6684);
				return;
			}
		}
		if (rules.contains(rule)) {
			rules.setTotalValue(rules.getTotalValue() - rule.getValue());
			rules.remove(rule);
		} else {
			rules.setTotalValue(rules.getTotalValue() + rule.getValue());
			rules.add(rule);
		}
		if (rules.contains(Rule.WHIP_AND_DDS) && rule != Rule.WHIP_AND_DDS && rule != Rule.NO_SPECIAL_ATTACK) {
			if (rule.equals(Rule.NO_MELEE) || rule.equals(Rule.OBSTACLES) || rule.equals(Rule.NO_WEAPON)) {
				rules.remove(Rule.WHIP_AND_DDS);
				rules.setTotalValue(rules.getTotalValue() - Rule.WHIP_AND_DDS.getValue());
			}
		}
		lastRuleModification = System.currentTimeMillis();
		stage.setAttachment(null);
		player.getPA().sendString("", 6684);
		getOther(player).getPA().sendString("", 6684);
		refreshRules();
	}

	public void moveAndClearAttributes(Player player) {
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		//player.getPA().showOption(3, 0, "Challenge", 3);
		player.getPA().createPlayerHints(10, -1);
		player.getPA().movePlayerDuel(Configuration.DUELING_RESPAWN_X, Configuration.DUELING_RESPAWN_Y, 0);
		player.freezeTimer = 1;
		player.specAmount = 10.0;
		player.getPA().resetFollow();
		player.attacking.reset();
		player.isSkulled = false;
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.attackedPlayers.clear();
		player.headIconPk = -1;
		player.skullTimer = -1;
		player.getPA().requestUpdates();
		clearPlayerAttributes(player);
	}

	private void clearPlayerAttributes(Player player) {
		for (int i = 0; i < player.playerLevel.length; i++) {
			player.playerLevel[i] = player.getPA().getLevelForXP(player.playerXP[i]);
			player.getPA().refreshSkill(i);
		}
		player.getHealth().reset();
		player.specAmount = 10.0;
		player.resetDamageTaken();
		CombatPrayer.resetPrayers(player);
		player.getPotions().resetOverload();
		player.getPotions().resetCombatDivine();
		player.getPotions().resetMageDivine();
		player.getPotions().resetRangeDivine();
		player.getPotions().resetPotionBoost();
		player.vengOn = false;
		player.usingSpecial = false;
		player.graniteMaulSpecialCharges = 0;
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.getHealth().removeAllStatuses();
		player.getHealth().removeAllImmunities();
		Server.getEventHandler().stop(player, "staff_of_the_dead");
	}

	public void showRewardComponent(Player c) {
		if (Objects.isNull(c) || Objects.isNull(getOther(c))) {
			return;
		}
		List<GameItem> itemList = items.get(getOther(c));
		if (itemList.size() > 28) {
			itemList.subList(0, 27);
		}
		c.getPA().sendString(Integer.toString(getOther(c).combatLevel), 6839);
		c.getPA().sendString(getOther(c).getDisplayName(), 6840);
		c.getPA().showInterface(6733);
		if (c.getOutStream() != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeUShort(6822);
			c.getOutStream().writeUShort(itemList.size());
			for (GameItem item : itemList) {
				if (item.amount() > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(item.amount());
				} else {
					c.getOutStream().writeByte(item.amount());
				}
				if (item.id() > Configuration.ITEM_LIMIT || item.id() < 0) {
					item = new GameItem(Configuration.ITEM_LIMIT, item.id());
				}
				c.getOutStream().writeWordBigEndianA(item.id() + 1);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public void sendDuelEquipment() {
		players.stream().filter(Objects::nonNull).forEach(c -> {
			if (c.getOutStream() != null) {
				for (int i = 0; i < c.playerEquipment.length; i++) {
					c.getOutStream().createFrameVarSizeWord(34);
					c.getOutStream().writeUShort(13824);
					c.getOutStream().writeByte(i);
					c.getOutStream().writeUShort(c.playerEquipment[i] + 1);
					if (c.playerEquipment[i] > -1) {
						if (c.playerEquipmentN[i] > 254) {
							c.getOutStream().writeByte(255);
							c.getOutStream().writeDWord(c.playerEquipmentN[i]);
						} else {
							c.getOutStream().writeByte(c.playerEquipmentN[i]);
						}
					} else {
						c.getOutStream().writeByte(0);
					}
					c.getOutStream().endFrameVarSizeWord();
					c.flushOutStream();
				}
			}
		});
	}

	private int getDisabledEquipmentCount(Player player) {
		final int MINIMUM_VALUE = Rule.NO_HELM.ordinal();
		int count = 0;
		for (Rule rule : Rule.values()) {
			if (rule.ordinal() < MINIMUM_VALUE) {
				continue;
			}
			if (!rules.contains(rule)) {
				continue;
			}
			int equipmentSlot = rule.ordinal() - MINIMUM_VALUE;
			if (equipmentSlot >= 6) {
				equipmentSlot++;
				if (equipmentSlot >= 8) {
					equipmentSlot++;
					if (equipmentSlot >= 11) {
						equipmentSlot++;
					}
				}
			}
			if (equipmentSlot == 6 || equipmentSlot == 8 || equipmentSlot == 11) {
				continue;
			}
			if (player.playerEquipment[equipmentSlot] > -1) {
				count++;
			}
		}
		return count;
	}

	private void removeDisabledEquipment(Player player) {
		final int MINIMUM_VALUE = Rule.NO_HELM.ordinal();
		for (Rule rule : Rule.values()) {
			if (rule.ordinal() < MINIMUM_VALUE) {
				continue;
			}
			if (!rules.contains(rule)) {
				continue;
			}
			int equipmentSlot = rule.ordinal() - MINIMUM_VALUE;
			if (equipmentSlot >= 6) {
				equipmentSlot++;
				if (equipmentSlot >= 8) {
					equipmentSlot++;
					if (equipmentSlot >= 11) {
						equipmentSlot++;
					}
				}
			}
			if (equipmentSlot == 6 || equipmentSlot == 8 || equipmentSlot == 11) {
				continue;
			}
			if (player.playerEquipment[equipmentSlot] > -1) {
				player.getItems().unequipItem(player.playerEquipment[equipmentSlot], equipmentSlot);
			}
		}
	}

	public void setWinner(Player winner) {
		this.winner = Optional.of(winner);
	}

	public Optional<Player> getWinner() {
		return winner;
	}

	public void refreshRules() {
		players.stream().filter(Objects::nonNull).forEach(p -> p.getPA().sendFrame87(286, rules.getTotalValue()));
	}

	public DuelSessionRules getRules() {
		return rules;
	}

	public Boundary getArenaBoundary() {
		return arenaBoundary;
	}

	public boolean isAttackingOperationable() {
		return attackingOperationable;
	}

	class AttackingOperation extends CycleEvent {

		int time = 3;

		@Override
		public void execute(CycleEventContainer container) {
			for (Player player : players) {
				if (Objects.isNull(player)) {
					finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					container.stop();
					return;
				}
			}
			if (time <= 0) {
				players.stream().filter(Objects::nonNull).forEach(p -> p.forcedChat("FIGHT!"));
				attackingOperationable = true;
				container.stop();
			} else if (time > 0) {
				players.stream().filter(Objects::nonNull).forEach(p -> p.forcedChat(Integer.toString(time)));
				time--;
			} else {
				container.stop();
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void logSession(MultiplayerSessionFinalizeType type) {
		if (type == MultiplayerSessionFinalizeType.WITHDRAW_ITEMS || !winner.isPresent()) {
			return;
		}
		ArrayList<Entry<Player, String>> participantList = new ArrayList<>();
		for (Player player : items.keySet()) {
			String items = createItemList(player);
			Entry<Player, String> participant = new SimpleMapEntry(player, items);
			participantList.add(participant);
		}
	}

	private String createItemList(Player player) {
		if (items.get(player).size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (GameItem item : items.get(player)) {
			sb.append(ItemAssistant.getItemName(item.id()));
			if (item.amount() != 1) {
				sb.append(" x" + item.amount());
			}
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2).replaceAll("'", "\\\\'");
	}

}
