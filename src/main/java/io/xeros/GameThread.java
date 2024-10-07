package io.xeros;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.xeros.content.instances.InstanceHeight;
import io.xeros.content.minigames.pk_arena.Highpkarena;
import io.xeros.content.minigames.pk_arena.Lowpkarena;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.net.ChannelHandler;
import io.xeros.net.login.RS2LoginProtocol;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameThread extends Thread {

    public static final String THREAD_NAME = "GameThread";
    public static final int PRIORITY = 9;
    private static final int MAX_TICK_TIME = 600;
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);
    private final List<Consumer<GameThread>> tickables = new ArrayList<>();
    private final Runnable startup;
    private long totalCycleTime = 0;
    public GameThread(Runnable startup) {
        setName(THREAD_NAME);
        setPriority(PRIORITY);
        this.startup = startup;
        setTickables();
    }

    private void setTickables() {
        tickables.add(i -> Server.npcHandler.process());
        tickables.add(i -> Server.playerHandler.process());
        tickables.add(i -> Server.shopHandler.process());
        tickables.add(i -> Highpkarena.process());
        tickables.add(i -> Lowpkarena.process());
        tickables.add(i -> Server.getGlobalObjects().pulse());
        tickables.add(i -> CycleEventHandler.getSingleton().process());
        tickables.add(i -> Server.getEventHandler().process());
        tickables.add(i -> Server.tickCount++);
    }

    private void tick() {
        for (Consumer<GameThread> tickable : tickables) {
            try {
                tickable.accept(this);
            } catch (Exception e) {
                logger.error("Error caught in GameThread, should be caught up the chain and handled.", e);
                e.printStackTrace(System.err);
            }
        }

        // Report
        if (Server.getTickCount() % 50 == 0) {
            var joiner = new StringJoiner(", ");
                joiner.add("runtime=" + Misc.cyclesToTime(Server.getTickCount()));
                joiner.add("connections=" + ChannelHandler.getActiveConnections());
                joiner.add("players=" + PlayerHandler.getPlayers().size());
                joiner.add("uniques=" + PlayerHandler.getUniquePlayerCount());
                joiner.add("npcs=" + (int) NPCHandler.nonNullStream().count());
                joiner.add("reserved-heights=" + InstanceHeight.getReservedCount());
                joiner.add("average-cycle-time=" + (totalCycleTime / Server.getTickCount()) + "ms");
                joiner.add("handshakes-per-tick=" + (RS2LoginProtocol.getHandshakeRequests() / Server.getTickCount()));

            long totalMemory = Runtime.getRuntime().totalMemory();
            long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
            joiner.add("memory=" + Misc.formatMemory(usedMemory) + "/" + Misc.formatMemory(totalMemory));

            logger.info("Status [{}]", joiner);
        }
    }

    @Override
    public void run() {
        startup.run();
        while (!Thread.interrupted()) {
            long startTime = System.currentTimeMillis();
            tick();
            long elapsedTime = System.currentTimeMillis() - startTime;
            totalCycleTime += elapsedTime;
            sleepThreadIfNeeded(elapsedTime);
        }
    }
    private void sleepThreadIfNeeded(long elapsedTime) {
        if (elapsedTime < MAX_TICK_TIME) {
            try {
                Thread.sleep(MAX_TICK_TIME - elapsedTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
        else
            logger.error("Game thread took {}ms to process!", Misc.insertCommas(String.valueOf(elapsedTime)));
    }
}
