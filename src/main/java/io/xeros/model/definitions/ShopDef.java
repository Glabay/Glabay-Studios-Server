package io.xeros.model.definitions;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.model.shops.NamedShopItem;
import io.xeros.model.shops.ShopItem;
import io.xeros.util.ItemConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class ShopDef {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShopDef.class);
    @Getter
    private static final Int2ObjectMap<ShopDef> definitions = new Int2ObjectOpenHashMap<>();
    private static final String DIRECTORY = Server.getDataDirectory() + "/cfg/shops/";

    public static void load() throws IOException {
        definitions.clear();
        loadFromDirectory(new File(DIRECTORY));
        log.info("Loaded {} shops.", definitions.size());
    }

    private static void loadFromDirectory(File directory) throws IOException {
        Preconditions.checkState(directory.isDirectory(), "Not a directory.");
        ItemConstants itemConstants = new ItemConstants().load();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                loadFromDirectory(file);
            } else {
                try {
                    _ShopDef shop = new ObjectMapper(new YAMLFactory()).readValue(file, _ShopDef.class);
                    Preconditions.checkState(!definitions.containsKey(shop.id), "Shop already present: " + shop + ", " + definitions.get(shop.id));

                    List<ShopItem> shopItems = shop.items.stream().map(item -> item.toShopItem(itemConstants)).toList();
                    List<ShopItem> items = Lists.newArrayList();

                    shopItems.forEach(item -> {
                        Preconditions.checkState(item.getPrice() != Integer.MAX_VALUE, "Don't use max int value for price, use something below it.");
                        items.add(new ShopItem(item.id() + 1, item.amount() == 0 ? 1_000_000_000 : item.amount(), item.getPrice()));
                    }); // Item amount is zero for almost all shops due to infinity
                    definitions.put(shop.id, ShopDef.builder()
                            .id(shop.id)
                            .items(items)
                            .name(shop.name)
                            .build());
                } catch (Exception e) {
                    System.err.println("Error in file " + file);
                    throw e;
                }
            }
        }
    }

    public static ShopDef get(int shopId) {
        return getDefinitions().get(shopId);
    }

    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final List<ShopItem> items;

    @Override
    public String toString() {
        return "ShopDef{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    public int getPrice(int itemId) {
        Optional<ShopItem> first = items.stream().filter(it -> it.id() - 1 == itemId).findFirst();
        return first.map(ShopItem::getPrice).orElse(Integer.MAX_VALUE);
    }

    /**
     * Shop definition with named items.
     */
    @Setter
    @Getter
    private static class _ShopDef {
        private int id;
        private String name;
        private List<NamedShopItem> items;
    }

    ShopDef(final int id, final String name, final List<ShopItem> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public static class ShopDefBuilder {

        private int id;

        private String name;

        private List<ShopItem> items;

        ShopDefBuilder() {}

        public ShopDef.ShopDefBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ShopDef.ShopDefBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ShopDef.ShopDefBuilder items(final List<ShopItem> items) {
            this.items = items;
            return this;
        }

        public ShopDef build() {
            return new ShopDef(this.id, this.name, this.items);
        }

        @Override
    public String toString() {
            return "ShopDef.ShopDefBuilder(id=" + this.id + ", name=" + this.name + ", items=" + this.items + ")";
        }
    }

    public static ShopDef.ShopDefBuilder builder() {
        return new ShopDef.ShopDefBuilder();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ShopDef other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (!Objects.equals(this$name, other$name)) return false;
        final Object this$items = this.getItems();
        final Object other$items = other.getItems();
        return Objects.equals(this$items, other$items);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ShopDef;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $items = this.getItems();
        result = result * PRIME + ($items == null ? 43 : $items.hashCode());
        return result;
    }
}
