package io.xeros.model.entity.player;

import java.util.*;
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
import io.xeros.content.skills.Fishing;
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
import io.xeros.model.AmountInput;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.Projectile;
import io.xeros.model.SkillLevel;
import io.xeros.model.SoundType;
import io.xeros.model.StringInput;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.cycleevent.impl.WheatPortalEvent;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.entity.player.broadcasts.BroadcastManager;
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
import io.xeros.model.projectile.ProjectileEntity;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.model.world.Clan;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.net.outgoing.messages.ComponentVisibility;
import io.xeros.util.Misc;
import io.xeros.util.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

public class PlayerAssistant {

    public static int ALCH_WARNING_AMOUNT = 200_000;
    public final Player c;

    public PlayerAssistant(Player Client) {
        this.c = Client;
    }

    public void sendUpdateTimer() {
        if (c.getOutStream() != null) {
            long seconds = ((PlayerHandler.updateSeconds * 1000) - (System.currentTimeMillis() - PlayerHandler.updateStartTime)) / 1000;
            c.getOutStream().createFrame(114);
            c.getOutStream().writeWordBigEndian((int) seconds * 50 / 30);
        }
    }

    public void sendDropTableData(String message, int npcIndex) {
        c.getOutStream().createFrameVarSize(197);
        c.getOutStream().writeString(message);
        c.getOutStream().writeDWord(npcIndex);
        c.getOutStream().endFrameVarSize();
        c.flushOutStream();
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
        openQuestInterface(header, lines.toArray(new String[lines.size()]));
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
        if (c.inPits && PlayerHandler.getPlayers().get(i).inPits || PlayerHandler.getPlayers().get(i).inDuel) {
            if (PlayerHandler.getPlayers().get(i) != null || i == c.getIndex())
                return true;
        }
        int combatDif1 = c.getCombatLevelDifference(PlayerHandler.getPlayers().get(i));
        if (PlayerHandler.getPlayers().get(i) == null || i != c.getIndex()
                || !PlayerHandler.getPlayers().get(i).getPosition().inWild() || combatDif1 > c.wildLevel
                || combatDif1 > PlayerHandler.getPlayers().get(i).wildLevel) {
            return false;
        }
        if (Configuration.SINGLE_AND_MULTI_ZONES) {
            if (!PlayerHandler.getPlayers().get(i).getPosition().inMulti()) {
                return PlayerHandler.getPlayers().get(i).underAttackByPlayer != c.getIndex()
                        && PlayerHandler.getPlayers().get(i).underAttackByPlayer > 0
                        && PlayerHandler.getPlayers().get(i).getIndex() != c.underAttackByPlayer && c.underAttackByPlayer > 0;
            }
        }
        return true;
    }

    public void openWebAddress(String address) {
        sendString(address, 12_000);
    }

