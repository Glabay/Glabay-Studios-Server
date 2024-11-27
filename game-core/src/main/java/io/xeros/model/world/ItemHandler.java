package io.xeros.model.world;

import io.xeros.content.skills.Skill;
import io.xeros.content.skills.prayer.Bone;
import io.xeros.content.skills.prayer.Prayer;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.grounditem.GroundItemManager;
import io.xeros.model.entity.grounditem.GroundItem;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.util.Misc;

import java.util.Optional;

public class ItemHandler {

	public ItemHandler() { }

	public boolean itemExists(int itemId, int itemX, int itemY, int height) {
		return GroundItemManager.INSTANCE.itemExists(new Position(itemX,itemY,height),itemId);
	}

	public void removeGroundItem(int id, Position position) {
		GroundItem itemNew = GroundItemManager.INSTANCE.getItemWithId(position,id);
		if (itemNew == null) return;
		GroundItemManager.INSTANCE.deregisterGroundItem(itemNew);
	}


	public void createGroundItem(GameItem gameItem, Position position) {
		GroundItemManager.INSTANCE.registerGroundItem(new GroundItem(gameItem, position));
	}

	public void createGroundItem(GameItem gameItem, Position position, int cycles) {
		GroundItemManager.INSTANCE.registerGroundItem(new GroundItem(gameItem, position,cycles));
	}

	public void createGroundItem(Player player, GameItem gameItem, Position position) {
		createGroundItem(player, gameItem.id(), position.getX(), position.getY(), position.getHeight(), gameItem.amount(), player.getIndex());
	}

	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int height, int itemAmount) {
		GroundItemManager.INSTANCE.registerGroundItem(new GroundItem(new GameItem(itemId, itemAmount), Optional.of(player), new Position(itemX, itemY, height)));
	}

	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int height, int itemAmount, int playerId) {
		if (playerId < 0 || playerId > PlayerHandler.players.length - 1) {
			return;
		}
		Player owner = PlayerHandler.players[playerId];
		if (owner == null) {
			return;
		}
		if (itemId > 0 && itemAmount > 0) {
			if (player.lootValue > 0) {
				if (ShopAssistant.getItemShopValue(itemId) >= player.lootValue) {
					player.getPA().stillGfx(1177, itemX, itemY, height, 5);
					player.sendMessage("@red@Your lootvalue senses a drop valued at or over " + Misc.getValueWithoutRepresentation(player.lootValue) + " coins.");
				}
			}

			boolean crusher = player.getItems().playerHasItem(13116) || player.playerEquipment[Player.playerAmulet] == 22986;

			Optional<Bone> boneOptional = Prayer.isOperableBone(itemId);
			if (crusher && boneOptional.isPresent()) {
				Bone bone = boneOptional.get();

				double experience = player.getRechargeItems().hasItem(13114) ? 0.75 : player.getRechargeItems().hasItem(13115) ? 1 : player.getDiaryManager().getMorytaniaDiary().hasCompleted("ELITE") ? 1 : 0.50;
				if (itemId == bone.getItemId()) {
					player.getPrayer().onBonesBuriedOrCrushed(bone, true);
					player.getPA().addSkillXPMultiplied((int) (bone.getExperience() * experience), Skill.PRAYER.getId(), true);
					return;
				}
			}

			GroundItemManager.INSTANCE.registerGroundItem(new GroundItem(new GameItem(itemId, itemAmount), Optional.of(player), new Position(itemX, itemY, height)));
		}
	}


	/**
	 * The counterpart of the item whether it is the noted or un noted version
	 *
	 * @param itemId the item id we're finding the counterpart of
	 * @return the note or unnoted version or -1 if none exists
	 */
	public int getCounterpart(int itemId) {
		return ItemDef.forId(itemId).getNoteId() == 0 ? -1 : ItemDef.forId(itemId).getNoteId();
	}

}
