package io.xeros.model.entity.player.migration.impl;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.migration.PlayerMigration;

/**
 * An example player migration.
 */
public class V1__example_migration implements PlayerMigration {
    @Override
    public void migrate(Player player) {
        // Put anything you want to change for existing accounts on their first login here
        // It only runs once!
    }
}
