package io.xeros.model.definitions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.skills.herblore.PoisonedWeapon;
import io.xeros.model.SkillLevel;
import io.xeros.model.items.EquipmentModelType;
import io.xeros.util.JsonUtil;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ItemDef {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(ItemDef.class.getName());
    @Getter
    private static Map<Integer, ItemDef> definitions = null;
    private static final List<SkillLevel> EMPTY_REQUIREMENTS = Lists.newArrayList();

    public static void load() throws Exception {
        definitions = new HashMap<>();
        List<ItemDef> list = JsonUtil.fromYaml(Server.getDataDirectory() + "/cfg/item/item_definitions.yaml", new TypeReference<>() {});

        // Load regulars first
        list.forEach(it -> definitions.put(it.getId(), it));

        // Then update parent->children relationship
        list.forEach(def -> {
            if (def.parent != 0) {
                ItemDef parent = forId(def.parent);
                Preconditions.checkState(parent != null, "No parent definition for id={}, parent={}", def.id, def.parent);
                definitions.put(def.id, def.fromParent(parent));
            }
        });

        log.info("Loaded " + list.size() + " item definitions.");
    }

    public static ItemDef forId(int itemId) {
        Preconditions.checkState(definitions != null, "Item definitions weren't loaded.");
        return definitions.getOrDefault(itemId, builder().id(itemId).build());
    }

    public static ItemDefBuilder builderOf(ItemDef def) {
        ItemDefBuilder builder = new ItemDefBuilder().id(def.id).name(def.name).description(def.description).shopValue(def.shopValue).noteId(def.noteId).noted(def.noted).stackable(def.stackable).untradeable(def.untradeable).checkBeforeDrop(def.checkBeforeDrop).undroppable(def.undroppable).equipmentModelType(def.equipmentModelType).requirements(def.requirements);
        Preconditions.checkState(builder.build().equals(def));
        return builder;
    }

    @Getter
    private final int id;
    private final String name;
    @Getter
    private final String description;
    private final int shopValue;
    @Getter
    private final int noteId;
    @Getter
    private final boolean noted;
    private final boolean stackable;
    private final boolean untradeable;
    @Getter
    private final boolean checkBeforeDrop;
    private final boolean undroppable;
    @Getter
    private final boolean destroyable;
    @Getter
    private final EquipmentModelType equipmentModelType;
    private final List<SkillLevel> requirements;

    /**
     * A parent is an item definition from which this item definition will inherit
     * most values (shopValue, equipmentModelType, requirements). It will only inherit
     * values that are not set, i.e. shopValue must be zero to inherit, objects must be null.
     */
    private final int parent;

    public ItemDef(int id, String name, String description, int shopValue, int noteId, boolean noted, boolean stackable, boolean untradeable, boolean checkBeforeDrop, boolean undroppable, boolean destroyable, EquipmentModelType equipmentModelType, List<SkillLevel> requirements, int parent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shopValue = shopValue;
        this.noteId = noteId;
        this.noted = noted;
        this.stackable = stackable;
        this.untradeable = untradeable;
        this.checkBeforeDrop = checkBeforeDrop;
        this.undroppable = undroppable;
        this.destroyable = destroyable;
        this.equipmentModelType = equipmentModelType;
        this.requirements = requirements;
        this.parent = parent;
    }

    private ItemDef() {
        id = -1;
        name = "";
        description = "";
        shopValue = 0;
        noteId = 0;
        noted = false;
        stackable = false;
        untradeable = false;
        checkBeforeDrop = false;
        undroppable = false;
        destroyable = false;
        equipmentModelType = null;
        requirements = null;
        parent = 0;
    }

    @JsonIgnore
    public int getUnNotedIdIfNoted() {
        return isNoted() ? getNoteId() : getId();
    }

    @JsonIgnore
    public int getNotedItemIfAvailable() {
        return isNoted() ? getId() : getNoteId() > 0 ? getNoteId() : getId();
    }

    private ItemDef fromParent(ItemDef parent) {
        ItemDefBuilder b = new ItemDefBuilder();
        b.id(id);
        b.name(name);
        b.description(description);
        b.noteId(noteId);
        b.noted(noted);
        b.stackable(stackable);
        b.untradeable(untradeable);
        b.checkBeforeDrop(checkBeforeDrop);
        b.undroppable(undroppable);
        b.parent(this.parent);

        if (shopValue == 0)
            b.shopValue(parent.shopValue);
        if (equipmentModelType == null)
            b.equipmentModelType(parent.equipmentModelType);
        if (requirements == null)
            b.requirements(parent.requirements);

        return b.build();
    }

    public String getName() {
        if (name == null) {
            return "unknown item " + getId();
        }
        return name;
    }

    /**
     * @return the shop value. for noted items it grabs from the unnoted version to
     * enforce the prices between noted and unnoted.
     */
    public int getShopValue() {
        if (noted && !forId(noteId).isNoted()) {
            return forId(noteId).getShopValue();
        }
        return shopValue;
    }

    /**
     * Gets the actual shop value stored in the definition.
     * Doesn't enforce noted and unnoted having the same value.
     */
    public int getRawShopValue() {
        return shopValue;
    }

    public boolean isStackable() {
        return stackable || isNoted();
    }

    @JsonIgnore
    public boolean isTradable() {
        return !untradeable;
    }

    @JsonIgnore
    public boolean isDroppable() {
        return !undroppable;
    }

    public List<SkillLevel> getRequirements() {
        if (PoisonedWeapon.getOriginal(id).isPresent()) {
            return forId(PoisonedWeapon.getOriginal(id).get()).getRequirements();
        }
        return requirements == null ? EMPTY_REQUIREMENTS : requirements;
    }

    @Override
    public String toString() {
        return "ItemDef{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", shopValue=" + shopValue + ", noteId=" + noteId + ", noted=" + noted + ", stackable=" + stackable + ", untradeable=" + untradeable + ", checkBeforeDrop=" + checkBeforeDrop + ", undroppable=" + undroppable + ", equipmentModelType=" + equipmentModelType + ", requirements=" + requirements + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDef itemDef = (ItemDef) o;
        return id == itemDef.id && shopValue == itemDef.shopValue && noteId == itemDef.noteId && noted == itemDef.noted && stackable == itemDef.stackable && untradeable == itemDef.untradeable && checkBeforeDrop == itemDef.checkBeforeDrop && undroppable == itemDef.undroppable && Objects.equals(name, itemDef.name) && Objects.equals(description, itemDef.description) && equipmentModelType == itemDef.equipmentModelType && Objects.equals(requirements, itemDef.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, shopValue, noteId, noted, stackable, untradeable, checkBeforeDrop, undroppable, equipmentModelType, requirements);
    }

    public static class ItemDefBuilder {

        private int id;

        private String name;

        private String description;

        private int shopValue;

        private int noteId;

        private boolean noted;

        private boolean stackable;

        private boolean untradeable;

        private boolean checkBeforeDrop;

        private boolean undroppable;

        private boolean destroyable;

        private EquipmentModelType equipmentModelType;

        private List<SkillLevel> requirements;

        private int parent;

        ItemDefBuilder() {
        }

        public ItemDefBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ItemDefBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ItemDefBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public ItemDefBuilder shopValue(final int shopValue) {
            this.shopValue = shopValue;
            return this;
        }

        public ItemDefBuilder noteId(final int noteId) {
            this.noteId = noteId;
            return this;
        }

        public ItemDefBuilder noted(final boolean noted) {
            this.noted = noted;
            return this;
        }

        public ItemDefBuilder stackable(final boolean stackable) {
            this.stackable = stackable;
            return this;
        }

        public ItemDefBuilder untradeable(final boolean untradeable) {
            this.untradeable = untradeable;
            return this;
        }

        public ItemDefBuilder checkBeforeDrop(final boolean checkBeforeDrop) {
            this.checkBeforeDrop = checkBeforeDrop;
            return this;
        }

        public ItemDefBuilder undroppable(final boolean undroppable) {
            this.undroppable = undroppable;
            return this;
        }

        public ItemDefBuilder destroyable(final boolean destroyable) {
            this.destroyable = destroyable;
            return this;
        }

        public ItemDefBuilder equipmentModelType(final EquipmentModelType equipmentModelType) {
            this.equipmentModelType = equipmentModelType;
            return this;
        }

        public ItemDefBuilder requirements(final List<SkillLevel> requirements) {
            this.requirements = requirements;
            return this;
        }

        public ItemDefBuilder parent(final int parent) {
            this.parent = parent;
            return this;
        }

        public ItemDef build() {
            return new ItemDef(this.id, this.name, this.description, this.shopValue, this.noteId, this.noted, this.stackable, this.untradeable, this.checkBeforeDrop, this.undroppable, destroyable, this.equipmentModelType, this.requirements, this.parent);
        }
    }

    public static ItemDefBuilder builder() {
        return new ItemDefBuilder();
    }
}
