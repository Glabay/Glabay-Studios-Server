package io.xeros.net.packets;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.combat.CombatItems;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.effects.damageeffect.impl.DragonfireShieldEffect;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.items.Degrade;
import io.xeros.content.skills.crafting.BryophytaStaff;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Objects;

public class OperateItem implements PacketType {

    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
        if (player.getMovementState().locked() || player.getLock().cannotInteract(player)) return;
        if (player.isFping()) return;

        player.interruptActions();
        int slot = player.getInStream().readSignedWordA(); // the row of the action
        int itemId = player.getInStream().readUnsignedWord(); //the item's id

        if (player.debugMessage)
            player.sendMessage("Operate Item - itemId: " + itemId + " slot: " + slot);

        var def = ItemDef.forId(itemId);
        // Handle degrade actions
        Degrade.DegradableItem.forId(itemId).ifPresent(degradableItem -> Degrade.checkPercentage(player, itemId));

        if (CombatItems.SLAYER_HELMETS_REGULAR.contains(itemId) || CombatItems.SLAYER_HELMETS_IMBUED.contains(itemId)) {
            switch (slot) {
                case 1:
                    if (player.getSlayer().getTask().isEmpty()) {
                        player.sendMessage("You do not have a task!");
                        return;
                    }
                    player.sendMessage("I currently have @blu@" + player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask().get().getPrimaryName() + "@bla@ to kill.");
                    player.getPA().closeAllWindows();
                    break;
                case 2:
                    if (Server.getMultiplayerSessionListener().inAnySession(player)) return;
                    player.getPA().resetQuestInterface();
                    int[] frames = {
                        8149, 8150, 8151, 8152, 8153, 8154, 8155,
                        8156, 8157, 8158, 8159, 8160, 8161, 8162,
                        8163, 8164, 8165, 8166, 8167, 8168, 8169,
                        8170, 8171, 8172, 8173, 8174, 8175, 8176,
                        8177, 8178, 8179, 8180, 8181, 8182, 8183,
                        8184, 8185, 8186, 8187, 8188, 8189, 8190,
                        8191, 8192, 8193, 8194
                    };

                    player.getPA().sendFrame126("@dre@Kill Tracker for @blu@" + player.getDisplayName(), 8144);
                    player.getPA().sendFrame126("", 8145);
                    player.getPA().sendFrame126("@blu@Total kills@bla@ - " + player.getNpcDeathTracker().getTotal(), 8147);
                    player.getPA().sendFrame126("", 8148);
                    int frameIndex = 0;
                    for (var entry : player.getNpcDeathTracker().getTracker().entrySet()) {
                        if (entry == null) continue;
                        if (frameIndex > frames.length - 1) break;
                        if (entry.getValue() > 0) {
                            player.getPA().sendFrame126("@blu@" + WordUtils.capitalize(entry.getKey().toLowerCase()) + ": @red@" + entry.getValue(), frames[frameIndex]);
                            frameIndex++;
                        }
                    }
                    player.getPA().openQuestInterface();
                    break;
            }
            return;
        }

        if (BryophytaStaff.handleItemOption(player, itemId, 2))
            return;

