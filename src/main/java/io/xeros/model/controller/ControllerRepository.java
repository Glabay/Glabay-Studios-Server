package io.xeros.model.controller;

import com.google.common.base.Preconditions;
import io.xeros.annotate.Init;
import io.xeros.model.entity.player.Player;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerRepository {

    public static final String DEFAULT_CONTROLLER_KEY = "standard";
    private static final Logger logger = LoggerFactory.getLogger(ControllerRepository.class);
    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final List<Controller> boundaryControllers = new ArrayList<>();

    @Init
    public static void init() {
        Reflections reflections = new Reflections("io.xeros", new SubTypesScanner());

        List<Controller> controllerList = reflections.getSubTypesOf(Controller.class).stream()
                .filter(it -> !it.isInterface() && !Modifier.isAbstract(it.getModifiers())).map(it -> {
            try {
                return (Controller) it.getConstructors()[0].newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace(System.err);
                return null;
            }
        }).toList();

        controllerList.forEach(controller -> {
            Controller current = controllers.get(controller.getKey());
            Preconditions.checkState(current == null, String.format("Controller key is already taken: %s, %s, %s", controller.getKey(),
                    controller, current));
            controllers.put(controller.getKey(), controller);
            if (controller.getBoundaries() != null)
                boundaryControllers.add(controller);
        });

        controllerList.forEach(a -> {
            if (a.getBoundaries() == null)
                return;
            controllerList.forEach(b -> {
                if (a == b || b.getBoundaries() == null)
                    return;
                if (a.getBoundaries().stream().anyMatch(ab -> b.getBoundaries().stream().anyMatch(ab::intersects))) {
                    throw new IllegalStateException(String.format("Multiple controller boundaries are intersecting, a=%s, b=%s", a, b));
                }
            });
        });

        logger.info("Loaded {} controllers.", controllers.size());
    }

    /**
     * Get controller for the player.
     * <p>
     * Checks all controller boundaries to see if the player is inside the boundaries.
     * If player isn't inside any controller boundaries it will return the default controller.
     */
    public static Controller get(Player player, Controller fail) {
        Optional<Controller> boundary = boundaryControllers.stream().filter(controller -> controller.inBoundary(player)).findFirst();
        return boundary.orElse(fail);
    }

    public static Controller getOrDefault(Player player) {
        return get(player, getDefault());
    }

    public static Controller get(String key) {
        Controller controller = controllers.get(key);
        if (controller == null) {
            logger.error("Controller doesn't exist for key[{}], returning default controller", key);
            return getDefault();
        }
        return controller;
    }

    public static Controller getDefault() {
        return Objects.requireNonNull(controllers.get(DEFAULT_CONTROLLER_KEY));
    }

}