    public void openGameframeTab(int tabId) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(24);
            c.getOutStream().writeByte(tabId);
            c.flushOutStream();
        }
    }

    public void sendPlayerObjectAnimation(GlobalObject obj, int animation) {
        sendPlayerObjectAnimation(obj.getX(), obj.getY(), animation, obj.getType(), obj.getFace());
    }

    public void sendPlayerObjectAnimation(int x, int y, int animation, int type, int orientation) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC(y - (c.mapRegionY * 8));
            c.getOutStream().writeByteC(x - (c.mapRegionX * 8));
            c.getOutStream().createFrame(160);
            c.getOutStream().writeByteS(((0 & 7) << 4) + (0 & 7));
            c.getOutStream().writeByteS((type << 2) + (orientation & 3));
            c.getOutStream().writeWordA(animation);
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
        c.destroyItem = itemToDestroy;
        String itemName = ItemAssistant.getItemName(itemToDestroy.getItemId());
        String[][] info = {
                {"Are you sure you want to " + itemToDestroy.getType().getText() + " this item?", "14174"}, {"Yes.", "14175"},
                {"No.", "14176"}, {"There is no way to reverse this.", "14177"}, {"", "14182"},
                {"", "14183"}, {itemName, "14184"}};
        sendFrame34(itemToDestroy.getItemId(), 0, 14171, 1);
        for (String[] anInfo : info)
            sendFrame126(anInfo[0], Integer.parseInt(anInfo[1]));
        sendChatboxInterface(14170);
    }

    public void destroyInterface(String config) {
        int itemId = c.destroyItem.getItemId();
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
        c.playerLevel[6] += imbuedHeartStats();
        c.getPA().refreshSkill(6);
        c.gfx0(1316);
        c.sendMessage("Your Magic has been temporarily raised.");
    }

    private int imbuedHeartStats() {
        int increaseBy;
        int skill = 6;
        increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .10);
        if (c.playerLevel[skill] + increaseBy > c.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
            return c.getLevelForXP(c.playerXP[skill]) + increaseBy - c.playerLevel[skill];
        }
        return increaseBy;
    }

    public void assembleSlayerHelmet() {
        if (!c.getSlayer().getUnlocks().contains(SlayerUnlock.MALEVOLENT_MASQUERADE)) {
            c.sendMessage("You don't have the knowledge to do this. You must learn how to first.");
            return;
        }
        if (c.playerLevel[Player.playerCrafting] < 55) {
            c.sendMessage("@blu@You need a crafting level of 55 to assemble a slayer helmet.");
            return;
        }
        if (c.getItems().playerHasItem(4166) && c.getItems().playerHasItem(4168) && c.getItems().playerHasItem(4164)
                && c.getItems().playerHasItem(4551) && c.getItems().playerHasItem(8901)) {
            c.sendMessage("@blu@You assemble the pieces and create a full slayer helmet!");
            c.getItems().deleteItem(4166, 1);
            c.getItems().deleteItem(4164, 1);
            c.getItems().deleteItem(4168, 1);
            c.getItems().deleteItem(4551, 1);
            c.getItems().deleteItem(8901, 1);
            c.getItems().addItem(11864, 1);
        } else {
            c.sendMessage(
                    "You need a @blu@Facemask@bla@, @blu@Nose peg@bla@, @blu@Spiny helmet@bla@ and @blu@Earmuffs");
            c.sendMessage("@bla@in order to assemble a slayer helmet.");
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
        c.outStream.createFrame(61);
        c.outStream.writeByte(i1);
        c.setUpdateRequired(true);
        c.setAppearanceUpdateRequired(true);

    }

    public void sendString(final int id, final String s) {
        sendString(s, id);
    }

    public void sendString(final String s, final int id) {
        sendFrame126(s, id);
    }

    /**
     * @param s
     * @author Grant_ | www.rune-server.ee/members/grant_ | 10/15/19
     */
    public void sendBroadCast(final String s) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrameVarSizeWord(170);
            c.getOutStream().writeString(s);
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    /**
     * Latest
     *
     * @param broadcast
     */
    public void sendBroadCast(Broadcast broadcast) {//
        BroadcastType type;//
        if (broadcast.getTeleport() != null)//
            type = BroadcastType.TELEPORT;//
        else if (broadcast.getUrl() != null)//
            type = BroadcastType.LINK;//
        else//
            type = BroadcastType.MESSAGE;//
        c.getOutStream().createFrameVarSizeWord(179);//
        c.getOutStream().writeByte(type.ordinal());//
        c.getOutStream().writeByte(broadcast.getIndex());//
        if (type.ordinal() == -1) {//
            /**
             * Never removes server sided
             */
            BroadcastManager.removeBroadcast(broadcast.index);//
            return;//
        }//
        c.getOutStream().writeString(broadcast.getMessage());//
//
        if (type == BroadcastType.LINK)//
            c.getOutStream().writeString(broadcast.getUrl());//
        if (type == BroadcastType.TELEPORT) {//
            c.getOutStream().writeDWord(broadcast.getTeleport().getX());//
            c.getOutStream().writeDWord(broadcast.getTeleport().getY());//
            c.getOutStream().writeByte(broadcast.getTeleport().getHeight());//
        }//
        c.getOutStream().endFrameVarSizeWord();//
        c.flushOutStream();//
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
        if (c == null || c.getOutStream() == null) {
            return;
        }
        Stream stream = c.getOutStream();
        stream.createFrame(7);
        stream.writeDWord(componentId);
        stream.writeByte(index);
        c.flushOutStream();
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
            if (item.getAmount() > 254) {
                player.getOutStream().writeByte(255);
                player.getOutStream().writeDWord_v2(item.getAmount());
            } else {
                player.getOutStream().writeByte(item.getAmount());
            }
            player.getOutStream().writeWordBigEndianA(item.getId() + 1);
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
        for (int i = 0; i < c.playerItems.length; i++) {
            c.playerItems[i] = 0;
        }
        for (int i = 0; i < c.playerItemsN.length; i++) {
            c.playerItemsN[i] = 0;
        }
        c.getItems().sendInventoryInterface(3214);
    }

    public void setConfig(int id, int state) {
        // synchronized (c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(36);
            c.getOutStream().writeWordBigEndian(id);
            c.getOutStream().writeByte(state);
            c.flushOutStream();
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
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(246);
            c.getOutStream().writeWordBigEndian(interfaceChild);
            c.getOutStream().writeUShort(zoom);
            c.getOutStream().writeUShort(itemId);
            c.flushOutStream();
        }
    }

    public void itemOnInterface(GameItem item, int frame, int slot) {
        int id = item == null ? -1 : item.getId();
        int amount = item == null ? 0 : item.getAmount();
        itemOnInterface(id, amount, frame, slot);
    }

    public void itemOnInterface(int item, int amount, int frame, int slot) {
        if (c.getOutStream() != null) {
            c.outStream.createFrameVarSizeWord(34);
            c.outStream.writeUShort(frame);
            if (item == 23231) {
                c.outStream.writeByte(slot);
            } else {
                c.outStream.writeDWord(slot);
            }
            c.outStream.writeUShort(item + 1);
            c.outStream.writeByte(255); //write byte of 255
            c.outStream.writeDWord(amount);
            c.outStream.endFrameVarSizeWord();
        }
    }

    public void sendTabAreaOverlayInterface(int interfaceId) {
        if (c.getOutStream() != null) {
            c.outStream.createFrame(142);
            c.outStream.writeUShort(interfaceId);
            c.flushOutStream();
        }
    }

    public void playerWalk(int x, int y) {
        PathFinder.getPathFinder().findRoute(c, x, y, true, 1, 1);
    }

    public void playerWalk(int x, int y, int xLength, int yLength) {
        PathFinder.getPathFinder().findRoute(c, x, y, true, xLength, yLength);
    }

    public Clan getClan() {
        if (Server.clanManager.clanExists(c.getLoginName())) {
            return Server.clanManager.getClan(c.getLoginName());
        }
        return null;
    }

    public void clearClanChat() {
        c.clanId = -1;
        c.getPA().sendString("Talking in: ", 18139);
        c.getPA().sendString("Owner: ", 18140);
        for (int j = 18144; j < 18244; j++) {
            c.getPA().sendString("", j);
        }
    }

    public void setClanData() {
        boolean exists = Server.clanManager.clanExists(c.getLoginName());
        if (!exists || c.clan == null) {
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
        Clan clan = Server.clanManager.getClan(c.getLoginName());
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
        c.usingClickCast = false;
        c.setSpellId(-1);
    }

    public void resetAutocast() {
        resetAutocast(true);
    }

    //	c.autocasting = true;
//	c.autocastingDefensive = defensive;
//	c.autocastId = spellIndex;
//	updateConfig(c, spell);
    public void resetAutocast(boolean sendWeapon) {
        c.setSpellId(-1);
        c.autocastId = -1;
        c.autocastingDefensive = false;
        c.autocasting = false;
        c.getPA().sendConfig(CombatSpellData.AUTOCAST_CONFIG, 0);
        c.getPA().sendConfig(CombatSpellData.AUTOCAST_DEFENCE_CONFIG, 0);
        c.getPA().sendFrame36(108, 0);
        c.getPA().sendFrame36(109, 0);
        if (sendWeapon) {
            c.setSidebarInterface(0, 328);
            c.getItems().sendWeapon(c.playerEquipment[Player.playerWeapon]);
        }
        c.getPA().sendAutocastState(false);

        c.getCombatConfigs().updateWeaponModeConfig();
    }

    public void movePlayerUnconditionally(int x, int y, int h) {
        c.resetWalkingQueue();
        c.setTeleportToX(x);
        c.setTeleportToY(y);
        c.heightLevel = h;
        c.teleTimer = 4;
        requestUpdates();
        c.lastMove = System.currentTimeMillis();
    }

    public void movePlayer(int x, int y) {
        movePlayer(x, y, c.heightLevel);
    }

    public void movePlayer(int x, int y, int h) {
        if (c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
            if (!c.isDead) {
                return;
            }
        }
        if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            return;
        }
        if (c.morphed) {
            return;
        }
        if (c.getSlayer().superiorSpawned) {
            c.getSlayer().superiorSpawned = false;
        }
        c.resetWalkingQueue();
        c.attacking.reset();
        c.setTeleportToX(x);
        c.setTeleportToY(y);
        c.heightLevel = h;
        c.teleTimer = 2;
        requestUpdates();
    }

    public void forceMove(int x, int y, int h, boolean forXlog) {
        c.resetWalkingQueue();
        c.attacking.reset();
        c.setTeleportToX(x);
        c.setTeleportToY(y);
        c.heightLevel = h;
        c.teleTimer = 2;
        if (forXlog) {
            c.absX = x;
            c.absY = y;
            c.heightLevel = h;
            c.updateController();
        }
        requestUpdates();
    }

    public void movePlayer(Coordinate coord) {
        movePlayer(coord.getX(), coord.getY(), coord.getH());
    }

    public void stopSkilling() {
        Server.getEventHandler().stop(c, "skilling");
    }

    public void movePlayerDuel(int x, int y, int h) {
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
                MultiplayerSessionType.DUEL);
        if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION
                && Boundary.isIn(c, Boundary.DUEL_ARENA)) {
            return;
        }
        c.resetWalkingQueue();
        c.setTeleportToX(x);
        c.setTeleportToY(y);
        c.heightLevel = h;
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

        if (c.getOutStream() != null) {
            c.getOutStream().createFrameVarSizeWord(126);
            c.getOutStream().writeString(s);
            c.getOutStream().writeWordA(id);
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }

    }

    private final Map<Integer, TinterfaceText> interfaceText = new HashMap<>();

    public class TinterfaceText {
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
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrameVarSizeWord(187);
            c.getOutStream().writeString(header);
            c.getOutStream().endFrameVarSizeWord();
            c.stringInputHandler = stringInputHandler;
        }
    }

    public void setSkillLevel(int skillNum, int currentLevel, int XP) {
        if (c.getOutStream() != null && c != null) {
            if (skillNum == 22) {    // Client hunter id, it's 21 in server
                return;
            }

            c.getOutStream().createFrame(134);
            if (skillNum == 21) {        // Server hunter id
                skillNum = 22;        // Convert to client hunter id
            }
            c.getOutStream().writeByte(skillNum);
            c.getOutStream().writeDWord_v1(XP);
            c.getOutStream().writeByte(currentLevel);
            c.flushOutStream();
        }
    }

    public void sendFrame106(int sideIcon) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(106);
            c.getOutStream().writeByteC(sideIcon);
            c.flushOutStream();
            requestUpdates();
        }
    }

    /**
     * Stops screen shaking
     */
    public void resetScreenShake() {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(107);
            c.flushOutStream();
        }
        // }
    }

    public void updateRunningToggle() {
        sendFrame36(173, c.isRunningToggled() ? 1 : 0);
    }

    public void sendFrame36(int id, int state) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(36);
            c.getOutStream().writeWordBigEndian(id);
            c.getOutStream().writeByte(state);
            c.flushOutStream();
        }

    }

    public void sendPlayerHeadOnInterface(int interfaceId) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(185);
            c.getOutStream().writeWordBigEndianA(interfaceId);
        }

    }

    public void sendLogout() {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(109);
            c.flushOutStream();
        }
    }

    public void resetScrollBar(int interfaceId) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(2);
            c.getOutStream().writeUShort(interfaceId);
            c.flushOutStream();
        }
    }

    public void setScrollableMaxHeight(int interfaceId, int scrollMax) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(3);
            c.getOutStream().writeUShort(interfaceId);
            c.getOutStream().writeUShort(scrollMax);
            c.flushOutStream();
        }
    }

    public void sendSprite(int id, int normal, int hover) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(98);
            c.getOutStream().writeUShort(id);
            c.getOutStream().writeUShort(normal);
            c.getOutStream().writeUShort(hover);
            c.flushOutStream();
        }
    }

    public void sendInterfaceAction(int id, boolean disabled) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(100);
            c.getOutStream().writeUShort(id);
            c.getOutStream().writeByte(disabled ? 1 : 0);
            c.flushOutStream();
        }
    }

    public void sendSprite(int id, int normal) {
        sendSprite(id, normal, normal);
    }

    public enum SceneType {
        OBJECT(0),
        NPC(1);

        private final int id;

        SceneType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }


    public void sendSceneOptions(SceneType type, int id, String[] message) {

        String finalstring = Arrays.toString(message).replace("String[", "").replace(", ", "&").replace("]", "").replace("[", "");

        if (c.getOutStream() != null) {
            c.getOutStream().createFrameVarSize(102);
            c.getOutStream().writeByte(type.id);
            c.getOutStream().writeUShort(id);
            c.getOutStream().writeString(finalstring);
            c.getOutStream().endFrameVarSize();
        }

    }


    public void showInterface(int interfaceid) {
        c.interruptActions();
        if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
            Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return;
        }
        if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen() || c.viewingRunePouch) {
            c.sendMessage("You should stop what you are doing before continuing.");
            return;
        }
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(97);
            c.getOutStream().writeUShort(interfaceid);
            c.flushOutStream();
            c.openedInterface(interfaceid);
        }
    }

    public void sendFrame248(int MainFrame, int SubFrame) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(248);
            c.getOutStream().writeWordA(MainFrame);
            c.getOutStream().writeUShort(SubFrame);
            c.flushOutStream();
            c.setOpenInterface(MainFrame);
        }
    }

    public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(246);
            c.getOutStream().writeWordBigEndian(MainFrame);
            c.getOutStream().writeUShort(SubFrame);
            c.getOutStream().writeUShort(SubFrame2);
            c.flushOutStream();
        }
    }

    public PlayerAssistant sendInterfaceHidden(int interfaceId, boolean hide) {
        sendInterfaceHidden(hide ? 1 : 0, interfaceId);
        return this;
    }

    public void sendInterfaceHidden(int state, int componentId) {
        if (c.getPacketDropper().requiresUpdate(171, new ComponentVisibility(state, componentId))) {
            if (c.getOutStream() != null) {
                c.getOutStream().createFrame(171);
                c.getOutStream().writeByte(state);
                c.getOutStream().writeUShort(componentId);
                c.flushOutStream();
            }
        }
    }

    public void sendInterfaceAnimation(int interfaceId, Animation animation) {
        sendInterfaceAnimation(interfaceId, animation.getId());
    }

    public void sendInterfaceAnimation(int interfaceId, int animation) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(200);
            c.getOutStream().writeUShort(interfaceId);
            c.getOutStream().writeUShort(animation);
            c.flushOutStream();
        }
    }

    public void sendFrame70(int i, int o, int id) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(70);
            c.getOutStream().writeUShort(i);
            c.getOutStream().writeWordBigEndian(o);
            c.getOutStream().writeWordBigEndian(id);
            c.flushOutStream();
        }

    }

    public void sendNpcHeadOnInterface(int npcId, int interfaceId) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(75);
            c.getOutStream().writeWordBigEndianA(npcId);
            c.getOutStream().writeWordBigEndianA(interfaceId);
            c.flushOutStream();
        }

    }

    public void sendChatboxInterface(int Frame) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(164);
            c.getOutStream().writeWordBigEndian_dup(Frame);
            c.flushOutStream();
        }

    }

    public void sendFrame87(int id, int state) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(87);
            c.getOutStream().writeWordBigEndian_dup(id);
            c.getOutStream().writeDWord_v1(state);
            c.flushOutStream();
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
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(254);
            c.getOutStream().writeByte(type);
            c.getOutStream().writeUShort(id);
            c.getOutStream().write3Byte(0);
            c.flushOutStream();
        }
    }

    public void createObjectHints(int x, int y, int height, int pos) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(254);
            c.getOutStream().writeByte(pos);
            c.getOutStream().writeUShort(x);
            c.getOutStream().writeUShort(y);
            c.getOutStream().writeByte(height);
            c.flushOutStream();
        }

    }

    private void sendAutocastState(boolean state) {
        if (c == null || c.isDisconnected() || c.getSession() == null) {
            return;
        }
        Stream stream = c.getOutStream();
        if (stream == null) {
            return;
        }
        stream.createFrame(15);
        stream.writeByte(state ? 1 : 0);
        c.flushOutStream();
    }

    public void updateFriendOnlineStatus(String displayName, int world) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(50);
            c.getOutStream().writeQWord(Misc.playerNameToInt64(displayName));
            c.getOutStream().writeByte(world);
        }
    }

    public void addFriendOrIgnore(String displayName, boolean friend, int world) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrameVarSize(18);
            c.getOutStream().writeNullTerminatedString(displayName);
            c.getOutStream().writeByte(friend ? 1 : 0);
            if (friend)
                c.getOutStream().writeByte(world);
            c.getOutStream().endFrameVarSize();
            c.flushOutStream();
        }
    }

    public void sendPM(String displayName, RightGroup rightsGroup, byte[] chatMessage) {
        c.getOutStream().createFrameVarSize(196);
        c.getOutStream().writeNullTerminatedString(displayName);
        c.getOutStream().writeDWord(new Random().nextInt());
        writeRightsGroup(c.getOutStream(), rightsGroup);
        c.getOutStream().writeBytes(chatMessage, chatMessage.length, 0);
        c.getOutStream().endFrameVarSize();
    }

    public void sendFriendUpdatedDisplayName(String oldName, String newName) {
        c.getOutStream().createFrameVarSize(19);
        c.getOutStream().writeNullTerminatedString(oldName);
        c.getOutStream().writeNullTerminatedString(newName);
        c.getOutStream().endFrameVarSize();
    }

    public void removeAllWindows() {
        if (c.getOutStream() != null && c != null) {
            c.getPA().resetVariables();

            c.getOutStream().createFrame(219);
            c.flushOutStream();

            c.nextChat = 0;
            c.dialogueOptions = 0;
            c.closedInterface();
            c.setDialogueBuilder(null);
            c.stringInputHandler = null;
            c.amountInputHandler = null;
            c.setViewingCollectionLog(null);
        }
        resetVariables();
    }

    public void updatePoisonStatus() {
        sendConfig(1359, c.getHealth().getStatus().ordinal());
    }

    public void closeAllWindows() {
        closeAllWindows(true);
    }

    public void closeAllWindows(boolean sendPacket) {
        if (c.getOutStream() != null && c != null) {
            if (sendPacket) {
                c.getOutStream().createFrame(219);
                c.flushOutStream();
            }
            c.lastDialogueNewSystem = false;
            c.getAttributes().remove("dangerous_tele");
            c.inTradingPost = false;
            c.isBanking = false;
            c.viewingPresets = false;
            c.inPresets = false;
            c.setEnterAmountInterfaceId(0);
            c.getLootingBag().setWithdrawInterfaceOpen(false);
            c.getLootingBag().setDepositInterfaceOpen(false);
            c.getLootingBag().setSelectDepositAmountInterfaceOpen(false);
            c.viewingRunePouch = false;
            c.nextChat = 0;
            c.dialogueOptions = 0;
            c.placeHolderWarning = false;
            resetVariables();
            c.interruptActions();
            c.closedInterface();
            c.inBank = false;
            c.myShopId = 0;
        }
    }

    public void sendFrame34(int id, int slot, int column, int amount) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.outStream.createFrameVarSizeWord(34); // init item to smith screen
            c.outStream.writeUShort(column); // Column Across Smith Screen
            c.outStream.writeByte(4); // Total Rows?
            c.outStream.writeDWord(slot); // Row Down The Smith Screen
            c.outStream.writeUShort(id + 1); // item
            c.outStream.writeByte(amount); // how many there are?
            c.outStream.endFrameVarSizeWord();
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

        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(208);
            c.getOutStream().writeUShort(id);
            c.flushOutStream();
            currentWalkableInterface = id;
        }
    }

    public void shakeScreen(int verticleAmount, int verticleSpeed, int horizontalAmount, int horizontalSpeed) {
        if (c != null && c.getOutStream() != null) {
            c.getOutStream().createFrame(35);
            c.getOutStream().writeByte(verticleAmount);
            c.getOutStream().writeByte(verticleSpeed);
            c.getOutStream().writeByte(horizontalAmount);
            c.getOutStream().writeByte(horizontalSpeed);
            c.flushOutStream();
        }
    }

    public void sendFrame99(int state) { // used for disabling map
        // synchronized(c) {
        if (c != null && c.getOutStream() != null) {
            c.getOutStream().createFrame(99);
            c.getOutStream().writeByte(state);
            c.flushOutStream();
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
                        if (c.distanceToPoint(person.getX(), person.getY()) <= 25) {
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
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
            c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
            c.getOutStream().createFrame(117);
            c.getOutStream().writeByte(angle);
            c.getOutStream().writeByte(offY);
            c.getOutStream().writeByte(offX);
            c.getOutStream().writeUShort(lockon);
            c.getOutStream().writeUShort(gfxMoving);
            c.getOutStream().writeByte(startHeight);
            c.getOutStream().writeByte(endHeight);
            c.getOutStream().writeUShort(time);
            c.getOutStream().writeUShort(speed);
            c.getOutStream().writeByte(16);
            c.getOutStream().writeByte(64);
            c.flushOutStream();
        }
    }

    public void createProjectile(int x, int y, int offX, int offY, int angle, int scale, int pitch, int speed, int gfxMoving,
                                 int startHeight, int endHeight, int lockon, int time) {
        // synchronized(c) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
            c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
            c.getOutStream().createFrame(117);
            c.getOutStream().writeByte(angle);
            c.getOutStream().writeByte(offY);
            c.getOutStream().writeByte(offX);
            c.getOutStream().writeUShort(lockon);
            c.getOutStream().writeUShort(gfxMoving);
            c.getOutStream().writeByte(startHeight);
            c.getOutStream().writeByte(endHeight);
            c.getOutStream().writeUShort(time);
            c.getOutStream().writeUShort(speed);
            c.getOutStream().writeByte(pitch);
            c.getOutStream().writeByte(scale);
            c.flushOutStream();
        }
    }

    public void sendProjectile(int startX, int startY, int destX, int destY, int angle, int duration, int gfxMoving, int startHeight, int endHeight, int lockon, int startDelay, int slope, int creatorSize, int startDistanceOffset) {

    }


    public void sendProjectile(Projectile projectile) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC((projectile.getStart().getY() - (c.getMapRegionY() * 8)));
            c.getOutStream().writeByteC((projectile.getStart().getX() - (c.getMapRegionX() * 8)));
            c.getOutStream().createFrame(117);
            c.getOutStream().writeByte(projectile.getCurve()); // Unknown
            c.getOutStream().writeByte(projectile.getOffset().getX());
            c.getOutStream().writeByte(projectile.getOffset().getY());
            c.getOutStream().writeUShort(projectile.getLockon());
            c.getOutStream().writeUShort(projectile.getProjectileId());
            c.getOutStream().writeByte(projectile.getStartHeight());
            c.getOutStream().writeByte(projectile.getEndHeight());
            c.getOutStream().writeUShort(projectile.getDelay());
            c.getOutStream().writeUShort(projectile.getSpeed());
            c.getOutStream().writeByte(projectile.getPitch());
            c.getOutStream().writeByte(projectile.getScale()); // Unknown
            c.flushOutStream();
        }

    }

    private void createProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
            c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
            c.getOutStream().createFrame(117);
            c.getOutStream().writeByte(angle);
            c.getOutStream().writeByte(offY);
            c.getOutStream().writeByte(offX);
            c.getOutStream().writeUShort(lockon);
            c.getOutStream().writeUShort(gfxMoving);
            c.getOutStream().writeByte(startHeight);
            c.getOutStream().writeByte(endHeight);
            c.getOutStream().writeUShort(time);
            c.getOutStream().writeUShort(speed);
            c.getOutStream().writeByte(slope);
            c.getOutStream().writeByte(64);
            c.flushOutStream();
        }
    }

    public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving,
                                        int startHeight, int endHeight, int lockon, int time, int delay) {
        ProjectileEntity projectile = new ProjectileEntity(new Position(x, y, c.getHeight()), new Position(offX, offY), lockon, gfxMoving, delay, speed, startHeight, endHeight, angle, 1, 5);
        projectile.sendProjectile();
        System.out.println(projectile.getStart());
    }

    public void mysteryBoxItemOnInterface(int item, int amount, int frame, int slot) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrameVarSizeWord(34);
            c.getOutStream().writeUShort(frame);
            c.getOutStream().writeByte(slot);
            c.getOutStream().writeUShort(item + 1);
            c.getOutStream().writeByte(255);
            c.getOutStream().writeDWord(amount);
            c.getOutStream().endFrameVarSizeWord();
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
                .filter(p -> p.getHeight() == c.getHeight())
                .filter(c::sameInstance)
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
                .filter(p -> p.getHeight() == c.getHeight())
                .filter(c::sameInstance)
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
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC(y - (c.getMapRegionY() * 8));
            c.getOutStream().writeByteC(x - (c.getMapRegionX() * 8));
            c.getOutStream().createFrame(4);
            c.getOutStream().writeByte(0);
            c.getOutStream().writeUShort(id);
            c.getOutStream().writeByte(height);
            c.getOutStream().writeUShort(time);
            c.flushOutStream();
        }

    }

    public void useChargeSkills() {
        int[][] skillsNeck = {{11968, 11970, 5}, {11970, 11105, 4}, {11105, 11107, 3}, {11107, 11109, 2},
                {11109, 11111, 1}, {11111, 11113, 0}};
        for (int[] aSkillsNeck : skillsNeck) {
            if (c.operateEquipmentItemId == aSkillsNeck[0]) {
                if (c.isOperate) {
                    c.getItems().deleteItem(aSkillsNeck[0], 1);
                    c.getItems().addItem(aSkillsNeck[1], 1);
                }
                if (aSkillsNeck[2] > 1) {
                    c.sendMessage("Your amulet has " + aSkillsNeck[2] + " charges left.");
                } else {
                    c.sendMessage("Your amulet has " + aSkillsNeck[2] + " charge left.");
                }
            }
        }
        // c.getItems().updateSlot(c.playerAmulet);
        c.isOperate = false;
        c.operateEquipmentItemId = -1;
    }

    public void sendScreenFlash(int color, int maxIntensity) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(10);
            c.getOutStream().writeDWord(color);
            c.getOutStream().writeByte(maxIntensity);
            c.getOutStream().writeByte(0);
            c.flushOutStream();
        }
    }

    public void sendFog(boolean enabled, int fogStrength) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrame(243);
            c.getOutStream().writeByte(enabled ? 1 : 0);
            if (fogStrength > 85)
                fogStrength = 85;
            c.getOutStream().writeDWord(fogStrength);
            c.flushOutStream();
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
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
            c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
            c.getOutStream().createFrame(101);
            c.getOutStream().writeByteC((objectType << 2) + (face & 3));
            c.getOutStream().writeByte(0);

            if (objectId != -1) { // removing
                c.getOutStream().createFrame(151);
                c.getOutStream().writeByteS(0);
                c.getOutStream().writeWordBigEndian(objectId);
                c.getOutStream().writeByteS((objectType << 2) + (face & 3));
            }
            if (flushOutStream) {
                c.flushOutStream();
            }
        }
    }

    private Map<Integer, String> playerOptions = new HashMap<>();

    public void showOption(int i, int l, String s) {
        if (c.getOutStream() != null) {
            if (!s.equals(playerOptions.get(i))) {
                playerOptions.put(i, s);
                c.getOutStream().createFrameVarSize(104);
                c.getOutStream().writeByteC(i);
                c.getOutStream().writeByteA(l);
                c.getOutStream().writeString(s);
                c.getOutStream().endFrameVarSize();
                c.flushOutStream();
            }
        }
    }

    /**
     * Open bank
     **/
    public void sendFrame34a(int frame, int item, int slot, int amount) {
        c.outStream.createFrameVarSizeWord(34);
        c.outStream.writeUShort(frame);
        c.outStream.writeByte(slot);
        c.outStream.writeUShort(item + 1);
        c.outStream.writeByte(255);
        c.outStream.writeDWord(amount);
        c.outStream.endFrameVarSizeWord();
    }

    /**
     * @param frame
     * @param item
     * @param slot
     * @param amount
     * @param opaque
     * @author Grant_ | www.rune-server.ee/members/grant_ | 10/6/19
     */
    public void sendItemToSlotWithOpacity(int frame, int item, int slot, int amount, boolean opaque) {
        final int bitpackedValue = opaque ? setBit(15, item + 1) : item + 1;
        c.outStream.createFrameVarSizeWord(34);
        c.outStream.writeUShort(frame);
        c.outStream.writeByte(slot);
        c.outStream.writeUShort(bitpackedValue);
        c.outStream.writeByte(255);
        c.outStream.writeDWord(amount);
        c.outStream.endFrameVarSizeWord();
    }

    public void sendStringContainer(int containerInterfaceId, List<String> strings) {
        String[] stringArray = new String[strings.size()];
        for (int index = 0; index < strings.size(); index++)
            stringArray[index] = strings.get(index);
        sendStringContainer(containerInterfaceId, stringArray);
    }

    public void sendStringContainer(int containerInterfaceId, String... strings) {
        if (c.outStream != null) {
            c.outStream.createFrameVarSizeWord(5);
            c.outStream.writeUShort(containerInterfaceId);
            c.outStream.writeUShort(strings.length);
            Arrays.stream(strings).forEach(string -> c.outStream.writeString(string));
            c.outStream.endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    private int setBit(int bit, int target) {
        // Create mask
        int mask = 1 << bit;
        // Set bit
        return target | mask;
    }

    public void sendInterfaceSet(int mainScreenInterface, int inventoryInterface) {
        c.getOutStream().createFrame(248);
        c.getOutStream().writeWordA(mainScreenInterface);
        c.getOutStream().writeUShort(inventoryInterface);
        c.openedInterface(mainScreenInterface);
        c.flushOutStream();
    }

    public boolean viewingOtherBank;
    private final BankTab[] backupTabs = new BankTab[9];

    public void resetOtherBank() {
        for (int i = 0; i < backupTabs.length; i++)
            c.getBank().setBankTab(i, backupTabs[i]);
        viewingOtherBank = false;
        removeAllWindows();
        c.isBanking = false;
        c.getBank().setCurrentBankTab(c.getBank().getBankTab()[0]);
        c.getItems().queueBankContainerUpdate();
        c.sendMessage("You are no longer viewing another players bank.");
    }

    public void openOtherBank(Player otherPlayer) {
        if (otherPlayer == null)
            return;

        if (c.getPA().viewingOtherBank) {
            c.getPA().resetOtherBank();
            return;
        }
        if (otherPlayer.getPA().viewingOtherBank) {
            c.sendMessage("That player is viewing another players bank, please wait.");
            return;
        }
        for (int i = 0; i < backupTabs.length; i++)
            backupTabs[i] = c.getBank().getBankTab(i);
        for (int i = 0; i < otherPlayer.getBank().getBankTab().length; i++)
            c.getBank().setBankTab(i, otherPlayer.getBank().getBankTab(i));
        c.getBank().setCurrentBankTab(c.getBank().getBankTab(0));
        viewingOtherBank = true;
        c.itemAssistant.openUpBank();
    }

    public void castVengeance() {
        c.usingMagic = true;
        if (c.playerLevel[1] < 40) {
            c.sendMessage("You need a defence level of 40 to cast this spell.");
            return;
        }
        if (System.currentTimeMillis() - c.lastCast < 30000) {
            c.sendMessage("You can only cast vengeance every 30 seconds.");
            return;
        }
        if (c.vengOn) {
            c.sendMessage("You already have vengeance casted.");
            return;
        }
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
                MultiplayerSessionType.DUEL);
        if (!Objects.isNull(session)) {
            if (session.getRules().contains(DuelSessionRules.Rule.NO_MAGE)) {
                c.sendMessage("You can't cast this spell because magic has been disabled.");
                return;
            }
        }

        if (!MagicRequirements.checkMagicReqs(c, 55, true)) {
            return;
        }
        c.getPA().sendGameTimer(ClientGameTimer.VENGEANCE, TimeUnit.SECONDS, 30);
        c.startAnimation(8317);
        c.gfx100(726);
        addSkillXPMultiplied(112, 6, true);
        refreshSkill(6);
        c.vengOn = true;
        c.usingMagic = false;
        c.lastCast = System.currentTimeMillis();
    }

    /**
     * Magic on items
     **/
    public void alchemy(int itemId, String alch) {

        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@There is no need to do this here.");
            return;
        }

        switch (alch) {
            case "low":
                if (System.currentTimeMillis() - c.alchDelay > 1000) {
                    for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            c.sendMessage("You cannot alch iron man amour.");
                            return;
                        }
                    }
                    for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            c.sendMessage("You cannot alch ultimate iron man amour.");
                            return;
                        }
                    }
                    for (int[] items : EquipmentSet.HC_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            c.sendMessage("You cannot alch hardcore iron man amour.");
                            return;
                        }
                    }
                    if (itemId == LootingBag.LOOTING_BAG || itemId == LootingBag.LOOTING_BAG_OPEN || itemId == RunePouch.RUNE_POUCH_ID) {
                        c.sendMessage("This kind of sorcery cannot happen.");
                        return;
                    }
                    if (!c.getItems().playerHasItem(itemId, 1) || itemId == 995) {
                        return;
                    }
                    if (!MagicRequirements.checkMagicReqs(c, 49, true)) {
                        return;
                    }
                    if (Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
                        c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.LOW_ALCH);
                    }
                    c.getItems().deleteItem(itemId, 1);
                    int amount = ShopAssistant.getItemShopValue(itemId) / 3;

                    if (BryophytaStaff.isWearingStaffWithCharge(c) && c.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)) {
                        amount *= 1.25;
                    }

                    c.getItems().addItem(995, amount);
                    c.startAnimation(CombatSpellData.MAGIC_SPELLS[49][2]);
                    c.gfx100(CombatSpellData.MAGIC_SPELLS[49][3]);
                    c.alchDelay = System.currentTimeMillis();
                    sendFrame106(6);
                    addSkillXPMultiplied(CombatSpellData.MAGIC_SPELLS[49][7], 6, true);
                    refreshSkill(6);
                }
                break;

            case "high":
                if (System.currentTimeMillis() - c.alchDelay > 2000) {
                    for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            c.sendMessage("You cannot alch iron man amour.");
                            break;
                        }
                    }
                    for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            c.sendMessage("You cannot alch ultimate iron man amour.");
                            break;
                        }
                    }
                    for (int[] items : EquipmentSet.HC_MAN_ARMOUR.getEquipment()) {
                        if (Misc.linearSearch(items, itemId) > -1) {
                            c.sendMessage("You cannot alch hardcore iron man amour.");
                            break;
                        }
                    }
                    if (itemId == LootingBag.LOOTING_BAG || itemId == LootingBag.LOOTING_BAG_OPEN || itemId == RunePouch.RUNE_POUCH_ID) {
                        c.sendMessage("This kind of sorcery cannot happen.");
                        break;
                    }
                    if (!c.getItems().playerHasItem(itemId, 1) || itemId == 995) {
                        break;
                    }
                    if (!MagicRequirements.checkMagicReqs(c, 50, true)) {
                        break;
                    }
                    c.getItems().deleteItem(itemId, 1);
                    int amount = (int) (ShopAssistant.getItemShopValue(itemId) * .75);

                    if (BryophytaStaff.isWearingStaffWithCharge(c) && c.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)) {
                        amount *= 1.25;
                    }

                    c.getItems().addItem(995, amount);
                    c.startAnimation(CombatSpellData.MAGIC_SPELLS[50][2]);
                    c.gfx100(CombatSpellData.MAGIC_SPELLS[50][3]);
                    c.alchDelay = System.currentTimeMillis();
                    sendFrame106(6);
                    addSkillXPMultiplied(CombatSpellData.MAGIC_SPELLS[50][7], 6, true);
                    refreshSkill(6);
                }
                break;
        }
    }

    public void magicOnItems(int itemId, int itemSlot, int spellId) {

        NonCombatSpellData.attemptDate(c, spellId);

        switch (spellId) {

            case 1173:
                NonCombatSpellData.superHeatItem(c, itemId);
                break;

            case 1162: // low alch
                if (ShopAssistant.getItemShopValue(itemId) >= ALCH_WARNING_AMOUNT && c.isAlchWarning()) {
                    c.destroyItem = new ItemToDestroy(itemId, itemSlot, DestroyType.LOW_ALCH);
                    c.getPA().destroyInterface("alch");
                    return;
                }
                alchemy(itemId, "low");
                break;

            case 1178: // high alch
                if (ShopAssistant.getItemShopValue(itemId) >= ALCH_WARNING_AMOUNT && c.isAlchWarning()) {
                    c.destroyItem = new ItemToDestroy(itemId, itemSlot, DestroyType.HIGH_ALCH);
                    c.getPA().destroyInterface("alch");
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
                Enchantment.getSingleton().enchantItem(c, itemId, spellId);
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
                            if (!MagicRequirements.checkMagicReqs(c, 56, true)) {
                                return;
                            }
                            break;
                        case 1165:
                            if (!MagicRequirements.checkMagicReqs(c, 57, true)) {
                                return;
                            }
                            break;
                        case 1176:
                            if (!MagicRequirements.checkMagicReqs(c, 58, true)) {
                                return;
                            }
                            break;
                        case 1180:
                            if (!MagicRequirements.checkMagicReqs(c, 59, true)) {
                                return;
                            }
                            break;
                        case 1187:
                            if (!MagicRequirements.checkMagicReqs(c, 60, true)) {
                                return;
                            }
                            break;
                        case 6003:
                            if (!MagicRequirements.checkMagicReqs(c, 61, true)) {
                                return;
                            }
                            break;
                        case 23649:
                            if (!MagicRequirements.checkMagicReqs(c, 99, true)) {
                                return;
                            }
                            break;
                    }
                    if (!c.getItems().playerHasItem(aBoltData[1], amount))
                        amount = c.getItems().getItemAmount(bolt);
                    c.getItems().deleteItem(aBoltData[1], c.getItems().getInventoryItemSlot(aBoltData[1]), amount);
                    c.getPA().addSkillXP(aBoltData[2] * amount, 6, true);
                    c.getItems().addItem(aBoltData[3], amount);
                    c.getPA().sendFrame106(6);
                    return;
                }
            }
        }
    }

    public void resetTb() {
        c.teleBlockLength = 0;
        c.teleBlockStartMillis = 0;
    }

    public void resetFollowers() {
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (PlayerHandler.players[j] != null) {
                if (PlayerHandler.players[j].playerFollowingIndex == c.getIndex()) {
                    Player c = PlayerHandler.players[j];
                    c.getPA().resetFollow();
                }
            }
        }
    }

    public void sendSpecialAttack(int amount, int specialEnabled) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrame(204);
            c.getOutStream().writeByte(amount);
            c.getOutStream().writeByte(specialEnabled);
            c.flushOutStream();
        }
    }

    public void startLeverTeleport(int x, int y, int height) {
        Consumer<Player> teleport = plr -> {
            if (c.isTeleblocked()) {
                c.sendMessage("You are teleblocked and can't teleport.");
                return;
            }
            if (Boundary.isIn(c, Boundary.OUTLAST) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
                return;
            }
            if (c.jailEnd > 1) {
                return;
            }
            if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                c.sendMessage("Please leave the outlast hut area to teleport.");
                return;
            }
            if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
                c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                return;
            }
            if (Boundary.isIn(c, Boundary.HESPORI) && !c.canLeaveHespori) {
                c.sendMessage("Hespori's magic prevents you from teleporting.");
                return;
            }
            if (Boundary.isIn(c, Boundary.ICE_PATH) || Boundary.isIn(c, Boundary.ICE_PATH_TOP) && c.heightLevel > 0) {
                c.sendMessage("The cold from the ice-path is preventing you from teleporting.");
                return;
            }
            if (c.getBankPin().requiresUnlock()) {
                c.getBankPin().open(2);
                return;
            }
            if (Boundary.isIn(c, Boundary.RAIDS)) {
                c.getPotions().resetOverload();
            }
            if (c.getSlayer().superiorSpawned) {
                c.getSlayer().superiorSpawned = false;
            }
            if (Lowpkarena.getState(c) != null || Highpkarena.getState(c) != null) {
                c.sendMessage("You can't teleport from a Pk event!");
                return;
            }
            if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
                if (c.playerAttackingIndex > 0 || c.npcAttackingIndex > 0)
                    c.attacking.reset();
                c.stopMovement();
                removeAllWindows();
                Inferno.reset(c);
                c.fightCavesWaveType = 0;
                c.waveId = 0;
                c.waveInfo = new int[3];
                c.teleX = x;
                c.teleY = y;
                c.faceUpdate(0);
                c.teleHeight = height;
                c.startAnimation(2140);
                c.teleTimer = 8;
                c.setTektonDamageCounter(0);
                if (c.getGlodDamageCounter() >= 80 || c.getIceQueenDamageCounter() >= 80) {
                    c.setGlodDamageCounter(79);
                    c.setIceQueenDamageCounter(79);
                }
                c.sendMessage("You pull the lever..");
            }
            c.getPA().stopSkilling();
            if (Server.getMultiplayerSessionListener().inAnySession(c)) {
                Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            }
        };

        if (WildWarning.isWarnable(c, x, y, height)) {
            WildWarning.sendWildWarning(c, teleport);
            return;
        }
        teleport.accept(c);
    }

    public boolean morphPermissions() {
        if (c.morphed) {
            return false;
        }
        if (c.getPosition().inWild()) {
            return false;
        }
        if (!Boundary.isIn(c, Boundary.EDGEVILLE_PERIMETER)) {
            return false;
        }
        if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
            return false;
        }
        if (System.currentTimeMillis() - c.lastSpear < 3000) {
            c.sendMessage("You cannot do this now.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
            DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
                    MultiplayerSessionType.DUEL);
            if (Objects.nonNull(session)) {
                c.sendMessage("You cannot do this here.");
                return false;
            }
        }
        if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            return false;
        }
        if (Boundary.isIn(c, Boundary.DAGANNOTH_MOTHER_HFTD)) {
            c.sendMessage("You cannot do this here.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.RFD)) {
            c.sendMessage("You cannot do this here.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
            c.sendMessage("You cannot do this here.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.ICE_PATH) || Boundary.isIn(c, Boundary.ICE_PATH_TOP) && c.heightLevel > 0) {
            c.sendMessage("You cannot do this here.");
            return false;
        }
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            c.sendMessage("You cannot do this now.");
            return false;
        }
        return true;
    }

    boolean flag;

    public void resetScrollPosition(int frame) {
        if (flag)
            c.getPA().sendFrame126(":scp: 0", frame);
        else
            c.getPA().sendFrame126(":scp: 00", frame);

        flag = !flag;
    }

    public boolean canTeleport() {
        return canTeleport("modern");
    }

    public boolean canTeleport(String teleportType) {
        if (!c.getController().canMagicTeleport(c) || c.getLock().cannotTeleport(c)) {
            c.sendMessage("You can't teleport right now.");
            return false;
        }

        if (Boundary.OURIANA_ALTAR.in(c) && !Boundary.OURIANA_ALTAR_BANK.in(c)) {
            c.sendMessage("A magical force blocks your teleport, you'll have to walk back.. OH NO!!!");
            return false;
        }
        if (TourneyManager.getSingleton().isInArenaBounds(c) || TourneyManager.getSingleton().isInLobbyBounds(c)) {
            c.sendMessage("You cannot teleport when in the tournament arena.");
            return false;
        }

        if (Boundary.isIn(c, Boundary.OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
            c.sendMessage("You cannot teleport when in the tournament arena.");
            return false;
        }
        if (c.morphed) {
            c.sendMessage("You cannot do this now.");
            return false;
        }

        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return false;
        }

        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            c.sendMessage("You must finish what you're doing first.");
            return false;
        }

        if (c.isForceMovementActive()) {
            c.sendMessage("You can't teleport during forced movement!");
            return false;
        }

        if (Boundary.isIn(c, Boundary.HUNLLEF_BOSS_ROOM) && c.hunllefDead == false) {
            c.sendMessage("The Hunllef's powers disable your teleport.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.RAIDROOMS)) {
            if (c.getRaidsInstance() != null) {
                c.sendMessage("Please use the stairs or type @red@::leaveraids!");
                return false;
            }
        }
        if (Boundary.isIn(c, Boundary.RFD)) {
            c.sendMessage("You cannot teleport from here, use the portal.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
            c.sendMessage("You cannot teleport out of fight caves.");
            return false;
        }
        if (Lowpkarena.getState(c) != null || Highpkarena.getState(c) != null) {
            c.sendMessage("You can't teleport from a Pk event!");
            return false;
        }
        if (Boundary.isIn(c, Boundary.ICE_PATH) || Boundary.isIn(c, Boundary.ICE_PATH_TOP) && c.heightLevel > 0) {
            c.sendMessage("The cold from the ice-path is preventing you from teleporting.");
            return false;
        }
        if (c.getPosition().isInJail() && !(c.getRights().isOrInherits(Right.MODERATOR))) {
            c.sendMessage("You cannot teleport out of jail.");
            return false;
        }
        if (c.isDead) {
            c.sendMessage("You can't teleport while dead!");
            return false;
        }
        if (c.getPosition().inWild() && !(c.getRights().isOrInherits(Right.MODERATOR))) {
            if (!teleportType.equals("glory") && !teleportType.equals("obelisk") && !teleportType.equals("pod")) {
                if (c.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
                    c.sendMessage("You can't teleport above level " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
                    c.getPA().closeAllWindows();
                    return false;
                }
            } else if (!teleportType.equals("obelisk")) {
                if (c.wildLevel > 30) {
                    c.sendMessage("You can't teleport above level 30 in the wilderness.");
                    c.getPA().closeAllWindows();
                    return false;
                }
            }
        }
        if (c.isTeleblocked()) {
            c.sendMessage("You are teleblocked and can't teleport.");
            return false;
        }

        if (Boundary.isIn(c, Boundary.HESPORI) && !c.canLeaveHespori) {
            c.sendMessage("Hespori's magic prevents you from teleporting.");
            return false;
        } else if (Boundary.isIn(c, Boundary.HESPORI) && c.canLeaveHespori) {
            c.canHarvestHespori = false;
            c.canLeaveHespori = false;
        }

        if (c.teleTimer != 0) {
            c.sendMessage("Please wait a few seconds before teleporting again.");
            return false;
        }

        if (c.respawnTimer != -6) {
            c.sendMessage("You must wait until you respawn to teleport.");
            return false;
        }

        if (Lowpkarena.getState(c) != null || Highpkarena.getState(c) != null) {
            c.sendMessage("You can't teleport from a Pk event!");
            return false;
        }
        if (Boundary.isIn(c, Boundary.OUTLAST) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)) {
            c.sendMessage("You can't teleport from outlast.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.TOURNAMENT_LOBBIES_AND_AREAS)) {
            c.sendMessage("You cannot do this right now.");
            return false;
        }
        if (c.jailEnd > 1) {
            c.sendMessage("You are still jailed!");
            return false;
        }
        if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
            c.sendMessage("Please leave the outlast hut area to teleport.");
            return false;
        }
        if (Boundary.isIn(c, Boundary.HESPORI) && !c.canLeaveHespori) {
            c.sendMessage("Hespori's magic prevents you from teleporting.");
            return false;
        }

        return true;
    }

    public void startTeleport(Position position, String teleportType, boolean homeTeleport) {
        startTeleport(position.getX(), position.getY(), position.getHeight(), teleportType, homeTeleport);
    }

    public void spellTeleport(int x, int y, int height, boolean homeTeleport) {
        startTeleport(x, y, height, c.playerMagicBook == 1 ? "ancient" : "modern", homeTeleport);
    }

    public void startTeleport(int x, int y, int height, String teleportType) {
        startTeleportFinal(x, y, height, teleportType, false);
    }


    public void startTeleport(int x, int y, int height, String teleportType, boolean homeTeleport) {
        if (WildWarning.isWarnable(c, x, y, height)) {
            WildWarning.sendWildWarning(c, plr -> startTeleportFinal(x, y, height, teleportType, homeTeleport));
            return;
        }

        startTeleportFinal(x, y, height, teleportType, homeTeleport);
    }

    private void startTeleportFinal(int x, int y, int height, String teleportType, boolean homeTeleport) {
        c.isWc = false;
        if (c.isOverloading) {
            c.sendMessage("You cannot teleport while taking overload damage.");
            return;
        }
        if (c.isFping()) {
            /**
             * Cannot do action while fping
             */
            c.sendMessage("You cannot teleport while fping.");

            return;
        }
        if (!canTeleport(teleportType)) {
            return;
        }

        if (c.stopPlayerSkill) {
            SkillHandler.resetPlayerSkillVariables(c);
            c.stopPlayerSkill = false;
        }

        resetVariables();
        SkillHandler.isSkilling[12] = false;

        if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            return;
        }

        if (Boundary.isIn(c, Boundary.RAIDS)) {
            c.getPotions().resetOverload();
        }

        if (c.getSlayer().superiorSpawned) {
            c.getSlayer().superiorSpawned = false;
        }

        if (c.playerAttackingIndex > 0 || c.npcAttackingIndex > 0) {
            c.attacking.reset();
        }
        c.stopMovement();
        removeAllWindows();
        c.teleX = x;
        c.teleY = y;
        c.faceUpdate(0);
        c.teleHeight = height;
        c.teleGfxTime = 9;
        c.teleGfxDelay = 0;
        c.teleSound = 0;
        c.setTektonDamageCounter(0);

        if (c.getGlodDamageCounter() >= 80 || c.getIceQueenDamageCounter() >= 80) {
            c.setGlodDamageCounter(79);
            c.setIceQueenDamageCounter(79);
        }

        if (teleportType.equalsIgnoreCase("modern") || teleportType.equals("glory")) {
            c.startAnimation(714);
            c.teleTimer = 11;
            c.teleGfxTime = 8;
            c.teleGfxDelay = 20;
            c.teleGfx = 308;
            c.teleEndAnimation = 715;
            c.teleSound = 200;
        } else if (teleportType.equalsIgnoreCase("ancient")) {
            c.startAnimation(1979);
            c.teleGfx = 0;
            c.teleTimer = 9;
            c.teleEndAnimation = 0;
            c.gfx0(392);
        } else if (teleportType.equalsIgnoreCase("pod")) {
            c.startAnimation(4544);
            c.teleEndAnimation = 65535;
            c.teleGfx = 0;
            c.teleTimer = 9;
            c.gfx0(767);

        } else if (teleportType.equals("obelisk")) {
            c.startAnimation(1816);
            c.teleTimer = 11;
            c.teleGfx = 661;
            c.teleEndAnimation = 65535;
        } else if (teleportType.equals("puropuro")) {
            c.startAnimation(6724);
            c.gfx0(1118);
            c.teleTimer = 13;
            c.teleEndAnimation = 65535;
        }
        if (!homeTeleport) {
            c.lastTeleportX = x;
            c.lastTeleportY = y;
            c.lastTeleportZ = height;
        }
        c.getPA().stopSkilling();
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return;
        }
    }

    public void startTeleport2(int x, int y, int height) {
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            c.sendMessage("You cannot teleport until you finish what you're doing.");
            return;
        }
        boolean inLobby = LobbyType.stream().anyMatch(lobbyType -> {
            Optional<Lobby> lobbyOpt = LobbyManager.get(lobbyType);
            if (lobbyOpt.isPresent()) {
                return lobbyOpt.get().inLobby(c);
            }
            return false;
        });
        if (c.jailEnd > 1) {
            return;
        }
        if (inLobby) {
            c.sendMessage("You cannot teleport from here, please exit the way you entered!");
            return;
        }
        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return;
        }
        if (c.morphed) {
            c.sendMessage("You cannot do this now.");
            return;
        }
        if (Boundary.isIn(c, Boundary.RAIDS)) {
            c.getPotions().resetOverload();
        }
        if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            return;
        }
        if (Boundary.isIn(c, Boundary.HUNLLEF_BOSS_ROOM) && c.hunllefDead == false) {
            c.sendMessage("The Hunllef's powers disable your teleport.");
            return;
        }
        if (Boundary.isIn(c, Boundary.HESPORI) && !c.canLeaveHespori) {
            c.sendMessage("Hespori's magic prevents you from teleporting.");
            return;
        }
        if (Boundary.isIn(c, Boundary.RFD)) {
            c.sendMessage("You cannot teleport from here, use the portal.");
            return;
        }
        if (Boundary.isIn(c, Boundary.RAIDROOMS)) {
            if (c.getRaidsInstance() != null) {
                c.sendMessage("You cannot teleport out of the raids. Please use the stairs!");
                return;
            }
        }
        if (c.isTeleblocked()) {
            c.sendMessage("You are teleblocked and can't teleport.");
            return;
        }
        if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
            c.sendMessage("You cannot teleport out of fight caves.");
            return;
        }
        if (Boundary.isIn(c, Boundary.ICE_PATH) || Boundary.isIn(c, Boundary.ICE_PATH_TOP) && c.heightLevel > 0) {
            c.sendMessage("The cold from the ice-path is preventing you from teleporting.");
            return;
        }
        if (Lowpkarena.getState(c) != null || Highpkarena.getState(c) != null) {
            c.sendMessage("You can't teleport from a Pk event!");
            return;
        }
        if (c.isDead) {
            return;
        }
        if (!c.isDead && c.teleTimer == 0) {
            c.stopMovement();
            removeAllWindows();
            c.teleX = x;
            c.teleY = y;
            c.faceUpdate(0);
            c.teleHeight = height;
            c.startAnimation(714);
            c.teleTimer = 9;
            c.teleGfx = 308;
            c.teleEndAnimation = 715;
            c.setTektonDamageCounter(0);
            if (c.getGlodDamageCounter() >= 80 || c.getIceQueenDamageCounter() >= 80) {
                c.setGlodDamageCounter(79);
                c.setIceQueenDamageCounter(79);
            }
            c.getPA().stopSkilling();
        }
    }

    public void setFollowingEntity(Entity entity, boolean combatFollowing) {
        resetFollow();
        c.combatFollowing = combatFollowing;
        if (entity.isNPC()) {
            c.npcFollowingIndex = entity.getIndex();
        } else {
            c.playerFollowingIndex = entity.getIndex();
        }
    }

    public void followNPC(NPC npc, boolean combat) {
        if (npc != null) {
            c.npcFollowingIndex = npc.getIndex();
            c.combatFollowing = combat;
        }
    }

    public void followNpc() {
        if (NPCHandler.npcs[c.npcFollowingIndex] == null || NPCHandler.npcs[c.npcFollowingIndex].isDead()) {
            c.npcFollowingIndex = 0;
            return;
        }
        if (c.morphed) {
            return;
        }
        if (c.freezeTimer > 0) {
            return;
        }
        if (c.isDead || c.playerLevel[3] <= 0)
            return;

        NPC npc = NPCHandler.npcs[c.npcFollowingIndex];
        int otherX = NPCHandler.npcs[c.npcFollowingIndex].getX();
        int otherY = NPCHandler.npcs[c.npcFollowingIndex].getY();
        double distanceToNpc = npc.getDistance(c.absX, c.absY);
        boolean withinDistance = distanceToNpc <= 2;

        if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
            c.npcFollowingIndex = 0;
            return;
        }

        c.faceUpdate(c.npcFollowingIndex);
        if (distanceToNpc <= 1) {
            if (!npc.insideOf(c.absX, c.absY)) {
                if (npc.getSize() == 0) {
                    stopDiagonal(otherX, otherY);
                }
                return;
            }
        }

        boolean projectile = false;
        if (c.combatFollowing) {
            projectile = c.usingOtherRangeWeapons || c.usingBow || c.usingMagic || c.autocasting
                    || c.getCombatItems().usingCrystalBow() || c.playerEquipment[Player.playerWeapon] == 22550;
            if (!projectile
                    || PathChecker.raycast(c, npc, true)
                    || PathChecker.raycast(npc, c, true)) {
                if (c.attacking.checkNpcAttackDistance(npc, c)) {
                    return;
                }
            }
        }

        final int x = c.absX;
        final int y = c.absY;
        final int z = c.heightLevel;
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
                                boolean allowsInteraction = !projectile
                                        || projectile && PathChecker.raycast(c, npc, true);
                                boolean accessible = PathFinder.getPathFinder().accessable(c, x3, y3);

                                if (allowsInteraction && accessible) {
                                    lowDist = Misc.distance(x3, y3, x, y);
                                    lowX = x3;
                                    lowY = y3;
                                }
                            }
                        }
                    }


                    if (lowX != 0 && lowY != 0) {
                        PathFinder.getPathFinder().findRoute(c, lowX, lowY, true, 18, 18);
                        if (c.debugMessage) {
                            c.sendMessage("Path found to : " + lowX + ", " + lowY);
                        }
                    } else {
                        PathFinder.getPathFinder().findRoute(c, npc.absX, npc.absY, true, 18, 18);
                        if (c.debugMessage) {
                            c.sendMessage("No path found, reverting to npc position.");
                        }
                    }
                }
            } else {

                if (otherX == c.absX && otherY == c.absY) {
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
                } else if (c.isRunningToggled() && !withinDistance) {
                    if (otherY > c.getY() && otherX == c.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY - 1) + getMove(c.getY(),
                        // otherY - 1));
                        playerWalk(otherX, otherY - 1);
                    } else if (otherY < c.getY() && otherX == c.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY + 1) + getMove(c.getY(),
                        // otherY + 1));
                        playerWalk(otherX, otherY + 1);
                    } else if (otherX > c.getX() && otherY == c.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(),
                        // otherX - 1), 0);
                        playerWalk(otherX - 1, otherY);
                    } else if (otherX < c.getX() && otherY == c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), 0);
                        playerWalk(otherX + 1, otherY);
                    } else if (otherX < c.getX() && otherY < c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), getMove(c.getY(), otherY + 1) +
                        // getMove(c.getY(), otherY + 1));
                        playerWalk(otherX + 1, otherY + 1);
                    } else if (otherX > c.getX() && otherY > c.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(),
                        // otherX - 1), getMove(c.getY(), otherY - 1) +
                        // getMove(c.getY(), otherY - 1));
                        playerWalk(otherX - 1, otherY - 1);
                    } else if (otherX < c.getX() && otherY > c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), getMove(c.getY(), otherY - 1) +
                        // getMove(c.getY(), otherY - 1));
                        playerWalk(otherX + 1, otherY - 1);
                    } else if (otherX > c.getX() && otherY < c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
                        // otherX + 1), getMove(c.getY(), otherY - 1) +
                        // getMove(c.getY(), otherY - 1));
                        playerWalk(otherX + 1, otherY - 1);
                    }
                } else {
                    if (otherY > c.getY() && otherX == c.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY - 1));n
                        playerWalk(otherX, otherY - 1);
                    } else if (otherY < c.getY() && otherX == c.getX()) {
                        // walkTo(0, getMove(c.getY(), otherY + 1));
                        playerWalk(otherX, otherY + 1);
                    } else if (otherX > c.getX() && otherY == c.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1), 0);
                        playerWalk(otherX - 1, otherY);
                    } else if (otherX < c.getX() && otherY == c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1), 0);
                        playerWalk(otherX + 1, otherY);
                    } else if (otherX < c.getX() && otherY < c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(),
                        // otherY + 1));
                        playerWalk(otherX + 1, otherY + 1);
                    } else if (otherX > c.getX() && otherY > c.getY()) {
                        // walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(),
                        // otherY - 1));
                        playerWalk(otherX - 1, otherY - 1);
                    } else if (otherX < c.getX() && otherY > c.getY()) {
                        // walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(),
                        // otherY - 1));
                        playerWalk(otherX + 1, otherY - 1);
                    } else if (otherX > c.getX() && otherY < c.getY()) {
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
        if (PlayerHandler.players[c.playerFollowingIndex] == null || PlayerHandler.players[c.playerFollowingIndex].isDead) {
            c.playerFollowingIndex = 0;
            return;
        }
        if (c.morphed) {
            return;
        }
        if (c.freezeTimer > 0) {
            return;
        }
        if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
            DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
                    MultiplayerSessionType.DUEL);
            if (!Objects.isNull(session)) {
                if (session.getRules().contains(DuelSessionRules.Rule.NO_MOVEMENT)) {
                    c.playerFollowingIndex = 0;
                    return;
                }
            }
        }
        if (inPitsWait()) {
            c.playerFollowingIndex = 0;
        }

        if (c.isDead || c.getHealth().getCurrentHealth() <= 0)
            return;
        if (System.currentTimeMillis() - c.lastSpear < 3000) {
            c.sendMessage("You are stunned, you cannot move.");
            c.playerFollowingIndex = 0;
            return;
        }

        final Player other = PlayerHandler.players[c.playerFollowingIndex];
        final int otherX = PlayerHandler.players[c.playerFollowingIndex].getX();
        final int otherY = PlayerHandler.players[c.playerFollowingIndex].getY();

        if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
            c.playerFollowingIndex = 0;
            return;
        }
        if (c.combatFollowing) {
            boolean projectile = c.attacking.getCombatType() != CombatType.MELEE;
            if (!projectile
                    || PathChecker.raycast(c, other, true)
                    || PathChecker.raycast(other, c, true)) {

                if (c.attacking.checkPlayerAttackDistance(other, true, c)) {
                    c.stopMovement();
                    return;
                }

            }

            if (otherX == c.absX && otherY == c.absY) {
                if (!other.isWalkingQueueEmpty()) {
                    c.stopMovement();
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
            if (followX != 0 && followY != 0 && PathFinder.getPathFinder().accessable(c, followX, followY)) {
                playerWalk(followX, followY);
            } else if (Misc.distance(c.absX, c.absY, otherX, otherY) != 1.0) {
                Pair<Integer, Integer> tile = getFollowPosition(other, otherX, otherY, false);
                if (tile.getLeft() != 0 && tile.getRight() != 0) {
                    playerWalk(tile.getLeft(), tile.getRight());
                } else {
                    playerWalk(otherX, otherY);
                }
            }
        }

        c.faceUpdate(c.playerFollowingIndex + 32768);
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
            double dist = Misc.distance(c.absX, c.absY, x2, y2);
            if (lowDist == 0 || dist < lowDist) {
                //if (wild == new Position(x2, y2).inWild()) {
                if (!projectile || PathChecker.raycast(c, other, true)
                        || PathChecker.raycast(other, c, true)) {
                    if (PathFinder.getPathFinder().accessable(c, x2, y2) && (c.combatFollowing || PathChecker.isMeleePathClear(c, x2, y2, c.heightLevel, otherX, otherY))) {
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
        sendFrame126(c.getRunEnergy() + "%", 22539);
    }

    public void sendStatement(String s) {
        sendFrame126(s, 357);
        sendFrame126("Click here to continue", 358);
        sendChatboxInterface(356);
    }

    public void resetFollow() {
        c.combatFollowing = false;
        c.playerFollowingIndex = 0;
        c.npcFollowingIndex = 0;
    }

    public void walkTo3(int i, int j) {
        c.newWalkCmdSteps = 0;
        if (++c.newWalkCmdSteps > 50)
            c.newWalkCmdSteps = 0;
        int k = c.absX + i;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = tmpNWCX[0] = tmpNWCY[0] = 0;
        int l = c.absY + j;
        l -= c.mapRegionY * 8;

        c.getNewWalkCmdX()[0] += k;
        c.getNewWalkCmdY()[0] += l;
        c.poimiY = l;
        c.poimiX = k;
    }

    private final int[] tmpNWCX = new int[50];
    private final int[] tmpNWCY = new int[50];

    public void walkTo(int i, int j) {
        c.newWalkCmdSteps = 0;
        if (++c.newWalkCmdSteps > 50)
            c.newWalkCmdSteps = 0;
        int k = c.getX() + i;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
        int l = c.getY() + j;
        l -= c.mapRegionY * 8;

        for (int n = 0; n < c.newWalkCmdSteps; n++) {
            c.getNewWalkCmdX()[n] += k;
            c.getNewWalkCmdY()[n] += l;
        }
    }

    public void walkTo2(int i, int j) {
        if (c.freezeDelay > 0)
            return;
        c.newWalkCmdSteps = 0;
        if (++c.newWalkCmdSteps > 50)
            c.newWalkCmdSteps = 0;
        int k = c.getX() + i;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
        int l = c.getY() + j;
        l -= c.mapRegionY * 8;

        for (int n = 0; n < c.newWalkCmdSteps; n++) {
            c.getNewWalkCmdX()[n] += k;
            c.getNewWalkCmdY()[n] += l;
        }
    }

    private void stopDiagonal(int otherX, int otherY) {
        if (c.freezeDelay > 0)
            return;
        if (c.freezeTimer > 0) // player can't move
            return;
        c.newWalkCmdSteps = 1;
        int xMove = otherX - c.getX();
        int yMove = 0;
        if (xMove == 0)
            yMove = otherY - c.getY();
        /*
         * if (!clipHor) { yMove = 0; } else if (!clipVer) { xMove = 0; }
         */

        int k = c.getX() + xMove;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
        int l = c.getY() + yMove;
        l -= c.mapRegionY * 8;

        for (int n = 0; n < c.newWalkCmdSteps; n++) {
            c.getNewWalkCmdX()[n] += k;
            c.getNewWalkCmdY()[n] += l;
        }

    }

    public void requestUpdates() {
        // if (!c.initialized) {
        // return;
        // }
        c.setUpdateRequired(true);
        c.setAppearanceUpdateRequired(true);
    }

    /*
     * public void Obelisks(int id) { if (!c.getItems().playerHasItem(id)) {
     * c.getItems().addItem(id, 1); } }
     */

    public void levelUp(int skill) {
        int totalLevel = (getLevelForXP(c.playerXP[0]) + getLevelForXP(c.playerXP[1]) + getLevelForXP(c.playerXP[2])
                + getLevelForXP(c.playerXP[3]) + getLevelForXP(c.playerXP[4]) + getLevelForXP(c.playerXP[5])
                + getLevelForXP(c.playerXP[6]) + getLevelForXP(c.playerXP[7]) + getLevelForXP(c.playerXP[8])
                + getLevelForXP(c.playerXP[9]) + getLevelForXP(c.playerXP[10]) + getLevelForXP(c.playerXP[11])
                + getLevelForXP(c.playerXP[12]) + getLevelForXP(c.playerXP[13]) + getLevelForXP(c.playerXP[14])
                + getLevelForXP(c.playerXP[15]) + getLevelForXP(c.playerXP[16]) + getLevelForXP(c.playerXP[17])
                + getLevelForXP(c.playerXP[18]) + getLevelForXP(c.playerXP[19]) + getLevelForXP(c.playerXP[20])
                + getLevelForXP(c.playerXP[21]));
        sendFrame126("" + totalLevel, 3984);
        switch (skill) {
            case 0:
                sendFrame126("Congratulations, you just advanced an attack level!", 6248);
                sendFrame126("Your attack level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6249);
                c.sendMessage("Congratulations, you just advanced an attack level.");
                sendChatboxInterface(6247);
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 1:
                sendFrame126("Congratulations, you just advanced a defence level!", 6254);
                sendFrame126("Your defence level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6255);
                c.sendMessage("Congratulations, you just advanced a defence level.");
                sendChatboxInterface(6253);
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 2:
                sendFrame126("Congratulations, you just advanced a strength level!", 6207);
                sendFrame126("Your strength level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6208);
                c.sendMessage("Congratulations, you just advanced a strength level.");
                sendChatboxInterface(6206);
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 3:
                c.getHealth().setMaximumHealth(c.getLevelForXP(c.playerXP[Player.playerHitpoints]));
                sendFrame126("Congratulations, you just advanced a hitpoints level!", 6217);
                sendFrame126("Your hitpoints level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6218);
                c.sendMessage("Congratulations, you just advanced a hitpoints level.");
                sendChatboxInterface(6216);
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 4:
                c.sendMessage("Congratulations, you just advanced a ranging level.");
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 5:
                sendFrame126("Congratulations, you just advanced a prayer level!", 6243);
                sendFrame126("Your prayer level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6244);
                c.sendMessage("Congratulations, you just advanced a prayer level.");
                sendChatboxInterface(6242);
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 6:
                sendFrame126("Congratulations, you just advanced a magic level!", 6212);
                sendFrame126("Your magic level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6213);
                c.sendMessage("Congratulations, you just advanced a magic level.");
                sendChatboxInterface(6211);
                if (c.combatLevel >= 126) {
                    c.getEventCalendar().progress(EventChallenge.HAVE_126_COMBAT);
                }
                break;

            case 7:
                sendFrame126("Congratulations, you just advanced a cooking level!", 6227);
                sendFrame126("Your cooking level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6228);
                c.sendMessage("Congratulations, you just advanced a cooking level.");
                sendChatboxInterface(6226);
                break;

            case 8:
                sendFrame126("Congratulations, you just advanced a woodcutting level!", 4273);
                sendFrame126("Your woodcutting level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4274);
                c.sendMessage("Congratulations, you just advanced a woodcutting level.");
                sendChatboxInterface(4272);
                break;

            case 9:
                sendFrame126("Congratulations, you just advanced a fletching level!", 6232);
                sendFrame126("Your fletching level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6233);
                c.sendMessage("Congratulations, you just advanced a fletching level.");
                sendChatboxInterface(6231);
                break;

            case 10:
                sendFrame126("Congratulations, you just advanced a fishing level!", 6259);
                sendFrame126("Your fishing level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6260);
                c.sendMessage("Congratulations, you just advanced a fishing level.");
                sendChatboxInterface(6258);
                break;

            case 11:
                sendFrame126("Congratulations, you just advanced a fire making level!", 4283);
                sendFrame126("Your firemaking level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4284);
                c.sendMessage("Congratulations, you just advanced a fire making level.");
                sendChatboxInterface(4282);
                break;

            case 12:
                sendFrame126("Congratulations, you just advanced a crafting level!", 6264);
                sendFrame126("Your crafting level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6265);
                c.sendMessage("Congratulations, you just advanced a crafting level.");
                sendChatboxInterface(6263);
                break;

            case 13:
                sendFrame126("Congratulations, you just advanced a smithing level!", 6222);
                sendFrame126("Your smithing level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6223);
                c.sendMessage("Congratulations, you just advanced a smithing level.");
                sendChatboxInterface(6221);
                break;

            case 14:
                sendFrame126("Congratulations, you just advanced a mining level!", 4417);
                sendFrame126("Your mining level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4438);
                c.sendMessage("Congratulations, you just advanced a mining level.");
                sendChatboxInterface(4416);
                break;

            case 15:
                sendFrame126("Congratulations, you just advanced a herblore level!", 6238);
                sendFrame126("Your herblore level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6239);
                c.sendMessage("Congratulations, you just advanced a herblore level.");
                sendChatboxInterface(6237);
                break;

            case 16:
                sendFrame126("Congratulations, you just advanced a agility level!", 4278);
                sendFrame126("Your agility level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4279);
                c.sendMessage("Congratulations, you just advanced an agility level.");
                sendChatboxInterface(4277);
                break;

            case 17:
                sendFrame126("Congratulations, you just advanced a thieving level!", 4263);
                sendFrame126("Your theiving level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4264);
                c.sendMessage("Congratulations, you just advanced a thieving level.");
                sendChatboxInterface(4261);
                break;

            case 18:
                sendFrame126("Congratulations, you just advanced a slayer level!", 12123);
                sendFrame126("Your slayer level is now " + getLevelForXP(c.playerXP[skill]) + ".", 12124);
                c.sendMessage("Congratulations, you just advanced a slayer level.");
                sendChatboxInterface(12122);
                break;

            case 19:
                c.sendMessage("Congratulations! You've just advanced a Farming level.");
                break;

            case 20:
                sendFrame126("Congratulations, you just advanced a runecrafting level!", 4268);
                sendFrame126("Your runecrafting level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4269);
                c.sendMessage("Congratulations, you just advanced a runecrafting level.");
                sendChatboxInterface(4267);
                break;

            case 21:
            case 22:
                c.sendMessage("Congratulations! You've just advanced a Hunter level.");
                break;
        }
        if (c.totalLevel >= 2000) {
            c.getEventCalendar().progress(EventChallenge.HAVE_2000_TOTAL_LEVEL);
        }

        if (c.getRights().isNotAdmin()) {
            if (getLevelForXP(c.playerXP[skill]) == 99) {
                Skill s = Skill.forId(skill);
                int iconId = Skill.iconForSkill(s) + 134;
                PlayerHandler.executeGlobalMessage(
                        "<col=6432a8>" + c.getDisplayNameFormatted() + " has reached level 99 <icon=" + iconId + "> " + s.toString() + " on " + c.getMode().getType().getFormattedName() + " mode!");
            }

            if (c.maxRequirements(c)) {
                PlayerHandler.executeGlobalMessage("<col=6432a8>" + c.getDisplayNameFormatted() + " has reached max total level on " + c.getMode().getType().getFormattedName() + " mode!");
            }
        }

        c.dialogueAction = 0;
        c.nextChat = 0;
    }

    public void refreshSkill(int i) {
        c.combatLevel = c.calculateCombatLevel();
        if (i == Player.playerHitpoints) {
            setSkillLevel(i, c.getHealth().getCurrentHealth(), c.playerXP[i]);
        } else {
            setSkillLevel(i, c.playerLevel[i], c.playerXP[i]);
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
            output = (int) Math.floor(points / 4);
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
            output = (int) Math.floor(points / 4);
            if (output >= exp) {
                return lvl;
            }
        }
        return 0;
    }

    public boolean hasSkillLevels(SkillLevel... skillLevels) {
        return Arrays.stream(skillLevels).allMatch(skill -> getLevelForXP(c.playerXP[skill.getSkill().getId()]) >= skill.getLevel());
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
            c.getPA().sendExperienceDrop(true, drop.amount, drop.skill);
        }
    }

    public boolean addSkillXPMultiplied(double amount, int skill, boolean dropExperience) {
        return addSkillXP((int) (c.getMode().getType().getExperienceRate(Skill.forId(skill)) * amount), skill, dropExperience);
    }

    public boolean addSkillXP(int amount, int skill, boolean dropExperience) {
        if (amount <= 0)
            return false;
        if (c.skillLock[skill]) {
            return false;
        }
        if (TourneyManager.getSingleton().isInArena(c)) {
            return false;
        }

        if (Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
            return false;
        }
        if (c.expLock && skill <= 6) {
            return false;
        }
        if (amount + c.playerXP[skill] < 0) {
            return false;
        }

        List<? extends Booster<?>> boosts = Boosts.getBoostsOfType(c, Skill.forId(skill), BoostType.EXPERIENCE);
        if (!boosts.isEmpty()) {
            amount *= 1.5; // All boosts are 1.5 for now!
        }
        if (c.getMode().getType().isStandardRate(Skill.forId(skill))) {
            LeaderboardUtils.addCount(LeaderboardType.STANDARD_XP, c, amount);
        } else {
            LeaderboardUtils.addCount(LeaderboardType.ROGUE_XP, c, amount);
        }

        if (dropExperience) {
            addXpDrop(new XpDrop(amount, skill));
        }
        int oldLevel = getLevelForXP(c.playerXP[skill]);
        int oldExperience = c.playerXP[skill];
        if (oldExperience < Skill.MAX_EXP && oldExperience + amount >= Skill.MAX_EXP) {
            Skill s = Skill.forId(skill);
            int iconId = Skill.iconForSkill(s) + 134;
            c.xpMaxSkills += 1;
            if (c.getRights().isNotAdmin()) {
                PlayerHandler.executeGlobalMessage("<col=6432a8>" + c.getDisplayNameFormatted() + " has reached 200M XP in " +
                        "<icon=" + iconId + "> " + s.toString() + " on " + c.getMode().getType().getFormattedName() + " mode!");
                c.sendMessage("@blu@You have now maxed 200m experience in @red@" + c.xpMaxSkills + " skills!");

                if (c.xpMaxSkills > 21) {
                    PlayerHandler.executeGlobalMessage(
                            "<col=6432a8>" + c.getDisplayNameFormatted() + " has reached 200M XP in all skills on " + c.getMode().getType().getFormattedName() + " mode!");
                }
            }
        }

        if (c.playerXP[skill] + amount > Skill.MAX_EXP) {
            c.playerXP[skill] = Skill.MAX_EXP;
        } else {
            c.playerXP[skill] += amount;
        }

        if (c.playerXP[skill] >= Skill.MAX_EXP && c.gained200mTime[skill] == 0) {
            c.gained200mTime[skill] = System.currentTimeMillis();
        }

        if (oldLevel < getLevelForXP(c.playerXP[skill])) {
            int newLevel = getLevelForXP(c.playerXP[skill]);
            if (c.playerLevel[skill] < newLevel && skill != 3 && skill != 5)
                c.playerLevel[skill] = newLevel;

            c.combatLevel = c.calculateCombatLevel();
            c.totalLevel = c.getPA().calculateTotalLevel();
            c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
            levelUp(skill);
            c.gfx100(199);
            if (skill == Skill.HITPOINTS.getId()) {
                c.getHealth().setMaximumHealth(newLevel);
                c.getHealth().increase(newLevel - oldLevel);
            }
            requestUpdates();
        }
        setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
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
        c.outStream.createFrame(254);
        c.outStream.writeByte(type);

        int k = 0, l = 0;

        if (type == 1 || type == 10) {
            c.outStream.writeUShort(index);
            c.outStream.writeUShort(k);
            c.outStream.writeByte(l);
        } else {
            c.outStream.writeUShort(k);
            c.outStream.writeUShort(l);
            c.outStream.writeByte(index);
        }
    }

    public void removePlayerHintIcon() {
        displayPlayerHintIcon(-1);
    }

    public void displayPlayerHintIcon(int index) {
        c.outStream.createFrame(254);
        c.outStream.writeByte(10);
        c.outStream.writeUShort(index);
        c.outStream.writeUShort(0);
        c.outStream.writeByte(0);
    }

    public void displayStaticIcon(int type, int x, int y, int iconHeight) {
        if (iconHeight > 255)
            iconHeight = 255;
        c.outStream.createFrame(254);
        c.outStream.writeByte(type);
        c.outStream.writeUShort(x);
        c.outStream.writeUShort(y);
        c.outStream.writeByte(iconHeight);
    }

    public void runClientScript(int scriptId, Object... params) {
        if (c.getOutStream() != null) {

            // Clear interface text
            if (scriptId == 4) {
                int startInterfaceId = (Integer) params[0];
                int length = (Integer) params[1];
                for (int i = startInterfaceId; i < startInterfaceId + length; i++)
                    interfaceText.put(i, new TinterfaceText("", i));
            }

            c.getOutStream().createFrameVarSizeWord(13);
            StringBuilder types = new StringBuilder();
            Arrays.stream(params).forEach(it -> types.append(it instanceof Integer ? "i" : "s"));
            c.getOutStream().writeString(types.toString());

            for (Object param : params) {
                if (param instanceof Integer) {
                    c.getOutStream().writeDWord((int) param);
                } else {
                    c.getOutStream().writeNullTerminatedString(param.toString());
                }
            }

            c.getOutStream().writeUShort(scriptId);
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public void updateQuestTab() {
        if (c.d1Complete == true) {
            sendFrame126("@gre@Varrock", 29480);
        } else {
            sendFrame126("@red@Varrock", 29480);
        }
        if (c.d2Complete == true) {
            sendFrame126("@gre@Ardougne", 29481);
        } else {
            sendFrame126("@red@Ardougne", 29481);
        }
        if (c.d3Complete == true) {
            sendFrame126("@gre@Desert", 29482);
        } else {
            sendFrame126("@red@Desert", 29482);
        }
        if (c.d4Complete == true) {
            sendFrame126("@gre@Falador", 29483);
        } else {
            sendFrame126("@red@Falador", 29483);
        }
        if (c.d5Complete == true) {
            sendFrame126("@gre@Fremnnik", 29484);
        } else {
            sendFrame126("@red@Fremnnik", 29484);
        }
        if (c.d6Complete == true) {
            sendFrame126("@gre@Kandarin", 29485);
        } else {
            sendFrame126("@red@Kandarin", 29485);
        }
        if (c.d7Complete == true) {
            sendFrame126("@gre@Karamja", 29486);
        } else {
            sendFrame126("@red@Karamja", 29486);
        }
        if (c.d8Complete == true) {
            sendFrame126("@gre@Lumbridge & Draynor", 29487);
        } else {
            sendFrame126("@red@Lumbridge & Draynor", 29487);
        }
        if (c.d9Complete == true) {
            sendFrame126("@gre@Morytania", 29488);
        } else {
            sendFrame126("@red@Morytania", 29488);
        }
        if (c.d10Complete == true) {
            sendFrame126("@gre@Western", 29489);
        } else {
            sendFrame126("@red@Western", 29489);
        }
        if (c.d11Complete == true) {
            sendFrame126("@gre@Wilderness", 29490);
        } else {
            sendFrame126("@red@Wilderness", 29490);
        }
    }

    public void handleGlory() {
        if (c.getPosition().inWild() && c.wildLevel > Configuration.JEWELERY_TELEPORT_MAX_WILD_LEVEL) {
            return;
        }
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            c.sendMessage("You cannot do that right now.");
            return;
        }
        c.getDH().sendOption4("Edgeville", "Karamja", "Draynor", "Al Kharid");
        c.sendMessage("You rub the amulet...");
        c.usingGlory = true;
    }

    public void handleSkills() {
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            c.sendMessage("You cannot do that right now.");
            return;
        }
        c.getDH().sendOption4("Land's End", "Piscarilius Mining", "Resource Area", "Beach Bank");
        c.sendMessage("You rub the amulet...");
        c.usingSkills = true;
    }

    public void resetVariables() {
        if (c.playerIsCrafting) {
            CraftingData.resetCrafting(c);
        }
        if (c.playerSkilling[9]) {
            c.playerSkilling[9] = false;
        }
        if (c.isBanking) {
            c.isBanking = false;
        }
        c.viewingRunePouch = false;
        if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen())
            c.getLootingBag().closeLootbag();
        c.viewingPresets = false;
        c.usingGlory = false;
        c.usingSkills = false;
        c.smeltInterface = false;
        if (c.dialogueAction > -1) {
            c.dialogueAction = -1;
        }
        if (c.teleAction > -1) {
            c.teleAction = -1;
        }
        if (c.battlestaffDialogue) {
            c.battlestaffDialogue = false;
        }
        if (c.craftDialogue) {
            c.craftDialogue = false;
        }
        c.closedInterface();
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.BONE_ON_ALTAR);
    }

    public boolean inPitsWait() {
        return c.getX() <= 2404 && c.getX() >= 2394 && c.getY() <= 5175 && c.getY() >= 5169;
    }

    public boolean checkForFlags() {
        int[][] itemsToCheck = {{995, 100000000}, {35, 5}, {667, 5}, {2402, 5}, {746, 5}, {4151, 150},
                {565, 100000}, {560, 100000}, {555, 300000}, {11235, 10}};
        for (int[] anItemsToCheck : itemsToCheck) {
            if (anItemsToCheck[1] < c.getItems().getTotalCount(anItemsToCheck[0]))
                return true;
        }
        return false;
    }

    public int calculateTotalLevel() {
        int total = 0;
        for (int i = 0; i <= 21; i++) {

            total += getLevelForXP(c.playerXP[i]);
        }
        return total;
    }

    public long getTotalXP() {
        return Arrays.stream(c.playerXP).asLongStream().sum();
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
            Degrade.checkPercentage(c, itemId);
            return;
        }
        switch (itemId) {
            case 9948: // Teleport to puro puro
            case 9949:
                if (WheatPortalEvent.xLocation > 0 && WheatPortalEvent.yLocation > 0) {
                    c.getPA().spellTeleport(WheatPortalEvent.xLocation + 1, WheatPortalEvent.yLocation + 1, 0, false);
                } else {
                    c.sendMessage("There is currently no portal available, wait 5 minutes.");
                    return;
                }
                break;
            case 12904:
                c.sendMessage(
                        "The toxic staff of the dead has " + c.getToxicStaffOfTheDeadCharge() + " charges remaining.");
                break;
            case 13199:
            case 13197:
                c.sendMessage("The " + def.getName() + " has " + c.getSerpentineHelmCharge() + " charges remaining.");
                break;
            case 11907:
            case 12899:
            case Items.SANGUINESTI_STAFF:
                int charge = itemId == 11907 ? c.getTridentCharge() : itemId == Items.SANGUINESTI_STAFF ? c.getSangStaffCharge() : c.getToxicTridentCharge();
                c.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
                break;
            case 12926:
                c.getCombatItems().checkBlowpipeShotsRemaining();
                break;
            case 19675:
                c.sendMessage("Your Arclight has " + c.getArcLightCharge() + " charges remaining.");
                break;
            case 12931:
                def = ItemDef.forId(itemId);
                if (def == null) {
                    return;
                }
                c.sendMessage("The " + def.getName() + " has " + c.getSerpentineHelmCharge() + " charge remaining.");
                break;

            case 13136:
                if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
                    c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                    return;
                }
                if (Server.getMultiplayerSessionListener().inAnySession(c)) {
                    c.sendMessage("You cannot do that right now.");
                    return;
                }
                if (c.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
                    c.sendMessage(
                            "You can't teleport above level " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
                    return;
                }
                c.getPA().spellTeleport(3426, 2927, 0, false);
                break;

            case 13125:
            case 13126:
            case 13127:
                if (c.getRunEnergy() < 100) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishRun(50);
                    }
                } else {
                    c.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13128:
                if (c.getRunEnergy() < 100) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishRun(100);
                    }
                } else {
                    c.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13117:
                if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishPrayer(4);
                    }
                } else {
                    c.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13118:
                if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishPrayer(2);
                    }
                } else {
                    c.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13119:
            case 13120:
                if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishPrayer(1);
                    }
                } else {
                    c.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13111:
                if (c.getRechargeItems().useItem(itemId)) {
                    c.getPA().spellTeleport(3236, 3946, 0, false);
                }
                break;
            case 10507:
                if (c.getItems().isWearingItem(10507)) {
                    if (System.currentTimeMillis() - c.lastPerformedEmote < 2500)
                        return;
                    c.startAnimation(6382);
                    c.gfx0(263);
                    c.lastPerformedEmote = System.currentTimeMillis();
                }
                break;
            case 10026:
                if (c.getItems().isWearingItem(10026)) {
                    if (System.currentTimeMillis() - c.lastPerformedEmote < 2500)
                        return;
                    c.startAnimation(2769);
                    c.lastPerformedEmote = System.currentTimeMillis();
                }
                break;
            case 20243:
                if (System.currentTimeMillis() - c.lastPerformedEmote < 2500)
                    return;
                c.startAnimation(7268);
                c.lastPerformedEmote = System.currentTimeMillis();
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
                c.sendMessage("You currently have " + (250 - c.crystalBowArrowCount)
                        + " charges left before degradation to " + (c.playerEquipment[3] == 4223 ? "Crystal seed"
                        : ItemAssistant.getItemName(c.playerEquipment[3] + 1)));
                break;

            case 11864:
            case 11865:
            case 19639:
            case 19641:
            case 19643:
            case 19645:
            case 19647:
            case 19649:
                if (!c.getSlayer().getTask().isPresent()) {
                    c.sendMessage("You do not have a task!");
                    return;
                }
                c.sendMessage("I currently have @blu@" + c.getSlayer().getTaskAmount() + " "
                        + c.getSlayer().getTask().get().getPrimaryName() + "@bla@ to kill.");
                c.getPA().closeAllWindows();
                break;

            case 4202:
            case 9786:
            case 9787:
                ringOfCharosTeleport(c);
                break;

            case 11283:
            case 11284:
                if (Boundary.isIn(c, Boundary.ZULRAH) || Boundary.isIn(c, Boundary.CERBERUS_BOSSROOMS)
                        || Boundary.isIn(c, Boundary.SKOTIZO_BOSSROOM)) {
                    return;
                }
                DragonfireShieldEffect dfsEffect = new DragonfireShieldEffect();
                if (c.npcAttackingIndex <= 0 && c.playerAttackingIndex <= 0) {
                    return;
                }
                if (c.getHealth().getCurrentHealth() <= 0 || c.isDead) {
                    return;
                }
                if (dfsEffect.isExecutable(c)) {
                    Damage damage = new Damage(Misc.random(25));
                    if (c.playerAttackingIndex > 0) {
                        Player target = PlayerHandler.players[c.playerAttackingIndex];
                        if (Objects.isNull(target)) {
                            return;
                        }
                        c.attackTimer = 7;
                        dfsEffect.execute(c, target, damage);
                        c.setLastDragonfireShieldAttack(System.currentTimeMillis());
                    } else if (c.npcAttackingIndex > 0) {
                        NPC target = NPCHandler.npcs[c.npcAttackingIndex];
                        if (Objects.isNull(target)) {
                            return;
                        }
                        c.attackTimer = 7;
                        dfsEffect.execute(c, target, damage);
                        c.setLastDragonfireShieldAttack(System.currentTimeMillis());
                    }
                }
                break;

            case 1712:
            case 1710:
            case 1708:
            case 1706:
            case 19707:
                if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
                    c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                    return;
                }
                c.getPA().handleGlory();
                c.operateEquipmentItemId = itemId;
                c.isOperate = true;
                break;
            case 11968:
            case 11970:
            case 11105:
            case 11107:
            case 11109:
            case 11111:
                if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
                    c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
                    return;
                }
                c.getPA().handleSkills();
                c.operateEquipmentItemId = itemId;
                c.isOperate = true;
                break;
            case 2552:
            case 2554:
            case 2556:
            case 2558:
            case 2560:
            case 2562:
            case 2564:
            case 2566:
                c.getPA().spellTeleport(3304, 3130, 0, false);
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
                c.getDH().sendDialogues(76, 1);
                break;

            /*
             * Crafting cape
             */
            case 9780:
            case 9781:
                c.getPA().startTeleport(2936, 3283, 0, "modern", false);
                break;

            /*
             * Magic skillcape
             */
            case 9762:
            case 9763:
                if (!Boundary.isIn(c, Boundary.EDGEVILLE_PERIMETER)) {
                    c.sendMessage("This cape can only be operated within the edgeville perimeter.");
                    return;
                }
                if (c.getPosition().inWild()) {
                    return;
                }
                if (c.playerMagicBook == 0) {
                    c.playerMagicBook = 1;
                    c.setSidebarInterface(6, 12855);
                    c.autocasting = false;
                    c.sendMessage("An ancient wisdomin fills your mind.");
                    c.getPA().resetAutocast();
                } else if (c.playerMagicBook == 1) {
                    c.sendMessage("You switch to the lunar spellbook.");
                    c.setSidebarInterface(6, 29999);
                    c.playerMagicBook = 2;
                    c.autocasting = false;
                    c.autocastId = -1;
                    c.getPA().resetAutocast();
                } else if (c.playerMagicBook == 2) {
                    c.setSidebarInterface(6, 1151);
                    c.playerMagicBook = 0;
                    c.autocasting = false;
                    c.sendMessage("You feel a drain on your memory.");
                    c.autocastId = -1;
                    c.getPA().resetAutocast();
                }
                break;
        }
    }

    public void getSpeared(int otherX, int otherY, int distance) {
        int x = c.absX - otherX;
        int y = c.absY - otherY;
        int xOffset = 0;
        int yOffset = 0;
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
                MultiplayerSessionType.DUEL);
        if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION) {
            c.sendMessage("You cannot use this special whilst in the duel arena.");
            return;
        }
        if (x > 0) {
            if (c.getRegionProvider().getClipping(c.getX() + distance, c.getY(), c.heightLevel, 1, 0)) {
                xOffset = distance;
            }
        } else if (x < 0) {
            if (c.getRegionProvider().getClipping(c.getX() - distance, c.getY(), c.heightLevel, -1, 0)) {
                xOffset = -distance;
            }
        }
        if (y > 0) {
            if (c.getRegionProvider().getClipping(c.getX(), c.getY() + distance, c.heightLevel, 0, 1)) {
                yOffset = distance;
            }
        } else if (y < 0) {
            if (c.getRegionProvider().getClipping(c.getX(), c.getY() - distance, c.heightLevel, 0, -1)) {
                yOffset = -distance;
            }
        }
        moveCheck(xOffset, yOffset);
        c.lastSpear = System.currentTimeMillis();
    }

    private void moveCheck(int x, int y) {
        PathFinder.getPathFinder().findRoute(c, c.getX() + x, c.getY() + y, true, 1, 1);
    }

    /**
     * @author Jason MacKeigan (http://www.rune-server.org/members/jason)
     * @date Sep 26, 2014, 12:57:42 PM
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
            int amount = c.getItems().getItemAmount(itemId);
            String pointAlias = Misc.capitalizeJustFirst(pointVar.name().toLowerCase().replaceAll("_", " "));
            if (exchangeRate <= 0 || itemId < 0) {
                throw new IllegalStateException();
            }
            if (amount <= 0) {
                c.getDH().sendStatement("You do not have the items required to exchange", "for " + pointAlias + ".");
                c.nextChat = -1;
                return;
            }
            int exchange = amount * exchangeRate;
            c.getItems().deleteItem2(itemId, amount);
            switch (pointVar) {
                case PK_POINTS:
                    c.pkp += exchange;
                    c.getQuestTab().updateInformationTab();
                    break;

                case VOTE_POINTS:
                    c.votePoints += exchange;
                    c.getQuestTab().updateInformationTab();
                    break;
                case BLOOD_POINTS:
                    c.bloodPoints += amount;
                    exchange = amount;
                    break;
            }
            c.getDH().sendStatement("You exchange " + amount + " currency for " + exchange + " " + pointAlias + ".");
            c.nextChat = -1;
        } catch (IllegalStateException exception) {
            Misc.println("WARNING: Illegal state has been reached.");
            exception.printStackTrace();
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
        if (c == null || c.getOutStream() == null) {
            return;
        }
        if (seconds < 1 && state != 0) {
            throw new IllegalArgumentException("The amount of seconds cannot be less than one.");
        }
        c.getOutStream().createFrameVarSize(9);
        c.getOutStream().writeString(text);
        c.getOutStream().writeByte(state);
        c.getOutStream().writeByte(seconds);
        c.getOutStream().endFrameVarSize();
    }

    public void stillCamera(int x, int y, int height, int speed, int angle) {
        c.outStream.createFrame(177);
        c.outStream.writeByte(x / 64);
        c.outStream.writeByte(y / 64);
        c.outStream.writeUShort(height);
        c.outStream.writeByte(speed);
        c.outStream.writeByte(angle);
    }

    public void resetCamera() {
        c.outStream.createFrame(107);
        c.setUpdateRequired(true);
        c.appearanceUpdateRequired = true;
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
            c.startAnimation(767);
            c.appendDamage(null, Misc.random(1) + 1, Hitmark.HIT);
            c.resetWalkingQueue();
            c.forcedChat("Ouch!");
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
        for (int[] fish : Fishing.data) {
            if (fish[4] == item)
                return true;
        }
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
        if (c.isDisconnected() || c.getOutStream() == null) {
            return;
        }
        Stream stream = c.getOutStream();
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
        if (c.getOutStream() != null) {
            c.getOutStream().createFrameVarSizeWord(27);
            c.getOutStream().writeString(header);
            c.setEnterAmountInterfaceId(interfaceId);
            c.getOutStream().endFrameVarSizeWord();
        }
    }

    public void sendEnterAmount(String header, AmountInput amountInputHandler) {
        if (c.getOutStream() != null) {
            c.getOutStream().createFrameVarSizeWord(27);
            c.getOutStream().writeString(header);
            c.setEnterAmountInterfaceId(0);
            c.getOutStream().endFrameVarSizeWord();
            c.amountInputHandler = amountInputHandler;
        }
    }

    public void sendGameTimer(ClientGameTimer timer, TimeUnit unitOfTime, int duration) {
        if (c == null || c.isDisconnected()) {
            return;
        }
        Stream stream = c.getOutStream();
        if (stream == null) {
            return;
        }

        if (timer.isItem()) {
            int seconds = (int) Long.min(unitOfTime.toSeconds(duration), 65535);
            stream.createFrame(224);
            stream.writeUShort(timer.getTimerId());
            stream.writeUShort(timer.getTimerId());
            stream.writeUShort(seconds);
            c.flushOutStream();
        } else {
            int seconds = (int) Long.min(unitOfTime.toSeconds(duration), 65535);
            stream.createFrame(223);
            stream.writeByte(timer.getTimerId());
            stream.writeUShort(seconds);
            c.flushOutStream();
        }
    }

    public void sendExperienceDrop(boolean increase, long amount, int... skills) {
        if (c.isDisconnected() || c.getOutStream() == null) {
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
                    ArrayUtils.toPrimitive(illegalSkills.toArray(new Integer[illegalSkills.size()])));
        }
        if (ArrayUtils.isEmpty(skills)) {
            return;
        }
        if (increase) {
            c.setExperienceCounter(c.getExperienceCounter() + amount);
        }

        Stream stream = c.getOutStream();

        stream.createFrameVarSize(11);
        stream.writeQWord(amount);
        stream.writeByte(skills.length);
        for (int skillId : skills) {
            stream.writeByte(skillId);
        }
        stream.endFrameVarSize();
    }

    public void sendConfig(final int id, final int state) {
        if (this.c.getOutStream() != null && this.c != null) {
            if (state < 128) {
                this.c.getOutStream().createFrame(36);
                this.c.getOutStream().writeWordBigEndian(id);
                this.c.getOutStream().writeByte(state);
            } else {
                this.c.getOutStream().createFrame(87);
                this.c.getOutStream().writeWordBigEndian_dup(id);
                this.c.getOutStream().writeDWord_v1(state);
            }
            this.c.flushOutStream();
        }
    }

    public void sendTradingPost(int frame, int item, int slot, int amount) {
        if (c.getOutStream() != null && c != null) {
            c.getOutStream().createFrameVarSizeWord(34);
            c.getOutStream().writeUShort(frame);
            c.getOutStream().writeByte(slot);
            c.getOutStream().writeUShort(item + 1);
            c.getOutStream().writeByte(255);
            c.getOutStream().writeDWord(amount);
            c.getOutStream().endFrameVarSizeWord();
        }
    }

    public Map<Integer, String> getPlayerOptions() {
        return playerOptions;
    }
}
