package io.xeros.content.bosses.hespori;

import io.xeros.content.QuestTab;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class CelastrusBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeCelastrusSeed = true;
        Hespori.CELASTRUS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted()+ " @bla@planted a Celastrus seed which" +
                " granted @red@1 hour of 2x Brimstone keys!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeCelastrusSeed = false;
        Hespori.CELASTRUS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.CELASTRUS;
    }
}
