package io.xeros.content.privatemessaging;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsListPlayerSaveEntry implements PlayerSaveEntry {
    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList("friends-list");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        List<FriendsListEntry> list = new ArrayList<>();
        String[] entries = value.split(";");
        if (entries.length == 0 || entries[0].length() == 0)
            return true;
        for (String entry : entries) {
            String[] data = entry.split(",");
            list.add(new FriendsListEntry(FriendType.valueOf(data[0]), data[1], ""));
        }

        player.getFriendsList().addFromSave(list);
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return player.getFriendsList().getRepository().getAll().values()
                .stream().map(it -> it.getType() + "," + it.getLoginName())
                .collect(Collectors.joining(";"));
    }

    @Override
    public void login(Player player) { }
}
