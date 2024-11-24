package io.xeros.content.itemskeptondeath.modifiers;

import io.xeros.content.combat.CombatItems;
import io.xeros.content.itemskeptondeath.DeathItemModifier;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZulrahEquipmentDeathItem implements DeathItemModifier {

    private static final Set<Integer> SERP_HELMETS_CHARGED;
    private static final Set<Integer> TOXIC_STAFF = Set.of(Items.TOXIC_STAFF_OF_THE_DEAD);
    private static final Set<Integer> BLOWPIPE = Set.of(Items.TOXIC_BLOWPIPE);

    private static final Set<Integer> TOXIC_TRIDENT = Set.of(Items.TRIDENT_OF_THE_SWAMP,
            Items.TRIDENT_OF_THE_SWAMP_E,
            Items.TRIDENT_OF_THE_SWAMP_E_2);

    private static final Set<Integer> ALL;

    static {
        SERP_HELMETS_CHARGED = new HashSet<>();
        Arrays.stream(CombatItems.MUTAGEN_HELMETS).forEach(it -> SERP_HELMETS_CHARGED.add(it[0]));

        ALL = new HashSet<>();
        ALL.addAll(SERP_HELMETS_CHARGED);
        ALL.addAll(TOXIC_STAFF);
        ALL.addAll(BLOWPIPE);
    }

    @Override
    public Set<Integer> getItemIds() {
        return ALL;
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (kept)
            return;

        lostItems.remove(gameItem);
        int charges = 0;

        if (SERP_HELMETS_CHARGED.contains(gameItem.id())) {
            charges = player.getSerpentineHelmCharge();
            player.setSerpentineHelmCharge(0);
            lostItems.add(new GameItem(CombatItems.getUnchargedSerpentineHelmet(gameItem.id())));
        } else if (TOXIC_STAFF.contains(gameItem.id())) {
            charges = player.getToxicStaffOfTheDeadCharge();
            player.setToxicStaffOfTheDeadCharge(0);
            lostItems.add(new GameItem(Items.TOXIC_STAFF_UNCHARGED));
        } else if (TOXIC_TRIDENT.contains(gameItem.id())) {
            charges = player.getToxicTridentCharge();
            player.setToxicTridentCharge(0);
            lostItems.add(new GameItem(Items.TRIDENT_OF_THE_SWAMP));
        } else if (BLOWPIPE.contains(gameItem.id())) {
            charges = player.getToxicBlowpipeCharge();
            player.setToxicBlowpipeCharge(0);
            lostItems.add(new GameItem(Items.TOXIC_BLOWPIPE_EMPTY));
            if (player.getToxicBlowpipeAmmo() > 0 && player.getToxicBlowpipeAmmoAmount() > 0) {
                lostItems.add(new GameItem(player.getToxicBlowpipeAmmo(), player.getToxicBlowpipeAmmoAmount()));
                player.setToxicBlowpipeAmmo(0);
                player.setToxicBlowpipeAmmoAmount(0);
            }
        }

        if (charges > 0)
            lostItems.add(new GameItem(Items.ZULRAHS_SCALES, charges));
    }
}
