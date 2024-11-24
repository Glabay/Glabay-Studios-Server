package io.xeros.model.entity.player;

import java.util.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.boosts.BoostType;
import io.xeros.content.boosts.Booster;
import io.xeros.content.boosts.Boosts;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.effects.damageeffect.impl.DragonfireShieldEffect;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.MagicRequirements;
import io.xeros.content.combat.magic.NonCombatSpellData;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.items.Degrade;
import io.xeros.content.items.Degrade.DegradableItem;
import io.xeros.content.items.pouch.RunePouch;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.minigames.inferno.Inferno;
import io.xeros.content.minigames.pk_arena.Highpkarena;
import io.xeros.content.minigames.pk_arena.Lowpkarena;
import io.xeros.content.skills.Cooking;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.SkillHandler;
import io.xeros.content.skills.crafting.BryophytaStaff;
import io.xeros.content.skills.crafting.CraftingData;
import io.xeros.content.skills.crafting.Enchantment;
import io.xeros.content.skills.mining.Mineral;
import io.xeros.content.skills.slayer.SlayerUnlock;
import io.xeros.content.skills.slayer.Task;
import io.xeros.content.skills.smithing.Smelting;
import io.xeros.content.skills.smithing.Smelting.Bars;
import io.xeros.content.skills.woodcutting.Tree;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.content.wildwarning.WildWarning;
import io.xeros.model.*;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.cycleevent.impl.WheatPortalEvent;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.entity.player.broadcasts.BroadcastType;
import io.xeros.model.items.EquipmentSet;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.items.bank.BankTab;
import io.xeros.model.lobby.Lobby;
import io.xeros.model.lobby.LobbyManager;
import io.xeros.model.lobby.LobbyType;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;
import io.xeros.model.projectile.Projectile;
import io.xeros.model.projectile.ProjectileEntity;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.model.world.Clan;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.net.outgoing.messages.ComponentVisibility;
import io.xeros.util.Misc;
import io.xeros.util.Stream;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

public class PlayerAssistant {

    public static int ALCH_WARNING_AMOUNT = 200_000;
    public final Player player;

    public PlayerAssistant(Player Client) {
        this.player = Client;
    }

    public void sendUpdateTimer() {
        if (player.getOutStream() != null) {
            long seconds = ((PlayerHandler.updateSeconds * 1000L) - (System.currentTimeMillis() - PlayerHandler.updateStartTime)) / 1000;
            player.getOutStream().createFrame(114);
            player.getOutStream().writeWordBigEndian((int) seconds * 50 / 30);
        }
    }

    public void sendDropTableData(String message, int npcIndex) {
        player.getOutStream().createFrameVarSize(197);
        player.getOutStream().writeString(message);
        player.getOutStream().writeDWord(npcIndex);
        player.getOutStream().endFrameVarSize();
        player.flushOutStream();
    }

    public void resetQuestInterface() {
        for (int i = 8145; i < 8196; i++)
            sendString("", i);
        for (int i = 21614; i < 21614 + 100; i++)
            sendString("", i);
    }

    public void openQuestInterface() {
        setScrollableMaxHeight(8140, 1600);
        resetScrollBar(8140);
        showInterface(8134);
    }

    public void openQuestInterface(String header, List<String> lines) {
        openQuestInterface(header, lines.toArray(new String[0]));
    }

    public void openQuestInterface(String header, String... lines) {
        Preconditions.checkArgument(lines.length <= 151, new IllegalArgumentException("Too many lines: " + lines.length));

        resetQuestInterface();
        sendString(header, 8144);

        int index = 0;
        for (int i = 8145; i < 8196; i++) {
            if (index >= lines.length) {
                break;
            }
            sendString(lines[index], i);
            index++;
        }

        for (int i = 21614; i < 21614 + 100; i++) {
            if (index >= lines.length) {
                break;
            }
            sendString(lines[index], i);
            index++;
        }


        setScrollableMaxHeight(8140, 218 + (lines.length > 10 ? (lines.length - 10) * 20 : 0));
        resetScrollBar(8140);

        showInterface(8134);
    }

    /**
     * Opens the new version of the quest interface, which uses a string container instead of individual lines.
     *
     * @param header The quest interface header.
     * @param lines  The lines of the interface.
     */
    public void openQuestInterfaceNew(String header, List<String> lines) {
        resetScrollBar(45_289);
        sendString(header, 45_288);
        sendStringContainer(45_290, lines);
        showInterface(45_285);
    }

    /**
     * Retribution
     *
     * @param i Player i
     * @return Return statement
     */
    private boolean checkRetributionReqsSingle(int i) {
        if (player.inPits && PlayerHandler.getPlayers().get(i).inPits || PlayerHandler.getPlayers().get(i).inDuel) {
            if (PlayerHandler.getPlayers().get(i) != null || i == player.getIndex())
                return true;
        }
        int combatDif1 = player.getCombatLevelDifference(PlayerHandler.getPlayers().get(i));
        if (PlayerHandler.getPlayers().get(i) == null || i != player.getIndex()
                || !PlayerHandler.getPlayers().get(i).getPosition().inWild() || combatDif1 > player.wildLevel
                || combatDif1 > PlayerHandler.getPlayers().get(i).wildLevel) {
            return false;
        }
        if (Configuration.SINGLE_AND_MULTI_ZONES) {
            if (!PlayerHandler.getPlayers().get(i).getPosition().inMulti()) {
                return PlayerHandler.getPlayers().get(i).underAttackByPlayer != player.getIndex()
                        && PlayerHandler.getPlayers().get(i).underAttackByPlayer > 0
                        && PlayerHandler.getPlayers().get(i).getIndex() != player.underAttackByPlayer && player.underAttackByPlayer > 0;
            }
        }
        return true;
    }

    public void openWebAddress(String address) {
        sendString(address, 12_000);
    }

    public void openGameframeTab(int tabId) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(24);
            player.getOutStream().writeByte(tabId);
            player.flushOutStream();
        }
    }

    public void sendPlayerObjectAnimation(GlobalObject obj, int animation) {
        sendPlayerObjectAnimation(obj.getX(), obj.getY(), animation, obj.getType(), obj.getFace());
    }

    public void sendPlayerObjectAnimation(int x, int y, int animation, int type, int orientation) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC(y - (player.mapRegionY * 8));
            player.getOutStream().writeByteC(x - (player.mapRegionX * 8));
            player.getOutStream().createFrame(160);
            player.getOutStream().writeByteS(((0 & 7) << 4) + (0 & 7));
            player.getOutStream().writeByteS((type << 2) + (orientation & 3));
            player.getOutStream().writeWordA(animation);
        }
    }

    public void sendSound(int soundId) {
        sendSound(soundId, SoundType.SOUND);
    }

    public void sendSound(int soundId, SoundType soundType) {
        sendSound(soundId, soundType, null);
    }

    public void sendSound(int soundId, SoundType soundType, Entity source) {
//		if (c.getOutStream() != null) {
//			if (c.debugMessage) {
//				c.sendMessage("Sent sound " + soundId);
//			}
//			c.getOutStream().createFrame(12);
//			c.getOutStream().writeUnsignedWord(soundId);
//			c.getOutStream().writeByte(soundType.ordinal());
//			int index = 0;
//			if (source != null) {
//				index = source.getIndex() + (source.isPlayer() ? Short.MAX_VALUE : 0);
//			}
//			c.getOutStream().writeUnsignedWord(index);
//		}
    }

    public void destroyInterface(ItemToDestroy itemToDestroy) {
        player.destroyItem = itemToDestroy;
        String itemName = ItemAssistant.getItemName(itemToDestroy.itemId());
        String[][] info = {
                {"Are you sure you want to " + itemToDestroy.type().getText() + " this item?", "14174"}, {"Yes.", "14175"},
                {"No.", "14176"}, {"There is no way to reverse this.", "14177"}, {"", "14182"},
                {"", "14183"}, {itemName, "14184"}};
        sendFrame34(itemToDestroy.itemId(), 0, 14171, 1);
        for (String[] anInfo : info)
            sendFrame126(anInfo[0], Integer.parseInt(anInfo[1]));
        sendChatboxInterface(14170);
    }

    public void destroyInterface(String config) {
        int itemId = player.destroyItem.itemId();
        String itemName = ItemAssistant.getItemName(itemId);
        String[][] info = { // The info the dialogue gives
                {"Are you sure you want to " + config + " this item?", "14174"}, {"Yes.", "14175"},
                {"No.", "14176"}, {"", "14177"}, {"If you wish to remove this confirmation,", "14182"},
                {"simply type '::toggle" + config + "'.", "14183"}, {itemName, "14184"}};
        sendFrame34(itemId, 0, 14171, 1);
        for (String[] anInfo : info)
            sendFrame126(anInfo[0], Integer.parseInt(anInfo[1]));
        sendChatboxInterface(14170);
    }

    public void imbuedHeart() {
        player.playerLevel[6] += imbuedHeartStats();
        player.getPA().refreshSkill(6);
        player.gfx0(1316);
        player.sendMessage("Your Magic has been temporarily raised.");
    }

    private int imbuedHeartStats() {
        int increaseBy;
        int skill = 6;
        increaseBy = (int) (player.getLevelForXP(player.playerXP[skill]) * .10);
        if (player.playerLevel[skill] + increaseBy > player.getLevelForXP(player.playerXP[skill]) + increaseBy + 1) {
            return player.getLevelForXP(player.playerXP[skill]) + increaseBy - player.playerLevel[skill];
        }
        return increaseBy;
    }

    public void assembleSlayerHelmet() {
        if (!player.getSlayer().getUnlocks().contains(SlayerUnlock.MALEVOLENT_MASQUERADE)) {
            player.sendMessage("You don't have the knowledge to do this. You must learn how to first.");
            return;
        }
        if (player.playerLevel[Player.playerCrafting] < 55) {
            player.sendMessage("@blu@You need a crafting level of 55 to assemble a slayer helmet.");
            return;
        }
        if (player.getItems().playerHasItem(4166) && player.getItems().playerHasItem(4168) && player.getItems().playerHasItem(4164)
                && player.getItems().playerHasItem(4551) && player.getItems().playerHasItem(8901)) {
            player.sendMessage("@blu@You assemble the pieces and create a full slayer helmet!");
            player.getItems().deleteItem(4166, 1);
            player.getItems().deleteItem(4164, 1);
            player.getItems().deleteItem(4168, 1);
            player.getItems().deleteItem(4551, 1);
            player.getItems().deleteItem(8901, 1);
            player.getItems().addItem(11864, 1);
        } else {
            player.sendMessage(
                    "You need a @blu@Facemask@bla@, @blu@Nose peg@bla@, @blu@Spiny helmet@bla@ and @blu@Earmuffs");
            player.sendMessage("@bla@in order to assemble a slayer helmet.");
        }
    }

    public void otherInv(Player c, Player o) {
        if (o == c || o == null || c == null)
            return;
        int[] backupItems = c.playerItems;
        int[] backupItemsN = c.playerItemsN;
        c.playerItems = o.playerItems;
        c.playerItemsN = o.playerItemsN;
        c.getItems().sendInventoryInterface(3214);
        c.playerItems = backupItems;
        c.playerItemsN = backupItemsN;
    }

    public void multiWay(int i1) {
        // synchronized(c) {
        player.outStream.createFrame(61);
        player.outStream.writeByte(i1);
        player.setUpdateRequired(true);
        player.setAppearanceUpdateRequired(true);

    }

    public void sendString(final int id, final String s) {
        sendString(s, id);
    }

    public void sendString(final String s, final int id) {
        sendFrame126(s, id);
    }

    /**
     * @author Grant_ | www.rune-server.ee/members/grant_ | 10/15/19
     */
    public void sendBroadCast(final String s) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(170);
            player.getOutStream().writeString(s);
            player.getOutStream().endFrameVarSizeWord();
            player.flushOutStream();
        }
    }

    /**
     * Latest
     *
     */
    public void sendBroadCast(Broadcast broadcast) {//
        BroadcastType type;//
        if (broadcast.getTeleport() != null)//
            type = BroadcastType.TELEPORT;//
        else if (broadcast.getUrl() != null)//
            type = BroadcastType.LINK;//
        else//
            type = BroadcastType.MESSAGE;//
        player.getOutStream().createFrameVarSizeWord(179);//
        player.getOutStream().writeByte(type.ordinal());//
        player.getOutStream().writeByte(broadcast.getIndex());//
        player.getOutStream().writeString(broadcast.getMessage());//
//
        if (type == BroadcastType.LINK)//
            player.getOutStream().writeString(broadcast.getUrl());//
        if (type == BroadcastType.TELEPORT) {//
            player.getOutStream().writeDWord(broadcast.getTeleport().getX());//
            player.getOutStream().writeDWord(broadcast.getTeleport().getY());//
            player.getOutStream().writeByte(broadcast.getTeleport().getHeight());//
        }//
        player.getOutStream().endFrameVarSizeWord();//
        player.flushOutStream();//
    }//

    public void sendURL(String URL) {
        sendFrame126(URL, 12000);
    }

    /**
     * Changes the main displaying sprite on an interface. The index represents the
     * location of the new sprite in the index of the sprite array.
     *
     * @param componentId the interface
     * @param index       the index in the array
     */
    public void sendChangeSprite(int componentId, byte index) {
        if (player == null || player.getOutStream() == null) {
            return;
        }
        Stream stream = player.getOutStream();
        stream.createFrame(7);
        stream.writeDWord(componentId);
        stream.writeByte(index);
        player.flushOutStream();
    }

    public static void sendItems(Player player, int componentId, List<GameItem> items, int capacity) {
        if (player == null || player.getOutStream() == null) {
            return;
        }
        player.getOutStream().createFrameVarSizeWord(53);
        player.getOutStream().writeUShort(componentId);
        int length = items.size();
        int current = 0;
        player.getOutStream().writeUShort(length);
        for (GameItem item : items) {
            if (item.amount() > 254) {
                player.getOutStream().writeByte(255);
                player.getOutStream().writeDWord_v2(item.amount());
            } else {
                player.getOutStream().writeByte(item.amount());
            }
            player.getOutStream().writeWordBigEndianA(item.id() + 1);
            current++;
        }
        for (; current < capacity; current++) {
            player.getOutStream().writeByte(1);
            player.getOutStream().writeWordBigEndianA(-1);
        }
        player.getOutStream().endFrameVarSizeWord();
        player.flushOutStream();
    }

    public void removeAllItems() {
        Arrays.fill(player.playerItems, 0);
        Arrays.fill(player.playerItemsN, 0);
        player.getItems().sendInventoryInterface(3214);
    }

    public void setConfig(int id, int state) {
        // synchronized (c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(36);
            player.getOutStream().writeWordBigEndian(id);
            player.getOutStream().writeByte(state);
            player.flushOutStream();
        }
        // }
    }

    public void sendInterfaceModel(int interfaceId, int itemId, int zoom) {
        sendItemOnInterface2(interfaceId, zoom, itemId);
    }

    public void sendItemOnInterface2(int interfaceId, int zoom, int itemId) {
        itemOnInterface(interfaceId, zoom, itemId);
    }

    public void itemOnInterface(int interfaceChild, int zoom, int itemId) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(246);
            player.getOutStream().writeWordBigEndian(interfaceChild);
            player.getOutStream().writeUShort(zoom);
            player.getOutStream().writeUShort(itemId);
            player.flushOutStream();
        }
    }

    public void itemOnInterface(GameItem item, int frame, int slot) {
        int id = item == null ? -1 : item.id();
        int amount = item == null ? 0 : item.amount();
        itemOnInterface(id, amount, frame, slot);
    }

    public void itemOnInterface(int item, int amount, int frame, int slot) {
        if (player.getOutStream() != null) {
            player.outStream.createFrameVarSizeWord(34);
            player.outStream.writeUShort(frame);
            if (item == 23231) {
                player.outStream.writeByte(slot);
            } else {
                player.outStream.writeDWord(slot);
            }
            player.outStream.writeUShort(item + 1);
            player.outStream.writeByte(255); //write byte of 255
            player.outStream.writeDWord(amount);
            player.outStream.endFrameVarSizeWord();
        }
    }

    public void sendTabAreaOverlayInterface(int interfaceId) {
        if (player.getOutStream() != null) {
            player.outStream.createFrame(142);
            player.outStream.writeUShort(interfaceId);
            player.flushOutStream();
        }
    }

    public void playerWalk(int x, int y) {
        PathFinder.getPathFinder().findRoute(player, x, y, true, 1, 1);
    }

    public void playerWalk(int x, int y, int xLength, int yLength) {
        PathFinder.getPathFinder().findRoute(player, x, y, true, xLength, yLength);
    }

    public Clan getClan() {
        if (Server.clanManager.clanExists(player.getLoginName())) {
            return Server.clanManager.getClan(player.getLoginName());
        }
        return null;
    }

    public void clearClanChat() {
        player.clanId = -1;
        player.getPA().sendString("Talking in: ", 18139);
        player.getPA().sendString("Owner: ", 18140);
        for (int j = 18144; j < 18244; j++) {
            player.getPA().sendString("", j);
        }
    }

    public void setClanData() {
        boolean exists = Server.clanManager.clanExists(player.getLoginName());
        if (!exists || player.clan == null) {
            sendString("Join chat", 18135);
            sendString("Talking in: Not in chat", 18139);
            sendString("Owner: None", 18140);
        }
        if (!exists) {
            sendString("Chat Disabled", 18306);
            String title = "";
            for (int id = 18307; id < 18317; id += 3) {
                if (id == 18307) {
                    title = "Anyone";
                } else if (id == 18310) {
                    title = "Anyone";
                } else if (id == 18313) {
                    title = "General+";
                } else if (id == 18316) {
                    title = "Only Me";
                }
                sendString(title, id + 2);
            }
            for (int index = 0; index < 100; index++) {
                sendString("", 18323 + index);
            }
            for (int index = 0; index < 100; index++) {
                sendString("", 18424 + index);
            }
            return;
        }
        Clan clan = Server.clanManager.getClan(player.getLoginName());
        sendString(clan.getTitle(), 18306);
        String title = "";
        for (int id = 18307; id < 18317; id += 3) {
            if (id == 18307) {
                title = clan.getRankTitle(clan.whoCanJoin)
                        + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
            } else if (id == 18310) {
                title = clan.getRankTitle(clan.whoCanTalk)
                        + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
            } else if (id == 18313) {
                title = clan.getRankTitle(clan.whoCanKick)
                        + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
            } else if (id == 18316) {
                title = clan.getRankTitle(clan.whoCanBan)
                        + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
            }
            sendString(title, id + 2);
        }
        if (clan.rankedMembers != null) {
            for (int index = 0; index < 100; index++) {
                if (index < clan.rankedMembers.size()) {
                    sendString("<clan=" + clan.ranks.get(index) + ">" + clan.rankedMembers.get(index), 18323 + index);
                } else {
                    sendString("", 18323 + index);
                }
            }
        }
        if (clan.bannedMembers != null) {
            for (int index = 0; index < 100; index++) {
                if (index < clan.bannedMembers.size()) {
                    sendString(clan.bannedMembers.get(index), 18424 + index);
                } else {
                    sendString("", 18424 + index);
                }
            }
        }
    }

    public void resetClickCast() {
        player.usingClickCast = false;
        player.setSpellId(-1);
    }

    public void resetAutocast() {
        resetAutocast(true);
    }

    //	c.autocasting = true;
