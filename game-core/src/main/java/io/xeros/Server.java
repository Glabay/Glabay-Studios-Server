package io.xeros;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.glabay.rsps.plugin.world.loc.WorldObjectActionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.xeros.model.AttributesSerializable;
import io.xeros.model.cycleevent.EventHandler;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.npc.interactions.NpcOptionActionManager;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.interactions.WorldItemActionManager;
import io.xeros.model.multiplayersession.MultiplayerSessionListener;
import io.xeros.model.world.ClanManager;
import io.xeros.model.world.ItemHandler;
import io.xeros.model.world.ShopHandler;
import io.xeros.model.world.event.CyclicEventManager;
import io.xeros.model.world.objects.GlobalObjects;
import io.xeros.net.PipelineFactory;
import io.xeros.punishments.Punishments;
import io.xeros.sql.DatabaseCredentials;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.EmbeddedDatabase;
import io.xeros.util.*;
import io.xeros.util.dateandtime.GameCalendar;
import io.xeros.util.logging.GameLogging;
import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.glabay.Discord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main class needed to start the server.
 *
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30 Revised by Shawn Notes by Shawn
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static final Server singleton = new Server();
    @Getter
    static long tickCount = 0;
    private static final Punishments PUNISHMENTS = new Punishments();
    @Getter
    private static final DropManager dropManager = new DropManager();
    @Getter
    private static ServerAttributes serverAttributes;
    /**
     * A class that will manage game events
     */
    private static final EventHandler events = new EventHandler();
    /**
     * Represents our calendar with a given delay using the TimeUnit class
     */
    @Getter
    private static final GameCalendar calendar = new GameCalendar(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
    @Getter
    private static final MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();
    @Getter
    private static final GlobalObjects globalObjects = new GlobalObjects();
    @Getter
    private final CyclicEventManager cyclicEventManager = new CyclicEventManager();
    @Getter
    private static final GameLogging logging = new GameLogging();
    @Getter
    private static final WorldObjectActionManager objectActionManager = new WorldObjectActionManager();
    @Getter
    private static final NpcOptionActionManager npcOptionActionManager = new NpcOptionActionManager();
    @Getter
    private static final WorldItemActionManager itemOptionActionManager = new WorldItemActionManager();
    /**
     * ClanChat Added by Valiant
     */
    public static ClanManager clanManager = new ClanManager();
    /**
     * Server updating.
     */
    public static boolean UpdateServer;
    /**
     * Calls the usage of player items.
     */
    public static ItemHandler itemHandler = new ItemHandler();
    /**
     * Handles logged in players.
     */
    public static PlayerHandler playerHandler = new PlayerHandler();
    /**
     * Handles global NPCs.
     */
    public static NPCHandler npcHandler = new NPCHandler();
    /**
     * Handles global shops.
     */
    public static ShopHandler shopHandler = new ShopHandler();
    /**
     * The server configuration.
     */
    @Getter
    private static ServerConfiguration configuration;
    /**
     * The database manager.
     */
    @Getter
    private static DatabaseManager databaseManager;
    @Getter
    private static EmbeddedDatabase embeddedDatabase;
    @Getter
    private static final ScheduledExecutorService ioExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("io-worker-%d").build());
    private static boolean loaded = false;

    private static final String LOGGER_FIELD_NAME = "logger";
    private static final String ILLEGAL_ACCESS_LOGGER = "jdk.internal.module.IllegalAccessLogger";
    private static void enableExceptionLogging() throws IOException {
        if (!new File(Configuration.ERROR_LOG_DIRECTORY).exists()) {
            Preconditions.checkState(new File(Configuration.ERROR_LOG_DIRECTORY).mkdirs());
        }
        if (!new File(Configuration.CONSOLE_LOG_DIRECTORY).exists()) {
            Preconditions.checkState(new File(Configuration.CONSOLE_LOG_DIRECTORY).mkdirs());
        }
        TeeOutputStream outputStream = new TeeOutputStream(System.err, new FileOutputStream(Configuration.ERROR_LOG_DIRECTORY + Configuration.ERROR_LOG_FILE, true));
        System.setErr(new TimeStampedPrintStream(outputStream));
        outputStream = new TeeOutputStream(System.out, new FileOutputStream(Configuration.CONSOLE_LOG_DIRECTORY + Configuration.CONSOLE_FILE, true));
        System.setOut(new TimeStampedPrintStream(outputStream));
    }

    public static void loadData() throws Exception {
        Preconditions.checkState(!loaded, "Already loaded data once.");
        logger.info("Server state: {}", configuration.getServerState());
        loadAttributes();
        databaseManager = new DatabaseManager(getConfiguration().getServerState().isSqlEnabled());
        embeddedDatabase = new EmbeddedDatabase(getConfiguration().getServerState().name().toLowerCase() + "_main", null, getConfiguration().getEmbeddedPassword());

        if (configuration.isLocalDatabaseEnabled()) {
            DatabaseCredentials localDatabase = configuration.getLocalDatabase();
            Flyway.configure()
                    .dataSource(localDatabase.getUrl(), localDatabase.getUsername(), localDatabase.getPassword())
                    .load()
                    .migrate();
        }

        ServerStartup.load();
        loaded = true;
    }

    public static void startServerless() throws Exception {
        startServerless(loadConfiguration());
    }

    /**
     * Start the server in 'serverless' mode which means it won't start the server,
     * only load data and give access to things that requires the server to start.
     */
    public static void startServerless(ServerConfiguration configuration) throws Exception {
        setConfiguration(configuration);
        loadData();
    }

    public static void main(String... args) {
        disableWarning();
        AssetLoader.initCache();
        new GameThread(() -> {
            try {
                System.out.println("[" + Calendar.getInstance().getTime() + "]: Launching " + Configuration.SERVER_NAME + ".");
                enableExceptionLogging();
                long startTime = System.nanoTime();
                System.setOut(new OutstreamStyle(System.out));
                if (configuration == null) {
                    setConfiguration(loadConfiguration());
                }
                loadData();
                Discord.getDiscordBot().init();

                if (isDebug()) {
                    Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA = true;
                    Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA = true;
                    Configuration.DISABLE_CAPTCHA_EVERY_LOGIN = true;
                }

                bindPorts();
                long endTime = System.nanoTime();
                long elapsed = endTime - startTime;
                System.out.println(Configuration.SERVER_NAME + " has successfully started up in " + TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS)+ " seconds.");
            } catch (Exception e) {
                logger.error("An error occurred while starting the server.", e);
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }).start();
    }

    public static void disableWarning() {
        try {
            Class<?> loggerClass = Class.forName(ILLEGAL_ACCESS_LOGGER);
            var loggerField = loggerClass.getDeclaredField(LOGGER_FIELD_NAME);
                loggerField.setAccessible(true);

            var varHandle = MethodHandles.privateLookupIn(loggerClass, MethodHandles.lookup()).unreflectVarHandle(loggerField);
                varHandle.set(null, null);
        }
        catch (Exception ignored) {}
    }

    public static boolean isPublic() {
        return configuration.getServerState() == ServerState.PUBLIC
                || configuration.getServerState() == ServerState.TEST_PUBLIC;
    }

    public static boolean isDebug() {
        return configuration.getServerState() == ServerState.DEBUG
                || configuration.getServerState() == ServerState.DEBUG_SQL;
    }

    public static boolean isTest() {
        return isDebug() || configuration.getServerState() == ServerState.TEST;
    }

    private static void loadAttributes() throws IOException {
        serverAttributes = new ServerAttributes();
        serverAttributes = AttributesSerializable.getFromFile(ServerAttributes.getSaveFile(), serverAttributes);
    }

    public static ServerConfiguration loadConfiguration() throws IOException {
        File configurationFile = new File(ServerConfiguration.CONFIGURATION_FILE);
        ServerConfiguration configuration;
        if (!configurationFile.exists()) {
            configuration = ServerConfiguration.getDefault();
            JsonUtil.toYaml(configuration, configurationFile.getPath());
            logger.warn("No configuration present, wrote default configuration file to {}", configurationFile.getAbsolutePath());
        } else {
            configuration = JsonUtil.fromYaml(configurationFile, ServerConfiguration.class);
        }
        return configuration;
    }

    /**
     * Get the directory where save files will be saved for the current {@link ServerState}.
     * Will not include files from other {@link ServerState}s.
     */
    public static String getSaveDirectory() {
        return Configuration.SAVE_DIRECTORY + "/" + configuration.getServerState().toString().toLowerCase() + "/";
    }

    public static String getGameLogDirectory() {
        return Configuration.SAVE_DIRECTORY + "/game_logs/" + configuration.getServerState().toString().toLowerCase() + "/";
    }

    public static String getBackupDirectory() {
        return Configuration.SAVE_DIRECTORY + "/backups/" + configuration.getServerState().toString().toLowerCase() + "/";
    }

    public static String getDataDirectory() {
        return "./" + Configuration.DATA_FOLDER;
    }

    public static final RandomGen random = new RandomGen();

    /**
     * Java connection. Ports.
     */
    private static void bindPorts() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setPriority(GameThread.PRIORITY - 1).build());
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setPriority(GameThread.PRIORITY - 1).build());

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new PipelineFactory());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.bind(new InetSocketAddress(configuration.getServerState().getPort()));
    }

    public static EventHandler getEventHandler() {
        return events;
    }

    public static Punishments getPunishments() {
        return PUNISHMENTS;
    }

    public static void setConfiguration(ServerConfiguration configuration) {
        Preconditions.checkState(Server.configuration == null, "Server configuration is already set.");
        Server.configuration = configuration;
    }

    public static Server getWorld() {
        return singleton;
    }

}
