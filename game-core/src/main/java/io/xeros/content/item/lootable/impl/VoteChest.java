package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class VoteChest implements Lootable {

    public static final int KEY = 22093; //vote key heree
    private static final int ANIMATION = 881;

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(6666), //flippers15
                new GameItem(2577), //ranger boots35
                new GameItem(12596), //ranger tunic25
                new GameItem(12600), //wreath30
                new GameItem(20214), //team cape x25
                new GameItem(20217), //team cape i25
                new GameItem(20211))); //team cape zero25
        items.put(LootRarity.UNCOMMON, Arrays.asList(
                new GameItem(12639), //guthix halo45
                new GameItem(12637), //sara halo45
                new GameItem(12638), //zammy halo45
                new GameItem(12526), //fury orn50
                new GameItem(10507), //reindeer hat
                new GameItem(23312), //sandwich outfit
                new GameItem(23315), //sandwich outfit
                new GameItem(23318), //sandwich outfit
                new GameItem(23357), //rain bpw
                new GameItem(23410), //wolf cloak
                new GameItem(23300), //parrot
                new GameItem(23407), //wolf head
                new GameItem(23413), //rock climbing boots g
                new GameItem(21028))); //harpoon


        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(23360), //ham joint
                new GameItem(23303), //monk robe t
                new GameItem(23306), //monk robe t
                new GameItem(23925), //crystal crowns
                new GameItem(23923), //crystal crowns
                new GameItem(23921), //crystal crowns
                new GameItem(23919), //crystal crowns
                new GameItem(23917), //crystal crowns
                new GameItem(23915), //crystal crowns
                new GameItem(23913), //crystal crowns
                new GameItem(23911), //crystal crowns
                new GameItem(21439), //champ cape65
                new GameItem(20836), //giant presetn85
                new GameItem(9920), //jack lantern120
                new GameItem(5608), //fox150
                new GameItem(19941), //heavy casket85
                new GameItem(1037) //bunny ears
        ));
    }

    private static GameItem randomChestRewards(Player c, int chance) {
        int random = Misc.random(chance);
        int rareChance = 90;
        int uncommonChance = 50;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 89;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        List<GameItem> itemList = random < uncommonChance ? items.get(LootRarity.COMMON) : random >= uncommonChance && random <= rareChance ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    private static void votePet(Player c) {
        int petchance = Misc.random(1500);
        if (petchance >= 1499) {
            c.getItems().addItem(21262, 1);
            c.getCollectionLog().handleDrop(c, 5, 21262, 1);
            PlayerHandler.executeGlobalMessage("@red@- "+ c.getDisplayName() +"@blu@ has just received the @red@Vote Genie Pet");
            c.sendMessage("@red@@cr10@You pet genie is waiting in your bank, waiting to serve you as his master.");
            c.gfx100(1028);
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            Achievements.increase(c, AchievementType.VOTE_CHEST_UNLOCK, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards(c, 100);
            String name = ItemDef.forId(reward.id()).getName();
            if (!c.getItems().addItem(reward.id(), reward.amount())) {
                Server.itemHandler.createGroundItem(c, reward.id(), c.getX(), c.getY(), c.heightLevel, reward.amount());
            }
            PlayerHandler.executeGlobalMessage("@pur@["+ c.getDisplayName() +"]@blu@ has just opened the vote chest and received a " + name + "!");
            int random = 1 + Misc.random(5);
            c.votePoints+= random;
            c.sendMessage("You have received an extra "+random+" vote points from the chest.");
            votePet(c);
        } else {
            c.sendMessage("@blu@Use @red@::vpanel @blu@to see when you'll get your next key!");
        }
    }
}
