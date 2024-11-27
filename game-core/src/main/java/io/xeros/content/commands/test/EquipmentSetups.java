package io.xeros.content.commands.test;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import io.xeros.content.commands.Command;
import io.xeros.model.EquipmentSetup;
import io.xeros.model.entity.player.Player;

public class EquipmentSetups extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        List<String> lines = Lists.newArrayList();
        lines.add("@dre@To create setups equip items and use");
        lines.add("@dre@::" + CreateEquipmentSetup.class.getSimpleName() + " preset_name.");
        lines.add("@dre@They will save and you can update them later if needed.");
        lines.add("");
        lines.addAll(EquipmentSetup.listSetups());

        player.getPA().openQuestInterface("Equipment Setups", lines);
    }

    public Optional<String> getDescription() {
        return Optional.of("List all the equipment setups");
    }
}
