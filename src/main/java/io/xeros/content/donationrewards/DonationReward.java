package io.xeros.content.donationrewards;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.items.GameItem;
import io.xeros.util.JsonUtil;
import lombok.Getter;

@Getter
public class DonationReward {
    private static final String FILE = Server.getDataDirectory() + "/cfg/donation_rewards.json";
    private static List<DonationReward> rewardList;

    public static void load() throws IOException {
        rewardList = JsonUtil.fromJson(FILE, new TypeToken<>() {});
        if (rewardList == null || rewardList.size() != 6) {
            throw new IllegalStateException(FILE + " must specify 6 items.");
        }
    }

    public static List<DonationReward> getRewardList() {
        return Collections.unmodifiableList(rewardList);
    }

    private final GameItem item;
    private final int price;

    public DonationReward(final GameItem item, final int price) {
        this.item = item;
        this.price = price;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DonationReward other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getPrice() != other.getPrice()) return false;
        final Object this$item = this.getItem();
        final Object other$item = other.getItem();
        return Objects.equals(this$item, other$item);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DonationReward;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getPrice();
        final Object $item = this.getItem();
        result = result * PRIME + ($item == null ? 43 : $item.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DonationReward(item=" + this.getItem() + ", price=" + this.getPrice() + ")";
    }
}
