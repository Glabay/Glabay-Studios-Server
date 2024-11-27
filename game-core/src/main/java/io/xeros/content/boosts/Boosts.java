package io.xeros.content.boosts;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.Lists;
import io.xeros.content.boosts.other.GenericBoost;
import io.xeros.content.boosts.xp.ExperienceBooster;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class Boosts {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Boosts.class.getName());
    private static List<Booster<?>> boosts;

    public static void init() {
        boosts = Lists.newArrayList();
        Reflections reflections = new Reflections("io.xeros.content.boosts", new SubTypesScanner());
        Set<Class<? extends Booster>> boosterClasses = reflections.getSubTypesOf(Booster.class);
        boosterClasses.forEach(clazz -> {
            try {
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) return;
                Booster<?> instance = (Booster<?>) clazz.newInstance();
                boosts.add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace(System.err);
            }
        });
        log.info("Loaded " + boosterClasses.size() + " boosts.");
    }

    public static List<? extends Booster<?>> getBoostsOfType(Player player, Skill skill, BoostType type) {
        return getActiveBoosts(player, skill).stream().filter(boost -> boost.getType() == type).collect(Collectors.toList());
    }

    public static List<? extends Booster<?>> getActiveBoosts(Player player, Skill skill) {
        return boosts.stream().filter(boost -> {
            if (boost.getType() == BoostType.EXPERIENCE) {
                return ((ExperienceBooster) boost).applied(new PlayerSkillWrapper(player, skill));
            } else if (boost.getType() == BoostType.GENERIC) {
                return ((GenericBoost) boost).applied(player);
            } else {
                return false;
            }
        }).collect(Collectors.toList());
    }
}