        var itemActionManager = Server.getItemOptionActionManager();
        if (itemActionManager.findHandlerById(itemId).isPresent()) {
            var itemAction = itemActionManager.findHandlerById(itemId).get();
            if (itemAction.performActionItemOperation(player, itemId))
                return;
            logger.error("Unhandled Item Action 4: {} ", itemAction.getClass().getSimpleName());
        }
        switch (itemId) {
            case Items.SANGUINESTI_STAFF:
                SanguinestiStaff.checkChargesRemaining(player);
                break;
            case 2550:
                player.sendMessage("You have @red@" + (40 - player.recoilHits) + "@bla@ recoil charges left.");
                break;
            case 21183:
                player.sendMessage("Your bracelet of slaughter has @red@" + player.slaughterCharge + "@bla@ charges left.");
                break;
            case 9948:
            case 9949:
                player.getPA().spellTeleport(2592, 4321, 0, false);
            case 12904:
                player.sendMessage("The toxic staff of the dead has " + player.getToxicStaffOfTheDeadCharge() + " charges remaining.");
                break;

            case 13660:
                if (player.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
                    player.sendMessage("You can't teleport above " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
                    return;
                }
                player.getTeleportInterface().openInterface();
                return;
            case Items.TOME_OF_FIRE_EMPTY:
            case Items.TOME_OF_FIRE:
                int pages = player.getTomeOfFire().getPages();
                int charges = player.getTomeOfFire().getCharges();
                player.sendMessage("You currently have " + pages + " pages and " + charges + " charges left in your tome of fire.");
                break;

            case 13199:
            case 13197:
                player.sendMessage("The " + def.getName() + " has " + player.getSerpentineHelmCharge() + " charges remaining.");
                break;
            case 19675:
                player.sendMessage("Your Arclight has " + player.getArcLightCharge() + " charges remaining.");
                break;
            case 11907:
            case 12899:
                int charge = itemId == 11907 ? player.getTridentCharge() : player.getToxicTridentCharge();
                player.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
                break;
            case 12926:
                player.getCombatItems().checkBlowpipeShotsRemaining();
                break;
            case 12931:
                def = ItemDef.forId(itemId);
                if (def == null) return;
                player.sendMessage("The " + def.getName() + " has " + player.getSerpentineHelmCharge() + " charge remaining.");
                break;

            case 13125:
            case 13126:
            case 13127:
                if (player.getRunEnergy() < 100) {
                    if (player.getRechargeItems().useItem(itemId))
                        player.getRechargeItems().replenishRun(50);
                }
                else {
                    player.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13128:
                if (player.getRunEnergy() < 100) {
                    if (player.getRechargeItems().useItem(itemId)) player.getRechargeItems().replenishRun(100);
                }
                else {
                    player.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13117:
                if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
                    if (player.getRechargeItems().useItem(itemId)) player.getRechargeItems().replenishPrayer(4);
                }
                else {
                    player.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13118:
                if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
                    if (player.getRechargeItems().useItem(itemId)) player.getRechargeItems().replenishPrayer(2);
                }
                else {
                    player.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13119:
            case 13120:
                if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
                    if (player.getRechargeItems().useItem(itemId)) player.getRechargeItems().replenishPrayer(1);
                }
                else {
                    player.sendMessage("You already have full prayer points.");
                    return;
                }
                break;
            case 13111:
                if (player.getRechargeItems().useItem(itemId)) player.getPA().spellTeleport(3236, 3946, 0, false);
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

            case 20243:
                if (System.currentTimeMillis() - player.lastPerformedEmote < 2500)
                    return;
                player.startAnimation(7268);
                player.lastPerformedEmote = System.currentTimeMillis();
                break;
            case 2572:
            case 12785:
                if (player.collectCoins) {
                    player.collectCoins = false;
                    player.sendMessage("@blu@You have now @red@Disabled@blu@ your coin collecting.");
                }
                else if (!player.collectCoins) {
                    player.collectCoins = true;
                    player.sendMessage("@blu@You have now @gre@Enabled@blu@ your coin collecting.");
                }
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
                player.sendMessage("You currently have " + (250 - player.crystalBowArrowCount) + " charges left before degradation to " + (player.playerEquipment[3] == 4223 ? "Crystal seed" : ItemAssistant.getItemName(player.playerEquipment[3] + 1)));
                break;

            case 4202:
            case 9786:
            case 9787:
                PlayerAssistant.ringOfCharosTeleport(player);
                break;

            case 11283:
            case 11284:
                if (Boundary.isIn(player, Boundary.ZULRAH) || Boundary.isIn(player, Boundary.CERBERUS_BOSSROOMS)) return;
                DragonfireShieldEffect dfsEffect = new DragonfireShieldEffect();
                if (player.npcAttackingIndex <= 0 && player.playerAttackingIndex <= 0) return;
                if (player.getHealth().getCurrentHealth() <= 0 || player.isDead) return;
                if (dfsEffect.isExecutable(player)) {
                    Damage damage = new Damage(Misc.random(25));
                    if (player.playerAttackingIndex > 0) {
                        Player target = PlayerHandler.players[player.playerAttackingIndex];
                        if (Objects.isNull(target)) return;
                        player.attackTimer = 7;
                        dfsEffect.execute(player, target, damage);
                        player.setLastDragonfireShieldAttack(System.currentTimeMillis());
                    }
                    else if (player.npcAttackingIndex > 0) {
                        NPC target = NPCHandler.npcs[player.npcAttackingIndex];
                        if (Objects.isNull(target)) return;
                        player.attackTimer = 7;
                        dfsEffect.execute(player, target, damage);
                        player.setLastDragonfireShieldAttack(System.currentTimeMillis());
                    }
                }
                break;

            /*
              Max capes
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
              Crafting cape
             */
            case 9780:
            case 9781:
                player.getPA().startTeleport(2936, 3283, 0, "modern", false);
                break;
            /*
              Bracelet of etherium
             */
            case 21816:
                switch (slot) {
                    case 1:
                        player.sendMessage("@blu@You have @red@" + player.braceletEtherCount + "@blu@ charges in your bracelet");
                        break;
                    case 2:
                        if (!player.absorption) {
                            player.absorption = true;
                            player.sendMessage("@blu@You have @red@Activated @blu@absorption for your bracelet.");
                            return;
                        }
                        else if (player.absorption) {
                            player.absorption = false;
                            player.sendMessage("@blu@You have @red@Deactivated @blu@absorption for your bracelet.");
                        }
                        break;
                }
                break;


            /*
              Magic skillcape
             */
            case 9762:
            case 9763:
                if (Boundary.isIn(player, Boundary.EDGEVILLE_PERIMETER)) {
                    if (player.playerMagicBook == 0) {
                        player.playerMagicBook = 1;
                        player.setSidebarInterface(6, 838);
                        player.autocasting = false;
                        player.sendMessage("An ancient wisdomin fills your mind.");
                        player.getPA().resetAutocast();
                    }
                    else if (player.playerMagicBook == 1) {
                        player.sendMessage("You switch to the lunar spellbook.");
                        player.setSidebarInterface(6, 29999);
                        player.playerMagicBook = 2;
                        player.autocasting = false;
                        player.autocastId = -1;
                        player.getPA().resetAutocast();
                    }
                    else if (player.playerMagicBook == 2) {
                        player.setSidebarInterface(6, 938);
                        player.playerMagicBook = 0;
                        player.autocasting = false;
                        player.sendMessage("You feel a drain on your memory.");
                        player.autocastId = -1;
                        player.getPA().resetAutocast();
                    }
                }
                else player.sendMessage("You need to be in Edgeville to use this.");
                break;
            case 13136:
                switch (slot) {
                    case 1:
                        player.getPA().spellTeleport(3484, 9510, 2, false);
                        break;
                    case 2:
                        player.getPA().spellTeleport(3426, 2927, 0, false);
                        break;
                }
                break;
            case 1704:
                player.sendMessage("@red@You currently have no charges in your glory.");
                break;
            case 1712:
            case 1710:
            case 1708:
            case 1706:
            case 19707:
                if (player.isTeleblocked()) {
                    player.sendMessage("You cannot use your glory as you are teleblocked.");
                    return;
                }
                if (player.wildLevel > 30) {
                    player.sendMessage("You can't teleport above level 30 in the wilderness.");
                    player.getPA().closeAllWindows();
                    return;
                }
                switch (slot) {
                    case 1:
                        if (System.currentTimeMillis() - player.potDelay < 4000) {
                            player.sendMessage("@blu@Please wait a few seconds before doing another action.");
                            player.getPA().closeAllWindows();
                            return;
                        }

                        if (player.playerEquipment[Player.playerAmulet] == 1712) { // new
                            player.getItems().equipItem(1710, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3088, 3493, 0, "glory", false);
                            player.sendMessage("@red@You now have 3 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1710) { // new
                            player.getItems().equipItem(1708, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3088, 3493, 0, "glory", false);
                            player.sendMessage("@red@You now have 2 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1708) { // new
                            player.getItems().equipItem(1706, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3088, 3493, 0, "glory", false);
                            player.sendMessage("@red@You now have 1 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1706) { // new
                            player.getItems().equipItem(1704, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3088, 3493, 0, "glory", false);
                            player.sendMessage("@red@You now have 0 charges left in your glory.");
                        }
                        else // new
                            if (player.playerEquipment[Player.playerAmulet] == 19707) player.getPA().startTeleport(3088, 3493, 0, "glory", false);
                        break;
                    case 2:
                        if (System.currentTimeMillis() - player.potDelay < 2500) {
                            player.sendMessage("@blu@Please wait a few seconds before doing another action.");
                            player.getPA().closeAllWindows();
                            return;
                        }
                        if (player.playerEquipment[Player.playerAmulet] == 1712) { // new
                            player.getItems().equipItem(1710, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(2925, 3173, 0, "glory", false);
                            player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
                            player.sendMessage("@red@You now have 3 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1710) { // new
                            player.getItems().equipItem(1708, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(2925, 3173, 0, "glory", false);
                            player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
                            player.sendMessage("@red@You now have 2 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1708) { // new
                            player.getItems().equipItem(1706, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(2925, 3173, 0, "glory", false);
                            player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
                            player.sendMessage("@red@You now have 1 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1706) { // new
                            player.getItems().equipItem(1704, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(2925, 3173, 0, "glory", false);
                            player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
                            player.sendMessage("@red@You now have 0 charges left in your glory.");
                        }
                        else // new
                            if (player.playerEquipment[Player.playerAmulet] == 19707) player.getPA().startTeleport(2925, 3173, 0, "glory", false);
                        break;
                    case 3:
                        if (System.currentTimeMillis() - player.potDelay < 2500) {
                            player.sendMessage("@blu@Please wait a few seconds before doing another action.");
                            player.getPA().closeAllWindows();
                            return;
                        }
                        if (player.playerEquipment[Player.playerAmulet] == 1712) { // new
                            player.getItems().equipItem(1710, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3079, 3250, 0, "glory", false);
                            player.sendMessage("@red@You now have 3 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1710) { // new
                            player.getItems().equipItem(1708, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3079, 3250, 0, "glory", false);
                            player.sendMessage("@red@You now have 2 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1708) { // new
                            player.getItems().equipItem(1706, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3079, 3250, 0, "glory", false);
                            player.sendMessage("@red@You now have 1 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1706) { // new
                            player.getItems().equipItem(1704, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3079, 3250, 0, "glory", false);
                            player.sendMessage("@red@You now have 0 charges left in your glory.");
                        }
                        else // new
                            if (player.playerEquipment[Player.playerAmulet] == 19707) player.getPA().startTeleport(3079, 3250, 0, "glory", false);
                        break;
                    case 4:
                        if (System.currentTimeMillis() - player.potDelay < 2500) {
                            player.sendMessage("@blu@Please wait a few seconds before doing another action.");
                            player.getPA().closeAllWindows();
                            return;
                        }
                        if (player.playerEquipment[Player.playerAmulet] == 1712) { // new
                            player.getItems().equipItem(1710, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3293, 3176, 0, "glory", false);
                            player.sendMessage("@red@You now have 3 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1710) { // new
                            player.getItems().equipItem(1708, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3293, 3176, 0, "glory", false);
                            player.sendMessage("@red@You now have 2 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1708) { // new
                            player.getItems().equipItem(1706, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3293, 3176, 0, "glory", false);
                            player.sendMessage("@red@You now have 1 charges left in your glory.");

                        }
                        else if (player.playerEquipment[Player.playerAmulet] == 1706) { // new
                            player.getItems().equipItem(1704, 1, 2);
                            player.setAppearanceUpdateRequired(true);
                            player.getPA().startTeleport(3293, 3176, 0, "glory", false);
                            player.sendMessage("@red@You now have 0 charges left in your glory.");
                        }
                        else // new
                            if (player.playerEquipment[Player.playerAmulet] == 19707) player.getPA().startTeleport(3293, 3176, 0, "glory", false);
                        break;
                }
                player.potDelay = System.currentTimeMillis();
                break;

            case 2552:
            case 2554:
            case 2556:
            case 2558:
            case 2560:
            case 2562:
            case 2564:
            case 2566:
                switch (slot) {
                    case 1:
                        player.getPA().spellTeleport(3370, 3157, 0, false);
                        break;
                    case 2:
                        player.getPA().spellTeleport(2441, 3088, 0, false);
                        break;
                    case 3:
                        player.getPA().spellTeleport(3304, 3130, 0, false);
                        break;
                }
                break;

            default:
                player.sendMessage("Nothing interesting happens..");
                break;
        }
    }
}
