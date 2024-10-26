package io.xeros.model.tickable.impl;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import io.xeros.model.Animation;
import io.xeros.model.SkillLevel;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.tickable.Tickable;
import io.xeros.model.tickable.TickableContainer;

public class ItemProductionTickable implements Tickable<Player> {
    private final Player player;
    private final int productionAmount;
    private final int productionDelay;
    private final boolean immediateExecution;
    private final boolean consumeItemsOnSuccessFailure;
    private final ImmutableItem[] itemsRequired;
    private final ImmutableItem[] itemsConsumed;
    private final ImmutableItem[] itemsProduced;
    private final SkillLevel[] experiencedGained;
    private final SkillLevel[] levelRequirements;
    private final Predicate<ItemProductionTickable> productionPredicate;
    private final Predicate<ItemProductionTickable> successFormulaPredicate;
    private final Consumer<ItemProductionTickable> productionConsumer;
    private final Consumer<ItemProductionTickable> executionConsumer;
    private final Consumer<ItemProductionTickable> finishConsumer;
    private final Function<ItemProductionTickable, ImmutableItem[]> itemProductionFunction;

    private int produced = 0;

    public ItemProductionTickable(Player player, int productionAmount, int productionDelay, boolean immediateExecution, boolean consumeItemsOnSuccessFailure, ImmutableItem[] itemsRequired, ImmutableItem[] itemsConsumed, ImmutableItem[] itemsProduced,
                                  SkillLevel[] experiencedGained, SkillLevel[] levelRequirements, Predicate<ItemProductionTickable> productionPredicate, Predicate<ItemProductionTickable> successFormulaPredicate,
                                  Consumer<ItemProductionTickable> productionConsumer, Consumer<ItemProductionTickable> executionConsumer, Consumer<ItemProductionTickable> finishConsumer, Function<ItemProductionTickable, ImmutableItem[]> itemProductionFunction) {
        Preconditions.checkArgument(player != null, new NullPointerException());
        Preconditions.checkArgument(productionAmount != 0, new IllegalArgumentException());
        Preconditions.checkArgument(productionDelay != 0, new IllegalArgumentException());
        this.player = player;
        this.productionAmount = productionAmount;
        this.productionDelay = productionDelay;
        this.immediateExecution = immediateExecution;
        this.consumeItemsOnSuccessFailure = consumeItemsOnSuccessFailure;
        this.itemsRequired = itemsRequired;
        this.itemsConsumed = itemsConsumed;
        this.itemsProduced = itemsProduced;
        this.experiencedGained = experiencedGained;
        this.levelRequirements = levelRequirements;
        this.productionPredicate = productionPredicate;
        this.successFormulaPredicate = successFormulaPredicate;
        this.productionConsumer = productionConsumer;
        this.executionConsumer = executionConsumer;
        this.finishConsumer = finishConsumer;
        this.itemProductionFunction = itemProductionFunction;
    }

    /**
     * Starts the production start.
     */
    public void begin() {
        player.getPA().removeAllWindows();
        player.interruptActions();

        if (checkRequirements()) {
            player.setTickable(this);
        }
    }

    private boolean checkRequirements() {
        if (itemsRequired != null && !player.getInventory().containsAll(itemsRequired)) {
            if (itemsConsumed != null && !player.getInventory().containsAll(itemsConsumed)) {
                player.sendStatement("You don't have all the required items:", ImmutableItem.getItemsAsString(ImmutableItem.concatItemArray(itemsRequired, itemsConsumed)));
                return false;
            } else {
                player.sendStatement("You don't have all the required items:", ImmutableItem.getItemsAsString(itemsRequired));
                return false;
            }
        }

        if (itemsConsumed != null) {
            for (ImmutableItem item : itemsConsumed) {
                if (!player.getInventory().containsAll(item)) {
                    if (produced != 0) {
                        player.sendMessage("You have run out of " + ItemDef.forId(item.getId()).getName() + ".");
                    } else {
                        player.sendStatement("You don't have all the required items:", ImmutableItem.getItemsAsString(itemsConsumed));
                    }
                    return false;
                }
            }
        }

        if (levelRequirements != null && !player.getPA().hasSkillLevels(levelRequirements)) {
            player.sendStatement("You don't have all the required levels:", SkillLevel.getLevelsAsString(levelRequirements));
            return false;
        }

        if (productionPredicate != null && !productionPredicate.test(this)) {
            return false;
        }

        return true;
    }

    private void consumeRequiredItems() {
        if (itemsConsumed != null) {
            for (ImmutableItem item : itemsConsumed) {
                player.getItems().deleteItem(item.getId(), item.getAmount());
            }
        }
    }

    @Override
    public void tick(TickableContainer<Player> container, Player player) {
        if (container.getTicks() == 0 || container.getTicks() % productionDelay != 0) {
            return;
        }

        if (!checkRequirements()) {
            container.stop();
            player.startAnimation(Animation.RESET_ANIMATION);
            return;
        }

        if (executionConsumer != null) {
            executionConsumer.accept(this);
        }

        if (successFormulaPredicate == null || successFormulaPredicate.test(this)) {
            consumeRequiredItems();

            if (itemsProduced != null) {
                Arrays.stream(itemsProduced).forEach(item -> player.getInventory().addAnywhere(item));
            }

            if (itemProductionFunction != null) {
                ImmutableItem[] produced = itemProductionFunction.apply(this);
                if (produced != null) {
                    Arrays.stream(produced).forEach(item -> player.getInventory().addAnywhere(item));
                }
            }

            if (experiencedGained != null) {
                Arrays.stream(experiencedGained).forEach(exp -> player.getPA().addSkillXPMultiplied(exp.level(), exp.skill().getId(), true));
            }

            if (productionConsumer != null) {
                productionConsumer.accept(this);
            }

            if (++produced >= productionAmount) {
                container.stop();
                player.interruptActions();
                if (finishConsumer != null) {
                    finishConsumer.accept(this);
                }
            }
        } else if (consumeItemsOnSuccessFailure) {
            consumeRequiredItems();
        }
    }

    public Player getPlayer() {
        return player;
    }
}