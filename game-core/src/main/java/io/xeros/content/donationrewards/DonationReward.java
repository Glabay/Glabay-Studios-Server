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

public record DonationReward(GameItem item, int price) {
    private static final String FILE = Server.getDataDirectory() + "/cfg/donation_rewards.json";
    private static List<DonationReward> rewardList;

    public static void load() throws IOException {
        rewardList = JsonUtil.fromJson(FILE, new TypeToken<>() {
        });
        if (rewardList == null || rewardList.size() != 6) {
            throw new IllegalStateException(FILE + " must specify 6 items.");
        }
    }

    public static List<DonationReward> getRewardList() {
        return Collections.unmodifiableList(rewardList);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DonationReward other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.price() != other.price()) return false;
        final Object this$item = this.item();
        final Object other$item = other.item();
        return Objects.equals(this$item, other$item);
    }

    private boolean canEqual(final Object other) {
        return other instanceof DonationReward;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.price();
        final Object $item = this.item();
        result = result * PRIME + ($item == null ? 43 : $item.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DonationReward(item=" + this.item() + ", price=" + this.price() + ")";
    }
}
