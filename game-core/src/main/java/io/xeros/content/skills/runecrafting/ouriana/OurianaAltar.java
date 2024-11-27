package io.xeros.content.skills.runecrafting.ouriana;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.runecrafting.Runecrafting;
import io.xeros.model.definition.Items;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

public class OurianaAltar {

    private static final int[] STANDARD_RUNES = {Items.AIR_RUNE, Items.FIRE_RUNE, Items.WATER_RUNE, Items.EARTH_RUNE, Items.MIND_RUNE, Items.BODY_RUNE};
    private static final int[] RARE_RUNES = {Items.CHAOS_RUNE, Items.DEATH_RUNE, Items.BLOOD_RUNE, Items.SOUL_RUNE,
            Items.COSMIC_RUNE, Items.NATURE_RUNE, Items.ASTRAL_RUNE, Items.LAW_RUNE, Items.WRATH_RUNE};

    public static boolean clickObject(Player player, WorldObject worldObject) {
        if (worldObject.getId() == 29_631) {
            if (player.getInventory().containsAll(new ImmutableItem(Items.PURE_ESSENCE, 1))) {
                int essence = player.getItems().getItemAmount(Items.PURE_ESSENCE);
                if (essence >= 20) {
                    player.getEventCalendar().progress(EventChallenge.COMPLETE_X_RUNS_AT_THE_ZMI_ALTAR, 1);
                }
                player.getItems().deleteItem2(Items.PURE_ESSENCE, essence);
                player.gfx100(186);
                player.startAnimation(791);
                List<ImmutableItem> runes = Lists.newArrayList();
                int xpGained = 0;
                for (int count = 0; count < essence; count++) {
                    boolean multiplier = Misc.trueRand(200) <= player.getLevel(Skill.RUNECRAFTING);
                    int runeMultiplier = multiplier ? 1 : 5;
                    boolean rare = Misc.trueRand(200) <= player.getLevel(Skill.RUNECRAFTING);
                    int[] runePool = rare ? RARE_RUNES : STANDARD_RUNES;
                    int runeId = runePool[Misc.trueRand(runePool.length)];
                    Runecrafting.RunecraftingData runecraftData = Objects.requireNonNull(Runecrafting.RunecraftingData.forId(runeId));
                    xpGained += (runecraftData.getExperience() * 5d);
                    runes.add(new ImmutableItem(runeId, (1 + Misc.trueRand(2))* runeMultiplier));
                }

                runes.forEach(rune -> player.getInventory().addOrDrop(rune));
                player.getPA().addSkillXPMultiplied(xpGained, Skill.RUNECRAFTING.getId(), true);
                Runecrafting.petRoll(player, Runecrafting.RunecraftingData.AIR);
            } else {
                player.sendMessage("You don't have any pure essence to craft.");
            }
            return true;
        } else if (worldObject.getId() == 29627 || worldObject.getId() == 29_626) {
            if (player.getX() < 3055) {
                player.moveTo(new Position(3055, 5585));
            } else {
                player.moveTo(new Position(3053, 5588));
            }
        } else if (worldObject.getId() == 29_636) {
            if (worldObject.getPosition().equals(new Position(3015, 5630))) {
                player.climbLadderTo(new Position(2453, 3231));
            }
        } else if (worldObject.getId() == 29_635) {
            if (worldObject.getPosition().equals(new Position(2452, 3231))) {
                player.climbLadderTo(new Position(3015, 5629), OurianaBanker::welcomePlayer);
            }
        }

        return false;
    }

}
