package io.xeros.model.world.objects.actions;

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
 * @since 2024-09-16
 */
public class WorldObjectActionManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldObjectActionManager.class);

    private final Map<Integer, WorldObjectAction> handlers = new HashMap<>();

    public WorldObjectActionManager() {
        try {
            loadHandlersFromDirectory();
        }
        catch (Exception e) {
            logger.error("Failed to load WorldObjectActionManager", e);
        }
    }

    private void loadHandlersFromDirectory() {
        try {
            for (Class<?> clazz : PackageLoader.load("io.xeros.model.world.objects.actions.handlers", WorldObjectAction.class))
                registerHandler((WorldObjectAction) clazz.getDeclaredConstructor().newInstance());
            logger.info("Loaded: {} WorldObjectAction Handlers", handlers.size());
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        catch (Exception ignored) {}
    }

    public void registerHandler(@NonNull WorldObjectAction action) {
        for (var id : action.getIds()) handlers.put(id, action);
    }

    public Optional<WorldObjectAction> findHandlerById(int id) {
        return Optional.ofNullable(handlers.get(id));
    }
}
