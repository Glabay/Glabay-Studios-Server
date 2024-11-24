package io.xeros.content.skills.smithing;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.types.MakeItemDialogue;
import io.xeros.content.skills.Skill;
import io.xeros.model.definition.Items;
import io.xeros.model.SkillLevel;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.tickable.impl.ItemProductionTickableBuilder;

public class CannonballSmelting {

    public static boolean isSmeltingCannonballs(Player player) {
        return player.getItems().playerHasItem(Items.AMMO_MOULD);
    }

    public static void smelt(Player player) {
        player.start(new DialogueBuilder(player).makeItems(
                100, CannonballSmelting::make,
                new MakeItemDialogue.MakeItem(Items.CANNONBALL)
        ));
    }

    private static void make(MakeItemDialogue.PlayerMakeItem makeItem) {
        new ItemProductionTickableBuilder()
                .setPlayer(makeItem.getPlayer())
                .setProductionDelay(3)
                .setProductionAmount(makeItem.getAmount())
                .setExecutionConsumer(task -> task.getPlayer().startAnimation(899))
                .setItemsConsumed(new ImmutableItem(Items.STEEL_BAR))
                .setItemsProduced(new ImmutableItem(Items.CANNONBALL, 4))
                .setExperiencedGained(new SkillLevel(Skill.SMITHING, 25))
                .createItemProductionTask().begin();
    }

}
