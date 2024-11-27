package io.xeros.content.wogw;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import io.xeros.Server;
import io.xeros.content.QuestTab;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.sql.wogw.AddContributionSqlQuery;
import io.xeros.util.Misc;

public class Wogw {

	public static String getSaveFile() {
		return Server.getSaveDirectory() + "wogw.txt";
	}

	private static final int LEAST_ACCEPTED_AMOUNT = 1_000_000;
	public static final int EXPERIENCE_COINS_REQUIRED = 50_000_000;
	public static final int PC_COINS_REQUIRED = 50_000_000;
	public static final int DROP_RATE_COINS_REQUIRED = 200_000_000;

	public static long EXPERIENCE_TIMER, PC_POINTS_TIMER, _20_PERCENT_DROP_RATE_TIMER;
	public static long MONEY_TOWARDS_EXPERIENCE, MONEY_TOWARDS_PC_POINTS, MONEY_TOWARDS_DROP_RATE_BOOST;

	public static final String WOGW_MESSAGE_HEADER = "@cr10@[@blu@WOGW@bla@]@blu@";

	public static void init() {
        try {
            File f = new File(getSaveFile());
            if (!f.exists()) {
				Preconditions.checkState(f.createNewFile());
			}
            Scanner sc = new Scanner(f);
            int i = 0;
            while(sc.hasNextLine()){
            	i++;
                String line = sc.nextLine();
                String[] details = line.split("="); //log onto game make sure its ok
                String amount = details[1];
                
                switch (i) {
                case 1:
                	MONEY_TOWARDS_EXPERIENCE = Long.parseLong(amount);
                	break;
                case 2:
                	EXPERIENCE_TIMER = Long.parseLong(amount);
                	break;
                case 3:
                	MONEY_TOWARDS_PC_POINTS = Long.parseLong(amount);
                	break;
                case 4:
                	PC_POINTS_TIMER = Long.parseLong(amount);
                	break;
                case 5:
                	MONEY_TOWARDS_DROP_RATE_BOOST = Long.parseLong(amount);
                	break;
                case 6:
                	_20_PERCENT_DROP_RATE_TIMER = Long.parseLong(amount);
                	break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

		checkForFillEvent();
	}
	
	public static void save() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(getSaveFile()));
			out.write("experience-amount=" + MONEY_TOWARDS_EXPERIENCE);
			out.newLine();
			out.write("experience-timer=" + EXPERIENCE_TIMER);
			out.newLine();
			out.write("pc-amount=" + MONEY_TOWARDS_PC_POINTS);
			out.newLine();
			out.write("pc-timer=" + PC_POINTS_TIMER);
			out.newLine();
			out.write("drops-amount=" + MONEY_TOWARDS_DROP_RATE_BOOST);
			out.newLine();
			out.write("drops-timer=" + _20_PERCENT_DROP_RATE_TIMER);
			out.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	private static void checkForFillEvent() {
		CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (MONEY_TOWARDS_PC_POINTS >= PC_COINS_REQUIRED && PC_POINTS_TIMER == 0) {
					MONEY_TOWARDS_PC_POINTS = MONEY_TOWARDS_PC_POINTS - PC_COINS_REQUIRED;
					sendActivateMessage("+5 bonus PC points");
					PC_POINTS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
					QuestTab.updateAllQuestTabs();
				}

				if (MONEY_TOWARDS_DROP_RATE_BOOST >= DROP_RATE_COINS_REQUIRED && _20_PERCENT_DROP_RATE_TIMER == 0) {
					MONEY_TOWARDS_DROP_RATE_BOOST = MONEY_TOWARDS_DROP_RATE_BOOST - DROP_RATE_COINS_REQUIRED;
					sendActivateMessage("+20% drop rate");
					_20_PERCENT_DROP_RATE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
					QuestTab.updateAllQuestTabs();
				}

				if (MONEY_TOWARDS_EXPERIENCE >= EXPERIENCE_COINS_REQUIRED && EXPERIENCE_TIMER == 0) {
					MONEY_TOWARDS_EXPERIENCE = MONEY_TOWARDS_EXPERIENCE - EXPERIENCE_COINS_REQUIRED;
					sendActivateMessage("bonus experience");
					EXPERIENCE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
				}


			}
		}, 1);
	}

	public static void sendActivateMessage(String action) {
		PlayerHandler.executeGlobalMessage(WOGW_MESSAGE_HEADER + "The Well of Goodwill is granting " + action + " for the next 60 minutes.");
	}
	public static void votingBonus() {
		PlayerHandler.executeGlobalMessage("@cr10@[@pur@VOTE@bla@] Global votes have reached @pur@50@bla@! 30 minutes of +20% drop rate activated!");
		_20_PERCENT_DROP_RATE_TIMER += TimeUnit.HOURS.toMillis(1) / 1200;
		QuestTab.updateAllQuestTabs();
	}

	public static void donateItem(Player player, int itemId) {
		player.start(new DialogueBuilder(player)
				.itemStatement(itemId, "Please select your donation choice for @blu@" + Misc.formatCoins(getFoeRate(itemId)) + ".")
				.option(
						new DialogueOption("Experience (x1.5)", plr ->{
							donate(player, getFoeRate(itemId), itemId, 0);
							plr.getPA().closeAllWindows();
						} ),
						new DialogueOption("Pest Control (+5)", plr -> {
							donate(player, getFoeRate(itemId), itemId, 1);
							plr.getPA().closeAllWindows();

						}),
						new DialogueOption("+20% Drop Rate", plr ->{
							donate(player, getFoeRate(itemId), itemId, 2);
							plr.getPA().closeAllWindows();
						})
				)
		);

	}

	public static int getFoeRate(int itemId) {
		switch (itemId) {
			case 691://10k foe cert
				return 20_000_000;
			case 692://25k foe cert
				return 50_000_000;
			case 693://50k foe cert
				return 100_000_000;
			case 696://250k foe cert
				return 500_000_000;
		}
		return 0;
	}

	public static void donate(Player player, int amount, int itemId, int choice) {
		if (amount < LEAST_ACCEPTED_AMOUNT) {
			player.sendMessage("You must donate at least one million coins.");
			return;
		}
		if (itemId == -1 && !player.getItems().playerHasItem(995, amount)) {
			player.sendMessage("@cr10@You do not have " + Misc.getValueWithoutRepresentation(amount) + ".");
			return;
		}
		if (itemId != -1 && !player.getItems().playerHasItem(itemId, 1)) {
			player.sendMessage("@cr10@You do not have the correct item to burn.");
			return;
		}
		if (amount >= 10_000_000) {
			player.getEventCalendar().progress(EventChallenge.CONTRIBUTE_10M_TO_THE_WOGW, 1);

		}
		if (itemId == -1) {
			player.getItems().deleteItem(995, amount);
		} else {
			player.getItems().deleteItem(itemId, 1);
		}
		player.getPA().sendInterfaceHidden(1, 38020);
		
		/**
		 * Updating latest donators
		 */
		String towards = player.getWogwContributeInterface().getSelectedButton() == WogwInterfaceButton.PEST_CONTROL_BOOST ? "+5 bonus PC Points!"
						: player.getWogwContributeInterface().getSelectedButton() == WogwInterfaceButton.EXPERIENCE_BOOST ? "bonus experience!"
						: player.getWogwContributeInterface().getSelectedButton() == WogwInterfaceButton.DROP_RATE_BOOST ? "+20% drop rate!" : "";
		player.sendMessage(WOGW_MESSAGE_HEADER + "You have donated " + Misc.formatCoins(amount) + " to the well of goodwill towards " + towards);
		Achievements.increase(player, AchievementType.WOGW, amount);


		/**
		 * Announcing donations over 10m
		 */
		if (amount >= 10_000_000) {
			PlayerHandler.executeGlobalMessage(WOGW_MESSAGE_HEADER + player.getDisplayNameFormatted() + "@bla@ donated @blu@" + Misc.getValueWithoutRepresentation(amount) + "@bla@ to the Well of Goodwill!");
		}

		/**
		 * Setting the amounts and enabling bonus if the amount reaches above required.
		 */
		if (itemId == -1) {
			switch (player.getWogwContributeInterface().getSelectedButton()) {
				case EXPERIENCE_BOOST:
					MONEY_TOWARDS_EXPERIENCE += amount;
					break;

				case PEST_CONTROL_BOOST:
					MONEY_TOWARDS_PC_POINTS += amount;
					break;

				case DROP_RATE_BOOST:
					MONEY_TOWARDS_DROP_RATE_BOOST += amount;
					break;
			}
		} else {
			switch (choice) {
				case 0:
					MONEY_TOWARDS_EXPERIENCE += amount;
					break;
				case 1:
					MONEY_TOWARDS_PC_POINTS += amount;
					break;
				case 2:
					MONEY_TOWARDS_DROP_RATE_BOOST += amount;
					break;
			}
		}

		if (Server.isTest() || !player.getRights().contains(Right.OWNER)) {
			Server.getDatabaseManager().exec((context, connection) -> {
				new AddContributionSqlQuery(player, amount).execute(context, connection);
				player.addQueuedAction(plr -> player.getWogwContributeInterface().open());
				return null;
			});
		} else {
			player.getWogwContributeInterface().open();
		}

		player.getQuestTab().updateInformationTab();
	}
}
