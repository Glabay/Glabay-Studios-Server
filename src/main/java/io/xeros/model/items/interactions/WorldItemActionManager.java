package io.xeros.model.items.interactions;

import io.xeros.model.world.objects.actions.WorldObjectAction;
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
 * @since 2024-10-04
 */
public class WorldItemActionManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldItemActionManager.class);

    private final Map<Integer, WorldItemAction> handlers = new HashMap<>();

    public WorldItemActionManager() {
        loadHandlersFromDirectory();
    }

    private void loadHandlersFromDirectory() {
        try {
            for (Class<?> clazz : PackageLoader.load("io.xeros.model.items.interactions.impl", WorldItemAction.class))
                registerHandler((WorldItemAction) clazz.getDeclaredConstructor().newInstance());
            logger.info("Loaded: {} WorldObjectAction Handlers", handlers.size());
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        catch (Exception ignored) {}
    }

    public void registerHandler(@NonNull WorldItemAction action) {
        for (var id : action.getIds()) handlers.put(id, action);
    }

    public Optional<WorldItemAction> findHandlerById(int id) {
        return Optional.ofNullable(handlers.get(id));
    }
}
