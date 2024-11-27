package io.xeros.content.commands.moderator;

import io.xeros.Configuration;
import io.xeros.content.OneYearQuiz;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Q extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		
		String[] args = input.split("-");
		
		switch (args[0]) {
		
		case "":
			player.sendMessage("@red@Usage: ::q start, end, show, check or set-question-answer");
			break;
		
		case "start":
			OneYearQuiz.configureEvent("start");
			player.sendMessage("@red@Quizmode started");
			PlayerHandler.executeGlobalMessage("[@red@Quiz@bla@] Quizmode started, get ready..");
			break;
			
		case "end":
			OneYearQuiz.configureEvent("end");
			player.sendMessage("@red@Quizmode ended");
			PlayerHandler.executeGlobalMessage("[@red@Quiz@bla@] Quizmode ended, make sure to try your luck on the next one!");
			break;
			
		case "check":
			player.sendMessage("Question: " + Configuration.QUESTION);
			player.sendMessage("Answer: " + Configuration.ANSWER);
			break;
			
		case "show":
			PlayerHandler.executeGlobalMessage("[@red@Quiz@bla@] " + Configuration.QUESTION);
			PlayerHandler.executeGlobalMessage("[@red@Quiz@bla@] Answer by using ::answer (your answer)");
			break;
			
		case "set":
			OneYearQuiz.configureEvent("start");
			OneYearQuiz.setQA(args[1], args[2]);
			player.sendMessage("Questions set: " + args[1]);
			player.sendMessage("Answer set: " + args[2]);
			break;
		}
	}
}
