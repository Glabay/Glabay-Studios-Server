package io.xeros.model.tickable.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.xeros.model.SkillLevel;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class ItemProductionTickableBuilder {
    private Player player;
    private int productionAmount;
    private int productionDelay;
    private boolean immediateExecution;
    private boolean consumeItemsOnSuccessFailure;
    private ImmutableItem[] itemsRequired;
    private ImmutableItem[] itemsConsumed;
    private ImmutableItem[] itemsProduced;
    private SkillLevel[] experiencedGained;
    private SkillLevel[] levelRequirements;
    private Predicate<ItemProductionTickable> productionPredicate;
    private Predicate<ItemProductionTickable> successFormulaPredicate;
    private Consumer<ItemProductionTickable> productionConsumer;
    private Consumer<ItemProductionTickable> executionConsumer;
    private Consumer<ItemProductionTickable> finishConsumer;
    private Function<ItemProductionTickable, ImmutableItem[]> itemProductionFunction;

    public ItemProductionTickableBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemProductionTickableBuilder setProductionAmount(int productionAmount) {
        this.productionAmount = productionAmount;
        return this;
    }

    public ItemProductionTickableBuilder setProductionDelay(int productionDelay) {
        this.productionDelay = productionDelay;
        return this;
    }

    public ItemProductionTickableBuilder setImmediateExecution(boolean immediateExecution) {
        this.immediateExecution = immediateExecution;
        return this;
    }

    public ItemProductionTickableBuilder setConsumeItemsOnSuccessFailure(boolean consumeItemsOnSuccessFailure) {
        this.consumeItemsOnSuccessFailure = consumeItemsOnSuccessFailure;
        return this;
    }

    public ItemProductionTickableBuilder setItemsRequired(ImmutableItem...itemsRequired) {
        this.itemsRequired = itemsRequired;
        return this;
    }

    public ItemProductionTickableBuilder setItemsConsumed(ImmutableItem...itemsConsumed) {
        this.itemsConsumed = itemsConsumed;
        return this;
    }

    public ItemProductionTickableBuilder setItemsProduced(ImmutableItem...itemsProduced) {
        this.itemsProduced = itemsProduced;
        return this;
    }

    public ItemProductionTickableBuilder setExperiencedGained(SkillLevel...experiencedGained) {
        this.experiencedGained = experiencedGained;
        return this;
    }

    public ItemProductionTickableBuilder setLevelRequirements(SkillLevel...levelRequirements) {
        this.levelRequirements = levelRequirements;
        return this;
    }

    public ItemProductionTickableBuilder setProductionPredicate(Predicate<ItemProductionTickable> productionPredicate) {
        this.productionPredicate = productionPredicate;
        return this;
    }

    public ItemProductionTickableBuilder setSuccessFormulaPredicate(Predicate<ItemProductionTickable> successFormulaPredicate) {
        this.successFormulaPredicate = successFormulaPredicate;
        return this;
    }

    public ItemProductionTickableBuilder setProductionConsumer(Consumer<ItemProductionTickable> productionConsumer) {
        this.productionConsumer = productionConsumer;
        return this;
    }

    public ItemProductionTickableBuilder setExecutionConsumer(Consumer<ItemProductionTickable> executionConsumer) {
        this.executionConsumer = executionConsumer;
        return this;
    }

    public ItemProductionTickableBuilder setFinishConsumer(Consumer<ItemProductionTickable> finishConsumer) {
        this.finishConsumer = finishConsumer;
        return this;
    }

    public ItemProductionTickableBuilder setItemProductionFunction(Function<ItemProductionTickable, ImmutableItem[]> itemProductionFunction) {
        this.itemProductionFunction = itemProductionFunction;
        return this;
    }

    public ItemProductionTickable createItemProductionTask() {
        return new ItemProductionTickable(player, productionAmount, productionDelay, immediateExecution, consumeItemsOnSuccessFailure, itemsRequired, itemsConsumed, itemsProduced, experiencedGained, levelRequirements, productionPredicate, successFormulaPredicate, productionConsumer, executionConsumer, finishConsumer, itemProductionFunction);
    }
}