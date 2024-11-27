package io.xeros.content.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.xeros.content.items.item_combinations.*;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public enum ItemCombinations {
	
	SARADOMINS_BLESSED_SWORD(new SaradominsBlessedSword(new GameItem(12809), Optional.of(Arrays.asList(new GameItem(12804))), new GameItem[] { new GameItem(12804), new GameItem(11838) })),

	BLESSED_SPIRIT_SHIELD(new BlessedSpiritShield(new GameItem(12831), Optional.empty(), new GameItem[] { new GameItem(12829), new GameItem(12833) })),
	ARCANE_SPIRIT_SHIELD(new ArcaneSpiritShield(new GameItem(12825), Optional.empty(), new GameItem[] { new GameItem(12827), new GameItem(12831) })),
	ELYSIAN_SPIRIT_SHIELD(new ElysianSpiritShield(new GameItem(12817), Optional.empty(), new GameItem[] { new GameItem(12819), new GameItem(12831) })),
	SPECTRAL_SPIRIT_SHIELD(new SpectralSpiritShield(new GameItem(12821), Optional.empty(), new GameItem[] { new GameItem(12823), new GameItem(12831) })),

	TENTACLE_WHIP(new TentacleWhip(new GameItem(12006), Optional.of(Arrays.asList(new GameItem(12004))), new GameItem[] { new GameItem(12004), new GameItem(4151) })),

	HOLY_BOOK(new HolyBook(new GameItem(3840), Optional.empty(),
			new GameItem[] { new GameItem(3839), new GameItem(3827), new GameItem(3828), new GameItem(3829), new GameItem(3830) })),

	UNHOLY_BOOK(new UnholyBook(new GameItem(3842), Optional.empty(),
			new GameItem[] { new GameItem(3841), new GameItem(3831), new GameItem(3832), new GameItem(3833), new GameItem(3834) })),

	BALANCE_BOOK(new BalanceBook(new GameItem(3844), Optional.empty(),
			new GameItem[] { new GameItem(3843), new GameItem(3835), new GameItem(3836), new GameItem(3837), new GameItem(3838) })),

	RING_OF_WEALTH_IMBUED(new RingOfWealthImbued(new GameItem(12785), Optional.empty(), new GameItem[] { new GameItem(2572), new GameItem(12783) })),

	ETERNAL_BOOTS(new EternalBoots(new GameItem(13235), Optional.empty(), new GameItem[] { new GameItem(13227), new GameItem(6920) })),
	PEGASIAN_BOOTS(new PegasianBoots(new GameItem(13237), Optional.empty(), new GameItem[] { new GameItem(13229), new GameItem(2577) })),
	PRIMORDIAL_BOOTS(new PrimordialBoots(new GameItem(13239), Optional.empty(), new GameItem[] { new GameItem(13231), new GameItem(11840) })),

	INFERNAL_AXE(new InfernalAxe(new GameItem(13241), Optional.empty(), new GameItem[] { new GameItem(13233), new GameItem(6739) })),
	INFERNAL_PICKAXE(new InfernalPickaxe(new GameItem(13243), Optional.empty(), new GameItem[] { new GameItem(13233), new GameItem(11920) })),

	;

	private final ItemCombination itemCombination;

	ItemCombinations(ItemCombination itemCombination) {
		this.itemCombination = itemCombination;
	}

	public ItemCombination getItemCombination() {
		return itemCombination;
	}

	static final Set<ItemCombinations> COMBINATIONS = Collections.unmodifiableSet(EnumSet.allOf(ItemCombinations.class));

	public static List<ItemCombinations> getCombinations(GameItem item1, GameItem item2) {
		return COMBINATIONS.stream().filter(combos -> combos.getItemCombination().itemsMatch(item1, item2)).collect(Collectors.toList());
	}

	public static Optional<ItemCombination> isRevertable(GameItem item) {
		Predicate<ItemCombinations> itemMatches = ic -> ic.getItemCombination().getRevertItems().isPresent() && ic.getItemCombination().getOutcome().id() == item.id();
		Optional<ItemCombinations> revertable = COMBINATIONS.stream().filter(itemMatches).findFirst();
		if (revertable.isPresent() && revertable.get().getItemCombination().isRevertable()) {
			return Optional.of(revertable.get().getItemCombination());
		}
		return Optional.empty();
	}

}
