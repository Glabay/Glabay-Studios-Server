package io.xeros.content.items;

import io.xeros.annotate.PostInit;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

public enum OrnamentedItem {

    FURY_ORNAMENT(Items.AMULET_OF_FURY, Items.FURY_ORNAMENT_KIT, Items.AMULET_OF_FURY_OR),
    TORTURE_ORNAMENT(Items.AMULET_OF_TORTURE, Items.TORTURE_ORNAMENT_KIT, Items.AMULET_OF_TORTURE_OR),
    ANGUISH(Items.NECKLACE_OF_ANGUISH, Items.ANGUISH_ORNAMENT_KIT, Items.NECKLACE_OF_ANGUISH_OR),
    TORMENTED_BRACELET(Items.TORMENTED_BRACELET, Items.TORMENTED_ORNAMENT_KIT, Items.TORMENTED_BRACELET_OR),
    OCCULT_NECKLACE(Items.OCCULT_NECKLACE, Items.OCCULT_ORNAMENT_KIT, Items.OCCULT_NECKLACE_OR),

    BLUE_DARK_BOW(Items.DARK_BOW, Items.BLUE_DARK_BOW_PAINT, Items.DARK_BOW_BLUE),
    GREEN_DARK_BOW(Items.DARK_BOW, Items.GREEN_DARK_BOW_PAINT, Items.DARK_BOW_GREEN),
    YELLOW_DARK_BOW(Items.DARK_BOW, Items.YELLOW_DARK_BOW_PAINT, Items.DARK_BOW_YELLOW),
    WHITE_DARK_BOW(Items.DARK_BOW, Items.WHITE_DARK_BOW_PAINT, Items.DARK_BOW_WHITE),

    MALEDICTION_WARD(Items.MALEDICTION_WARD, Items.WARD_UPGRADE_KIT, Items.MALEDICTION_WARD_2),
    ODIUM_WARD(Items.ODIUM_WARD, Items.WARD_UPGRADE_KIT, Items.ODIUM_WARD_2),

    STEAM_STAFF(Items.MYSTIC_STEAM_STAFF, Items.STEAM_STAFF_UPGRADE_KIT, Items.MYSTIC_STEAM_STAFF_2),

    GRANITE_MAUL(Items.GRANITE_MAUL, Items.GRANITE_CLAMP, Items.GRANITE_MAUL_OR),

    DRAGON_PICKAXE(Items.DRAGON_PICKAXE, Items.DRAGON_PICKAXE_UPGRADE_KIT, Items.DRAGON_PICKAXE_2),

    DRAGON_DEFENDER(Items.DRAGON_DEFENDER, Items.DRAGON_DEFENDER_ORNAMENT_KIT, Items.DRAGON_DEFENDER_T),
    DRAGON_PLATEBODY(Items.DRAGON_PLATEBODY, Items.DRAGON_PLATEBODY_ORNAMENT_KIT, Items.DRAGON_PLATEBODY_G),
    DRAGON_KITESHIELD(Items.DRAGON_KITESHIELD, Items.DRAGON_KITESHIELD_ORNAMENT_KIT, Items.DRAGON_KITESHIELD_G),

    FROZEN_WHIP(Items.ABYSSAL_WHIP, Items.FROZEN_WHIP_MIX, Items.FROZEN_ABYSSAL_WHIP),
    VOLCANIC_WHIP(Items.ABYSSAL_WHIP, Items.VOLCANIC_WHIP_MIX, Items.VOLCANIC_ABYSSAL_WHIP),

    AMARDYL_GODSWORD(Items.ARMADYL_GODSWORD, Items.ARMADYL_GODSWORD_ORNAMENT_KIT, Items.ARMADYL_GODSWORD_OR),
    SARADOMIN_GODSWORD(Items.SARADOMIN_GODSWORD, Items.SARADOMIN_GODSWORD_ORNAMENT_KIT, Items.SARADOMIN_GODSWORD_OR),
    BANDOS_GODSWORD(Items.BANDOS_GODSWORD, Items.BANDOS_GODSWORD_ORNAMENT_KIT, Items.BANDOS_GODSWORD_OR),
    ZAMORAK_GODSWORD(Items.ZAMORAK_GODSWORD, Items.ZAMORAK_GODSWORD_ORNAMENT_KIT, Items.ZAMORAK_GODSWORD_OR),


