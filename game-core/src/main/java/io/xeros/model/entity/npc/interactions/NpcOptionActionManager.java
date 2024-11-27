package io.xeros.model.entity.npc.interactions;

import io.xeros.util.PackageLoader;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-17
 */
public class NpcOptionActionManager {

    public static final Logger logger = LoggerFactory.getLogger(NpcOptionActionManager.class);
    private final Map<Integer, NpcOptionAction> handlers = new HashMap<>();

    public NpcOptionActionManager() {
        try {
            loadHandlersFromDirectory();
        }
        catch (Exception e) {
            logger.error("Failed to load NpcOptionActionManager", e);
        }
    }

    private void loadHandlersFromDirectory() {
        try {
            for (Class<?> clazz : PackageLoader.load("io.xeros.model.entity.npc.interactions.impl", NpcOptionAction.class))
                registerHandler((NpcOptionAction) clazz.getDeclaredConstructor().newInstance());
            logger.info("Loaded: {} NpcOptionAction Handlers", handlers.size());
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        catch (Exception ignored) {}
    }
    private void registerHandler(@NonNull NpcOptionAction action) {
        for (var id : action.getIds()) handlers.put(id, action);
    }
    public Optional<NpcOptionAction> findHandlerById(int id) {
        return Optional.ofNullable(handlers.get(id));
    }
}
