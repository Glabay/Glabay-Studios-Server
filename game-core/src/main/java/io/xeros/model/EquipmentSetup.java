package io.xeros.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.JsonUtil;

/**
 * Equipment setups for testing purposes only!
 */
public class EquipmentSetup {
    private static final String SAVE_DIRECTORY = Server.getDataDirectory() + "/cfg/equipment_setups/";
    private static final TypeToken<EquipmentSetup> TOKEN = new TypeToken<>() {
    };

    public static void equip(Player player, String name) throws IOException {
        EquipmentSetup setup = get(name + ".json");
        if (setup == null) {
            player.sendMessage("No equipment setup exists with name: " + name);
            player.getPA().openQuestInterface("Equipment Setups", listSetups());
        } else {
            setup.equip(player);
            player.sendMessage("Equipped setup \'" + setup.getName() + "\'.");
        }
    }

    public static EquipmentSetup get(String name) throws IOException {
        File file = new File(SAVE_DIRECTORY + name);
        if (!file.exists()) {
            return null;
        } else {
            return JsonUtil.fromJson(file.getPath(), TOKEN);
        }
    }

    public static List<String> listSetups() {
        File file = new File(SAVE_DIRECTORY);
        File[] files = file.listFiles();
        if (file.exists() && files != null) {
            return Arrays.stream(files).map(file2 -> {
                try {
                    EquipmentSetup setup = Objects.requireNonNull(get(file2.getName()));
                    return setup.getName() + " (Created by " + setup.getCreator() + ")";
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                return "Cannot load " + file2.getPath();
            }).collect(Collectors.toList());
        } else {
            return Lists.newArrayList();
        }
    }

    public static EquipmentSetup from(Player player, String name) {
        Map<Integer, ImmutableItem> inventory = Maps.newHashMap();
        Map<Integer, ImmutableItem> equipment = Maps.newHashMap();
        Map<Integer, Integer> levels = Maps.newHashMap();
        SpellBook spellBook = player.getSpellBook();
        player.getItems().getInventoryItems().forEach(item -> {
            inventory.put(item.slot(), new ImmutableItem(item.id(), item.amount()));
        });
        player.getItems().getEquipmentItems().forEach(item -> {
            equipment.put(item.slot(), new ImmutableItem(item.id(), item.amount()));
        });
        for (int id = 0; id < player.playerLevel.length; id++) {
            levels.put(id, player.playerLevel[id]);
        }
        return new EquipmentSetup(name, player.getLoginName().toLowerCase(), inventory, equipment, levels, spellBook);
    }

    private final String name;
    private final String creator;
    private final Map<Integer, ImmutableItem> inventory;
    private final Map<Integer, ImmutableItem> equipment;
    private final Map<Integer, Integer> levels;
    private final SpellBook spellBook;

    public boolean serialize() throws IOException {
        File file = new File(SAVE_DIRECTORY + name + ".json");
        if (file.exists() && !get(name + ".json").creator.equalsIgnoreCase(creator)) {
            return false;
        } else {
            JsonUtil.toJson(this, SAVE_DIRECTORY + name + ".json");
            return true;
        }
    }

    public void equip(Player player) {
        player.getItems().deleteAllItems();
        inventory.forEach((key, value) -> {
            player.playerItems[key] = value.getId() + 1;
            player.playerItemsN[key] = value.getAmount();
        });
        equipment.forEach((key, value) -> {
            player.playerEquipment[key] = value.getId();
            player.playerEquipmentN[key] = value.getAmount();
        });
        player.setSpellBook(spellBook);
        player.getPA().requestUpdates();
        MeleeData.setWeaponAnimations(player);
        player.getItems().calculateBonuses();
        player.getItems().sendEquipmentContainer();
        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
    }


    public EquipmentSetup(final String name, final String creator, final Map<Integer, ImmutableItem> inventory, final Map<Integer, ImmutableItem> equipment, final Map<Integer, Integer> levels, final SpellBook spellBook) {
        this.name = name;
        this.creator = creator;
        this.inventory = inventory;
        this.equipment = equipment;
        this.levels = levels;
        this.spellBook = spellBook;
    }

    public String getName() {
        return this.name;
    }

    public String getCreator() {
        return this.creator;
    }

    public Map<Integer, ImmutableItem> getInventory() {
        return this.inventory;
    }

    public Map<Integer, ImmutableItem> getEquipment() {
        return this.equipment;
    }

    public Map<Integer, Integer> getLevels() {
        return this.levels;
    }

    public SpellBook getSpellBook() {
        return this.spellBook;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EquipmentSetup)) return false;
        final EquipmentSetup other = (EquipmentSetup) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$creator = this.getCreator();
        final Object other$creator = other.getCreator();
        if (this$creator == null ? other$creator != null : !this$creator.equals(other$creator)) return false;
        final Object this$inventory = this.getInventory();
        final Object other$inventory = other.getInventory();
        if (this$inventory == null ? other$inventory != null : !this$inventory.equals(other$inventory)) return false;
        final Object this$equipment = this.getEquipment();
        final Object other$equipment = other.getEquipment();
        if (this$equipment == null ? other$equipment != null : !this$equipment.equals(other$equipment)) return false;
        final Object this$levels = this.getLevels();
        final Object other$levels = other.getLevels();
        if (this$levels == null ? other$levels != null : !this$levels.equals(other$levels)) return false;
        final Object this$spellBook = this.getSpellBook();
        final Object other$spellBook = other.getSpellBook();
        if (this$spellBook == null ? other$spellBook != null : !this$spellBook.equals(other$spellBook)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EquipmentSetup;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $creator = this.getCreator();
        result = result * PRIME + ($creator == null ? 43 : $creator.hashCode());
        final Object $inventory = this.getInventory();
        result = result * PRIME + ($inventory == null ? 43 : $inventory.hashCode());
        final Object $equipment = this.getEquipment();
        result = result * PRIME + ($equipment == null ? 43 : $equipment.hashCode());
        final Object $levels = this.getLevels();
        result = result * PRIME + ($levels == null ? 43 : $levels.hashCode());
        final Object $spellBook = this.getSpellBook();
        result = result * PRIME + ($spellBook == null ? 43 : $spellBook.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "EquipmentSetup(name=" + this.getName() + ", creator=" + this.getCreator() + ", inventory=" + this.getInventory() + ", equipment=" + this.getEquipment() + ", levels=" + this.getLevels() + ", spellBook=" + this.getSpellBook() + ")";
    }
}
