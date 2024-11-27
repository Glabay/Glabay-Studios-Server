package io.xeros.content.commands.test;

import io.xeros.content.commands.Command;
import io.xeros.content.skills.herblore.Herb;
import io.xeros.content.skills.herblore.PotionData;
import io.xeros.content.skills.herblore.PotionDecanting;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

public class Herbs extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getItems().sendItemToAnyTab(Items.VIAL_OF_WATER, 1_000_000);

        Arrays.stream(Herb.values()).forEach(it -> {
            player.getItems().sendItemToAnyTab(it.getGrimy(), 10_000);
            player.getItems().sendItemToAnyTab(it.getClean(), 10_000);
        });

        Arrays.stream(PotionData.UnfinishedPotions.values()).forEach(it ->
                player.getItems().sendItemToAnyTab(it.getPotion().id(), 10_000));

        Arrays.stream(PotionDecanting.Potion.values()).forEach(pot -> {
            for (int i = 1; i <= 4; i++)
                player.getItems().sendItemToAnyTab(pot.getItemId(i), 10_000);
        });

        Arrays.stream(PotionData.FinishedPotions.values()).forEach(it -> Arrays.stream(it.getIngredients()).forEach(ingredient ->
        {
            if (ingredient.id() <= 0)
                return;
            player.getItems().sendItemToAnyTab(ingredient.id(), 80_000);
        }));
    }

    public Optional<String> getDescription() {
        return Optional.of("Add herblore ingredients to your bank.");
    }
}
