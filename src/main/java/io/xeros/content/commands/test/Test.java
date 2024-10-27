package io.xeros.content.commands.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import io.xeros.content.CompletionistCape;
import io.xeros.content.bosses.hydra.AlchemicalHydra;
import io.xeros.content.combat.pvp.Killstreak;
import io.xeros.content.commands.Command;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.content.items.MaxCapeCombinations;
import io.xeros.content.itemskeptondeath.modifiers.AlwaysKeptDeathItem;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.content.minigames.tob.party.TobParty;
import io.xeros.content.privatemessaging.FriendType;
import io.xeros.content.skills.Skill;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.content.wildwarning.WildWarning;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.Items;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.lock.CompleteLock;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class Test extends Command {
	
	private static final int[] rewards = { 20773, 20775, 20777, 20779 };
	
	private enum RankUpgrade {
		REGULAR_DONATOR(Right.REGULAR_DONATOR, 50),
		EXTREME_DONOR(Right.EXTREME_DONOR, 100),
		LEGENDARY_DONATOR(Right.LEGENDARY_DONATOR, 250),
		DIAMOND_CLUB(Right.DIAMOND_CLUB, 500), 
		ONYX_CLUB(Right.ONYX_CLUB, 1000);
		
		/**
		 * The rights that will be appended if upgraded
		 */
		private final Right rights;

		/**
		 * The amount required for the upgrade
		 */
		private final int amount;

		RankUpgrade(Right rights, int amount) {
			this.rights = rights;
			this.amount = amount;
		}
	}

	@Override
	public void execute(Player player, String commandName, String input) {
		switch (input) {
			case "gimlog":
				GroupIronmanRepository.getGroupForOnline(player).ifPresentOrElse(group -> {
					for (int i = 0; i < 550; i++)
						group.addDropItemLog(player, new GameItem(i));
					for (int i = 0; i < 550; i++)
						group.addWithdrawItemLog(player, new GameItem(i));
				}, () -> player.sendMessage("No gim group."));
				break;
			case "collect":
				for (int i = 0; i < 20; i++)
					player.getCollectionBox().add(player, new GameItem(4151));
				break;
			case "resetwildwarn":
				WildWarning.resetWarningCount(player);
				break;
			case "perdulost":
				player.getPerduLostPropertyShop().add(player, new GameItem(Items.FIGHTER_TORSO));
				player.getPerduLostPropertyShop().add(player, new GameItem(Items.FIRE_CAPE));
				player.getPerduLostPropertyShop().add(player, new GameItem(Items.MAX_CAPE));
				player.getPerduLostPropertyShop().add(player, new GameItem(Items.COMPLETIONIST_CAPE));
				break;
			case "printuntradeables":
				ItemDef.getDefinitions().values().stream().filter(it -> !it.isTradable()
						&& !new AlwaysKeptDeathItem().getItemIds().contains(it.getId())
				).sorted((a, b) -> GameItem.comparePrice(new GameItem(a.getId()), new GameItem(b.getId()))).forEach(it -> {
					System.out.println(it.getId() + ", " + it.getName() + ", value=" + Misc.insertCommas(it.getShopValue()));
				});
				break;
			case "maxcapes":
				Arrays.stream(MaxCapeCombinations.values()).forEach(it -> {
					player.getItems().sendItemToAnyTab(it.getCombinedWithItemId(), 1);
					player.getItems().sendItemToAnyTab(Items.MAX_CAPE, 1);
					player.getItems().sendItemToAnyTab(Items.MAX_HOOD, 1);
				});
				break;
			case "pm":
				for (int i = 0; i < 100; i++)
					player.getFriendsList().addNew("bot " + i, FriendType.FRIEND);
				break;
			case "lock":
				player.lock(new CompleteLock());
				break;
			case "unlock":
				player.unlock();
				break;
			case "compreq":
				CompletionistCape.sendRequirementsInterface(player);
				break;
			case "skillset":
				for (Skill skill : Skill.values()) {
					player.getPA().setSkillLevel(skill.getId(), skill.getId(), player.getPA().getXPForLevel(skill.getId()) + 1);
				}
				break;
		case "tobroll":
			TheatreOfBloodChest.rewardItems(player, TheatreOfBloodChest.getRandomItems(true, 5));
			break;
		case "tob":
			player.moveTo(new Position(3671, 3219, 0));
			new TobParty().add(player);
			player.getTobContainer().startTob();
			break;
		case "tobnext":
			if (player.getTobContainer().inTob()) {
				Lists.newArrayList(player.getInstance().getNpcs()).forEach(NPC::unregisterInstant);
				TobInstance instance = (TobInstance) player.getInstance();
				instance.getPlayers().forEach(plr -> {
					instance.moveToNextRoom(plr);
					instance.getMvpPoints().award(plr, 1);
				});
			}
			break;
			case "corpo":
				NPCHandler.nonNullStream().filter(npc -> Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR))
						.forEach(npc -> {
							if (npc.getHealth().getCurrentHealth() < npc.getHealth().getMaximumHealth()) {
								npc.getHealth().increase(npc.getHealth().getMaximumHealth());
							}
						});
				break;
		case "hydra":
			new AlchemicalHydra(player);
		case "tekton":
			player.getPA().startTeleport(3309, 5277, 1, "modern", false);
			break;
		case "mystics":
			player.getPA().startTeleport(3343, 5248, 1, "modern", false);
			break;
		case "votebonus":
			player.xpScrollTicks += TimeUnit.MINUTES.toMillis(30) / 600;
			player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, (VotePanelManager.getBonusXPTimeInMinutes(player) + 30));
			break;
		case "scrollbonus":
			player.xpScroll = true;
			player.xpScrollTicks += TimeUnit.MINUTES.toMillis(30) / 600;
			player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, (int) (player.xpScrollTicks/100));
			break;
		case "upgrade":
			ArrayList<RankUpgrade> orderedList = new ArrayList<>(Arrays.asList(RankUpgrade.values()));
			orderedList.sort((one, two) -> Integer.compare(two.amount, one.amount));
			orderedList.stream().filter(r -> player.amDonated >= r.amount).findFirst().ifPresent(rank -> {
				RightGroup rights = player.getRights();
				Right right = rank.rights;
				if (!rights.contains(right)) {
					player.sendMessage("Congratulations, your rank has been upgraded to " + right.toString() + ".");
					player.sendMessage("This rank is hidden, but you will have all it's perks.");
					rights.add(right);
				}
			});
		break;
		
		case "tek":
			NPCHandler.tektonAttack = "SPECIAL";
			System.out.println("Setting attack: " + NPCHandler.tektonAttack);
			break;
			
		case "walk":
			NPC TEKTON = NPCHandler.getNpc(7544);
			NPCDumbPathFinder.walkTowards(TEKTON, 3308, 5296);
			break;
		
		case "placeholder"://might conflict with the other placeholder cmd
			player.placeHolders = !player.placeHolders;
			player.sendMessage("placeholder: " + player.placeHolders);
			break;
		
		case "save":
			Wogw.save();
			break;
			
		case "load":
			Wogw.init();
			break;
			
		case "corp":
			player.getPA().walkableInterface(38000);
			break;
			
		case "tele":
			player.getPA().sendFrame126("Slayer Tower", 62112);
			player.getPA().sendFrame126("Lletya", 62119);
			player.getPA().sendFrame126("Mithril Dragons", 62120);
			player.getPA().sendFrame126("Demonic Gorillas", 62121);
			player.getPA().sendFrame126("@cr10@@bla@Boss Locations @cr22@ = @red@Wilderness", 62122);
			player.getPA().sendFrame126("@cr22@King Black Dragon", 62123);
			player.getPA().sendFrame126("@cr22@Chaos Elemental", 62124);
			player.getPA().sendFrame126("@cr22@Kraken", 62125);
			player.getPA().sendFrame126("@cr22@Venenatis", 62126);
			player.getPA().sendFrame126("@cr22@Vetion", 62127);
			player.getPA().sendFrame126("@cr22@Callisto", 62128);
			player.getPA().sendFrame126("@cr22@Giant mole", 62129);
			player.getPA().sendFrame126("@cr22@Barrelchest", 62130);
			player.getPA().sendFrame126("Godwars Dungeon", 62131);
			player.getPA().sendFrame126("Dagannoth Cave", 62132);
			player.getPA().sendFrame126("Lizardman Canyon", 62133);
			player.getPA().sendFrame126("Abyssal Sire", 62134);
			player.getPA().sendFrame126("@cr10@@bla@Minigame Locations", 62135);
			player.getPA().sendFrame126("Pest Control", 62136);
			player.getPA().sendFrame126("Duel Arena", 62137);
			player.getPA().sendFrame126("Fight Caves", 62138);
			player.getPA().sendFrame126("Barrows", 62139);
			player.getPA().sendFrame126("Warriors Guild", 62140);
			player.getPA().sendFrame126("Mage Arena", 62141);
			player.getPA().sendFrame126("Lighthouse", 62142);
			player.getPA().sendFrame126("Recipe For Disaster", 62143);
			player.getPA().sendFrame126("@cr18@@bla@Skill Locations", 62144);
			player.getPA().sendFrame126("Skilling Area (Falador)", 62145);
			player.getPA().sendFrame126("Hunting Grounds (Feldip Hills)", 62146);
			player.getPA().sendFrame126("Woodcutting Guild (Zeah)", 62147);
			player.getPA().sendFrame126("@cr22@@bla@Player Killing Locations", 62148);
			player.getPA().sendFrame126("Wilderness Portals", 62149);
			player.getPA().sendFrame126("West Dragons", 62150);
			player.getPA().sendFrame126("East Dragons", 62151);
			player.getPA().sendFrame126("Hill Giants", 62152);
			player.getPA().sendFrame126("", 62153);
			player.getPA().sendFrame126("", 62154);
			player.getPA().sendFrame126("", 62155);
			player.getPA().showInterface(62100);
			break;
			
		case "pk":
			player.sendMessage("Killstreaks of players online:");
			PlayerHandler.nonNullStream().filter(Objects::nonNull).forEach(a -> {
				//if (a.getKillstreak().getTotalKillstreak() > 0) {
					player.sendMessage("" + a.getDisplayName() + " -> " + a.getKillstreak().getTotalKillstreak() +" streak.");
				//}
			});
			break;
			
		case "pkkills":
			player.sendMessage("Kills of players online:");
			PlayerHandler.nonNullStream().filter(Objects::nonNull).forEach(a -> {
					player.sendMessage("" + a.getDisplayName() + " -> " + a.killcount +" kills.");
			});
			break;
			
		case "ks":
			player.getKillstreak().increase(Killstreak.Type.HUNTER);
			player.getBH().upgradePlayerEmblem();
			player.getBH().setTotalHunterKills(player.getBH().getTotalHunterKills() + 1);
			break;
			
		case "check":
			player.getRechargeItems().checkCharges(4151);
			break;
			
		case "use":
			player.getRechargeItems().useItem(4151);
			break;
			
		case "b":
			for (int i = 31011; i < 31069; i++) {
				player.getPA().sendChangeSprite(i, (byte) 0);
				player.getPA().sendFrame126("S:" + (i - 1) + " T:"+ i +"", i);
			}
//			player.getPA().sendChangeSprite(31052, (byte) 1);
			//TeleportationInterface.open(player)
//			player.getPA().sendFrame246(1734, 150, 8007);
//			player.getPA().sendFrame246(1735, 150, 8008);
//			player.getPA().sendFrame246(1736, 150, 8009);
//			player.getPA().sendFrame246(1737, 150, 8010);
//			player.getPA().sendFrame246(1738, 150, 8011);
//			player.getPA().sendFrame246(15348, 150, 8012);
//			player.getPA().sendFrame126("What teleport would you like to create?", 1732);
//			player.getPA().showInterface(205);
			break;

		case "st":
			player.setSidebarInterface(2, 45000);
			break;
			
		case "scare":
			player.getPA().showInterface(18681);
			player.sendMessage("@red@Mwuhahahaha..");
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (player.isDisconnected()) {
						container.stop();
						return;
					}
					player.getPA().closeAllWindows();
					container.stop();
				}
				@Override
				public void onStopped() {
					
				}
			}, 5);
			break;
			
		
			
		case "win":
			player.getItems().addItem(rewards[Misc.random(rewards.length - 1)], 1);
			break;
		
		}
	}
}
