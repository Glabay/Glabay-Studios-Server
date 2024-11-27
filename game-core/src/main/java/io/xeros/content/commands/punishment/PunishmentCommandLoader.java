package io.xeros.content.commands.punishment;

import com.google.common.base.Preconditions;
import io.xeros.annotate.Init;
import io.xeros.util.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class PunishmentCommandLoader {

    private static final Map<String, PunishmentCommandParser> COMMANDS = new HashMap<>();

    @Init
    public static void init() {
        load(PunishmentCommandParser.class);
    }

    private static void load(Class<?> theClazz) {
        Reflection.getSubClasses(theClazz).forEach(clazz -> {
            try {
                if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                    if (clazz != theClazz)
                        load(clazz);
                    return;
                }

                PunishmentCommandParser parser = (PunishmentCommandParser) clazz.getConstructors()[0].newInstance();
                Preconditions.checkState(parser.name() != null, "Command name is null " + parser.getClass());
                COMMANDS.put(parser.name(), parser);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                System.err.println(clazz);
                e.printStackTrace(System.err);
            }
        });
    }

    public static PunishmentCommandParser getByName(String name, boolean remove) {
        name = name.toLowerCase();
        if (remove)
            name = name.replaceFirst("un", "");
        return COMMANDS.get(name);
    }
}
