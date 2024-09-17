package io.xeros.content.skills.slayer;

import io.xeros.Server;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.impl.LarransChest;
import io.xeros.model.Items;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class LarrensKey {

    private static final int LARGE_CHEST_OBJECT = 34_832;
    private static final int SMALL_CHEST_OBJECT = 34_831;


    public static void roll(Player player, NPC npc) {
        int rewardAmount = 1;
        if (Hespori.activeKeldaSeed) {
            rewardAmount = 2;
        }
        if (Misc.trueRand(110) == 0) {
            player.getEventCalendar().progress(EventChallenge.OBTAIN_X_LARRENS_KEYS, 1);
            Server.itemHandler.createGroundItem(player, Items.LARRANS_KEY, npc.getX(), npc.getY(), npc.getHeight(), rewardAmount);
            //PlayerHandler.executeGlobalMessage("@cr21@ @pur@" + player.playerName + " received a drop: Larran's key from wildy slayer.");
            player.sendMessage("@red@A " + ItemDef.forId(Items.LARRANS_KEY).getName() + " has has dropped.");
        }
    }

    public static boolean clickObject(Player player, WorldObject object) {
        if (object.getId() == LARGE_CHEST_OBJECT) {
            new LarransChest().roll(player);
            return true;
        } else if (object.getId() == SMALL_CHEST_OBJECT) {
            player.sendMessage("Larran's small chest wasn't added, if you feel it should be suggest it on ::discord!");
            return true;
        }

        return false;
    }
}
