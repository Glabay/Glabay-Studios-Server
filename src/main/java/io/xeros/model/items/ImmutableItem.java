package io.xeros.model.items;

import com.google.common.base.Preconditions;
import io.xeros.model.definitions.ItemDef;
import io.xeros.util.Misc;
import lombok.Getter;

@Getter
public class ImmutableItem {
    public static String getItemsAsString(ImmutableItem... itemArray) {
        var string = new StringBuilder();
        for (int i = 0; i < itemArray.length; i++) {
            string.append(itemArray[i].getFormattedString());
            if (i != itemArray.length - 1) {
                string.append(", ");
            }
        }
        return string.toString();
    }

    public static ImmutableItem[] concatItemArray(ImmutableItem[] a, ImmutableItem[] b) {
        int aLen = a.length;
        int bLen = b.length;
        ImmutableItem[] c = new ImmutableItem[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private final int id;
    private final int amount;

    public ImmutableItem(int id, int amount) {
        Preconditions.checkState(amount > -1);
        this.id = id;
        this.amount = amount;
    }

    private ImmutableItem() {
        id = -1;
        amount = 0;
    }

    @Override
    public String toString() {
        ItemDef definition = ItemDef.forId(id);
        String name = definition == null ? "null" : definition.getName();
        return "ImmutableItem{" + "name=" + name + ", id=" + id + ", amount=" + Misc.insertCommas(String.valueOf(amount)) + '}';
    }

    public String getFormattedString() {
        return amount + " x " + ItemDef.forId(id).getName();
    }

    public ImmutableItem(int id) {
        this(id, 1);
    }

    public ImmutableItem(GameItem item) {
        this(item.id(), item.amount());
    }

    public ImmutableItem withAmount(int amount) {
        return new ImmutableItem(id, amount);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ImmutableItem other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getId() != other.getId()) return false;
        return this.getAmount() == other.getAmount();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ImmutableItem;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        result = result * PRIME + this.getAmount();
        return result;
    }
}
