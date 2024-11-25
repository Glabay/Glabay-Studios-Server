package xyz.glabaystudios.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
public interface SlashCommand {
    void handleSlashCommand(SlashCommandInteractionEvent event);
}