//	c.autocastingDefensive = defensive;
//	c.autocastId = spellIndex;
//	updateConfig(c, spell);
    public void resetAutocast(boolean sendWeapon) {
        player.setSpellId(-1);
        player.autocastId = -1;
        player.autocastingDefensive = false;
        player.autocasting = false;
        player.getPA().sendConfig(CombatSpellData.AUTOCAST_CONFIG, 0);
        player.getPA().sendConfig(CombatSpellData.AUTOCAST_DEFENCE_CONFIG, 0);
        player.getPA().sendFrame36(108, 0);
        player.getPA().sendFrame36(109, 0);
        if (sendWeapon) {
            player.setSidebarInterface(0, 328);
            player.getItems().sendWeapon(player.playerEquipment[Player.playerWeapon]);
        }
        player.getPA().sendAutocastState(false);

        player.getCombatConfigs().updateWeaponModeConfig();
    }

    public void movePlayerUnconditionally(int x, int y, int h) {
        player.resetWalkingQueue();
        player.setTeleportToX(x);
        player.setTeleportToY(y);
        player.heightLevel = h;
        player.teleTimer = 4;
        requestUpdates();
        player.lastMove = System.currentTimeMillis();
    }

    public void movePlayer(int x, int y) {
        movePlayer(x, y, player.heightLevel);
    }

    public void movePlayer(int x, int y, int h) {
        if (player.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(player)) {
            if (!player.isDead) {
                return;
            }
        }
        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return;
        }
        if (player.morphed) {
            return;
        }
        if (player.getSlayer().superiorSpawned) {
            player.getSlayer().superiorSpawned = false;
        }
        player.resetWalkingQueue();
        player.attacking.reset();
        player.setTeleportToX(x);
        player.setTeleportToY(y);
        player.heightLevel = h;
        player.teleTimer = 2;
        requestUpdates();
    }

    public void forceMove(int x, int y, int h, boolean forXlog) {
        player.resetWalkingQueue();
        player.attacking.reset();
        player.setTeleportToX(x);
        player.setTeleportToY(y);
        player.heightLevel = h;
        player.teleTimer = 2;
        if (forXlog) {
            player.absX = x;
            player.absY = y;
            player.updateController();
        }
        requestUpdates();
    }

    public void movePlayer(Coordinate coord) {
        movePlayer(coord.getX(), coord.getY(), coord.getH());
    }

    public void stopSkilling() {
        Server.getEventHandler().stop(player, "skilling");
    }

    public void movePlayerDuel(int x, int y, int h) {
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                MultiplayerSessionType.DUEL);
        if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION
                && Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            return;
        }
        player.resetWalkingQueue();
        player.setTeleportToX(x);
        player.setTeleportToY(y);
        player.heightLevel = h;
        requestUpdates();
    }

    public void sendFrame126(long content, int id) {
        sendFrame126(Long.toString(content), id);
    }

    public void sendFrame126(int content, int id) {
        sendFrame126(Integer.toString(content), id);
    }

    public void sendFrame126(String s, int id) {
        sendFrame126(s, id, false);
    }

    public void sendFrame126(String s, int id, boolean alwaysUpdate) {
        if (s == null) {
            return;
        }
        if (!alwaysUpdate) {
            if (!checkPacket126Update(s, id) && !s.startsWith("www")
                    && !s.startsWith("http") && id != 12_000/* url id */) {
                return;
            }
        }

        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(126);
            player.getOutStream().writeString(s);
            player.getOutStream().writeWordA(id);
            player.getOutStream().endFrameVarSizeWord();
            player.flushOutStream();
        }

    }

    private final Map<Integer, TinterfaceText> interfaceText = new HashMap<>();

    public static class TinterfaceText {
        public int id;
        public String currentState;

        public TinterfaceText(String s, int id) {
            this.currentState = s;
            this.id = id;
        }

    }

    public boolean checkPacket126Update(String text, int id) {
        if (interfaceText.containsKey(id)) {
            TinterfaceText t = interfaceText.get(id);
            if (text.equals(t.currentState)) {
                return false;
            }
        }
        interfaceText.put(id, new TinterfaceText(text, id));
        return true;
    }


    public void sendEnterString(String header) {
        sendEnterString(header, null);
    }

    public void sendEnterString(String header, StringInput stringInputHandler) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(187);
            player.getOutStream().writeString(header);
            player.getOutStream().endFrameVarSizeWord();
            player.stringInputHandler = stringInputHandler;
        }
    }

    public void setSkillLevel(int skillNum, int currentLevel, int XP) {
        if (player != null && player.getOutStream() != null) {
            if (skillNum == 22) {    // Client hunter id, it's 21 in server
                return;
            }

            player.getOutStream().createFrame(134);
            if (skillNum == 21) {        // Server hunter id
                skillNum = 22;        // Convert to client hunter id
            }
            player.getOutStream().writeByte(skillNum);
            player.getOutStream().writeDWord_v1(XP);
            player.getOutStream().writeByte(currentLevel);
            player.flushOutStream();
        }
    }

    public void sendFrame106(int sideIcon) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(106);
            player.getOutStream().writeByteC(sideIcon);
            player.flushOutStream();
            requestUpdates();
        }
    }

    /**
     * Stops screen shaking
     */
    public void resetScreenShake() {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(107);
            player.flushOutStream();
        }
        // }
    }

    public void updateRunningToggle() {
        sendFrame36(173, player.isRunningToggled() ? 1 : 0);
    }

    public void sendFrame36(int id, int state) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(36);
            player.getOutStream().writeWordBigEndian(id);
            player.getOutStream().writeByte(state);
            player.flushOutStream();
        }

    }

    public void sendPlayerHeadOnInterface(int interfaceId) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(185);
            player.getOutStream().writeWordBigEndianA(interfaceId);
        }

    }

    public void sendLogout() {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(109);
            player.flushOutStream();
        }
    }

    public void resetScrollBar(int interfaceId) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(2);
            player.getOutStream().writeUShort(interfaceId);
            player.flushOutStream();
        }
    }

    public void setScrollableMaxHeight(int interfaceId, int scrollMax) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(3);
            player.getOutStream().writeUShort(interfaceId);
            player.getOutStream().writeUShort(scrollMax);
            player.flushOutStream();
        }
    }

    public void sendSprite(int id, int normal, int hover) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(98);
            player.getOutStream().writeUShort(id);
            player.getOutStream().writeUShort(normal);
            player.getOutStream().writeUShort(hover);
            player.flushOutStream();
        }
    }

    public void sendInterfaceAction(int id, boolean disabled) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(100);
            player.getOutStream().writeUShort(id);
            player.getOutStream().writeByte(disabled ? 1 : 0);
            player.flushOutStream();
        }
    }

    public void sendSprite(int id, int normal) {
        sendSprite(id, normal, normal);
    }

    @Getter
    public enum SceneType {
        OBJECT(0),
        NPC(1);

        private final int id;

        SceneType(int id) {
            this.id = id;
        }

    }


    public void sendSceneOptions(SceneType type, int id, String[] message) {

        String finalstring = Arrays.toString(message).replace("String[", "").replace(", ", "&").replace("]", "").replace("[", "");

        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSize(102);
            player.getOutStream().writeByte(type.id);
            player.getOutStream().writeUShort(id);
            player.getOutStream().writeString(finalstring);
            player.getOutStream().endFrameVarSize();
        }

    }


    public void showInterface(int interfaceid) {
        player.interruptActions();
        if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
            Server.getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return;
        }
        if (player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen() || player.viewingRunePouch) {
            player.sendMessage("You should stop what you are doing before continuing.");
            return;
        }
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(97);
            player.getOutStream().writeUShort(interfaceid);
            player.flushOutStream();
            player.openedInterface(interfaceid);
        }
    }

    public void sendFrame248(int MainFrame, int SubFrame) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(248);
            player.getOutStream().writeWordA(MainFrame);
            player.getOutStream().writeUShort(SubFrame);
            player.flushOutStream();
            player.setOpenInterface(MainFrame);
        }
    }

    public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(246);
            player.getOutStream().writeWordBigEndian(MainFrame);
            player.getOutStream().writeUShort(SubFrame);
            player.getOutStream().writeUShort(SubFrame2);
            player.flushOutStream();
        }
    }

    public PlayerAssistant sendInterfaceHidden(int interfaceId, boolean hide) {
        sendInterfaceHidden(hide ? 1 : 0, interfaceId);
        return this;
    }

    public void sendInterfaceHidden(int state, int componentId) {
        if (player.getPacketDropper().requiresUpdate(171, new ComponentVisibility(state, componentId))) {
            if (player.getOutStream() != null) {
                player.getOutStream().createFrame(171);
                player.getOutStream().writeByte(state);
                player.getOutStream().writeUShort(componentId);
                player.flushOutStream();
            }
        }
    }

    public void sendInterfaceAnimation(int interfaceId, Animation animation) {
        sendInterfaceAnimation(interfaceId, animation.getId());
    }

    public void sendInterfaceAnimation(int interfaceId, int animation) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(200);
            player.getOutStream().writeUShort(interfaceId);
            player.getOutStream().writeUShort(animation);
            player.flushOutStream();
        }
    }

    public void sendFrame70(int i, int o, int id) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(70);
            player.getOutStream().writeUShort(i);
            player.getOutStream().writeWordBigEndian(o);
            player.getOutStream().writeWordBigEndian(id);
            player.flushOutStream();
        }

    }

    public void sendNpcHeadOnInterface(int npcId, int interfaceId) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(75);
            player.getOutStream().writeWordBigEndianA(npcId);
            player.getOutStream().writeWordBigEndianA(interfaceId);
            player.flushOutStream();
        }

    }

    public void sendChatboxInterface(int Frame) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(164);
            player.getOutStream().writeWordBigEndian_dup(Frame);
            player.flushOutStream();
        }

    }

    public void sendFrame87(int id, int state) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(87);
            player.getOutStream().writeWordBigEndian_dup(id);
            player.getOutStream().writeDWord_v1(state);
            player.flushOutStream();
        }

    }

    public static void writeRightsGroup(Stream outStream, RightGroup rightsGroup) {
        List<Right> rightsSet = rightsGroup.getDisplayedRights();
        outStream.writeByte(rightsSet.size());
        for (Right right : rightsSet) {
            outStream.writeByte(right.ordinal());
        }
    }

    public void createPlayerHints(int type, int id) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(254);
            player.getOutStream().writeByte(type);
            player.getOutStream().writeUShort(id);
            player.getOutStream().write3Byte(0);
            player.flushOutStream();
        }
    }

    public void createObjectHints(int x, int y, int height, int pos) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(254);
            player.getOutStream().writeByte(pos);
            player.getOutStream().writeUShort(x);
            player.getOutStream().writeUShort(y);
            player.getOutStream().writeByte(height);
            player.flushOutStream();
        }

    }

    private void sendAutocastState(boolean state) {
        if (player == null || player.isDisconnected() || player.getSession() == null) {
            return;
        }
        Stream stream = player.getOutStream();
        if (stream == null) {
            return;
        }
        stream.createFrame(15);
        stream.writeByte(state ? 1 : 0);
        player.flushOutStream();
    }

    public void updateFriendOnlineStatus(String displayName, int world) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(50);
            player.getOutStream().writeQWord(Misc.playerNameToInt64(displayName));
            player.getOutStream().writeByte(world);
        }
    }

    public void addFriendOrIgnore(String displayName, boolean friend, int world) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSize(18);
            player.getOutStream().writeNullTerminatedString(displayName);
            player.getOutStream().writeByte(friend ? 1 : 0);
            if (friend)
                player.getOutStream().writeByte(world);
            player.getOutStream().endFrameVarSize();
            player.flushOutStream();
        }
    }

    public void sendPM(String displayName, RightGroup rightsGroup, byte[] chatMessage) {
        player.getOutStream().createFrameVarSize(196);
        player.getOutStream().writeNullTerminatedString(displayName);
        player.getOutStream().writeDWord(new Random().nextInt());
        writeRightsGroup(player.getOutStream(), rightsGroup);
        player.getOutStream().writeBytes(chatMessage, chatMessage.length, 0);
        player.getOutStream().endFrameVarSize();
    }

    public void sendFriendUpdatedDisplayName(String oldName, String newName) {
        player.getOutStream().createFrameVarSize(19);
        player.getOutStream().writeNullTerminatedString(oldName);
        player.getOutStream().writeNullTerminatedString(newName);
        player.getOutStream().endFrameVarSize();
    }

    public void removeAllWindows() {
        if (player != null && player.getOutStream() != null) {
            player.getPA().resetVariables();

            player.getOutStream().createFrame(219);
            player.flushOutStream();

            player.nextChat = 0;
            player.dialogueOptions = 0;
            player.closedInterface();
            player.setDialogueBuilder(null);
            player.stringInputHandler = null;
            player.amountInputHandler = null;
            player.setViewingCollectionLog(null);
        }
        resetVariables();
    }

    public void updatePoisonStatus() {
        sendConfig(1359, player.getHealth().getStatus().ordinal());
    }

    public void closeAllWindows() {
        closeAllWindows(true);
    }

    public void closeAllWindows(boolean sendPacket) {
        if (player != null && player.getOutStream() != null) {
            if (sendPacket) {
                player.getOutStream().createFrame(219);
                player.flushOutStream();
            }
            player.lastDialogueNewSystem = false;
            player.getAttributes().remove("dangerous_tele");
            player.inTradingPost = false;
            player.isBanking = false;
            player.viewingPresets = false;
            player.inPresets = false;
            player.setEnterAmountInterfaceId(0);
            player.getLootingBag().setWithdrawInterfaceOpen(false);
            player.getLootingBag().setDepositInterfaceOpen(false);
            player.getLootingBag().setSelectDepositAmountInterfaceOpen(false);
            player.viewingRunePouch = false;
            player.nextChat = 0;
            player.dialogueOptions = 0;
            player.placeHolderWarning = false;
            resetVariables();
            player.interruptActions();
            player.closedInterface();
            player.inBank = false;
            player.myShopId = 0;
        }
    }

    public void sendFrame34(int id, int slot, int column, int amount) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.outStream.createFrameVarSizeWord(34); // init item to smith screen
            player.outStream.writeUShort(column); // Column Across Smith Screen
            player.outStream.writeByte(4); // Total Rows?
            player.outStream.writeDWord(slot); // Row Down The Smith Screen
            player.outStream.writeUShort(id + 1); // item
            player.outStream.writeByte(amount); // how many there are?
            player.outStream.endFrameVarSizeWord();
        }

    }

    private int currentWalkableInterface;

    public boolean hasWalkableInterface() {
        return currentWalkableInterface != -1;
    }

    public void removeWalkableInterface() {
        walkableInterface(-1);
    }

    public void walkableInterface(int id) {
        // synchronized(c) {
        if (currentWalkableInterface == id && id != -2)
            return;

        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(208);
            player.getOutStream().writeUShort(id);
            player.flushOutStream();
            currentWalkableInterface = id;
        }
    }

    public void shakeScreen(int verticleAmount, int verticleSpeed, int horizontalAmount, int horizontalSpeed) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(35);
            player.getOutStream().writeByte(verticleAmount);
            player.getOutStream().writeByte(verticleSpeed);
            player.getOutStream().writeByte(horizontalAmount);
            player.getOutStream().writeByte(horizontalSpeed);
            player.flushOutStream();
        }
    }

    public void sendFrame99(int state) { // used for disabling map
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(99);
            player.getOutStream().writeByte(state);
            player.flushOutStream();
        }
    }

    /**
     * Reseting animations for everyone
     **/

    public void frame1() {
        // synchronized(c) {
        for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
            if (PlayerHandler.players[i] != null) {
                Player person = PlayerHandler.players[i];
                if (person != null) {
                    if (person.getOutStream() != null && !person.isDisconnected()) {
                        if (player.distanceToPoint(person.getX(), person.getY()) <= 25) {
                            person.getOutStream().createFrame(1);
                            person.flushOutStream();
                            person.getPA().requestUpdates();
                        }
                    }
                }

            }
        }
    }

    /**
     * Creating projectile
     **/
    public void createProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving,
                                 int startHeight, int endHeight, int lockon, int time) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
            player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
            player.getOutStream().createFrame(117);
            player.getOutStream().writeByte(angle);
            player.getOutStream().writeByte(offY);
            player.getOutStream().writeByte(offX);
            player.getOutStream().writeUShort(lockon);
            player.getOutStream().writeUShort(gfxMoving);
            player.getOutStream().writeByte(startHeight);
            player.getOutStream().writeByte(endHeight);
            player.getOutStream().writeUShort(time);
            player.getOutStream().writeUShort(speed);
            player.getOutStream().writeByte(16);
            player.getOutStream().writeByte(64);
            player.flushOutStream();
        }
    }

    public void createProjectile(int x, int y, int offX, int offY, int angle, int scale, int pitch, int speed, int gfxMoving,
                                 int startHeight, int endHeight, int lockon, int time) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
            player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
            player.getOutStream().createFrame(117);
            player.getOutStream().writeByte(angle);
            player.getOutStream().writeByte(offY);
            player.getOutStream().writeByte(offX);
            player.getOutStream().writeUShort(lockon);
            player.getOutStream().writeUShort(gfxMoving);
            player.getOutStream().writeByte(startHeight);
            player.getOutStream().writeByte(endHeight);
            player.getOutStream().writeUShort(time);
            player.getOutStream().writeUShort(speed);
            player.getOutStream().writeByte(pitch);
            player.getOutStream().writeByte(scale);
            player.flushOutStream();
        }
    }

    public void sendProjectile(int startX, int startY, int destX, int destY, int angle, int duration, int gfxMoving, int startHeight, int endHeight, int lockon, int startDelay, int slope, int creatorSize, int startDistanceOffset) {

    }


    public void sendProjectile(Projectile projectile) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC((projectile.getStart().getY() - (player.getMapRegionY() * 8)));
            player.getOutStream().writeByteC((projectile.getStart().getX() - (player.getMapRegionX() * 8)));
            player.getOutStream().createFrame(117);
            player.getOutStream().writeByte(projectile.getCurve()); // Unknown
            player.getOutStream().writeByte(projectile.getOffset().getX());
            player.getOutStream().writeByte(projectile.getOffset().getY());
            player.getOutStream().writeUShort(projectile.getLockon());
            player.getOutStream().writeUShort(projectile.getProjectileId());
            player.getOutStream().writeByte(projectile.getStartHeight());
            player.getOutStream().writeByte(projectile.getEndHeight());
            player.getOutStream().writeUShort(projectile.getDelay());
            player.getOutStream().writeUShort(projectile.getSpeed());
            player.getOutStream().writeByte(projectile.getPitch());
            player.getOutStream().writeByte(projectile.getScale()); // Unknown
            player.flushOutStream();
        }

    }

    private void createProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
            player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
            player.getOutStream().createFrame(117);
            player.getOutStream().writeByte(angle);
            player.getOutStream().writeByte(offY);
            player.getOutStream().writeByte(offX);
            player.getOutStream().writeUShort(lockon);
            player.getOutStream().writeUShort(gfxMoving);
            player.getOutStream().writeByte(startHeight);
            player.getOutStream().writeByte(endHeight);
            player.getOutStream().writeUShort(time);
            player.getOutStream().writeUShort(speed);
            player.getOutStream().writeByte(slope);
            player.getOutStream().writeByte(64);
            player.flushOutStream();
        }
    }

    public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving,
                                        int startHeight, int endHeight, int lockon, int time, int delay) {
        ProjectileEntity projectile = new ProjectileEntity(new Position(x, y, player.getHeight()), new Position(offX, offY), lockon, gfxMoving, delay, speed, startHeight, endHeight, angle, 1, 5);
        projectile.sendProjectile();
        System.out.println(projectile.getStart());
    }

    public void mysteryBoxItemOnInterface(int item, int amount, int frame, int slot) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(34);
            player.getOutStream().writeUShort(frame);
            player.getOutStream().writeByte(slot);
            player.getOutStream().writeUShort(item + 1);
            player.getOutStream().writeByte(255);
            player.getOutStream().writeDWord(amount);
            player.getOutStream().endFrameVarSizeWord();
        }
    }

    /**
     * Creates a projectile for players
     *
     * @param x           The x coordinate to fire the projectile to
     * @param y           The y coordinate to fire the projectile to
     * @param offX        The offset x of the projectile
     * @param offY        The offset y of the projectile
     * @param angle       The angle of the projectile
     * @param speed       The speed of the projectile. Higher speed is slower projectile
     * @param gfxMoving   ?
     * @param startHeight The start height of the projectile
     * @param endHeight   The end height of the projectile
     * @param lockon      The lock on target index
     * @param time        The time it takes for the projectile to fire. High time is longer delay
     */
    public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving,
                                        int startHeight, int endHeight, int lockon, int time) {
        PlayerHandler
                .nonNullStream()
                .filter(p -> p.distanceToPoint(x, y) <= 25)
                .filter(p -> p.getHeight() == player.getHeight())
                .filter(player::sameInstance)
                .forEach(p -> {
               /*     ProjectileEntity projectile = new ProjectileEntity(x, y, p.getHeight(), offX, offY, lockon, gfxMoving, 100, 100, startHeight, endHeight, angle, 1, 64, 5);
                    projectile.sendProjectile();*/
                });

    }


    public void createPlayersProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving,
                                         int startHeight, int endHeight, int lockon, int time, int slope) {
        PlayerHandler
                .nonNullStream()
                .filter(p -> p.distanceToPoint(x, y) <= 25)
                .filter(p -> p.getHeight() == player.getHeight())
                .filter(player::sameInstance)
                .forEach(p -> {
                    p.getPA().createProjectile2(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight,
                            lockon, time, slope);
                });
    }

    /**
     * * GFX
     **/
    public void stillGfx(int id, int x, int y, int height, int time) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC(y - (player.getMapRegionY() * 8));
            player.getOutStream().writeByteC(x - (player.getMapRegionX() * 8));
            player.getOutStream().createFrame(4);
            player.getOutStream().writeByte(0);
            player.getOutStream().writeUShort(id);
            player.getOutStream().writeByte(height);
            player.getOutStream().writeUShort(time);
            player.flushOutStream();
        }

    }

    public void useChargeSkills() {
        int[][] skillsNeck = {{11968, 11970, 5}, {11970, 11105, 4}, {11105, 11107, 3}, {11107, 11109, 2},
                {11109, 11111, 1}, {11111, 11113, 0}};
        for (int[] aSkillsNeck : skillsNeck) {
            if (player.operateEquipmentItemId == aSkillsNeck[0]) {
                if (player.isOperate) {
                    player.getItems().deleteItem(aSkillsNeck[0], 1);
                    player.getItems().addItem(aSkillsNeck[1], 1);
                }
                if (aSkillsNeck[2] > 1) {
                    player.sendMessage("Your amulet has " + aSkillsNeck[2] + " charges left.");
                } else {
                    player.sendMessage("Your amulet has " + aSkillsNeck[2] + " charge left.");
                }
            }
        }
        // c.getItems().updateSlot(c.playerAmulet);
        player.isOperate = false;
        player.operateEquipmentItemId = -1;
    }

    public void sendScreenFlash(int color, int maxIntensity) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(10);
            player.getOutStream().writeDWord(color);
            player.getOutStream().writeByte(maxIntensity);
            player.getOutStream().writeByte(0);
            player.flushOutStream();
        }
    }

    public void sendFog(boolean enabled, int fogStrength) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(243);
            player.getOutStream().writeByte(enabled ? 1 : 0);
            if (fogStrength > 85)
                fogStrength = 85;
            player.getOutStream().writeDWord(fogStrength);
            player.flushOutStream();
        }
    }

    // creates gfx for everyone
    public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
        for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
            Player p = PlayerHandler.players[i];
            if (p != null) {
                if (p.getOutStream() != null) {
                    if (p.distanceToPoint(x, y) <= 25) {
                        p.getPA().stillGfx(id, x, y, height, time);
                    }
                }
            }
        }
    }

    public void object(GlobalObject obj) {
        object(obj.getObjectId(), obj.getX(), obj.getY(), obj.getFace(), obj.getType(), false);
    }

    /**
     * Objects, add and remove
     **/
    public void object(int objectId, int objectX, int objectY, int face, int objectType, boolean flushOutStream) {
        // synchronized(c) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(85);
            player.getOutStream().writeByteC(objectY - (player.getMapRegionY() * 8));
            player.getOutStream().writeByteC(objectX - (player.getMapRegionX() * 8));
            player.getOutStream().createFrame(101);
            player.getOutStream().writeByteC((objectType << 2) + (face & 3));
            player.getOutStream().writeByte(0);

            if (objectId != -1) { // removing
                player.getOutStream().createFrame(151);
                player.getOutStream().writeByteS(0);
                player.getOutStream().writeWordBigEndian(objectId);
                player.getOutStream().writeByteS((objectType << 2) + (face & 3));
            }
            if (flushOutStream) {
                player.flushOutStream();
            }
        }
    }

    @Getter
    private final Map<Integer, String> playerOptions = new HashMap<>();

    public void showOption(int i, int l, String s) {
        if (player.getOutStream() != null) {
            if (!s.equals(playerOptions.get(i))) {
                playerOptions.put(i, s);
                player.getOutStream().createFrameVarSize(104);
                player.getOutStream().writeByteC(i);
                player.getOutStream().writeByteA(l);
                player.getOutStream().writeString(s);
                player.getOutStream().endFrameVarSize();
                player.flushOutStream();
            }
        }
    }

    /**
     * Open bank
     **/
    public void sendFrame34a(int frame, int item, int slot, int amount) {
        player.outStream.createFrameVarSizeWord(34);
        player.outStream.writeUShort(frame);
        player.outStream.writeByte(slot);
        player.outStream.writeUShort(item + 1);
        player.outStream.writeByte(255);
        player.outStream.writeDWord(amount);
        player.outStream.endFrameVarSizeWord();
    }

    /**
     * @author Grant_ | www.rune-server.ee/members/grant_ | 10/6/19
     */
    public void sendItemToSlotWithOpacity(int frame, int item, int slot, int amount, boolean opaque) {
        final int bitpackedValue = opaque ? setBit(15, item + 1) : item + 1;
        player.outStream.createFrameVarSizeWord(34);
        player.outStream.writeUShort(frame);
        player.outStream.writeByte(slot);
        player.outStream.writeUShort(bitpackedValue);
        player.outStream.writeByte(255);
        player.outStream.writeDWord(amount);
        player.outStream.endFrameVarSizeWord();
    }

    public void sendStringContainer(int containerInterfaceId, List<String> strings) {
        String[] stringArray = new String[strings.size()];
        for (int index = 0; index < strings.size(); index++)
            stringArray[index] = strings.get(index);
        sendStringContainer(containerInterfaceId, stringArray);
    }

    public void sendStringContainer(int containerInterfaceId, String... strings) {
        if (player.outStream != null) {
            player.outStream.createFrameVarSizeWord(5);
            player.outStream.writeUShort(containerInterfaceId);
            player.outStream.writeUShort(strings.length);
            Arrays.stream(strings).forEach(string -> player.outStream.writeString(string));
            player.outStream.endFrameVarSizeWord();
            player.flushOutStream();
        }
    }

    private int setBit(int bit, int target) {
        // Create mask
        int mask = 1 << bit;
        // Set bit
        return target | mask;
    }

    public void sendInterfaceSet(int mainScreenInterface, int inventoryInterface) {
        player.getOutStream().createFrame(248);
        player.getOutStream().writeWordA(mainScreenInterface);
        player.getOutStream().writeUShort(inventoryInterface);
        player.openedInterface(mainScreenInterface);
        player.flushOutStream();
    }

    public boolean viewingOtherBank;
    private final BankTab[] backupTabs = new BankTab[9];

    public void resetOtherBank() {
        for (int i = 0; i < backupTabs.length; i++)
            player.getBank().setBankTab(i, backupTabs[i]);
        viewingOtherBank = false;
        removeAllWindows();
        player.isBanking = false;
        player.getBank().setCurrentBankTab(player.getBank().getBankTab()[0]);
        player.getItems().queueBankContainerUpdate();
        player.sendMessage("You are no longer viewing another players bank.");
    }

    public void openOtherBank(Player otherPlayer) {
        if (otherPlayer == null)
            return;

        if (player.getPA().viewingOtherBank) {
            player.getPA().resetOtherBank();
            return;
        }
        if (otherPlayer.getPA().viewingOtherBank) {
            player.sendMessage("That player is viewing another players bank, please wait.");
            return;
        }
        for (int i = 0; i < backupTabs.length; i++)
            backupTabs[i] = player.getBank().getBankTab(i);
        for (int i = 0; i < otherPlayer.getBank().getBankTab().length; i++)
            player.getBank().setBankTab(i, otherPlayer.getBank().getBankTab(i));
        player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
        viewingOtherBank = true;
        player.itemAssistant.openUpBank();
    }

    public void castVengeance() {
        player.usingMagic = true;
        if (player.playerLevel[1] < 40) {
            player.sendMessage("You need a defence level of 40 to cast this spell.");
            return;
        }
        if (System.currentTimeMillis() - player.lastCast < 30000) {
            player.sendMessage("You can only cast vengeance every 30 seconds.");
            return;
        }
        if (player.vengOn) {
            player.sendMessage("You already have vengeance casted.");
            return;
        }
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                MultiplayerSessionType.DUEL);
        if (!Objects.isNull(session)) {
            if (session.getRules().contains(DuelSessionRules.Rule.NO_MAGE)) {
                player.sendMessage("You can't cast this spell because magic has been disabled.");
                return;
            }
        }

        if (!MagicRequirements.checkMagicReqs(player, 55, true)) {
            return;
        }
        player.getPA().sendGameTimer(ClientGameTimer.VENGEANCE, TimeUnit.SECONDS, 30);
        player.startAnimation(8317);
        player.gfx100(726);
        addSkillXPMultiplied(112, 6, true);
        refreshSkill(6);
        player.vengOn = true;
        player.usingMagic = false;
        player.lastCast = System.currentTimeMillis();
    }

    /**
     * Magic on items
     **/
    public void alchemy(int itemId, String alch) {

        if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
            player.sendMessage("@cr10@There is no need to do this here.");
            return;
        }

        switch (alch) {
            case "low":
                if (System.currentTimeMillis() - player.alchDelay > 1000) {
                    for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            player.sendMessage("You cannot alch iron man amour.");
                            return;
                        }
                    }
                    for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            player.sendMessage("You cannot alch ultimate iron man amour.");
                            return;
                        }
                    }
                    for (int[] items : EquipmentSet.HC_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            player.sendMessage("You cannot alch hardcore iron man amour.");
                            return;
                        }
                    }
                    if (itemId == LootingBag.LOOTING_BAG || itemId == LootingBag.LOOTING_BAG_OPEN || itemId == RunePouch.RUNE_POUCH_ID) {
                        player.sendMessage("This kind of sorcery cannot happen.");
                        return;
                    }
                    if (!player.getItems().playerHasItem(itemId, 1) || itemId == 995) {
                        return;
                    }
                    if (!MagicRequirements.checkMagicReqs(player, 49, true)) {
                        return;
                    }
                    if (Boundary.isIn(player, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
                        player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.LOW_ALCH);
                    }
                    player.getItems().deleteItem(itemId, 1);
                    int amount = ShopAssistant.getItemShopValue(itemId) / 3;

                    if (BryophytaStaff.isWearingStaffWithCharge(player) && player.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)) {
                        amount *= 1.25;
                    }

                    player.getItems().addItem(995, amount);
                    player.startAnimation(CombatSpellData.MAGIC_SPELLS[49][2]);
                    player.gfx100(CombatSpellData.MAGIC_SPELLS[49][3]);
                    player.alchDelay = System.currentTimeMillis();
                    sendFrame106(6);
                    addSkillXPMultiplied(CombatSpellData.MAGIC_SPELLS[49][7], 6, true);
                    refreshSkill(6);
                }
                break;

            case "high":
                if (System.currentTimeMillis() - player.alchDelay > 2000) {
                    for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            player.sendMessage("You cannot alch iron man amour.");
                            break;
                        }
                    }
                    for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            player.sendMessage("You cannot alch ultimate iron man amour.");
                            break;
                        }
                    }
                    for (int[] items : EquipmentSet.HC_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            player.sendMessage("You cannot alch hardcore iron man amour.");
                            break;
                        }
                    }
                    if (itemId == LootingBag.LOOTING_BAG || itemId == LootingBag.LOOTING_BAG_OPEN || itemId == RunePouch.RUNE_POUCH_ID) {
                        player.sendMessage("This kind of sorcery cannot happen.");
                        break;
                    }
                    if (!player.getItems().playerHasItem(itemId, 1) || itemId == 995) {
                        break;
                    }
                    if (!MagicRequirements.checkMagicReqs(player, 50, true)) {
                        break;
                    }
                    player.getItems().deleteItem(itemId, 1);
                    int amount = (int) (ShopAssistant.getItemShopValue(itemId) * .75);

                    if (BryophytaStaff.isWearingStaffWithCharge(player) && player.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)) {
                        amount *= 1.25;
                    }

                    player.getItems().addItem(995, amount);
                    player.startAnimation(CombatSpellData.MAGIC_SPELLS[50][2]);
                    player.gfx100(CombatSpellData.MAGIC_SPELLS[50][3]);
                    player.alchDelay = System.currentTimeMillis();
                    sendFrame106(6);
                    addSkillXPMultiplied(CombatSpellData.MAGIC_SPELLS[50][7], 6, true);
                    refreshSkill(6);
                }
                break;
        }
    }

    public void magicOnItems(int itemId, int itemSlot, int spellId) {

        NonCombatSpellData.attemptDate(player, spellId);

        switch (spellId) {

            case 1173:
                NonCombatSpellData.superHeatItem(player, itemId);
                break;

            case 1162: // low alch
                if (ShopAssistant.getItemShopValue(itemId) >= ALCH_WARNING_AMOUNT && player.isAlchWarning()) {
                    player.destroyItem = new ItemToDestroy(itemId, itemSlot, DestroyType.LOW_ALCH);
                    player.getPA().destroyInterface("alch");
                    return;
                }
                alchemy(itemId, "low");
                break;

            case 1178: // high alch
                if (ShopAssistant.getItemShopValue(itemId) >= ALCH_WARNING_AMOUNT && player.isAlchWarning()) {
                    player.destroyItem = new ItemToDestroy(itemId, itemSlot, DestroyType.HIGH_ALCH);
                    player.getPA().destroyInterface("alch");
                    return;
                }
                alchemy(itemId, "high");
                break;

            case 1155: // Lvl-1 enchant sapphire
            case 1165: // Lvl-2 enchant emerald
            case 1176: // Lvl-3 enchant ruby
            case 1180: // Lvl-4 enchant diamond
            case 1187: // Lvl-5 enchant dragonstone
            case 6003: // Lvl-6 enchant onyx
            case 23649://lvl-7 enchant zenyte
                Enchantment.getSingleton().enchantItem(player, itemId, spellId);
                enchantBolt(spellId, itemId, 28);
                break;
        }
    }

    private final int[][] boltData = {{1155, 879, 9, 9236}, {1155, 9337, 17, 9240}, {1165, 9335, 19, 9237},
            {1165, 880, 29, 9238}, {1165, 9338, 37, 9241}, {1176, 9336, 39, 9239}, {1176, 9339, 59, 9242},
            {1180, 9340, 67, 9243}, {1187, 9341, 78, 9244}, {6003, 9342, 97, 9245}, {6003, 9341, 78, 9244},

            {1155, 21955, 9, 21932}, {1155, 21957, 17, 21934}, {1155, 21959, 19, 21936},
            {1155, 21961, 20, 21938}, {1155, 21963, 21, 21940}, {1165, 21965, 37, 21942}, {1176, 21967, 39, 21944},
            {1180, 21969, 67, 21946}, {1187, 21971, 78, 21948}, {6003, 21973, 97, 21950}};

    private void enchantBolt(int spell, int bolt, int amount) {
        for (int[] aBoltData : boltData) {
            if (spell == aBoltData[0]) {
                if (bolt == aBoltData[1]) {
                    switch (spell) {
                        case 1155:
                            if (!MagicRequirements.checkMagicReqs(player, 56, true)) {
                                return;
                            }
                            break;
                        case 1165:
                            if (!MagicRequirements.checkMagicReqs(player, 57, true)) {
                                return;
                            }
                            break;
                        case 1176:
                            if (!MagicRequirements.checkMagicReqs(player, 58, true)) {
                                return;
                            }
                            break;
                        case 1180:
                            if (!MagicRequirements.checkMagicReqs(player, 59, true)) {
                                return;
                            }
                            break;
                        case 1187:
                            if (!MagicRequirements.checkMagicReqs(player, 60, true)) {
                                return;
                            }
                            break;
                        case 6003:
                            if (!MagicRequirements.checkMagicReqs(player, 61, true)) {
                                return;
                            }
                            break;
                        case 23649:
                            if (!MagicRequirements.checkMagicReqs(player, 99, true)) {
                                return;
                            }
                            break;
                    }
                    if (!player.getItems().playerHasItem(aBoltData[1], amount))
                        amount = player.getItems().getItemAmount(bolt);
                    player.getItems().deleteItem(aBoltData[1], player.getItems().getInventoryItemSlot(aBoltData[1]), amount);
                    player.getPA().addSkillXP(aBoltData[2] * amount, 6, true);
                    player.getItems().addItem(aBoltData[3], amount);
                    player.getPA().sendFrame106(6);
                    return;
                }
            }
        }
    }

    public void resetTb() {
        player.teleBlockLength = 0;
        player.teleBlockStartMillis = 0;
    }

    public void resetFollowers() {
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (PlayerHandler.players[j] != null) {
                if (PlayerHandler.players[j].playerFollowingIndex == player.getIndex()) {
                    Player c = PlayerHandler.players[j];
                    c.getPA().resetFollow();
                }
            }
        }
    }

    public void sendSpecialAttack(int amount, int specialEnabled) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrame(204);
            player.getOutStream().writeByte(amount);
            player.getOutStream().writeByte(specialEnabled);
            player.flushOutStream();
        }
    }

    public void startLeverTeleport(int x, int y, int height) {
        Consumer<Player> teleport = plr -> {
            if (player.isTeleblocked()) {
                player.sendMessage("You are teleblocked and can't teleport.");
                return;
            }
            if (Boundary.isIn(player, Boundary.OUTLAST) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
                return;
            }
            if (player.jailEnd > 1) {
                return;
            }
            if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
                player.sendMessage("Please leave the outlast hut area to teleport.");
                return;
            }
            if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
                player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                return;
            }
            if (Boundary.isIn(player, Boundary.HESPORI) && !player.canLeaveHespori) {
                player.sendMessage("Hespori's magic prevents you from teleporting.");
                return;
            }
            if (Boundary.isIn(player, Boundary.ICE_PATH) || Boundary.isIn(player, Boundary.ICE_PATH_TOP) && player.heightLevel > 0) {
                player.sendMessage("The cold from the ice-path is preventing you from teleporting.");
                return;
            }
            if (player.getBankPin().requiresUnlock()) {
                player.getBankPin().open(2);
                return;
            }
            if (Boundary.isIn(player, Boundary.RAIDS)) {
                player.getPotions().resetOverload();
            }
            if (player.getSlayer().superiorSpawned) {
                player.getSlayer().superiorSpawned = false;
            }
            if (Lowpkarena.getState(player) != null || Highpkarena.getState(player) != null) {
                player.sendMessage("You can't teleport from a Pk event!");
                return;
            }
            if (!player.isDead && player.teleTimer == 0 && player.respawnTimer == -6) {
                if (player.playerAttackingIndex > 0 || player.npcAttackingIndex > 0)
                    player.attacking.reset();
                player.stopMovement();
                removeAllWindows();
                Inferno.reset(player);
                player.fightCavesWaveType = 0;
                player.waveId = 0;
                player.waveInfo = new int[3];
                player.teleX = x;
                player.teleY = y;
                player.faceUpdate(0);
                player.teleHeight = height;
                player.startAnimation(2140);
                player.teleTimer = 8;
                player.setTektonDamageCounter(0);
                if (player.getGlodDamageCounter() >= 80 || player.getIceQueenDamageCounter() >= 80) {
                    player.setGlodDamageCounter(79);
                    player.setIceQueenDamageCounter(79);
                }
                player.sendMessage("You pull the lever..");
            }
            player.getPA().stopSkilling();
            if (Server.getMultiplayerSessionListener().inAnySession(player)) {
                Server.getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            }
        };

        if (WildWarning.isWarnable(player, x, y, height)) {
            WildWarning.sendWildWarning(player, teleport);
            return;
        }
        teleport.accept(player);
    }

    public boolean morphPermissions() {
        if (player.morphed) {
            return false;
        }
        if (player.getPosition().inWild()) {
            return false;
        }
        if (!Boundary.isIn(player, Boundary.EDGEVILLE_PERIMETER)) {
            return false;
        }
        if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
            return false;
        }
        if (System.currentTimeMillis() - player.lastSpear < 3000) {
            player.sendMessage("You cannot do this now.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                    MultiplayerSessionType.DUEL);
            if (Objects.nonNull(session)) {
                player.sendMessage("You cannot do this here.");
                return false;
            }
        }
        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return false;
        }
        if (Boundary.isIn(player, Boundary.DAGANNOTH_MOTHER_HFTD)) {
            player.sendMessage("You cannot do this here.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.RFD)) {
            player.sendMessage("You cannot do this here.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
            player.sendMessage("You cannot do this here.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.ICE_PATH) || Boundary.isIn(player, Boundary.ICE_PATH_TOP) && player.heightLevel > 0) {
            player.sendMessage("You cannot do this here.");
            return false;
        }
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("You cannot do this now.");
            return false;
        }
        return true;
    }

    boolean flag;

    public void resetScrollPosition(int frame) {
        if (flag)
            player.getPA().sendFrame126(":scp: 0", frame);
        else
            player.getPA().sendFrame126(":scp: 00", frame);

        flag = !flag;
    }

    public boolean canTeleport() {
        return canTeleport("modern");
    }

    public boolean canTeleport(String teleportType) {
        if (!player.getController().canMagicTeleport(player) || player.getLock().cannotTeleport(player)) {
            player.sendMessage("You can't teleport right now.");
            return false;
        }

        if (Boundary.OURIANA_ALTAR.in(player) && !Boundary.OURIANA_ALTAR_BANK.in(player)) {
            player.sendMessage("A magical force blocks your teleport, you'll have to walk back.. OH NO!!!");
            return false;
        }
        if (TourneyManager.getSingleton().isInArenaBounds(player) || TourneyManager.getSingleton().isInLobbyBounds(player)) {
            player.sendMessage("You cannot teleport when in the tournament arena.");
            return false;
        }

        if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
            player.sendMessage("You cannot teleport when in the tournament arena.");
            return false;
        }
        if (player.morphed) {
            player.sendMessage("You cannot do this now.");
            return false;
        }

        if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
            player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return false;
        }

        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("You must finish what you're doing first.");
            return false;
        }

        if (player.isForceMovementActive()) {
            player.sendMessage("You can't teleport during forced movement!");
            return false;
        }

        if (Boundary.isIn(player, Boundary.HUNLLEF_BOSS_ROOM) && !player.hunllefDead) {
            player.sendMessage("The Hunllef's powers disable your teleport.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.RAIDROOMS)) {
            if (player.getRaidsInstance() != null) {
                player.sendMessage("Please use the stairs or type @red@::leaveraids!");
                return false;
            }
        }
        if (Boundary.isIn(player, Boundary.RFD)) {
            player.sendMessage("You cannot teleport from here, use the portal.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
            player.sendMessage("You cannot teleport out of fight caves.");
            return false;
        }
        if (Lowpkarena.getState(player) != null || Highpkarena.getState(player) != null) {
            player.sendMessage("You can't teleport from a Pk event!");
            return false;
        }
        if (Boundary.isIn(player, Boundary.ICE_PATH) || Boundary.isIn(player, Boundary.ICE_PATH_TOP) && player.heightLevel > 0) {
            player.sendMessage("The cold from the ice-path is preventing you from teleporting.");
            return false;
        }
        if (player.getPosition().isInJail() && !(player.getRights().isOrInherits(Right.MODERATOR))) {
            player.sendMessage("You cannot teleport out of jail.");
            return false;
        }
        if (player.isDead) {
            player.sendMessage("You can't teleport while dead!");
            return false;
        }
        if (player.getPosition().inWild() && !(player.getRights().isOrInherits(Right.MODERATOR))) {
            if (!teleportType.equals("glory") && !teleportType.equals("obelisk") && !teleportType.equals("pod")) {
                if (player.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
                    player.sendMessage("You can't teleport above level " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
                    player.getPA().closeAllWindows();
                    return false;
                }
            } else if (!teleportType.equals("obelisk")) {
                if (player.wildLevel > 30) {
                    player.sendMessage("You can't teleport above level 30 in the wilderness.");
                    player.getPA().closeAllWindows();
                    return false;
                }
            }
        }
        if (player.isTeleblocked()) {
            player.sendMessage("You are teleblocked and can't teleport.");
            return false;
        }

        if (Boundary.isIn(player, Boundary.HESPORI) && !player.canLeaveHespori) {
            player.sendMessage("Hespori's magic prevents you from teleporting.");
            return false;
        } else if (Boundary.isIn(player, Boundary.HESPORI) && player.canLeaveHespori) {
            player.canHarvestHespori = false;
            player.canLeaveHespori = false;
        }

        if (player.teleTimer != 0) {
            player.sendMessage("Please wait a few seconds before teleporting again.");
            return false;
        }

        if (player.respawnTimer != -6) {
            player.sendMessage("You must wait until you respawn to teleport.");
            return false;
        }

        if (Lowpkarena.getState(player) != null || Highpkarena.getState(player) != null) {
            player.sendMessage("You can't teleport from a Pk event!");
            return false;
        }
        if (Boundary.isIn(player, Boundary.OUTLAST) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
            player.sendMessage("You can't teleport from outlast.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS)) {
            player.sendMessage("You cannot do this right now.");
            return false;
        }
        if (player.jailEnd > 1) {
            player.sendMessage("You are still jailed!");
            return false;
        }
        if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
            player.sendMessage("Please leave the outlast hut area to teleport.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.HESPORI) && !player.canLeaveHespori) {
            player.sendMessage("Hespori's magic prevents you from teleporting.");
            return false;
        }

        return true;
    }

    public void startTeleport(Position position, String teleportType, boolean homeTeleport) {
        startTeleport(position.getX(), position.getY(), position.getHeight(), teleportType, homeTeleport);
    }

    public void spellTeleport(int x, int y, int height, boolean homeTeleport) {
        startTeleport(x, y, height, player.playerMagicBook == 1 ? "ancient" : "modern", homeTeleport);
    }

    public void startTeleport(int x, int y, int height, String teleportType) {
        startTeleportFinal(x, y, height, teleportType, false);
    }


    public void startTeleport(int x, int y, int height, String teleportType, boolean homeTeleport) {
        if (WildWarning.isWarnable(player, x, y, height)) {
            WildWarning.sendWildWarning(player, plr -> startTeleportFinal(x, y, height, teleportType, homeTeleport));
            return;
        }

        startTeleportFinal(x, y, height, teleportType, homeTeleport);
    }

    private void startTeleportFinal(int x, int y, int height, String teleportType, boolean homeTeleport) {
        player.isWc = false;
        if (player.isOverloading) {
            player.sendMessage("You cannot teleport while taking overload damage.");
            return;
        }
        if (player.isFping()) {
            /*
              Cannot do action while fping
             */
            player.sendMessage("You cannot teleport while fping.");

            return;
        }
        if (!canTeleport(teleportType)) {
            return;
        }

        if (player.stopPlayerSkill) {
            SkillHandler.resetPlayerSkillVariables(player);
            player.stopPlayerSkill = false;
        }

        resetVariables();
        SkillHandler.isSkilling[12] = false;

        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return;
        }

        if (Boundary.isIn(player, Boundary.RAIDS)) {
            player.getPotions().resetOverload();
        }

        if (player.getSlayer().superiorSpawned) {
            player.getSlayer().superiorSpawned = false;
        }

        if (player.playerAttackingIndex > 0 || player.npcAttackingIndex > 0) {
            player.attacking.reset();
        }
        player.stopMovement();
        removeAllWindows();
        player.teleX = x;
        player.teleY = y;
        player.faceUpdate(0);
        player.teleHeight = height;
        player.teleGfxTime = 9;
        player.teleGfxDelay = 0;
        player.teleSound = 0;
        player.setTektonDamageCounter(0);

        if (player.getGlodDamageCounter() >= 80 || player.getIceQueenDamageCounter() >= 80) {
            player.setGlodDamageCounter(79);
            player.setIceQueenDamageCounter(79);
        }

        if (teleportType.equalsIgnoreCase("modern") || teleportType.equals("glory")) {
            player.startAnimation(714);
            player.teleTimer = 11;
            player.teleGfxTime = 8;
            player.teleGfxDelay = 20;
            player.teleGfx = 308;
            player.teleEndAnimation = 715;
            player.teleSound = 200;
        } else if (teleportType.equalsIgnoreCase("ancient")) {
            player.startAnimation(1979);
            player.teleGfx = 0;
            player.teleTimer = 9;
            player.teleEndAnimation = 0;
            player.gfx0(392);
        } else if (teleportType.equalsIgnoreCase("pod")) {
            player.startAnimation(4544);
            player.teleEndAnimation = 65535;
            player.teleGfx = 0;
            player.teleTimer = 9;
            player.gfx0(767);

        } else if (teleportType.equals("obelisk")) {
            player.startAnimation(1816);
            player.teleTimer = 11;
            player.teleGfx = 661;
            player.teleEndAnimation = 65535;
        } else if (teleportType.equals("puropuro")) {
            player.startAnimation(6724);
            player.gfx0(1118);
            player.teleTimer = 13;
            player.teleEndAnimation = 65535;
        }
        if (!homeTeleport) {
            player.lastTeleportX = x;
            player.lastTeleportY = y;
            player.lastTeleportZ = height;
        }
        player.getPA().stopSkilling();
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            Server.getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
        }
    }

    public void startTeleport2(int x, int y, int height) {
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("You cannot teleport until you finish what you're doing.");
            return;
        }
        boolean inLobby = LobbyType.stream().anyMatch(lobbyType -> {
            Optional<Lobby> lobbyOpt = LobbyManager.get(lobbyType);
            return lobbyOpt.map(lobby -> lobby.inLobby(player)).orElse(false);
        });
        if (player.jailEnd > 1) {
            return;
        }
        if (inLobby) {
            player.sendMessage("You cannot teleport from here, please exit the way you entered!");
            return;
        }
        if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
            player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return;
        }
        if (player.morphed) {
            player.sendMessage("You cannot do this now.");
            return;
        }
        if (Boundary.isIn(player, Boundary.RAIDS)) {
            player.getPotions().resetOverload();
        }
        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return;
        }
        if (Boundary.isIn(player, Boundary.HUNLLEF_BOSS_ROOM) && !player.hunllefDead) {
            player.sendMessage("The Hunllef's powers disable your teleport.");
            return;
        }
        if (Boundary.isIn(player, Boundary.HESPORI) && !player.canLeaveHespori) {
            player.sendMessage("Hespori's magic prevents you from teleporting.");
            return;
        }
        if (Boundary.isIn(player, Boundary.RFD)) {
            player.sendMessage("You cannot teleport from here, use the portal.");
            return;
        }
        if (Boundary.isIn(player, Boundary.RAIDROOMS)) {
            if (player.getRaidsInstance() != null) {
                player.sendMessage("You cannot teleport out of the raids. Please use the stairs!");
                return;
            }
        }
        if (player.isTeleblocked()) {
            player.sendMessage("You are teleblocked and can't teleport.");
            return;
        }
        if (Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
            player.sendMessage("You cannot teleport out of fight caves.");
            return;
        }
        if (Boundary.isIn(player, Boundary.ICE_PATH) || Boundary.isIn(player, Boundary.ICE_PATH_TOP) && player.heightLevel > 0) {
            player.sendMessage("The cold from the ice-path is preventing you from teleporting.");
            return;
        }
        if (Lowpkarena.getState(player) != null || Highpkarena.getState(player) != null) {
            player.sendMessage("You can't teleport from a Pk event!");
            return;
        }
        if (player.isDead) {
            return;
        }
        if (player.teleTimer == 0) {
            player.stopMovement();
            removeAllWindows();
            player.teleX = x;
            player.teleY = y;
            player.faceUpdate(0);
            player.teleHeight = height;
            player.startAnimation(714);
            player.teleTimer = 9;
            player.teleGfx = 308;
            player.teleEndAnimation = 715;
            player.setTektonDamageCounter(0);
            if (player.getGlodDamageCounter() >= 80 || player.getIceQueenDamageCounter() >= 80) {
                player.setGlodDamageCounter(79);
                player.setIceQueenDamageCounter(79);
            }
            player.getPA().stopSkilling();
        }
    }

    public void setFollowingEntity(Entity entity, boolean combatFollowing) {
        resetFollow();
        player.combatFollowing = combatFollowing;
        if (entity.isNPC()) {
            player.npcFollowingIndex = entity.getIndex();
        } else {
            player.playerFollowingIndex = entity.getIndex();
        }
    }

    public void followNPC(NPC npc, boolean combat) {
        if (npc != null) {
            player.npcFollowingIndex = npc.getIndex();
            player.combatFollowing = combat;
        }
    }

    public void followNpc() {
        if (NPCHandler.npcs[player.npcFollowingIndex] == null || NPCHandler.npcs[player.npcFollowingIndex].isDead()) {
            player.npcFollowingIndex = 0;
            return;
        }
        if (player.morphed) {
            return;
        }
        if (player.freezeTimer > 0) {
            return;
        }
        if (player.isDead || player.playerLevel[3] <= 0)
            return;

        NPC npc = NPCHandler.npcs[player.npcFollowingIndex];
        int otherX = NPCHandler.npcs[player.npcFollowingIndex].getX();
        int otherY = NPCHandler.npcs[player.npcFollowingIndex].getY();
        double distanceToNpc = npc.getDistance(player.absX, player.absY);
        boolean withinDistance = distanceToNpc <= 2;

        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.npcFollowingIndex = 0;
            return;
        }

        player.faceUpdate(player.npcFollowingIndex);
        if (distanceToNpc <= 1) {
            if (!npc.insideOf(player.absX, player.absY)) {
                if (npc.getSize() == 0) {
                    stopDiagonal(otherX, otherY);
                }
                return;
            }
        }

        boolean projectile = false;
        if (player.combatFollowing) {
            projectile = player.usingOtherRangeWeapons || player.usingBow || player.usingMagic || player.autocasting
                    || player.getCombatItems().usingCrystalBow() || player.playerEquipment[Player.playerWeapon] == 22550;
            if (!projectile
                    || PathChecker.raycast(player, npc, true)
                    || PathChecker.raycast(npc, player, true)) {
                if (player.attacking.checkNpcAttackDistance(npc, player)) {
                    return;
                }
            }
        }

        final int x = player.absX;
        final int y = player.absY;
        final int z = player.heightLevel;
        final boolean inWater = npc.getNpcId() == 2042 || npc.getNpcId() == 2043 || npc.getNpcId() == 2044;

        if (npc.getFollowPosition() == null) {
            if (!inWater) {
                if (npc.getSize() == 1) {
                    Pair<Integer, Integer> tile = getFollowPosition(npc, otherX, otherY, projectile);
                    if (tile.getLeft() != 0 && tile.getRight() != 0) {
                        playerWalk(tile.getLeft(), tile.getRight());
                    } else {
                        playerWalk(otherX, otherY);
                    }
                } else {
                    double lowDist = 99999;
                    int lowX = 0;
                    int lowY = 0;
                    int x3 = otherX;
                    int y3 = otherY - 1;
                    final int loop = npc.getSize();

                    for (int k = 0; k < 4; k++) {
                        for (int i = 0; i < loop - (k == 0 ? 1 : 0); i++) {
                            if (k == 0) {
                                x3++;
                            } else if (k == 1) {
                                if (i == 0) {
                                    x3++;
                                }
                                y3++;
                            } else if (k == 2) {
                                if (i == 0) {
                                    y3++;
                                }
                                x3--;
                            } else if (k == 3) {
                                if (i == 0) {
                                    x3--;
                                }
                                y3--;
                            }

                            if (Misc.distance(x3, y3, x, y) < lowDist) {
                                boolean allowsInteraction = !projectile || PathChecker.raycast(player, npc, true);
                                boolean accessible = PathFinder.getPathFinder().accessable(player, x3, y3);

                                if (allowsInteraction && accessible) {
                                    lowDist = Misc.distance(x3, y3, x, y);
                                    lowX = x3;
                                    lowY = y3;
                                }
                            }
                        }
                    }


                    if (lowX != 0 && lowY != 0) {
                        PathFinder.getPathFinder().findRoute(player, lowX, lowY, true, 18, 18);
                        if (player.debugMessage) {
                            player.sendMessage("Path found to : " + lowX + ", " + lowY);
                        }
                    } else {
                        PathFinder.getPathFinder().findRoute(player, npc.absX, npc.absY, true, 18, 18);
                        if (player.debugMessage) {
                            player.sendMessage("No path found, reverting to npc position.");
                        }
                    }
                }
            } else {

                if (otherX == player.absX && otherY == player.absY) {
                    int r = Misc.random(3);
                    switch (r) {
                        case 0:
                            playerWalk(0, -1);
                            break;
                        case 1:
                            playerWalk(0, 1);
                            break;
                        case 2:
                            playerWalk(1, 0);
                            break;
                        case 3:
                            playerWalk(-1, 0);
                            break;
                    }
                } else if (player.isRunningToggled() && !withinDistance) {
                    if (otherY > player.getY() && otherX == player.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY - 1) + getMove(c.getY(),
                        // otherY - 1));
                        playerWalk(otherX, otherY - 1);
                    } else if (otherY < player.getY() && otherX == player.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY + 1) + getMove(c.getY(),
                        // otherY + 1));
                        playerWalk(otherX, otherY + 1);
                    } else if (otherX > player.getX() && otherY == player.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(),
                        // otherX - 1), 0);
                        playerWalk(otherX - 1, otherY);
                    } else if (otherX < player.getX() && otherY == player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), 0);
                        playerWalk(otherX + 1, otherY);
                    } else if (otherX < player.getX() && otherY < player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), getMove(c.getY(), otherY + 1) +
                        // getMove(c.getY(), otherY + 1));
                        playerWalk(otherX + 1, otherY + 1);
                    } else if (otherX > player.getX() && otherY > player.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(),
                        // otherX - 1), getMove(c.getY(), otherY - 1) +
                        // getMove(c.getY(), otherY - 1));
                        playerWalk(otherX - 1, otherY - 1);
                    } else if (otherX < player.getX() && otherY > player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), getMove(c.getY(), otherY - 1) +
                        // getMove(c.getY(), otherY - 1));
                        playerWalk(otherX + 1, otherY - 1);
                    } else if (otherX > player.getX() && otherY < player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), getMove(c.getY(), otherY - 1) +
                        // getMove(c.getY(), otherY - 1));
                        playerWalk(otherX + 1, otherY - 1);
                    }
                } else {
                    if (otherY > player.getY() && otherX == player.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY - 1));n
                        playerWalk(otherX, otherY - 1);
                    } else if (otherY < player.getY() && otherX == player.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY + 1));
                        playerWalk(otherX, otherY + 1);
                    } else if (otherX > player.getX() && otherY == player.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1), 0);
                        playerWalk(otherX - 1, otherY);
                    } else if (otherX < player.getX() && otherY == player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1), 0);
                        playerWalk(otherX + 1, otherY);
                    } else if (otherX < player.getX() && otherY < player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(),
                        // otherY + 1));
                        playerWalk(otherX + 1, otherY + 1);
                    } else if (otherX > player.getX() && otherY > player.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(),
                        // otherY - 1));
                        playerWalk(otherX - 1, otherY - 1);
                    } else if (otherX < player.getX() && otherY > player.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(),
                        // otherY - 1));
                        playerWalk(otherX + 1, otherY - 1);
                    } else if (otherX > player.getX() && otherY < player.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(),
                        // otherY + 1));
                        playerWalk(otherX - 1, otherY + 1);
                    }
                }
            }
        } else {
            playerWalk(npc.getFollowPosition().getX(), npc.getFollowPosition().getY());
        }
    }

    /**
     * Following
     **/

    public void followPlayer() {
        if (PlayerHandler.players[player.playerFollowingIndex] == null || PlayerHandler.players[player.playerFollowingIndex].isDead) {
            player.playerFollowingIndex = 0;
            return;
        }
        if (player.morphed) {
            return;
        }
        if (player.freezeTimer > 0) {
            return;
        }
        if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                    MultiplayerSessionType.DUEL);
            if (!Objects.isNull(session)) {
                if (session.getRules().contains(DuelSessionRules.Rule.NO_MOVEMENT)) {
                    player.playerFollowingIndex = 0;
                    return;
                }
            }
        }
        if (inPitsWait()) {
            player.playerFollowingIndex = 0;
        }

        if (player.isDead || player.getHealth().getCurrentHealth() <= 0)
            return;
        if (System.currentTimeMillis() - player.lastSpear < 3000) {
            player.sendMessage("You are stunned, you cannot move.");
            player.playerFollowingIndex = 0;
            return;
        }

        final Player other = PlayerHandler.players[player.playerFollowingIndex];
        final int otherX = PlayerHandler.players[player.playerFollowingIndex].getX();
        final int otherY = PlayerHandler.players[player.playerFollowingIndex].getY();

        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.playerFollowingIndex = 0;
            return;
        }
        if (player.combatFollowing) {
            boolean projectile = player.attacking.getCombatType() != CombatType.MELEE;
            if (!projectile
                    || PathChecker.raycast(player, other, true)
                    || PathChecker.raycast(other, player, true)) {

                if (player.attacking.checkPlayerAttackDistance(other, true, player)) {
                    player.stopMovement();
                    return;
                }

            }

            if (otherX == player.absX && otherY == player.absY) {
                if (!other.isWalkingQueueEmpty()) {
                    player.stopMovement();
                    return;
                }
            }

            Pair<Integer, Integer> tile = getFollowPosition(other, otherX, otherY, projectile);
            if (tile.getLeft() != 0 && tile.getRight() != 0) {
                playerWalk(tile.getLeft(), tile.getRight());
            } else {
                playerWalk(otherX, otherY);
            }

        } else {
            int followX = other.lastX;
            int followY = other.lastY;
            if (followX != 0 && followY != 0 && PathFinder.getPathFinder().accessable(player, followX, followY)) {
                playerWalk(followX, followY);
            } else if (Misc.distance(player.absX, player.absY, otherX, otherY) != 1.0) {
                Pair<Integer, Integer> tile = getFollowPosition(other, otherX, otherY, false);
                if (tile.getLeft() != 0 && tile.getRight() != 0) {
                    playerWalk(tile.getLeft(), tile.getRight());
                } else {
                    playerWalk(otherX, otherY);
                }
            }
        }

        player.faceUpdate(player.playerFollowingIndex + 32768);
    }

    public Pair<Integer, Integer> getFollowPosition(Entity other, int otherX, int otherY, boolean projectile) {
        int lowX = 0;
        int lowY = 0;
        double lowDist = 0;
        //boolean wild = other.getPosition().inWild();

        int[][] nondiags = {{0, 1}, {-1, 0}, {1, 0}, {0, -1},};
        for (int[] nondiag : nondiags) {
            int x2 = otherX + nondiag[0];
            int y2 = otherY + nondiag[1];
            double dist = Misc.distance(player.absX, player.absY, x2, y2);
            if (lowDist == 0 || dist < lowDist) {
                //if (wild == new Position(x2, y2).inWild()) {
                if (!projectile || PathChecker.raycast(player, other, true)
                        || PathChecker.raycast(other, player, true)) {
                    if (PathFinder.getPathFinder().accessable(player, x2, y2) && (player.combatFollowing || PathChecker.isMeleePathClear(player, x2, y2, player.heightLevel, otherX, otherY))) {
                        lowX = x2;
                        lowY = y2;
                        lowDist = dist;
                    }
                }
                //}
            }
        }

        return Pair.of(lowX, lowY);
    }

    public void updateRunEnergy() {
        sendFrame126(player.getRunEnergy() + "%", 22539);
    }

    public void sendStatement(String s) {
        sendFrame126(s, 357);
        sendFrame126("Click here to continue", 358);
        sendChatboxInterface(356);
    }

    public void resetFollow() {
        player.combatFollowing = false;
        player.playerFollowingIndex = 0;
        player.npcFollowingIndex = 0;
    }

    public void walkTo3(int i, int j) {
        player.newWalkCmdSteps = 0;
        if (++player.newWalkCmdSteps > 50)
            player.newWalkCmdSteps = 0;
        int k = player.absX + i;
        k -= player.mapRegionX * 8;
        player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = tmpNWCX[0] = tmpNWCY[0] = 0;
        int l = player.absY + j;
        l -= player.mapRegionY * 8;

        player.getNewWalkCmdX()[0] += k;
        player.getNewWalkCmdY()[0] += l;
        player.poimiY = l;
        player.poimiX = k;
    }

    private final int[] tmpNWCX = new int[50];
    private final int[] tmpNWCY = new int[50];

    public void walkTo(int i, int j) {
        player.newWalkCmdSteps = 0;
        if (++player.newWalkCmdSteps > 50)
            player.newWalkCmdSteps = 0;
        int k = player.getX() + i;
        k -= player.mapRegionX * 8;
        player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;
        int l = player.getY() + j;
        l -= player.mapRegionY * 8;

        for (int n = 0; n < player.newWalkCmdSteps; n++) {
            player.getNewWalkCmdX()[n] += k;
            player.getNewWalkCmdY()[n] += l;
        }
    }

    public void walkTo2(int i, int j) {
        if (player.freezeDelay > 0)
            return;
        player.newWalkCmdSteps = 0;
        if (++player.newWalkCmdSteps > 50)
            player.newWalkCmdSteps = 0;
        int k = player.getX() + i;
        k -= player.mapRegionX * 8;
        player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;
        int l = player.getY() + j;
        l -= player.mapRegionY * 8;

        for (int n = 0; n < player.newWalkCmdSteps; n++) {
            player.getNewWalkCmdX()[n] += k;
            player.getNewWalkCmdY()[n] += l;
        }
    }

    private void stopDiagonal(int otherX, int otherY) {
        if (player.freezeDelay > 0)
            return;
        if (player.freezeTimer > 0) // player can't move
            return;
        player.newWalkCmdSteps = 1;
        int xMove = otherX - player.getX();
        int yMove = 0;
        if (xMove == 0)
            yMove = otherY - player.getY();
        /*
         * if (!clipHor) { yMove = 0; } else if (!clipVer) { xMove = 0; }
         */

        int k = player.getX() + xMove;
        k -= player.mapRegionX * 8;
        player.getNewWalkCmdX()[0] = player.getNewWalkCmdY()[0] = 0;
        int l = player.getY() + yMove;
        l -= player.mapRegionY * 8;

        for (int n = 0; n < player.newWalkCmdSteps; n++) {
            player.getNewWalkCmdX()[n] += k;
            player.getNewWalkCmdY()[n] += l;
        }

    }

    public void requestUpdates() {
        // if (!c.initialized) {
        // return;
        // }
        player.setUpdateRequired(true);
        player.setAppearanceUpdateRequired(true);
    }

    /*
     * public void Obelisks(int id) { if (!c.getItems().playerHasItem(id)) {
     * c.getItems().addItem(id, 1); } }
     */

    public void levelUp(int skill) {
        int totalLevel = (getLevelForXP(player.playerXP[0]) + getLevelForXP(player.playerXP[1]) + getLevelForXP(player.playerXP[2])
                + getLevelForXP(player.playerXP[3]) + getLevelForXP(player.playerXP[4]) + getLevelForXP(player.playerXP[5])
                + getLevelForXP(player.playerXP[6]) + getLevelForXP(player.playerXP[7]) + getLevelForXP(player.playerXP[8])
                + getLevelForXP(player.playerXP[9]) + getLevelForXP(player.playerXP[10]) + getLevelForXP(player.playerXP[11])
                + getLevelForXP(player.playerXP[12]) + getLevelForXP(player.playerXP[13]) + getLevelForXP(player.playerXP[14])
                + getLevelForXP(player.playerXP[15]) + getLevelForXP(player.playerXP[16]) + getLevelForXP(player.playerXP[17])
                + getLevelForXP(player.playerXP[18]) + getLevelForXP(player.playerXP[19]) + getLevelForXP(player.playerXP[20])
                + getLevelForXP(player.playerXP[21]));
        sendFrame126("" + totalLevel, 3984);
        switch (skill) {
            case 0:
                sendFrame126("Congratulations, you just advanced an attack level!", 6248);
                sendFrame126("Your attack level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6249);
                player.sendMessage("Congratulations, you just advanced an attack level.");
                sendChatboxInterface(6247);
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 1:
                sendFrame126("Congratulations, you just advanced a defence level!", 6254);
                sendFrame126("Your defence level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6255);
                player.sendMessage("Congratulations, you just advanced a defence level.");
                sendChatboxInterface(6253);
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 2:
                sendFrame126("Congratulations, you just advanced a strength level!", 6207);
                sendFrame126("Your strength level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6208);
                player.sendMessage("Congratulations, you just advanced a strength level.");
                sendChatboxInterface(6206);
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 3:
                player.getHealth().setMaximumHealth(player.getLevelForXP(player.playerXP[Player.playerHitpoints]));
                sendFrame126("Congratulations, you just advanced a hitpoints level!", 6217);
                sendFrame126("Your hitpoints level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6218);
                player.sendMessage("Congratulations, you just advanced a hitpoints level.");
                sendChatboxInterface(6216);
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 4:
                player.sendMessage("Congratulations, you just advanced a ranging level.");
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 5:
                sendFrame126("Congratulations, you just advanced a prayer level!", 6243);
                sendFrame126("Your prayer level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6244);
                player.sendMessage("Congratulations, you just advanced a prayer level.");
                sendChatboxInterface(6242);
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 6:
                sendFrame126("Congratulations, you just advanced a magic level!", 6212);
                sendFrame126("Your magic level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6213);
                player.sendMessage("Congratulations, you just advanced a magic level.");
                sendChatboxInterface(6211);
                if (player.combatLevel >= 126) {
                    player.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 7:
                sendFrame126("Congratulations, you just advanced a cooking level!", 6227);
                sendFrame126("Your cooking level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6228);
                player.sendMessage("Congratulations, you just advanced a cooking level.");
                sendChatboxInterface(6226);
                break;

            case 8:
                sendFrame126("Congratulations, you just advanced a woodcutting level!", 4273);
                sendFrame126("Your woodcutting level is now " + getLevelForXP(player.playerXP[skill]) + ".", 4274);
                player.sendMessage("Congratulations, you just advanced a woodcutting level.");
                sendChatboxInterface(4272);
                break;

            case 9:
                sendFrame126("Congratulations, you just advanced a fletching level!", 6232);
                sendFrame126("Your fletching level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6233);
                player.sendMessage("Congratulations, you just advanced a fletching level.");
                sendChatboxInterface(6231);
                break;

            case 10:
                sendFrame126("Congratulations, you just advanced a fishing level!", 6259);
                sendFrame126("Your fishing level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6260);
                player.sendMessage("Congratulations, you just advanced a fishing level.");
                sendChatboxInterface(6258);
                break;

            case 11:
                sendFrame126("Congratulations, you just advanced a fire making level!", 4283);
                sendFrame126("Your firemaking level is now " + getLevelForXP(player.playerXP[skill]) + ".", 4284);
                player.sendMessage("Congratulations, you just advanced a fire making level.");
                sendChatboxInterface(4282);
                break;

            case 12:
                sendFrame126("Congratulations, you just advanced a crafting level!", 6264);
                sendFrame126("Your crafting level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6265);
                player.sendMessage("Congratulations, you just advanced a crafting level.");
                sendChatboxInterface(6263);
                break;

            case 13:
                sendFrame126("Congratulations, you just advanced a smithing level!", 6222);
                sendFrame126("Your smithing level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6223);
                player.sendMessage("Congratulations, you just advanced a smithing level.");
                sendChatboxInterface(6221);
                break;

            case 14:
                sendFrame126("Congratulations, you just advanced a mining level!", 4417);
                sendFrame126("Your mining level is now " + getLevelForXP(player.playerXP[skill]) + ".", 4438);
                player.sendMessage("Congratulations, you just advanced a mining level.");
                sendChatboxInterface(4416);
                break;

            case 15:
                sendFrame126("Congratulations, you just advanced a herblore level!", 6238);
                sendFrame126("Your herblore level is now " + getLevelForXP(player.playerXP[skill]) + ".", 6239);
                player.sendMessage("Congratulations, you just advanced a herblore level.");
                sendChatboxInterface(6237);
                break;

            case 16:
                sendFrame126("Congratulations, you just advanced a agility level!", 4278);
                sendFrame126("Your agility level is now " + getLevelForXP(player.playerXP[skill]) + ".", 4279);
                player.sendMessage("Congratulations, you just advanced an agility level.");
                sendChatboxInterface(4277);
                break;

            case 17:
                sendFrame126("Congratulations, you just advanced a thieving level!", 4263);
                sendFrame126("Your theiving level is now " + getLevelForXP(player.playerXP[skill]) + ".", 4264);
                player.sendMessage("Congratulations, you just advanced a thieving level.");
                sendChatboxInterface(4261);
                break;

            case 18:
                sendFrame126("Congratulations, you just advanced a slayer level!", 12123);
                sendFrame126("Your slayer level is now " + getLevelForXP(player.playerXP[skill]) + ".", 12124);
                player.sendMessage("Congratulations, you just advanced a slayer level.");
                sendChatboxInterface(12122);
                break;

            case 19:
                player.sendMessage("Congratulations! You've just advanced a Farming level.");
                break;

            case 20:
                sendFrame126("Congratulations, you just advanced a runecrafting level!", 4268);
                sendFrame126("Your runecrafting level is now " + getLevelForXP(player.playerXP[skill]) + ".", 4269);
                player.sendMessage("Congratulations, you just advanced a runecrafting level.");
                sendChatboxInterface(4267);
                break;

            case 21:
            case 22:
                player.sendMessage("Congratulations! You've just advanced a Hunter level.");
                break;
        }
        if (player.totalLevel >= 2000) {
            player.getEventCalendar().progress(EventChallenge.HAVE_2000_TOTAL_LEVEL);
        }

        if (player.getRights().isNotAdmin()) {
            if (getLevelForXP(player.playerXP[skill]) == 99) {
                Skill s = Skill.forId(skill);
                int iconId = Skill.iconForSkill(s) + 134;
                PlayerHandler.executeGlobalMessage(
                        "<col=6432a8>" + player.getDisplayNameFormatted() + " has reached level 99 <icon=" + iconId + "> " + s.toString() + " on " + player.getMode().getType().getFormattedName() + " mode!");
            }

            if (player.maxRequirements(player)) {
                PlayerHandler.executeGlobalMessage("<col=6432a8>" + player.getDisplayNameFormatted() + " has reached max total level on " + player.getMode().getType().getFormattedName() + " mode!");
            }
        }

        player.dialogueAction = 0;
        player.nextChat = 0;
    }

    public void refreshSkill(int i) {
        player.combatLevel = player.calculateCombatLevel();
        if (i == Player.playerHitpoints) {
            setSkillLevel(i, player.getHealth().getCurrentHealth(), player.playerXP[i]);
        } else {
            setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
        }
    }

    public void refreshSkills() {
        for (Skill skill : Skill.values())
            refreshSkill(skill.getId());
    }

    public int getXPForLevel(int level) {
        int points = 0;
        int output = 0;

        for (int lvl = 1; lvl <= level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level)
                return output;
            output = (int) (double) (points / 4);
        }
        return 0;
    }

    public int getLevelForXP(int exp) {
        int points = 0;
        int output;
        if (exp > 13034430)
            return 99;
        for (int lvl = 1; lvl <= 99; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) (double) (points / 4);
            if (output >= exp) {
                return lvl;
            }
        }
        return 0;
    }

    public boolean hasSkillLevels(SkillLevel... skillLevels) {
        return Arrays.stream(skillLevels).allMatch(skill -> getLevelForXP(player.playerXP[skill.skill().getId()]) >= skill.level());
    }

    public static class XpDrop {
        private int[] skill;
        private int amount;

        public XpDrop(int amount, int... skill) {
            this.skill = skill;
            this.amount = amount;
        }
    }

    private final List<XpDrop> xpDrops = new ArrayList<>();

    public void addXpDrop(XpDrop xpDrop) {
        if (xpDrop.amount <= 0)
            return;
        if (xpDrop.skill.length > 1) {
            throw new IllegalArgumentException("Only length of 1 is allowed for xp drop skill.");
        }

        for (Iterator<XpDrop> i = xpDrops.iterator(); i.hasNext(); ) {
            XpDrop drop = i.next();
            if (drop.skill == xpDrop.skill) {
                i.remove();
                xpDrops.add(new XpDrop(xpDrop.amount + drop.amount, drop.skill));
                return;
            }
        }

        xpDrops.add(xpDrop);
    }

    public void sendXpDrops() {
        List<XpDrop> send = new ArrayList<>();

        main:
        for (Iterator<XpDrop> i = xpDrops.iterator(); i.hasNext(); ) {
            XpDrop drop = i.next();
            i.remove();

            if (drop.skill[0] <= 6) {
                // Combine combat skill drops

                for (XpDrop dropSend : send) {
                    if (dropSend.skill[0] <= 6) {
                        for (int index = 0; index < dropSend.skill.length; index++) {
                            if (dropSend.skill[index] == drop.skill[0]) {
                                dropSend.amount += drop.amount;
                                continue main;
                            }
                        }

                        int[] newSkills = new int[dropSend.skill.length + 1];
                        System.arraycopy(dropSend.skill, 0, newSkills, 0, dropSend.skill.length);
                        dropSend.skill = newSkills;
                        dropSend.amount += drop.amount;
                        dropSend.skill[dropSend.skill.length - 1] = drop.skill[0];
                        continue main;
                    }
                }

                send.add(drop);
            } else {
                send.add(drop);
            }
        }

        for (Iterator<XpDrop> i = send.iterator(); i.hasNext(); ) {
            XpDrop drop = i.next();
            i.remove();
            player.getPA().sendExperienceDrop(true, drop.amount, drop.skill);
        }
    }

    public boolean addSkillXPMultiplied(double amount, int skill, boolean dropExperience) {
        return addSkillXP((int) (player.getMode().getType().getExperienceRate(Skill.forId(skill)) * amount), skill, dropExperience);
    }

    public boolean addSkillXP(int amount, int skill, boolean dropExperience) {
        if (amount <= 0)
            return false;
        if (player.skillLock[skill]) {
            return false;
        }
        if (TourneyManager.getSingleton().isInArena(player)) {
            return false;
        }

        if (Boundary.isIn(player, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
            return false;
        }
        if (player.expLock && skill <= 6) {
            return false;
        }
        if (amount + player.playerXP[skill] < 0) {
            return false;
        }

        List<? extends Booster<?>> boosts = Boosts.getBoostsOfType(player, Skill.forId(skill), BoostType.EXPERIENCE);
        if (!boosts.isEmpty()) {
            amount *= 1.5; // All boosts are 1.5 for now!
        }
        if (player.getMode().getType().isStandardRate(Skill.forId(skill))) {
            LeaderboardUtils.addCount(LeaderboardType.STANDARD_XP, player, amount);
        } else {
            LeaderboardUtils.addCount(LeaderboardType.ROGUE_XP, player, amount);
        }

        if (dropExperience) {
            addXpDrop(new XpDrop(amount, skill));
        }
        int oldLevel = getLevelForXP(player.playerXP[skill]);
        int oldExperience = player.playerXP[skill];
        if (oldExperience < Skill.MAX_EXP && oldExperience + amount >= Skill.MAX_EXP) {
            Skill s = Skill.forId(skill);
            int iconId = Skill.iconForSkill(s) + 134;
            player.xpMaxSkills += 1;
            if (player.getRights().isNotAdmin()) {
                PlayerHandler.executeGlobalMessage("<col=6432a8>" + player.getDisplayNameFormatted() + " has reached 200M XP in " +
                        "<icon=" + iconId + "> " + s.toString() + " on " + player.getMode().getType().getFormattedName() + " mode!");
                player.sendMessage("@blu@You have now maxed 200m experience in @red@" + player.xpMaxSkills + " skills!");

                if (player.xpMaxSkills > 21) {
                    PlayerHandler.executeGlobalMessage(
                            "<col=6432a8>" + player.getDisplayNameFormatted() + " has reached 200M XP in all skills on " + player.getMode().getType().getFormattedName() + " mode!");
                }
            }
        }

        if (player.playerXP[skill] + amount > Skill.MAX_EXP) {
            player.playerXP[skill] = Skill.MAX_EXP;
        } else {
            player.playerXP[skill] += amount;
        }

        if (player.playerXP[skill] >= Skill.MAX_EXP && player.gained200mTime[skill] == 0) {
            player.gained200mTime[skill] = System.currentTimeMillis();
        }

        if (oldLevel < getLevelForXP(player.playerXP[skill])) {
            int newLevel = getLevelForXP(player.playerXP[skill]);
            if (player.playerLevel[skill] < newLevel && skill != 3 && skill != 5)
                player.playerLevel[skill] = newLevel;

            player.combatLevel = player.calculateCombatLevel();
            player.totalLevel = player.getPA().calculateTotalLevel();
            player.getPA().sendFrame126("Combat Level: " + player.combatLevel, 3983);
            levelUp(skill);
            player.gfx100(199);
            if (skill == Skill.HITPOINTS.getId()) {
                player.getHealth().setMaximumHealth(newLevel);
                player.getHealth().increase(newLevel - oldLevel);
            }
            requestUpdates();
        }
        setSkillLevel(skill, player.playerLevel[skill], player.playerXP[skill]);
        refreshSkill(skill);
        return true;
    }

    private static final int[] Runes = {4740, 558, 560, 565};
    private static final int[] Pots = {};

    /**
     * Show an arrow icon on the selected player.
     *
     * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
     * @Param j - The player/Npc that the arrow will be displayed above.
     * @Param k - Keep this set as 0
     * @Param l - Keep this set as 0
     */
    public void drawHeadicon(int type, int index) {
        // synchronized(c) {
        player.outStream.createFrame(254);
        player.outStream.writeByte(type);

        int k = 0, l = 0;

        if (type == 1 || type == 10) {
            player.outStream.writeUShort(index);
            player.outStream.writeUShort(k);
            player.outStream.writeByte(l);
        } else {
            player.outStream.writeUShort(k);
            player.outStream.writeUShort(l);
            player.outStream.writeByte(index);
        }
    }

    public void removePlayerHintIcon() {
        displayPlayerHintIcon(-1);
    }

    public void displayPlayerHintIcon(int index) {
        player.outStream.createFrame(254);
        player.outStream.writeByte(10);
        player.outStream.writeUShort(index);
        player.outStream.writeUShort(0);
        player.outStream.writeByte(0);
    }

    public void displayStaticIcon(int type, int x, int y, int iconHeight) {
        if (iconHeight > 255)
            iconHeight = 255;
        player.outStream.createFrame(254);
        player.outStream.writeByte(type);
        player.outStream.writeUShort(x);
        player.outStream.writeUShort(y);
        player.outStream.writeByte(iconHeight);
    }

    public void runClientScript(int scriptId, Object... params) {
        if (player.getOutStream() != null) {

            // Clear interface text
            if (scriptId == 4) {
                int startInterfaceId = (Integer) params[0];
                int length = (Integer) params[1];
                for (int i = startInterfaceId; i < startInterfaceId + length; i++)
                    interfaceText.put(i, new TinterfaceText("", i));
            }

            player.getOutStream().createFrameVarSizeWord(13);
            StringBuilder types = new StringBuilder();
            Arrays.stream(params).forEach(it -> types.append(it instanceof Integer ? "i" : "s"));
            player.getOutStream().writeString(types.toString());

            for (Object param : params) {
                if (param instanceof Integer) {
                    player.getOutStream().writeDWord((int) param);
                } else {
                    player.getOutStream().writeNullTerminatedString(param.toString());
                }
            }

            player.getOutStream().writeUShort(scriptId);
            player.getOutStream().endFrameVarSizeWord();
            player.flushOutStream();
        }
    }

    public void updateQuestTab() {
        if (player.d1Complete) {
            sendFrame126("@gre@Varrock", 29480);
        } else {
            sendFrame126("@red@Varrock", 29480);
        }
        if (player.d2Complete) {
            sendFrame126("@gre@Ardougne", 29481);
        } else {
            sendFrame126("@red@Ardougne", 29481);
        }
        if (player.d3Complete) {
            sendFrame126("@gre@Desert", 29482);
        } else {
            sendFrame126("@red@Desert", 29482);
        }
        if (player.d4Complete) {
            sendFrame126("@gre@Falador", 29483);
        } else {
            sendFrame126("@red@Falador", 29483);
        }
        if (player.d5Complete) {
            sendFrame126("@gre@Fremnnik", 29484);
        } else {
            sendFrame126("@red@Fremnnik", 29484);
        }
        if (player.d6Complete) {
            sendFrame126("@gre@Kandarin", 29485);
        } else {
            sendFrame126("@red@Kandarin", 29485);
        }
        if (player.d7Complete) {
            sendFrame126("@gre@Karamja", 29486);
        } else {
            sendFrame126("@red@Karamja", 29486);
        }
        if (player.d8Complete) {
            sendFrame126("@gre@Lumbridge & Draynor", 29487);
        } else {
            sendFrame126("@red@Lumbridge & Draynor", 29487);
        }
        if (player.d9Complete) {
            sendFrame126("@gre@Morytania", 29488);
        } else {
            sendFrame126("@red@Morytania", 29488);
        }
        if (player.d10Complete) {
            sendFrame126("@gre@Western", 29489);
        } else {
            sendFrame126("@red@Western", 29489);
        }
        if (player.d11Complete) {
            sendFrame126("@gre@Wilderness", 29490);
        } else {
            sendFrame126("@red@Wilderness", 29490);
        }
    }

    public void handleGlory() {
        if (player.getPosition().inWild() && player.wildLevel > Configuration.JEWELERY_TELEPORT_MAX_WILD_LEVEL) {
            return;
        }
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("You cannot do that right now.");
            return;
        }
        player.getDH().sendOption4("Edgeville", "Karamja", "Draynor", "Al Kharid");
        player.sendMessage("You rub the amulet...");
        player.usingGlory = true;
    }

    public void handleSkills() {
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("You cannot do that right now.");
            return;
        }
        player.getDH().sendOption4("Land's End", "Piscarilius Mining", "Resource Area", "Beach Bank");
        player.sendMessage("You rub the amulet...");
        player.usingSkills = true;
    }

    public void resetVariables() {
        if (player.playerIsCrafting) {
            CraftingData.resetCrafting(player);
        }
        if (player.playerSkilling[9]) {
            player.playerSkilling[9] = false;
        }
        if (player.isBanking) {
            player.isBanking = false;
        }
        player.viewingRunePouch = false;
        if (player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen())
            player.getLootingBag().closeLootbag();
        player.viewingPresets = false;
        player.usingGlory = false;
        player.usingSkills = false;
        player.smeltInterface = false;
        if (player.dialogueAction > -1) {
            player.dialogueAction = -1;
        }
        if (player.teleAction > -1) {
            player.teleAction = -1;
        }
        if (player.battlestaffDialogue) {
            player.battlestaffDialogue = false;
        }
        if (player.craftDialogue) {
            player.craftDialogue = false;
        }
        player.closedInterface();
        CycleEventHandler.getSingleton().stopEvents(player, CycleEventHandler.Event.BONE_ON_ALTAR);
    }

    public boolean inPitsWait() {
        return player.getX() <= 2404 && player.getX() >= 2394 && player.getY() <= 5175 && player.getY() >= 5169;
    }

    public boolean checkForFlags() {
        int[][] itemsToCheck = {{995, 100000000}, {35, 5}, {667, 5}, {2402, 5}, {746, 5}, {4151, 150},
                {565, 100000}, {560, 100000}, {555, 300000}, {11235, 10}};
        for (int[] anItemsToCheck : itemsToCheck) {
            if (anItemsToCheck[1] < player.getItems().getTotalCount(anItemsToCheck[0]))
                return true;
        }
        return false;
    }

    public int calculateTotalLevel() {
        int total = 0;
        for (int i = 0; i <= 21; i++) {

            total += getLevelForXP(player.playerXP[i]);
        }
        return total;
    }

    public long getTotalXP() {
        return Arrays.stream(player.playerXP).asLongStream().sum();
    }

    public static boolean ringOfCharosTeleport(final Player player) {
        Task task = player.getSlayer().getTask().orElse(null);

        if (task == null) {
            player.sendMessage("You need a slayer task to use this.");
            return false;
        }
        if (player.getPosition().inWild()) {
            player.sendMessage("You cannot use this from the wilderness.");
            return false;
        }
        int x = task.getTeleportLocation()[0];
        int y = task.getTeleportLocation()[1];
        int z = task.getTeleportLocation()[2];
        if (x == -1 && y == -1 && z == -1) {
            player.sendMessage("This task cannot be easily teleported to.");
            return false;
        }

        player.sendMessage("You are teleporting to your task of " + task.getPrimaryName() + ".");
        player.getPA().startTeleport(x, y, z, "modern", false);
        return true;
    }

    public void useOperate(int itemId) {
        ItemDef def = ItemDef.forId(itemId);
        Optional<DegradableItem> d = DegradableItem.forId(itemId);
        if (d.isPresent()) {
            Degrade.checkPercentage(player, itemId);
            return;
        }
        switch (itemId) {
            case 9948: // Teleport to puro puro
            case 9949:
                if (WheatPortalEvent.xLocation > 0 && WheatPortalEvent.yLocation > 0) {
                    player.getPA().spellTeleport(WheatPortalEvent.xLocation + 1, WheatPortalEvent.yLocation + 1, 0, false);
                } else {
                    player.sendMessage("There is currently no portal available, wait 5 minutes.");
                    return;
                }
                break;
            case 12904:
                player.sendMessage(
                        "The toxic staff of the dead has " + player.getToxicStaffOfTheDeadCharge() + " charges remaining.");
                break;
            case 13199:
            case 13197:
                player.sendMessage("The " + def.getName() + " has " + player.getSerpentineHelmCharge() + " charges remaining.");
                break;
            case 11907:
            case 12899:
            case Items.SANGUINESTI_STAFF:
                int charge = itemId == 11907 ? player.getTridentCharge() : itemId == Items.SANGUINESTI_STAFF ? player.getSangStaffCharge() : player.getToxicTridentCharge();
                player.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
                break;
            case 12926:
                player.getCombatItems().checkBlowpipeShotsRemaining();
                break;
            case 19675:
                player.sendMessage("Your Arclight has " + player.getArcLightCharge() + " charges remaining.");
                break;
            case 12931:
                def = ItemDef.forId(itemId);
                if (def == null) {
                    return;
                }
                player.sendMessage("The " + def.getName() + " has " + player.getSerpentineHelmCharge() + " charge remaining.");
                break;

            case 13136:
                if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
                    player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                    return;
                }
                if (Server.getMultiplayerSessionListener().inAnySession(player)) {
                    player.sendMessage("You cannot do that right now.");
                    return;
                }
                if (player.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
                    player.sendMessage(
                            "You can't teleport above level " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
                    return;
                }
                player.getPA().spellTeleport(3426, 2927, 0, false);
                break;

            case 13125:
            case 13126:
            case 13127:
                if (player.getRunEnergy() < 100) {
                    if (player.getRechargeItems().useItem(itemId)) {
                        player.getRechargeItems().replenishRun(50);
                    }
                } else {
                    player.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13128:
                if (player.getRunEnergy() < 100) {
                    if (player.getRechargeItems().useItem(itemId)) {
                        player.getRechargeItems().replenishRun(100);
                    }
                } else {
                    player.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13117:
                if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
                    if (player.getRechargeItems().useItem(itemId)) {
                        player.getRechargeItems().replenishPrayer(4);
                    }
                } else {
                    player.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13118:
                if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
                    if (player.getRechargeItems().useItem(itemId)) {
                        player.getRechargeItems().replenishPrayer(2);
                    }
                } else {
                    player.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13119:
            case 13120:
                if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
                    if (player.getRechargeItems().useItem(itemId)) {
                        player.getRechargeItems().replenishPrayer(1);
                    }
                } else {
                    player.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13111:
                if (player.getRechargeItems().useItem(itemId)) {
                    player.getPA().spellTeleport(3236, 3946, 0, false);
                }
                break;
            case 10507:
                if (player.getItems().isWearingItem(10507)) {
                    if (System.currentTimeMillis() - player.lastPerformedEmote < 2500)
                        return;
                    player.startAnimation(6382);
                    player.gfx0(263);
                    player.lastPerformedEmote = System.currentTimeMillis();
                }
                break;
            case 10026:
                if (player.getItems().isWearingItem(10026)) {
                    if (System.currentTimeMillis() - player.lastPerformedEmote < 2500)
                        return;
                    player.startAnimation(2769);
                    player.lastPerformedEmote = System.currentTimeMillis();
                }
                break;
            case 20243:
                if (System.currentTimeMillis() - player.lastPerformedEmote < 2500)
                    return;
                player.startAnimation(7268);
                player.lastPerformedEmote = System.currentTimeMillis();
                break;

            case 4212:
            case 4214:
            case 4215:
            case 4216:
            case 4217:
            case 4218:
            case 4219:
            case 4220:
            case 4221:
            case 4222:
            case 4223:
                player.sendMessage("You currently have " + (250 - player.crystalBowArrowCount)
                        + " charges left before degradation to " + (player.playerEquipment[3] == 4223 ? "Crystal seed"
                        : ItemAssistant.getItemName(player.playerEquipment[3] + 1)));
                break;

            case 11864:
            case 11865:
            case 19639:
            case 19641:
            case 19643:
            case 19645:
            case 19647:
            case 19649:
                if (player.getSlayer().getTask().isEmpty()) {
                    player.sendMessage("You do not have a task!");
                    return;
                }
                player.sendMessage("I currently have @blu@" + player.getSlayer().getTaskAmount() + " "
                        + player.getSlayer().getTask().get().getPrimaryName() + "@bla@ to kill.");
                player.getPA().closeAllWindows();
                break;

            case 4202:
            case 9786:
            case 9787:
                ringOfCharosTeleport(player);
                break;

            case 11283:
            case 11284:
                if (Boundary.isIn(player, Boundary.ZULRAH) || Boundary.isIn(player, Boundary.CERBERUS_BOSSROOMS)
                        || Boundary.isIn(player, Boundary.SKOTIZO_BOSSROOM)) {
                    return;
                }
                DragonfireShieldEffect dfsEffect = new DragonfireShieldEffect();
                if (player.npcAttackingIndex <= 0 && player.playerAttackingIndex <= 0) {
                    return;
                }
                if (player.getHealth().getCurrentHealth() <= 0 || player.isDead) {
                    return;
                }
                if (dfsEffect.isExecutable(player)) {
                    Damage damage = new Damage(Misc.random(25));
                    if (player.playerAttackingIndex > 0) {
                        Player target = PlayerHandler.players[player.playerAttackingIndex];
                        if (Objects.isNull(target)) {
                            return;
                        }
                        player.attackTimer = 7;
                        dfsEffect.execute(player, target, damage);
                        player.setLastDragonfireShieldAttack(System.currentTimeMillis());
                    } else if (player.npcAttackingIndex > 0) {
                        NPC target = NPCHandler.npcs[player.npcAttackingIndex];
                        if (Objects.isNull(target)) {
                            return;
                        }
                        player.attackTimer = 7;
                        dfsEffect.execute(player, target, damage);
                        player.setLastDragonfireShieldAttack(System.currentTimeMillis());
                    }
                }
                break;

            case 1712:
            case 1710:
            case 1708:
            case 1706:
            case 19707:
                if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
                    player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                    return;
                }
                player.getPA().handleGlory();
                player.operateEquipmentItemId = itemId;
                player.isOperate = true;
                break;
            case 11968:
            case 11970:
            case 11105:
            case 11107:
            case 11109:
            case 11111:
                if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
                    player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                    return;
                }
                player.getPA().handleSkills();
                player.operateEquipmentItemId = itemId;
                player.isOperate = true;
                break;
            case 2552:
            case 2554:
            case 2556:
            case 2558:
            case 2560:
            case 2562:
            case 2564:
            case 2566:
                player.getPA().spellTeleport(3304, 3130, 0, false);
                break;

            /*
             * Max capes
             */
            case Items.COMPLETIONIST_CAPE:
            case 13280:
            case 13329:
            case 13337:
            case 21898:
            case 13331:
            case 13333:
            case 13335:
            case 20760:
            case 21285:
            case 21776:
            case 21778:
            case 21780:
            case 21782:
            case 21784:
            case 21786:
                player.getDH().sendDialogues(76, 1);
                break;

            /*
             * Crafting cape
             */
            case 9780:
            case 9781:
                player.getPA().startTeleport(2936, 3283, 0, "modern", false);
                break;

            /*
             * Magic skillcape
             */
            case 9762:
            case 9763:
                if (!Boundary.isIn(player, Boundary.EDGEVILLE_PERIMETER)) {
                    player.sendMessage("This cape can only be operated within the edgeville perimeter.");
                    return;
                }
                if (player.getPosition().inWild()) {
                    return;
                }
                if (player.playerMagicBook == 0) {
                    player.playerMagicBook = 1;
                    player.setSidebarInterface(6, 12855);
                    player.autocasting = false;
                    player.sendMessage("An ancient wisdomin fills your mind.");
                    player.getPA().resetAutocast();
                } else if (player.playerMagicBook == 1) {
                    player.sendMessage("You switch to the lunar spellbook.");
                    player.setSidebarInterface(6, 29999);
                    player.playerMagicBook = 2;
                    player.autocasting = false;
                    player.autocastId = -1;
                    player.getPA().resetAutocast();
                } else if (player.playerMagicBook == 2) {
                    player.setSidebarInterface(6, 1151);
                    player.playerMagicBook = 0;
                    player.autocasting = false;
                    player.sendMessage("You feel a drain on your memory.");
                    player.autocastId = -1;
                    player.getPA().resetAutocast();
                }
                break;
        }
    }

    public void getSpeared(int otherX, int otherY, int distance) {
        int x = player.absX - otherX;
        int y = player.absY - otherY;
        int xOffset = 0;
        int yOffset = 0;
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                MultiplayerSessionType.DUEL);
        if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION) {
            player.sendMessage("You cannot use this special whilst in the duel arena.");
            return;
        }
        if (x > 0) {
            if (player.getRegionProvider().getClipping(player.getX() + distance, player.getY(), player.heightLevel, 1, 0)) {
                xOffset = distance;
            }
        } else if (x < 0) {
            if (player.getRegionProvider().getClipping(player.getX() - distance, player.getY(), player.heightLevel, -1, 0)) {
                xOffset = -distance;
            }
        }
        if (y > 0) {
            if (player.getRegionProvider().getClipping(player.getX(), player.getY() + distance, player.heightLevel, 0, 1)) {
                yOffset = distance;
            }
        } else if (y < 0) {
            if (player.getRegionProvider().getClipping(player.getX(), player.getY() - distance, player.heightLevel, 0, -1)) {
                yOffset = -distance;
            }
        }
        moveCheck(xOffset, yOffset);
        player.lastSpear = System.currentTimeMillis();
    }

    private void moveCheck(int x, int y) {
        PathFinder.getPathFinder().findRoute(player, player.getX() + x, player.getY() + y, true, 1, 1);
    }

    /**
     * @author Jason MacKeigan (<a href="http://www.rune-server.org/members/jason">...</a>)
     * @since Sep 26, 2014, 12:57:42 PM
     */
    public enum PointExchange {
        PK_POINTS, VOTE_POINTS, BLOOD_POINTS
    }

    /**
     * Exchanges all items in the player owners inventory to a specific to whatever
     * the exchange specifies. Its up to the switch statement to make the
     * conversion.
     *
     * @param pointVar     the point exchange we're trying to make
     * @param itemId       the item id being exchanged
     * @param exchangeRate the exchange rate for each item
     */
    public void exchangeItems(PointExchange pointVar, int itemId, int exchangeRate) {
        try {
            int amount = player.getItems().getItemAmount(itemId);
            String pointAlias = Misc.capitalizeJustFirst(pointVar.name().toLowerCase().replaceAll("_", " "));
            if (exchangeRate <= 0 || itemId < 0) {
                throw new IllegalStateException();
            }
            if (amount <= 0) {
                player.getDH().sendStatement("You do not have the items required to exchange", "for " + pointAlias + ".");
                player.nextChat = -1;
                return;
            }
            int exchange = amount * exchangeRate;
            player.getItems().deleteItem2(itemId, amount);
            switch (pointVar) {
                case PK_POINTS:
                    player.pkp += exchange;
                    player.getQuestTab().updateInformationTab();
                    break;

                case VOTE_POINTS:
                    player.votePoints += exchange;
                    player.getQuestTab().updateInformationTab();
                    break;
                case BLOOD_POINTS:
                    player.bloodPoints += amount;
                    exchange = amount;
                    break;
            }
            player.getDH().sendStatement("You exchange " + amount + " currency for " + exchange + " " + pointAlias + ".");
            player.nextChat = -1;
        } catch (IllegalStateException exception) {
            Misc.println("WARNING: Illegal state has been reached.");
            exception.printStackTrace(System.err);
            System.out.println("PlayerAssistant - Check for error");
        }
    }

    /**
     * Sends some information to the client about screen fading.
     *
     * @param text    the text that will be displayed in the center of the screen
     * @param state   the state should be either 0, -1, or 1.
     * @param seconds the amount of time in seconds it takes for the fade to transition.
     *                <p>
     *                If the state is -1 then the screen fades from black to
     *                transparent. When the state is +1 the screen fades from
     *                transparent to black. If the state is 0 all drawing is stopped.
     */
    public void sendScreenFade(String text, int state, int seconds) {
        if (player == null || player.getOutStream() == null) {
            return;
        }
        if (seconds < 1 && state != 0) {
            throw new IllegalArgumentException("The amount of seconds cannot be less than one.");
        }
        player.getOutStream().createFrameVarSize(9);
        player.getOutStream().writeString(text);
        player.getOutStream().writeByte(state);
        player.getOutStream().writeByte(seconds);
        player.getOutStream().endFrameVarSize();
    }

    public void stillCamera(int x, int y, int height, int speed, int angle) {
        player.outStream.createFrame(177);
        player.outStream.writeByte(x / 64);
        player.outStream.writeByte(y / 64);
        player.outStream.writeUShort(height);
        player.outStream.writeByte(speed);
        player.outStream.writeByte(angle);
    }

    public void resetCamera() {
        player.outStream.createFrame(107);
        player.setUpdateRequired(true);
        player.appearanceUpdateRequired = true;
    }

    /*
     * c.getHealth().removeAllStatuses(); c.getHealth().reset(); c.setRunEnergy(99);
     * c.sendMessage("@red@Your hitpoints and run energy have been restored!"); if
     * (c.specRestore > 0) { c.sendMessage("You have to wait another " +
     * c.specRestore + " seconds to restore special."); } else { c.specRestore =
     * 120; c.specAmount = 10.0;
     * c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]); c.
     * sendMessage("Your special attack has been restored. You can restore it again in 3 minutes."
     * ); }
     */

    public static void switchSpellBook(Player c) {
        switch (c.playerMagicBook) {
            case 0:
                c.playerMagicBook = 1;
                c.setSidebarInterface(6, 838);
                c.sendMessage("An ancient wisdomin fills your mind.");
                c.getPA().resetAutocast();
                break;
            case 1:
                c.sendMessage("You switch to the lunar spellbook.");
                c.setSidebarInterface(6, 29999);
                c.playerMagicBook = 2;
                c.getPA().resetAutocast();
                break;
            case 2:
                c.setSidebarInterface(6, 938);
                c.playerMagicBook = 0;
                c.sendMessage("You feel a drain on your memory.");
                c.getPA().resetAutocast();
                break;
        }
    }

    public static void refreshHealthWithoutPenalty(Player c) {
        c.getHealth().setCurrentHealth(c.getHealth().getMaximumHealth() + 2);
        c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]) + 2;
        c.startAnimation(645);
        c.setRunEnergy(100, true);
        c.getPA().refreshSkill(5);
        c.sendMessage("You recharge your hitpoints, prayer and run energy.");
    }

    public static void refreshSpecialAndHealth(Player c) {
        c.getHealth().removeAllStatuses();
        c.getHealth().reset();
        c.setRunEnergy(100, true);
        c.sendMessage("@red@Your hitpoints and run energy have been restored!");
        if (c.specRestore > 0) {
            c.sendMessage("You have to wait another " + c.specRestore + " seconds to restore special.");
        } else {
            c.specRestore = 120;
            c.specAmount = 10.0;
            c.getItems().addSpecialBar(c.playerEquipment[Player.playerWeapon]);
            c.sendMessage("Your special attack has been restored. You can restore it again in 3 minutes.");
        }
    }

    public void icePath() {
        int random = Misc.random(20);
        if (random == 5) {
            player.startAnimation(767);
            player.appendDamage(null, Misc.random(1) + 1, Hitmark.HIT);
            player.resetWalkingQueue();
            player.forcedChat("Ouch!");
        }
    }

    public static void noteItems(Player player, int item) {
        ItemDef definition = ItemDef.forId(item);

        if (definition.getNoteId() == 0 || definition.isNoted() || definition.getNoteId() > 22000) {
            player.sendMessage("This item can't be noted.");
            return;
        }

        if (!player.getItems().playerHasItem(item, 1)) {
            return;
        }
        for (int index = 0; index < player.playerItems.length; index++) {
            int amount = player.playerItemsN[index];
            if (player.playerItems[index] == item + 1 && amount > 0) {
                player.getItems().deleteItem(item, index, amount);
                player.getItems().addItem(item + 1, amount);
            }
        }
        player.getDH().sendStatement("You note all your " + definition.getName() + ".");
        player.nextChat = -1;
    }

    public static void decantHerbs(Player player, int item) {
        ItemDef definition = ItemDef.forId(item);

        if (definition.getNoteId() == 0 || definition.isNoted()) {
            return;
        }

        if ((!(item >= 199) || !(item <= 220)) && (!(item >= 249) || !(item <= 270)) && !(item == 2481) && !(item == 2998) && !(item == 3049) &&
                !(item == 3000) && !(item == 1942) && !(item == 1957) && !(item == 1982) && !(item == 3051) && !(item == 2485) &&
                !(item == 5986) && !(item == 5504) && !(item == 5982) && !(item == 1965) && !(item == 225) && !(item == 6010)) {
            player.sendMessage("The master farmer cannot assist you with this.");
            return;
        }
        for (int index = 0; index < player.playerItems.length; index++) {
            int amount = player.playerItemsN[index];
            if (player.playerItems[index] == item + 1 && amount > 0) {
                player.getItems().deleteItem(item, index, amount);
                player.getItems().addItem(item + 1, amount);
            }
        }
        player.getDH().sendStatement("You note all your " + definition.getName() + ".");
        player.nextChat = -1;
    }

    public static void decantResource(Player player, int item) {
        ItemDef definition = ItemDef.forId(item);

        if (definition.getNoteId() == 0 || definition.isNoted()) {
            return;
        }

        int cost = 0;
        if (!isSkillAreaItem(item)) {
            player.sendMessage("You can only note items that are resources obtained from skilling in this area.");
            return;
        }
        if (!player.getRights().isOrInherits(Right.REGULAR_DONATOR) && !player.getRechargeItems().hasItem(13111)) {
            int inventoryAmount = player.getItems().getItemAmount(item);
            if (inventoryAmount < 4) {
                player.sendMessage("You need at least 4 of this item to note it.");
                return;
            }
            cost = (int) Math.round(inventoryAmount / 4.0D);
            if (!player.getItems().playerHasItem(item, cost)) {
                return;
            }
            player.getItems().deleteItem2(item, cost);
        }
        for (int index = 0; index < player.playerItems.length; index++) {
            int amount = player.playerItemsN[index];
            if (player.playerItems[index] == item + 1 && amount > 0) {
                player.getItems().deleteItem(item, index, amount);
                player.getItems().addItem(item + 1, amount);
            }
        }
        if (!player.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
            player.getDH().sendStatement(
                    "You note most of your " + definition.getName() + " at the cost of " + cost + " resources.");
        } else {
            player.getDH().sendStatement("You note all your " + definition.getName() + ".");
        }
        player.nextChat = -1;
    }

    private static boolean isSkillAreaItem(int item) {
        for (Mineral m : Mineral.values()) {
            if (Misc.linearSearch(m.getMineralReturn().inclusives(), item) != -1) {
                return true;
            }
        }
        for (Tree t : Tree.values()) {
            if (t.getWood() == item)
                return true;
        }
        for (var fish : FishingSpotTypes.values())
            if (fish.getRawFishId() == item) return true;

//        for (int[] fish : Fishing.data)
//            if (fish[4] == item) return true;

        for (int cookFish : Cooking.fishIds) {
            if (cookFish == item)
                return true;
        }
        for (Bars b : Smelting.Bars.values()) {
            if (b.getBar() == item)
                return true;
        }
        return false;
    }

    public void sendEntityTarget(int state, Entity entity) {
        if (player.isDisconnected() || player.getOutStream() == null) {
            return;
        }
        Stream stream = player.getOutStream();
        stream.createFrameVarSize(222);
        stream.writeByte(state);
        if (state != 0) {
            stream.writeUShort(entity.getIndex());
            stream.writeUShort(entity.getHealth().getCurrentHealth());
            stream.writeUShort(entity.getHealth().getMaximumHealth());
        }
        stream.endFrameVarSize();
    }

    public void sendEnterAmount(int interfaceId) {
        sendEnterAmount(interfaceId, "Enter an amount");
    }

    public void sendEnterAmount(int interfaceId, String header) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(27);
            player.getOutStream().writeString(header);
            player.setEnterAmountInterfaceId(interfaceId);
            player.getOutStream().endFrameVarSizeWord();
        }
    }

    public void sendEnterAmount(String header, AmountInput amountInputHandler) {
        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(27);
            player.getOutStream().writeString(header);
            player.setEnterAmountInterfaceId(0);
            player.getOutStream().endFrameVarSizeWord();
            player.amountInputHandler = amountInputHandler;
        }
    }

    public void sendGameTimer(ClientGameTimer timer, TimeUnit unitOfTime, int duration) {
        if (player == null || player.isDisconnected()) {
            return;
        }
        Stream stream = player.getOutStream();
        if (stream == null) {
            return;
        }

        if (timer.isItem()) {
            int seconds = (int) Long.min(unitOfTime.toSeconds(duration), 65535);
            stream.createFrame(224);
            stream.writeUShort(timer.getTimerId());
            stream.writeUShort(timer.getTimerId());
            stream.writeUShort(seconds);
            player.flushOutStream();
        } else {
            int seconds = (int) Long.min(unitOfTime.toSeconds(duration), 65535);
            stream.createFrame(223);
            stream.writeByte(timer.getTimerId());
            stream.writeUShort(seconds);
            player.flushOutStream();
        }
    }

    public void sendExperienceDrop(boolean increase, long amount, int... skills) {
        if (player.isDisconnected() || player.getOutStream() == null) {
            return;
        }
        List<Integer> illegalSkills = new ArrayList<>();

        for (int index = 0; index < skills.length; index++) {
            int skillId = skills[index];
            if (skillId < 0 || skillId > Skill.MAXIMUM_SKILL_ID) {
                illegalSkills.add(index);
            }
        }
        if (!illegalSkills.isEmpty()) {
            skills = ArrayUtils.removeAll(skills,
                    ArrayUtils.toPrimitive(illegalSkills.toArray(new Integer[0])));
        }
        if (ArrayUtils.isEmpty(skills)) {
            return;
        }
        if (increase) {
            player.setExperienceCounter(player.getExperienceCounter() + amount);
        }

        Stream stream = player.getOutStream();

        stream.createFrameVarSize(11);
        stream.writeQWord(amount);
        stream.writeByte(skills.length);
        for (int skillId : skills) {
            stream.writeByte(skillId);
        }
        stream.endFrameVarSize();
    }

    public void sendConfig(final int id, final int state) {
        if (this.player != null && this.player.getOutStream() != null) {
            if (state < 128) {
                this.player.getOutStream().createFrame(36);
                this.player.getOutStream().writeWordBigEndian(id);
                this.player.getOutStream().writeByte(state);
            } else {
                this.player.getOutStream().createFrame(87);
                this.player.getOutStream().writeWordBigEndian_dup(id);
                this.player.getOutStream().writeDWord_v1(state);
            }
            this.player.flushOutStream();
        }
    }

    public void sendTradingPost(int frame, int item, int slot, int amount) {
        if (player != null && player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(34);
            player.getOutStream().writeUShort(frame);
            player.getOutStream().writeByte(slot);
            player.getOutStream().writeUShort(item + 1);
            player.getOutStream().writeByte(255);
            player.getOutStream().writeDWord(amount);
            player.getOutStream().endFrameVarSizeWord();
        }
    }

}