    TWISTED_SLAYER_HELMET(Items.SLAYER_HELMET, Items.TWISTED_HORNS, Items.TWISTED_SLAYER_HELMET),
    TWISTED_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.TWISTED_HORNS, Items.TWISTED_SLAYER_HELMET_I),

    HYDRA_SLAYER_HELMET(Items.SLAYER_HELMET, Items.ALCHEMICAL_HYDRA_HEADS, Items.HYDRA_SLAYER_HELMET),
    HYDRA_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.ALCHEMICAL_HYDRA_HEADS, Items.HYDRA_SLAYER_HELMET_I),

    KBD_SLAYER_HELMET(Items.SLAYER_HELMET, Items.KBD_HEADS, Items.BLACK_SLAYER_HELMET),
    KBD_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.KBD_HEADS, Items.BLACK_SLAYER_HELMET_I),

    KQ_SLAYER_HELMET(Items.SLAYER_HELMET, Items.KQ_HEAD, Items.GREEN_SLAYER_HELMET),
    KQ_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.KQ_HEAD, Items.GREEN_SLAYER_HELMET_I),

    ABYSSAL_SLAYER_HELMET(Items.SLAYER_HELMET, Items.ABYSSAL_HEAD, Items.RED_SLAYER_HELMET),
    ABYSSAL_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.ABYSSAL_HEAD, Items.RED_SLAYER_HELMET_I),

    DARK_CLAW_SLAYER_HELMET(Items.SLAYER_HELMET, Items.DARK_CLAW, Items.PURPLE_SLAYER_HELMET),
    DARK_CLAW_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.DARK_CLAW, Items.PURPLE_SLAYER_HELMET_I),

    VORKATH_SLAYER_HELMET(Items.SLAYER_HELMET, Items.VORKATHS_HEAD, Items.TURQUOISE_SLAYER_HELMET),
    VORKATH_SLAYER_HELMET_I(Items.SLAYER_HELMET_I, Items.VORKATHS_HEAD, Items.TURQUOISE_SLAYER_HELMET_I),
    ;

    private final int standardItem;
    private final int ornamentKitItem;
    private final int ornamentedItem;

    OrnamentedItem(int standardItem, int ornamentKitItem, int ornamentedItem) {
        this.standardItem = standardItem;
        this.ornamentKitItem = ornamentKitItem;
        this.ornamentedItem = ornamentedItem;
    }

    public int getStandardItem() {
        return standardItem;
    }

    public int getOrnamentKitItem() {
        return ornamentKitItem;
    }

    public int getOrnamentedItem() {
        return ornamentedItem;
    }

    public static OrnamentedItem forOrnamentedItem(int ornamentedItemId) {
        return Arrays.stream(OrnamentedItem.values()).filter(it -> it.getOrnamentedItem() == ornamentedItemId).findFirst().orElse(null);
    }

    public static OrnamentedItem getOrnamentedItem(int item1, int item2) {
        Optional<OrnamentedItem> ornament = Arrays.stream(OrnamentedItem.values()).filter(it -> it.getOrnamentKitItem() == item1 && it.getStandardItem() == item2
                || it.getOrnamentKitItem() == item2 && it.getStandardItem() == item1).findFirst();
        return ornament.orElse(null);
    }

    public static boolean ornament(Player player, int item1, int item2) {
        OrnamentedItem ornament = getOrnamentedItem(item1, item2);
        if (ornament == null)
            return false;
        player.getItems().deleteItem(ornament.getStandardItem(), 1);
        player.getItems().deleteItem(ornament.getOrnamentKitItem(), 1);
        player.getItems().addItem(ornament.getOrnamentedItem(), 1);
        player.sendMessage("You've ornamented your {}.", ItemDef.forId(ornament.getStandardItem()).getName());
        return true;
    }

    @PostInit
    public static void verifyItemPrices() {
        Arrays.stream(OrnamentedItem.values()).forEach(it -> {
            ItemDef standard = ItemDef.forId(it.getStandardItem());
            ItemDef ornamented = ItemDef.forId(it.getOrnamentedItem());
            if (standard.getShopValue() != ornamented.getShopValue()) {
                System.err.printf("Ornamented item doesn't share the same price as standard: orig=%d, ornament=%d, price=%d%n",
                        standard.getId(), ornamented.getId(), standard.getShopValue());
            }
        });
    }
}
