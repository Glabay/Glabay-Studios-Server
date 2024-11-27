package io.xeros.util.rswikii;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author Arthur Behesnilian 2:15 PM
 */
public class NpcWikiDumper {

    /**
     * npcNames.txt
     */

    public static String RSWIKI_URL = "http://oldschoolrunescape.wikia.com/wiki/";

    //public static HashMap<Integer, String> npcs = new HashMap<>();
    public static ArrayList<NpcCombatDefinition> statsList = new ArrayList<>();
    public static double completion = 0;

    public static void main(String[] args) {
        NpcWikiDumper.loadNpcList();
        //dumpNpcCache();
        dumpStats();
    }

    public static String formatNameForWiki(String entityName) {
        try {
            entityName = entityName.replace(" ", "_")
                    .replace("<.*>\b|<.*>", "")
                    .replace(" ", "_")
                    .replace("<.*>\b|<.*>", "");
            entityName = URLEncoder.encode(entityName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return entityName;
    }

    public static void dumpNpcCache() {
        ArrayList<String> npcsAlreadyDumped = new ArrayList<>();
        NpcDef.getDefinitions().forEach((id, def) -> {
            if (!npcsAlreadyDumped.contains(def.getName())) {
                if(cacheNpc(id, def.getName())) {
                    npcsAlreadyDumped.add(def.getName());
                }
            }
        });
    }

    public static void dumpStats() {

        long startTime = System.currentTimeMillis();

        int size = NpcDef.getDefinitions().size();
        NpcDef.getDefinitions().forEach((id, def) -> {
            dumpStats(id, def.getName());
            System.out.println("%(" + ((double) (++completion / size) * 100) + ") - (" + completion + "/" + size + ")");
            System.out.println("Time Elapsed: " + ((System.currentTimeMillis() - startTime) / 1000));
        });

        NpcWikiDumper.saveNpcStats();
        System.out.println("It took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds to finish " +
                "dumping " + statsList.size() + " combat definitions.");
    }

    public static boolean cacheNpc(int npcId, String name) {
        String fileName = name;
        name = formatNameForWiki(name);
        String filePath = "./data/cfg/wiki/npcs/cached/" + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + ".html"))) {
            Document page = Jsoup.connect("http://oldschool.runescape.wiki/w/" + name).get();
            writer.write(page.html());
            System.out.println("Cached NPC: " + npcId + " - " + name);
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public static void dumpStats(int npcId, String name) {
        try {
            File file = new File("./data/cfg/wiki/npcs/cached/" + name + ".html");
            if (!file.exists()) {
                System.out.println(name + " is not cached.");
                return;
            }

            Document page = Jsoup.parse(file, "UTF-8");
           // Document page = Jsoup.connect("http://oldschool.runescape.wiki/w/" + name).get();

            Elements infoTable = page.select(".infobox-monster");

            NpcCombatDefinition stats;

            if (infoTable.size() > 0) {
                stats = new NpcCombatDefinition(npcId);

                Elements tableRows = infoTable.select("tr");
                for (Element tableRow : tableRows) {
                    boolean rowHasCombatOptions = !tableRow.select("a").select("[title=Combat Options]").isEmpty();
                    if (rowHasCombatOptions) {

                        Elements attackStyleElements = tableRow.select("td").select("a");

                        String attackStyle = attackStyleElements.size() > 1 ? "SPECIAL"
                                : attackStyleElements.hasText() ? attackStyleElements.first().text() : "NONE";

                        stats.setAttackStyle(attackStyle);
                    }
                    boolean rowHasAggressive = !tableRow.select("a").select("[title=Aggressiveness]").isEmpty();
                    if (rowHasAggressive) {
                        Element aggressiveElement = tableRow.select("td").first();

                        boolean aggressive = aggressiveElement.text().equals("Yes");
                        stats.setAggressive(aggressive);
                    }
                    boolean rowisPoisonous = !tableRow.select("a").select("[title=Poisonous]").isEmpty();
                    if (rowisPoisonous) {
                        Element isPoisonousElement = tableRow.select("td").first();

                        boolean isPoisonous = isPoisonousElement.text().equals("Yes");
                        stats.setPoisonous(isPoisonous);
                    }
                    boolean rowHasAttackSpeed = !tableRow.select("a").select("[title=Monster attack speed]").isEmpty();
                    if (rowHasAttackSpeed) {
                        Element attackSpeedElement = tableRow.select("td").select("img").first();

                        String parsedAttackSpeed = attackSpeedElement.attr("alt").replaceAll("Monster attack speed ",
                                "").replaceAll(".png", "");
                        int attackSpeed = Integer.valueOf(parsedAttackSpeed);
                        stats.setAttackSpeed(attackSpeed);
                    }
                }

                Elements nestedInfoBoxes = infoTable.select(".infobox-nested");

                Elements statsBoxes = nestedInfoBoxes.select("td");

                Element poisonImmunityTag = infoTable
                        .select("tr")
                        .select("a[title=Poison]")
                        .first();

                Element venomImmunityTag = infoTable
                        .select("tr")
                        .select("a[title=Venom]")
                        .first();

                Element cannonImmunityTag = infoTable
                        .select("tr")
                        .select("a[title=Cannons]")
                        .first();

                Element thrallImmunityTag = infoTable
                        .select("tr")
                        .select("a[title=Thralls]")
                        .first();

                boolean poisonImmunity = poisonImmunityTag != null && NpcWikiDumper.getImmunity(poisonImmunityTag);
                boolean venomImmunity = venomImmunityTag != null && NpcWikiDumper.getImmunity(venomImmunityTag);
                boolean cannonImmunity = cannonImmunityTag != null && NpcWikiDumper.getImmunity(cannonImmunityTag);
                boolean thrallImmunity = thrallImmunityTag != null && NpcWikiDumper.getImmunity(thrallImmunityTag);

                if (statsBoxes.size() == 17) {
                    // COMBAT LEVELS
                    int hitpointsLevel = Integer.valueOf(statsBoxes.get(0).text().replace(",", ""));
                    int attackLevel = Integer.valueOf(statsBoxes.get(1).text());
                    int strengthLevel = Integer.valueOf(statsBoxes.get(2).text());
                    int defenceLevel = Integer.valueOf(statsBoxes.get(3).text());
                    int magicLevel = Integer.valueOf(statsBoxes.get(4).text());
                    int rangeLevel = Integer.valueOf(statsBoxes.get(5).text());

                    stats.setLevel(NpcCombatSkill.HITPOINTS, hitpointsLevel);
                    stats.setLevel(NpcCombatSkill.ATTACK, attackLevel);
                    stats.setLevel(NpcCombatSkill.STRENGTH, strengthLevel);
                    stats.setLevel(NpcCombatSkill.DEFENCE, defenceLevel);
                    stats.setLevel(NpcCombatSkill.MAGIC, magicLevel);
                    stats.setLevel(NpcCombatSkill.RANGE, rangeLevel);

                    // ATTACK BONUSES
                    int attackBonus = Integer.valueOf(statsBoxes.get(6).text().replace("+", ""));
                    int strengthBonus = Integer.valueOf(statsBoxes.get(7).text().replace("+", ""));
                    int magicBonus = Integer.valueOf(statsBoxes.get(8).text().replace("+", ""));
                    int magicStrengthBonus = Integer.valueOf(statsBoxes.get(9).text().replace("+", ""));
                    int rangeBonus = Integer.valueOf(statsBoxes.get(10).text().replace("+", ""));
                    int rangeStengthBonus = Integer.valueOf(statsBoxes.get(11).text().replace("+", ""));

                    stats.setAttackBonus(NpcBonus.ATTACK_BONUS, attackBonus);
                    stats.setAttackBonus(NpcBonus.STRENGTH_BONUS, strengthBonus);
                    stats.setAttackBonus(NpcBonus.ATTACK_MAGIC_BONUS, magicBonus);
                    stats.setAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS, magicStrengthBonus);
                    stats.setAttackBonus(NpcBonus.ATTACK_RANGE_BONUS, rangeBonus);
                    stats.setAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS, rangeStengthBonus);

                    // DEFENCE BONUSES
                    int stabDefenceBonus = Integer.valueOf(statsBoxes.get(12).text());
                    int slashDefenceBonus = Integer.valueOf(statsBoxes.get(13).text());
                    int crushDefenceBonus = Integer.valueOf(statsBoxes.get(14).text());
                    int magicDefenceBonus = Integer.valueOf(statsBoxes.get(15).text());
                    int rangeDefenceBonus = Integer.valueOf(statsBoxes.get(16).text());

                    stats.setDefenceBonus(NpcBonus.STAB_BONUS, stabDefenceBonus);
                    stats.setDefenceBonus(NpcBonus.SLASH_BONUS, slashDefenceBonus);
                    stats.setDefenceBonus(NpcBonus.CRUSH_BONUS, crushDefenceBonus);
                    stats.setDefenceBonus(NpcBonus.MAGIC_BONUS, magicDefenceBonus);
                    stats.setDefenceBonus(NpcBonus.RANGE_BONUS, rangeDefenceBonus);

                    // Immunities
                    stats.setImmuneToPoison(poisonImmunity);
                    stats.setImmuneToVenom(venomImmunity);
                    stats.setImmuneToCannons(cannonImmunity);
                    stats.setImmuneToThralls(thrallImmunity);

                    statsList.add(stats);

                } else
                    System.out.println("Not enough information to generate stats for " + name);
            } else
                System.out.println("No info table for " + name);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.out.println("Error occured when dumping stats for " + name);
            e.printStackTrace(System.err);
        }
        //System.out.println("Dumped " + statsList.size() + " stats");
    }

    private static boolean getImmunity(Element element) {
        return element.parent().parent().select("td").first().text().equalsIgnoreCase("Not immune") ? false : true;
    }

    public static void saveNpcStats() {
        Path path = Paths.get("./data/cfg/npc/", "npc_combat_defs.json");
        File file = path.toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            for (NpcCombatDefinition stats : statsList) {

                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = new JsonObject();

                object.addProperty("id", stats.getId());
                object.addProperty("attackSpeed", stats.getAttackSpeed());
                object.addProperty("attackStyle", stats.getAttackStyle());
                object.addProperty("aggressive", stats.isAggressive());
                object.addProperty("isPoisonous", stats.isPoisonous());
                object.addProperty("isImmuneToPoison", stats.isImmuneToPoison());
                object.addProperty("isImmuneToVenom", stats.isImmuneToVenom());
                object.addProperty("isImmuneToCannons", stats.isImmuneToCannons());
                object.addProperty("isImmuneToThralls", stats.isImmuneToThralls());
                object.add("levels", builder.toJsonTree(stats.getLevels()));
                object.add("aggressiveBonuses", builder.toJsonTree(stats.getAttackBonuses()));
                object.add("defensiveBonuses", builder.toJsonTree(stats.getDefenceBonuses()));
                writer.write(builder.toJson(object) + ",");
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * LOADS OUR NPC LIST FOR EASY WIKI CHECKING
     */
    public static void loadNpcList() {
        try {
            NpcDef.load();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }


        /*Path path = Paths.get("./data/", "npc_list_underscores.txt");
        File file = path.toFile();
        System.out.println(file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String[] parts;
            while ((line = reader.readLine()) != null) {
                parts = line.split(" ");
                if (parts.length == 1)
                    continue;
                int npcId = Integer.valueOf(parts[0]);
                npcs.put(npcId, parts[1].replaceAll("_", " "));
            }
            reader.close();
            System.out.println("Loaded " + npcs.size() + " npc names");
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }*/
    }

}
