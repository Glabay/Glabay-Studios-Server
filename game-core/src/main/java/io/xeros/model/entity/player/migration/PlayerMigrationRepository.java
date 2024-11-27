package io.xeros.model.entity.player.migration;

import com.google.common.base.Preconditions;
import io.xeros.annotate.Init;
import io.xeros.model.entity.player.Player;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.xeros.util.Reflection.*;

public class PlayerMigrationRepository {

    private static final Logger logger = LoggerFactory.getLogger(PlayerMigration.class);
    private static final Map<Integer, PlayerMigration> migrations = new HashMap<>();
    @Getter
    private static int latestVersion;

    @Init
    public static void init() {
        List<PlayerMigration> migrationImpls = newInstances(getSubClasses(PlayerMigration.class));
        migrationImpls.sort(Comparator.comparingInt(PlayerMigrationRepository::version));

        for (int version = 1; version < migrationImpls.size() + 1; version++) {
            int index = version - 1;
            PlayerMigration migration = migrationImpls.get(index);
            int statedVersion = version(migration);
            Preconditions.checkState(statedVersion == version, "Invalid version for migration: " + migration.getClass().getSimpleName());
            migrations.put(version, migration);
            latestVersion = version;
            logger.debug("Loaded player migration version={} description='{}'", version, description(migration));
        }
    }

    public static void migrate(Player player) {
        for (int i = 1; i <= player.getMigrationVersion(); i++)
            Preconditions.checkState(migrations.get(i) != null, "Migration is missing but was already run on player, version={}", i);

        while (true) {
            int nextVersion = player.getMigrationVersion() + 1;
            PlayerMigration migration = migrations.get(nextVersion);
            if (migration == null)
                break;
            migration.migrate(player);
            player.setMigrationVersion(nextVersion);
            logger.debug("Migrated {} to version {}.", player.getLoginName(), nextVersion);
        }
    }

    private static String[] split(PlayerMigration migration) {
        String[] split = migration.getClass().getSimpleName().split("__");
        split[0] = split[0].substring(1); // Remove the 'V'

        String errorMessage = "Invalid PlayerMigration format, example: V11__fix_stuff";
        Preconditions.checkState(split.length == 2, errorMessage);
        Preconditions.checkState(StringUtils.isNumeric(split[0]), errorMessage);
        return split;
    }

    private static int version(PlayerMigration migration) {
        String version = split(migration)[0];
        return Integer.parseInt(version);
    }

    private static String description(PlayerMigration migration) {
        return split(migration)[1];
    }

}
