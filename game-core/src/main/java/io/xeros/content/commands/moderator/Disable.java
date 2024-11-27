package io.xeros.content.commands.moderator;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Show the current position.
 * 
 * @author Emiel
 *
 */
public class Disable extends Command {

	private static class Disabling {
		private final String string;
		private final String description;
		private final BooleanSupplier enabled;
		private final Consumer<Player> consumer;

		public Disabling(String string, String description, BooleanSupplier enabled, Consumer<Player> consumer) {
			this.string = string;
			this.description = description;
			this.consumer = consumer;
			this.enabled = enabled;
		}
	}

	private static final Disabling[] DISABLES = {
			new Disabling("hc death loss", "Disable hardcore losing status on death", () -> Configuration.DISABLE_HC_LOSS_ON_DEATH,
					plr -> Configuration.DISABLE_HC_LOSS_ON_DEATH = !Configuration.DISABLE_HC_LOSS_ON_DEATH
			),
			new Disabling("discord", "Disable sending messages to discord channels", () -> Configuration.DISABLE_DISCORD_MESSAGING,
					plr -> Configuration.DISABLE_DISCORD_MESSAGING = !Configuration.DISABLE_DISCORD_MESSAGING
			),
			new Disabling("connection request limit", "Limit connection requests per second", () -> Configuration.DISABLE_CONNECTION_REQUEST_LIMIT,
					plr -> Configuration.DISABLE_CONNECTION_REQUEST_LIMIT = !Configuration.DISABLE_CONNECTION_REQUEST_LIMIT
			),
			new Disabling("registration", "New players logging in", () -> Configuration.DISABLE_REGISTRATION,
					plr -> Configuration.DISABLE_REGISTRATION = !Configuration.DISABLE_REGISTRATION
			),
			new Disabling("address verification", "Check address formats on login", () -> Configuration.DISABLE_ADDRESS_VERIFICATION,
					plr -> Configuration.DISABLE_ADDRESS_VERIFICATION = !Configuration.DISABLE_ADDRESS_VERIFICATION
			),

			new Disabling("captcha every login", "Captcha for every login", () -> Configuration.DISABLE_CAPTCHA_EVERY_LOGIN,
					plr -> Configuration.DISABLE_CAPTCHA_EVERY_LOGIN = !Configuration.DISABLE_CAPTCHA_EVERY_LOGIN
			),
			new Disabling("new account captcha", "Captcha for new players", () -> Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA,
					plr -> Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA = !Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA
			),
			new Disabling("change address captcha", "Captcha when players mac/uuid changes", () -> Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA,
					plr -> Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA = !Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA
			),


			new Disabling("foe", "Fire of exchange burning", () -> Configuration.DISABLE_FOE,
					plr -> Configuration.DISABLE_FOE = !Configuration.DISABLE_FOE
			),
			new Disabling("presets", "Loading presets", () -> Configuration.DISABLE_PRESETS,
					plr -> Configuration.DISABLE_PRESETS = !Configuration.DISABLE_PRESETS
			),
			new Disabling("shop sell", "Selling to shop", () -> Configuration.DISABLE_SHOP_SELL,
					plr -> Configuration.DISABLE_SHOP_SELL = !Configuration.DISABLE_SHOP_SELL
			),
			new Disabling("shop buy", "Buying from shop", () -> Configuration.DISABLE_SHOP_BUY,
					plr -> Configuration.DISABLE_SHOP_BUY = !Configuration.DISABLE_SHOP_BUY
			),
			new Disabling("flower poker", "Flower poker", () -> Configuration.DISABLE_FLOWER_POKER,
					plr -> Configuration.DISABLE_FLOWER_POKER = !Configuration.DISABLE_FLOWER_POKER
			),

			new Disabling("display names", "Display names", () -> Configuration.DISABLE_DISPLAY_NAMES,
					plr -> Configuration.DISABLE_DISPLAY_NAMES = !Configuration.DISABLE_DISPLAY_NAMES
			),
			new Disabling("login throttle", "Throttle login attempts when too many incorrect passwords", () -> Configuration.DISABLE_LOGIN_THROTTLE,
					plr -> Configuration.DISABLE_LOGIN_THROTTLE = !Configuration.DISABLE_LOGIN_THROTTLE
			),
			new Disabling("ccmessage", "Disable cc messages.", () -> Configuration.DISABLE_CC_MESSAGE,
					plr -> {
						Configuration.DISABLE_CC_MESSAGE = !Configuration.DISABLE_CC_MESSAGE;
					}
			),
			new Disabling("freshlogin", "Disable fresh logins.", () -> Configuration.DISABLE_FRESH_LOGIN,
					plr -> {
						Configuration.DISABLE_FRESH_LOGIN = !Configuration.DISABLE_FRESH_LOGIN;
					}
			),
			new Disabling("newmac", "Disable new macs.", () -> Configuration.DISABLE_NEW_MAC,
					plr -> {
						Configuration.DISABLE_NEW_MAC = !Configuration.DISABLE_NEW_MAC;
					}
			),

			new Disabling("packet log", "Disable all packets being logged.", () -> Configuration.DISABLE_PACKET_LOG,
					plr -> {
						Configuration.DISABLE_PACKET_LOG = !Configuration.DISABLE_PACKET_LOG;
					}
			),
	};

	@Override
	public void execute(Player player, String commandName, String input) {
		Optional<Disabling> disable = Arrays.stream(DISABLES).filter(it -> input.equalsIgnoreCase(it.string)).findFirst();
		disable.ifPresentOrElse(it -> {
			it.consumer.accept(player);
			player.sendMessage(it.string + " is now " + (it.enabled.getAsBoolean() ? "disabled" : "enabled") + ".");
		}, () -> list(player));
	}

	private void list(Player player) {
		List<String> list = new ArrayList<>();
		Arrays.stream(DISABLES).forEach(it -> {
			list.add(it.string + " (" + (it.enabled.getAsBoolean() ? "disabled" : "enabled") + ")");
			list.add(it.description);
			list.add("");
		});

		player.getPA().openQuestInterface("Disable Types", list);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Disable things, use ::disable for a list.");
	}
}
