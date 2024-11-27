package io.xeros.content.skills.slayer;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class KonarSlayer {



    public static void assignKonarSlayer(Player player, Task task) {
        assignLocation(player, task);

    }

    private static void assignLocation(Player player, Task task) {//assigns a location based on task
        int randomLocation = Misc.random(1);
        if (task.getPrimaryName().contains("abyssal demon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("catacombs");
            } else {
                player.setKonarSlayerLocation("slayer tower");
            }
        } else if (task.getPrimaryName().contains("adamant dragon")) {
            player.setKonarSlayerLocation("lithkren vault");

        } else if (task.getPrimaryName().contains("undead druid")) {
            player.setKonarSlayerLocation("forthos dungeon");

        } else if (task.getPrimaryName().contains("ankou")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("stronghold cave");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("aviansie")) {
            player.setKonarSlayerLocation("god wars dungeon");

        } else if (task.getPrimaryName().contains("basilisk")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("fremennik dungeon");
            } else {
                player.setKonarSlayerLocation("jormungands prison");
            }

        } else if (task.getPrimaryName().contains("black demon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("catacombs");
            } else {
                player.setKonarSlayerLocation("taverly dungeon");
            }

        } else if (task.getPrimaryName().contains("black dragon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("taverly dungeon");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("bloodveld")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("stronghold cave");
            } else {
                player.setKonarSlayerLocation("slayer tower");
            }

        } else if (task.getPrimaryName().contains("blue dragon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("taverly dungeon");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("brine rat")) {
                player.setKonarSlayerLocation("brine rat cavern");

        } else if (task.getPrimaryName().contains("bronze dragon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("brimhaven dungeon");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("cave kraken")) {
            player.setKonarSlayerLocation("kraken cove");

        } else if (task.getPrimaryName().contains("dagannoth")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("waterbirth island");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("dark beast")) {
            player.setKonarSlayerLocation("catacombs");

        } else if (task.getPrimaryName().contains("drakes")) {
            player.setKonarSlayerLocation("karuulm dungeon");

        } else if (task.getPrimaryName().contains("dust devil")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("slayer tower");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("fire giant")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("stronghold cave");
            } else {
                player.setKonarSlayerLocation("brimhaven dungeon");
            }

        } else if (task.getPrimaryName().contains("gargoyle")) {
                player.setKonarSlayerLocation("slayer tower");

        } else if (task.getPrimaryName().contains("greater demon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("brimhaven dungeon");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("hellhound")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("stronghold cave");
            } else {
                player.setKonarSlayerLocation("taverly dungeon");
            }

        } else if (task.getPrimaryName().contains("hydra")) {
                player.setKonarSlayerLocation("karuulm dungeon");

        } else if (task.getPrimaryName().contains("iron dragon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("catacombs");
            } else {
                player.setKonarSlayerLocation("brimhaven dungeon");
            }

        } else if (task.getPrimaryName().contains("jelly")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("fremennik dungeon");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("kurask")) {
                player.setKonarSlayerLocation("fremennik dungeon");

        } else if (task.getPrimaryName().contains("lizardman shaman")) {
                player.setKonarSlayerLocation("lizardman canyon");


        } else if (task.getPrimaryName().contains("mithril dragon")) {
                player.setKonarSlayerLocation("ancient cavern");


        } else if (task.getPrimaryName().contains("nechryael")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("catacombs");
            } else {
                player.setKonarSlayerLocation("slayer tower");
            }

        } else if (task.getPrimaryName().contains("red dragon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("brimhaven dungeon");
            } else {
                player.setKonarSlayerLocation("catacombs");
            }

        } else if (task.getPrimaryName().contains("rune dragon")) {
                player.setKonarSlayerLocation("lithkren vault");


        } else if (task.getPrimaryName().contains("smoke devil")) {
                player.setKonarSlayerLocation("smoke devil dungeon");


        } else if (task.getPrimaryName().contains("steel dragon")) {
            if (randomLocation > 0) {
                player.setKonarSlayerLocation("catacombs");
            } else {
                player.setKonarSlayerLocation("brimhaven dungeon");
            }

        } else if (task.getPrimaryName().contains("mountain troll")) {
                player.setKonarSlayerLocation("death plateau");

        } else if (task.getPrimaryName().contains("wyrm")) {
                player.setKonarSlayerLocation("karuulm dungeon");

        } else {
            player.setKonarSlayerLocation("anywhere");
        }
    }

    public static boolean checkLocation(Player player) {//checks if player is in the assigned location when target is killed
        if (player.getKonarSlayerLocation().contains("taverly dungeon")	&& Boundary.isIn(player, Boundary.TAVERLY_DUNGEON)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("vorkath area")	&& Boundary.isIn(player, Boundary.VORKATH)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("forthos dungeon")	&& Boundary.isIn(player, Boundary.FORTHOS_DUNGEON)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("karuulm dungeon")	&& Boundary.isIn(player, Boundary.KARUULM_DUNGEON)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("death plateau")	&& Boundary.isIn(player, Boundary.DEATH_PLATEAU)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("brimhaven dungeon")	&& Boundary.isIn(player, Boundary.BRIMHAVEN_DUNGEON_BOUNDARY)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("catacombs")	&& Boundary.isIn(player, Boundary.CATACOMBS)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("smoke devil dungeon")	&& Boundary.isIn(player, Boundary.SMOKE_DEVIL_BOUNDARY)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("lithkren vault")	&& Boundary.isIn(player, Boundary.LITHKREN_VAULT)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("slayer tower")	&& Boundary.isIn(player, Boundary.SLAYER_TOWER_BOUNDARY)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("ancient cavern")	&& Boundary.isIn(player, Boundary.ANCIENT_CAVERN)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("lizardman canyon")	&& Boundary.isIn(player, Boundary.LIZARDMAN_CANYON)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("fremennik dungeon")	&& Boundary.isIn(player, Boundary.CANNON_FREMNIK_DUNGEON)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("stronghold cave")	&& Boundary.isIn(player, Boundary.STRONGHOLD_CAVE)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("waterbirth island")	&& Boundary.isIn(player, Boundary.WATERBIRTH_CAVES)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("jormungands prison")	&& Boundary.isIn(player, Boundary.JORMUNGANDS_PRISON)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("god wars dungeon")	&& Boundary.isIn(player, Boundary.GODWARS_AREA)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("brine rat cavern")	&& Boundary.isIn(player, Boundary.BRINE_RAT_CAVERN)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("kraken cove")	&& Boundary.isIn(player, Boundary.KRAKEN_CAVE)) {
            return true;
        } else if (player.getKonarSlayerLocation().contains("anywhere")) {
            return true;
        }
        return false;
    }


}
