package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import java.lang.Object;

public class Script extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split("-");
		String[] scriptArgs = new String[args.length - 1];
		System.arraycopy(args, 1, scriptArgs, 0, args.length - 1);
		int scriptId = Integer.parseInt(args[0]);

		Object[] params = new Object[scriptArgs.length];
		for (int i = 0; i < scriptArgs.length; i++) {
			if (StringUtils.isNumeric(scriptArgs[i])) {
				params[i] = Integer.parseInt(scriptArgs[i]);
			} else {
				params[i] = scriptArgs[i];
			}
		}

		c.getPA().runClientScript(scriptId, params);
	}
}
