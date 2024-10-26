package io.xeros.content.itemskeptondeath.modifiers;

import io.xeros.content.itemskeptondeath.DeathItemModifier;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RevenantEtherDeathItem implements DeathItemModifier {

    private static final Set<Integer> CRAWS = Set.of(Items.CRAWS_BOW);
    private static final Set<Integer> VIGGORAS = Set.of(Items.VIGGORAS_CHAINMACE);
    private static final Set<Integer> THAMMARONS = Set.of(Items.THAMMARONS_SCEPTRE);
    private static final Set<Integer> BRACELET = Set.of(Items.BRACELET_OF_ETHEREUM);
    private static final Set<Integer> ALL;

    static {
        ALL = new HashSet<>();
        ALL.addAll(CRAWS);
        ALL.addAll(VIGGORAS);
        ALL.addAll(THAMMARONS);
        ALL.addAll(BRACELET);
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
        int id = gameItem.id();
        int charge = 0;

        if (CRAWS.contains(id)) {
            player.getPvpWeapons().setCrawsBowCharges(0);
            lostItems.add(new GameItem(Items.CRAWS_BOW_U));
        } else if (VIGGORAS.contains(id)) {
            player.getPvpWeapons().setViggoraChainmaceCharges(0);
            lostItems.add(new GameItem(Items.VIGGORAS_CHAINMACE_U));
        } else if (THAMMARONS.contains(id)) {
            player.getPvpWeapons().setThammaronSceptreCharges(0);
            lostItems.add(new GameItem(Items.THAMMARONS_SCEPTRE_U));
        } else if (BRACELET.contains(id)) {
            charge = player.braceletEtherCount;
            player.braceletEtherCount = 0;
            lostItems.add(new GameItem(Items.BRACELET_OF_ETHEREUM_UNCHARGED));
        }

        if (charge > 0)
            lostItems.add(new GameItem(Items.REVENANT_ETHER, charge));
    }
}
