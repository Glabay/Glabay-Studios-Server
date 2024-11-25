package xyz.glabaystudios;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.glabaystudios.listener.SlashCommandListener;
import xyz.glabaystudios.util.ShutdownHook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
public class Discord {
    private static final Logger logger = LoggerFactory.getLogger(Discord.class);

    protected static JDA api;
    protected static JDABuilder jdaBuilder;
    private static Discord discordBot;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        Discord.getDiscordBot().init();
    }

    public static Discord getDiscordBot() {
        if (discordBot == null) discordBot = new Discord();
        return discordBot;
    }

    public JDA getApi() {
        return api;
    }

    public static void shutdown() {
        logger.info("Shutting down...");
        api.shutdown();
    }

    protected JDABuilder getJdaBuilder() {
        if (Objects.isNull(jdaBuilder))
            jdaBuilder = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"), getGatewayIntents())
                // Disable parts of the cache for Memory
                .disableCache(getDisabledCacheFlags())
                // Activity
                .setActivity(Activity.customStatus("Developing things"));
        return jdaBuilder;
    }

    public void init() {
        logger.info("Starting Discord Bot...");
        api = getJdaBuilder().build();
        logger.info("Updating commands...");
        api.updateCommands().addCommands(getCommandList()).queue();
        logger.info("Registering Event Listeners...");
        api.addEventListener(
            new SlashCommandListener()
        );
        logger.info("Successfully initialized Discord Bot");
    }

    /**
     * Creates and returns a list of command data for various available commands.
     *
     * @return List of CommandData objects each representing a command with its associated options.
     */
    private List<CommandData> getCommandList() {
        var commands = new ArrayList<CommandData>();

        return commands;
    }

    /**
     * Retrieves the necessary gateway intents for the bot.
     *
     * @return a list of GatewayIntent objects representing the required intents for the bot's functionality.
     */
    protected List<GatewayIntent> getGatewayIntents() {
        return Stream.of(
                GUILD_MEMBERS,
                GUILD_MESSAGES,
                MESSAGE_CONTENT,
                GUILD_EMOJIS_AND_STICKERS,
                DIRECT_MESSAGES)
            .toList();
    }

    /**
     * Retrieves a list of CacheFlag objects that represent the disabled cache flags for the bot.
     *
     * @return a list of CacheFlag objects indicating which caching features are disabled.
     */
    protected List<CacheFlag> getDisabledCacheFlags() {
        return Stream.of(
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.VOICE_STATE,
                CacheFlag.ONLINE_STATUS)
            .toList();
    }
}