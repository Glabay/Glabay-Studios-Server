package io.xeros.content.bosses;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class Tekton {
	
	public static int specialAmount;
	
	public static void tektonSpecial(Player player) {
		NPC TEKTON = NPCHandler.getNpc(7544);
		
		if (TEKTON.isDead()) {
			return;
		}
		
		if (TEKTON.getHealth().getCurrentHealth() < 1400 && specialAmount == 0 ||
			TEKTON.getHealth().getCurrentHealth() < 1100 && specialAmount == 1 ||
			TEKTON.getHealth().getCurrentHealth() < 900 && specialAmount == 2 ||
			TEKTON.getHealth().getCurrentHealth() < 700 && specialAmount == 3 ||
			TEKTON.getHealth().getCurrentHealth() < 400 && specialAmount == 4 ||
			TEKTON.getHealth().getCurrentHealth() < 100 && specialAmount == 5) {
			List<NPC> attacker = Arrays.asList(NPCHandler.npcs);
				if (attacker.stream().filter(Objects::nonNull)
						.anyMatch(n -> n.getNpcId() == 7617 && !n.isDead())) {
					return;
				}
				NPCHandler.npcs[TEKTON.getIndex()].forceChat("RAAAAAAAA!");
				TEKTON.underAttackBy = -1;
				TEKTON.underAttack = false;
				NPCHandler.tektonAttack = "SPECIAL";
				specialAmount++;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					int ticks;
					@Override
					public void execute(CycleEventContainer container) {
						if (player.isDisconnected()) {
							onStopped();
							return;
						}
						switch (ticks++) {
						case 1:
							NPCSpawning.spawnNpc(player, 7617, 3308, 5285, 1, 0, 30, true, false);
							NPCHandler.tektonAttack = "MELEE";
							break;
							
						case 2:
							NPCSpawning.spawnNpc(player, 7617, 3323, 5300, 1, 0, 30, true, false);
							break;
							
						case 3:
							NPCSpawning.spawnNpc(player, 7617, 3307, 5303, 1, 0, 30, true, false);
							break;
							
						case 4:
							NPCSpawning.spawnNpc(player, 7617, 3322, 5292, 1, 0, 30, true, false);
							break;
							
						case 5:
							NPCSpawning.spawnNpc(player, 7617, 3317, 5285, 1, 0, 30, true, false);
							break;
							
						case 7:
							NPCHandler.kill(7617, 1);
							container.stop();
							break;
						}
					}

					@Override
					public void onStopped() {

					}
				}, 2);
			}
		}
}
