package io.xeros.model.entity.player.migration;

import io.xeros.model.entity.player.Player;

/**
 * Migrates a player from one version to another.
 *
 * For instance, you need to remove an item from all existing players immediately before they finish logging in.
 * You can make a migration that removes the item and it will be called for all existing players, new players won't be migrated.
 *
 * Migration implementations should have the following format: V{version_number}__description_of_migration.
 * Migrations must follow a linear curve (i.e. 1, 2, 3, 4).
 */
public interface PlayerMigration {

    /**
     * When a player logs in and his migration level is less than the version of this migration,
     * this method is called and then his migration level is increased.
     */
    void migrate(Player player);

}
