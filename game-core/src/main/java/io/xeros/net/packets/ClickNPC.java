package io.xeros.net.packets;

import io.xeros.content.bosses.Vorkath;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.skills.hunter.impling.Impling;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;

/**
 * Click NPC
 */
public class ClickNPC implements PacketType {

	public static final int ATTACK_NPC = 72,
			MAGE_NPC = 131,
			FIRST_CLICK = 155,
			SECOND_CLICK = 17,
			THIRD_CLICK = 21,
			FOURTH_CLICK = 18;

	@Override
	public void processPacket(final Player c, int packetType, int packetSize) {
		if (c.getMovementState().locked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		c.npcAttackingIndex = 0;
		c.npcClickIndex = 0;
		c.playerAttackingIndex = 0;
		c.clickNpcType = 0;
		c.getPA().resetFollow();

		if (c.isForceMovementActive() || c.teleTimer > 0) {
			return;
		}

		switch (packetType) {
		/**
		 * Attack npc melee or range
		 **/
		case ATTACK_NPC:
			if (c.morphed) {
				return;
			}

			if (c.playerEquipment[Player.playerWeapon] == 22547 || c.playerEquipment[Player.playerWeapon] == 22542 || c.playerEquipment[Player.playerWeapon] == 22552  ) {
				c.stopMovement();
				break;
			}

			int index = c.getInStream().readUnsignedWordA();
			if (index < 0 || index >= NPCHandler.npcs.length) {
				c.stopMovement();
				return;
			}

			c.stopMovement();
			c.npcAttackingIndex = index;
			c.usingClickCast = false;

			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];

			if (npc == null || npc.getHealth().getMaximumHealth() == 0) {
				c.attacking.reset();
				return;
			}

			c.faceEntity(npc);

			if (c.getBankPin().requiresUnlock()) {
				c.attacking.reset();
				c.getBankPin().open(2);
				return;
			}

			if (c.attacking.attackEntityCheck(npc, true)) {
				c.attackEntity(npc);
			} else {
				c.attacking.reset();
			}
			break;

		/**
		 * Attack npc with magic
		 **/
		case MAGE_NPC:
			if (c.morphed) {
				return;
			}

			c.stopMovement();
			c.npcAttackingIndex = c.getInStream().readSignedWordBigEndianA();
			int castingSpellId = c.getInStream().readSignedWordA();
			c.usingClickCast = false;

			if (c.npcAttackingIndex >= NPCHandler.npcs.length || c.npcAttackingIndex < 0 || NPCHandler.npcs[c.npcAttackingIndex] == null) {
				c.attacking.reset();
				break;
			}

			npc = NPCHandler.npcs[c.npcAttackingIndex];
			c.faceEntity(npc);

			if (npc.getHealth().getMaximumHealth() == 0) {
				c.sendMessage("You can't attack this npc.");
				c.attacking.reset();
				break;
			}

			for (int i = 0; i < CombatSpellData.MAGIC_SPELLS.length; i++) {
				if (castingSpellId == CombatSpellData.MAGIC_SPELLS[i][0]) {
					if (c.attacking.attackEntityCheck(npc, true)) {
						c.setSpellId(i);
						c.usingClickCast = true;
						c.attackEntity(npc);
					} else {
						c.attacking.reset();
					}
					break;
				}
			}
			break;

		case FIRST_CLICK:
			int npcIndex = c.inStream.readSignedWordBigEndian();
			NPC n = NPCHandler.npcs[npcIndex];

			if (npcIndex >= NPCHandler.npcs.length || npcIndex < 0 || n == null)
				return;

			if (Impling.isImp(n.getNpcId())) {
				if (c.lastImpling > System.currentTimeMillis()) {
					return;
				}
			}

			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #1-> Click index: " + npcIndex + ", NPC Id: " + c.npcType);
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}

			c.npcClickIndex = npcIndex;
			c.faceUpdate(npcIndex);
			c.stopMovement();
			c.npcType = n.getNpcId();
			c.getPA().followNPC(n, false);

			if (n.getNpcId() == 8026) {
				c.stopMovement();
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getPA().playerWalk(2272, 4061);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.absX == 2272 && c.absY == 4061) {
							c.facePosition(2272, 4062);
							c.startAnimation(827);
							Vorkath.poke(c, n);
							container.stop();
						}
					}
					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
				return;
			}


			if (n.distance(c.getPosition()) <= 1) {
				c.facePosition(n.getX(), n.getY());
				n.facePlayer(c.getIndex());
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().firstClickNpc(n);
			} else {
				c.clickNpcType = 1;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 1) && n != null) {
							if (n.distance(c.getPosition()) <= 1) {
								c.facePosition(n.getX(), n.getY());
								n.facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().firstClickNpc(n);
								container.stop();
							}
						}
						if (c.clickNpcType == 0 || c.clickNpcType > 1)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}
			break;

		case SECOND_CLICK:
			npcIndex = c.inStream.readUnsignedWordBigEndianA();
			if (npcIndex >= NPCHandler.npcs.length || npcIndex < 0 || NPCHandler.npcs[npcIndex] == null) {
				break;
			}
			c.npcClickIndex = npcIndex;
			c.faceUpdate(c.npcClickIndex);
			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #2-> Click index: " + c.npcClickIndex + ", NPC Id: " + c.npcType);
			}
			if (NPCHandler.npcs[c.npcClickIndex] != null) {
				c.npcType = NPCHandler.npcs[c.npcClickIndex].getNpcId();
				c.getPA().followNPC(NPCHandler.npcs[c.npcClickIndex], false);
			} else {
				return;
			}
			if (NPCHandler.npcs[c.npcClickIndex].distance(c.getPosition()) <= 1) {
				c.facePosition(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
				NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				if (c.npcType == 6637 || c.npcType == 6638) {
					PetHandler.metamorphosis(c, c.npcType);
				}
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().secondClickNpc(NPCHandler.npcs[c.npcClickIndex]);
			} else {
				c.clickNpcType = 2;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 2) && NPCHandler.npcs[c.npcClickIndex] != null) {
							if (NPCHandler.npcs[c.npcClickIndex].distance(c.getPosition()) <= 1) {
								c.facePosition(NPCHandler.npcs[c.npcClickIndex].getX(),
										NPCHandler.npcs[c.npcClickIndex].getY());
								NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().secondClickNpc(NPCHandler.npcs[c.npcClickIndex]);
								container.stop();
							}
						}
						if (c.clickNpcType < 2 || c.clickNpcType > 2)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}

			break;

		case THIRD_CLICK:
			npcIndex = c.inStream.readSignedWord();
			if (npcIndex >= NPCHandler.npcs.length || npcIndex < 0 || NPCHandler.npcs[npcIndex] == null) {
				break;
			}
			c.npcClickIndex = npcIndex;
			c.faceUpdate(c.npcClickIndex);
			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #3-> Click index: " + c.npcClickIndex + ", NPC Id: " + c.npcType);
			}
			if (NPCHandler.npcs[c.npcClickIndex] != null) {
				c.npcType = NPCHandler.npcs[c.npcClickIndex].getNpcId();
				c.getPA().followNPC(NPCHandler.npcs[c.npcClickIndex], false);
			} else {
				return;
			}
			if (NPCHandler.npcs[c.npcClickIndex].distance(c.getPosition()) <= 1) {
				c.facePosition(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
				if (c.npcType == 2130 || c.npcType == 2131 || c.npcType == 2132 || c.npcType == 8492
						|| c.npcType == 8493 || c.npcType == 8494 || c.npcType == 8495
						|| c.npcType >= 7354 && c.npcType <= 7367 || c.npcType == 5559 || c.npcType == 5560
						|| c.npcType == 326 || c.npcType == 327 || c.npcType == 8737 || c.npcType == 8738
						|| c.npcType == 7674 || c.npcType == 8009 || c.npcType == 388 || c.npcType == 8010
						|| c.npcType == 762 || c.npcType == 763 || c.npcType == 30023) {
					PetHandler.metamorphosis(c, c.npcType);
					
				}
				NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().thirdClickNpc(NPCHandler.npcs[c.npcClickIndex]);
			} else {
				c.clickNpcType = 3;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 3) && NPCHandler.npcs[c.npcClickIndex] != null) {
							if (NPCHandler.npcs[c.npcClickIndex].distance(c.getPosition()) <= 1) {
								c.facePosition(NPCHandler.npcs[c.npcClickIndex].getX(),
										NPCHandler.npcs[c.npcClickIndex].getY());
								NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().thirdClickNpc(NPCHandler.npcs[c.npcClickIndex]);

								container.stop();
							}
						}
						if (c.clickNpcType < 3)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}

			break;

		case FOURTH_CLICK:
			npcIndex = c.inStream.readSignedWordBigEndian();
			if (npcIndex >= NPCHandler.npcs.length || npcIndex < 0 || NPCHandler.npcs[npcIndex] == null) {
				break;
			}
			c.npcClickIndex = npcIndex;
			c.faceUpdate(c.npcClickIndex);
			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #4-> Click index: " + c.npcClickIndex + ", NPC Id: " + c.npcType);
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}

			if (NPCHandler.npcs[c.npcClickIndex] != null) {
				c.npcType = NPCHandler.npcs[c.npcClickIndex].getNpcId();
				c.getPA().followNPC(NPCHandler.npcs[c.npcClickIndex], false);
			} else {
				return;
			}

			if (NPCHandler.npcs[c.npcClickIndex].distance(c.getPosition()) <= 1) {
				c.facePosition(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
				NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().fourthClickNpc(NPCHandler.npcs[c.npcClickIndex]);
			} else {
				c.clickNpcType = 4;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 4) && NPCHandler.npcs[c.npcClickIndex] != null) {
							if (NPCHandler.npcs[c.npcClickIndex].distance(c.getPosition()) <= 1) {
								c.facePosition(NPCHandler.npcs[c.npcClickIndex].getX(),
										NPCHandler.npcs[c.npcClickIndex].getY());
								NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().fourthClickNpc(NPCHandler.npcs[c.npcClickIndex]);
								container.stop();
							}
						}
						if (c.clickNpcType < 4)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}

			break;
		}

	}
}
