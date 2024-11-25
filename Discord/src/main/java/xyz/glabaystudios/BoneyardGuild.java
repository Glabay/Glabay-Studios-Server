package xyz.glabaystudios;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
public class BoneyardGuild {

    private static Guild boneyardGuild;
    public static Guild getBoneyardGuild() {
        if (Objects.isNull(boneyardGuild))
            boneyardGuild = Discord.getDiscordBot()
                .getApi()
                .getGuildById(Long.parseLong(System.getenv("DISCORD_GUILD_ID")));
        return boneyardGuild;
    }

    /* MESSAGE A CHANNEL */

    public void sendChannelMessageEmbed(TextChannel channel, MessageEmbed embed) {
        channel.sendMessageEmbeds(embed).queue();
    }

    /* ALERTS & NOTIFICATIONS */

    public void sendBroadcast(String message, MessageEmbed embed) {
        Objects.requireNonNull(getBoneyardGuild()
                .getTextChannelById(Long.parseLong(System.getenv("DISCORD_BROADCAST_CHANNEL_ID"))))
            .sendMessageEmbeds(embed)
            .queue();
    }

    public void sendStaffAlert(String message, MessageEmbed embed) {
        Objects.requireNonNull(getBoneyardGuild()
                .getTextChannelById(Long.parseLong(System.getenv("DISCORD_ALERT_CHANNEL_ID"))))
            .sendMessageEmbeds(embed)
            .queue();
    }

    public void sendDebugAlert(String message, MessageEmbed embed) {
        Objects.requireNonNull(getBoneyardGuild()
                .getTextChannelById(Long.parseLong(System.getenv("DISCORD_ALERT_DEBUG_CHANNEL_ID"))))
            .sendMessageEmbeds(embed)
            .queue();
    }

    public void sendErrorAlert(String message, MessageEmbed embed) {
        Objects.requireNonNull(getBoneyardGuild()
                .getTextChannelById(Long.parseLong(System.getenv("DISCORD_ALERT_ERROR_CHANNEL_ID"))))
            .sendMessageEmbeds(embed)
            .queue();
    }


    /* DIRECT MESSAGING A USER */

    /**
     * Sends a direct message to a specific user.
     *
     * @param user The user to whom the message should be sent.
     * @param message The plain text message to be sent.
     */
    public void sendUserDirectMessage(User user, String message) {
        sendUserMessage(user, message, null);
    }

    /**
     * Sends a message or an embedded message to a specific user via a private channel.
     *
     * @param user The user to whom the message should be sent.
     * @param message The plain text message to be sent. If empty, the embed will be used.
     * @param embed The embedded message to be sent. If null, the plain text message will be used.
     */
    public void sendUserMessage(User user, String message, MessageEmbed embed) {
        if (message.isBlank() && Objects.nonNull(embed))
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(embed).queue());
        if (!message.isBlank() && Objects.isNull(embed))
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
        if (!message.isBlank() && Objects.nonNull(embed))
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).setEmbeds(embed).queue());
    }
}
