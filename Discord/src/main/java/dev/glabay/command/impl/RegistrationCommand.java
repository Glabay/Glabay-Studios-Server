package dev.glabay.command.impl;

import dev.glabay.command.SlashCommand;
import dev.glabay.rest.DiscordClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
public class RegistrationCommand implements SlashCommand {
    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        // make a GET to the database to see if the user exists already
        var discordUserDto = DiscordClient.getInstance()
            .getDiscordUserByDiscordId(event.getUser().getIdLong());

        if (discordUserDto != null) {
            event.reply("You are already registered").queue();
            return;
        }
        // create a Form Modal to request the username and password the user would like to use
        var username = TextInput.create("Username", "What shall we call you, Adventurer?", TextInputStyle.SHORT)
            .setRequiredRange(3, 12)
            .setPlaceholder("Username here.")
            .build();
        var email = TextInput.create("Email", "Can you provide your email?", TextInputStyle.SHORT)
            .setPlaceholder("email here.")
            .build();
        var password = TextInput.create("Password", "Enter a password for your profile.", TextInputStyle.SHORT)
            .setRequiredRange(1, 18)
            .setPlaceholder("password here.")
            .build();

        var modal = Modal.create("Boneyard_Registration", "Register your profile.")
            .addActionRow(username)
            .addActionRow(email)
            .addActionRow(password)
            .build();

        event.replyModal(modal).queue();
    }
}
