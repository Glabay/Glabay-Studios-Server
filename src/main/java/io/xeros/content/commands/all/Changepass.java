package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.compromised.CompromisedPlayerSave;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Changepass extends Command {

	private static final String PASS_ATTR = "entered_new_password";

	public static void sendChangePasswordDialogue(Player player) {
		new DialogueBuilder(player).setNpcId(Npcs.XEROS_WIZARD)
				.npc("Hello, %s.".formatted(player.getDisplayName()),
					"I will help you change your password.",
					"Enter your password now.")
				.exit(plr -> plr.getPA().sendEnterString("Enter your new password", Changepass::enteredPassword))
				.send();
	}

	private static void enteredPassword(Player player, String newPassword) {
		if (newPassword == null || newPassword.length() < 5) {
			error(player, "Your password is too short, use at least 5 characters.");
			return;
		}

		if (newPassword.length() > 15) {
			error(player, "Your password is too long, use at most 15 characters.");
			return;
		}

		if (!newPassword.matches(Misc.KEYBOARD_CHARACTERS_LOWERCASE_REGEX)) {
			error(player, "Your password has invalid characters, try again.");
			return;
		}

		player.getAttributes().setString(PASS_ATTR, newPassword);

		new DialogueBuilder(player).setNpcId(Npcs.XEROS_WIZARD)
				.npc("Great, now enter your password again.")
				.exit(plr -> plr.getPA().sendEnterString("Enter your password again", Changepass::enteredPasswordAgain))
				.send();
	}

	private static void enteredPasswordAgain(Player player, String newPasswordReentry) {
		String password = player.getAttributes().getString(PASS_ATTR);
		if (password == null || newPasswordReentry == null)
			return;
		if (!password.equals(newPasswordReentry)) {
			error(player, "Those passwords didn't match.", "Try again.");
			return;
		}

		player.getAttributes().removeString(PASS_ATTR);
		player.playerPass = newPasswordReentry;
		CompromisedPlayerSave.setCompromised(player, null);
		new DialogueBuilder(player).setNpcId(Npcs.XEROS_WIZARD)
				.npc("Great, your password is now:", newPasswordReentry, "@red@Make sure your write it down!")
				.send();
	}

	private static void error(Player player, String...message) {
		new DialogueBuilder(player).setNpcId(Npcs.XEROS_WIZARD)
				.npc(message)
				.exit(Changepass::sendChangePasswordDialogue)
				.send();
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		sendChangePasswordDialogue(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Change your password");
	}

	@Override
	public Optional<String> getParameter() {
		return Optional.of("password");
	}

}
