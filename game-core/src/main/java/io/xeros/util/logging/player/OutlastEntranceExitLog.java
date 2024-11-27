package io.xeros.util.logging.player;

import io.xeros.model.SkillExperience;
import io.xeros.model.SlottedItem;
import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.PlayerLog;

import java.util.List;
import java.util.Set;

public class OutlastEntranceExitLog extends PlayerLog {

    private final boolean entered;
    private final List<SkillExperience> skillExperienceList;
    private final List<SlottedItem> inventory;
    private final List<SlottedItem> equipment;

    public OutlastEntranceExitLog(Player player, boolean entered, List<SkillExperience> skillExperienceList, List<SlottedItem> inventory, List<SlottedItem> equipment) {
        super(player);
        this.entered = entered;
        this.skillExperienceList = skillExperienceList;
        this.inventory = inventory;
        this.equipment = equipment;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("outlast");
    }

    @Override
    public String getLoggedMessage() {
        StringBuilder msg = new StringBuilder();
        if (entered) {
            msg.append("Enter outlast, stored ");
        } else {
            msg.append("Exit outlast, restored ");
        }

        msg.append("skills=");
        msg.append(skillExperienceList);
        msg.append(", inventory=");
        msg.append(inventory);
        msg.append(", equipment=");
        msg.append(equipment);
        return msg.toString();
    }
}
