package io.xeros.net.packets;

import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.LunarSpells;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		player.interruptActions();
		player.playerAttackingIndex = 0;
		player.npcAttackingIndex = 0;

		if (player.isForceMovementActive()) {
			return;
		}
		if (player.isNpc) {
			return;
		}
		switch (packetType) {
		case ATTACK_PLAYER:
			if (player.morphed || player.respawnTimer > 0) {
				return;
			}

			//player.stopMovement();
			int playerIndex = player.getInStream().readSignedWordBigEndian();

			String option = player.getPA().getPlayerOptions().getOrDefault(3, "null");
			player.debug(String.format("PlayerOption \"%s\" on player index %d.", option, player.playerAttackingIndex));

			PlayerHandler.getOptionalPlayerByIndex(playerIndex).ifPresentOrElse(player1 -> {
				if (player.getController().onPlayerOption(player, player1, option))
					return;

				player.usingClickCast = false;
				player.playerAttackingIndex = playerIndex;
				player.faceEntity(player1);

				if (player.getPA().viewingOtherBank) {
					player.getPA().resetOtherBank();
				}

				if (player.attacking.attackEntityCheck(player1, true)) {
					player.attackEntity(player1);
				} else {
					player.attacking.reset();
				}
			}, () -> player.attacking.reset());
			break;

		case MAGE_PLAYER:
			if (player.morphed || player.respawnTimer > 0) {
				player.attacking.reset();
				return;
			}

			player.stopMovement();
			player.playerAttackingIndex = player.getInStream().readSignedWordA();
			int castingSpellId = player.getInStream().readSignedWordBigEndian();
			player.usingClickCast = false;

			PlayerHandler.getOptionalPlayerByIndex(player.playerAttackingIndex).ifPresentOrElse(player1 -> {
				player.faceEntity(player1);

				if (player1.isTeleblocked() && castingSpellId == CombatSpellData.TELEBLOCK) {
					player.sendMessage("That player is already affected by this spell.");
					player.attacking.reset();
					return;
				}

				for (int r = 0; r < CombatSpellData.REDUCE_SPELLS.length; r++) {
					if (CombatSpellData.REDUCE_SPELLS[r] == castingSpellId) {
						if ((System.currentTimeMillis()
								- player1.reduceSpellDelay[r]) < CombatSpellData.REDUCE_SPELL_TIME[r]) {
							player.sendMessage("That player is currently immune to this spell.");
							player.attacking.reset();
							return;
						}
					}
				}

				if (castingSpellId > 30_000) {
					LunarSpells.CastingLunarOnPlayer(player, castingSpellId);
				}

				for (int i = 0; i < CombatSpellData.MAGIC_SPELLS.length; i++) {
					if (castingSpellId == CombatSpellData.MAGIC_SPELLS[i][0]) {
						if (player.attacking.attackEntityCheck(player1, true)) {
							player.attackEntity(player1);
							player.setSpellId(i);
							player.usingClickCast = true;
						} else {
							player.attacking.reset();
						}
						return;
					}
				}

				player.attacking.reset();
			}, () -> player.attacking.reset());
			break;

		}

	}

}
