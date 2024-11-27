package io.xeros.content.wilderness;

import io.xeros.Server;
import io.xeros.content.combat.weapon.WeaponData;
import io.xeros.model.entity.Bonus;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import java.util.*;

/**
 * @author Ynneh
 */
public class SpiderWeb {

    /**
     * Object IDs
     */
    public static final int OBJECT_ID = 733, RESTORE_ID = 734;
    /**
     * Arraylist for wilderness weapons
     */
    private static List<Integer> wildernessWeapons = Arrays.asList(13108, 13109, 13110, 13111);

    public static void slash(Player player, GlobalObject object) {
        /**
         * Delay check implemented for the object not to be spammable
         */
        if (player.lastWebSlash > System.currentTimeMillis())
            return;

        /**
         * The Y location of the object.
         */
        final int objectX = object.getX();

        /**
         * The X location of the object.
         */
        final int objectY = object.getY();

        /**
         * Grabs the players current slash bonus
         */
        int slashBonus = player.getItems().getBonus(Bonus.ATTACK_SLASH);

        /**
         * Grabs the players current weapon to ID
         */
        int weaponId = player.playerEquipment[Player.playerWeapon];

        /**
         * Check to see if the player has a wilderness weapon
         */
        boolean isWildernessWeapon = wildernessWeapons.stream().filter(Objects::nonNull).anyMatch(i -> i.intValue() == weaponId);

        /**
         * Randomly generated number into %
         */
        double chance = Math.random();

        /**
         * The players % chance to be sucessful
         */
        double playerChance = 0;

        /**
         * Check added for no weapon.
         */
        if (weaponId == -1) {
            player.sendMessage("You need something sharp to cut through this web.");
            return;
        }
        /**
         * According to OSRS Wiki anything with 100+ slash bonus or is a wilderness weapon gives 100% chance.
         */
        if (slashBonus >= 100 || isWildernessWeapon)
            playerChance = 1;

        if (slashBonus > 20 && slashBonus < 100)
            playerChance = ((double)slashBonus / 100);

        /**
         * More info here: https://oldschool.runescape.wiki/w/Weapons/Types#Slash_swords
         */
        else if (WeaponData.forItemId(weaponId).getWeaponInterface().getNameInterfaceId() == 2426)
            playerChance = .5;

        /**
         * Anything with more than 0 slash bonus has a 35% chance
         */
        else if (slashBonus > 0 && slashBonus < 20)
            playerChance = .35;
        /**
         * 25% chance by default
         */
        else
            playerChance = .25;
        /**
         * 15% chance by default
         */
       // else
           // playerChance = .15;
        /**
         * TODO add scaling with slash bonus.
         */

        /**
         * Animates the player to slash.
         */
        player.startAnimation(451);

        /**
         * Adds a 2 second delay from the object being spammed
         */
        player.lastWebSlash = System.currentTimeMillis() + 2_000;

        /**
         * Grabs actual object data straight from the cache. (landscape data with object definitions)
         */
        Optional<WorldObject> obj = player.getRegionProvider().get(objectX, objectY).getWorldObject(object.getObjectId(), objectX, objectY, object.getHeight());

        /**
         * Debug
         */
        //player.sendMessage("Chance="+playerChance+" rolled="+chance+" slashBonus="+slashBonus+" FaceDirection="+obj.get().getFace()+" - "+obj.get().type);

        /**
         * Boolean check for when the player is sucessful.
         */
        if (playerChance >= chance) {
            player.sendMessage("You slash the web apart.");
            Server.getGlobalObjects().add(new GlobalObject(734, objectX, objectY, obj.get().getHeight(), obj.get().getFace(), obj.get().getType(), 20, 733));
        } else
            player.sendMessage("You fail to cut through the web.");
    }
}
