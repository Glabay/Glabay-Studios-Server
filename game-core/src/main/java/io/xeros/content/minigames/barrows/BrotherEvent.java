package io.xeros.content.minigames.barrows;

import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;

public class BrotherEvent extends Event<Player> {

	public BrotherEvent(Player attachment, int ticks) {
		super(attachment, ticks);
	}

	@Override
	public void execute() {
//		if (attachment == null || !attachment.getBarrows().getActive().isPresent() || attachment.getBarrows().getActive().get().isDefeated()) {
//			stop();
//			return;
//		}
//		attachment.getBarrows().getActive().ifPresent(brother -> {
//			if (brother.getNPC() == null) {
//				stop();
//			} else {
//				NPC npc = brother.getNPC();
//				if (attachment.distanceToPoint(npc.absX, npc.absY, npc.heightLevel) > 20) {
//					stop();
//				}
//			}
//		});
//		if ((getElapsedTicks() + 1) % 30 == 0) {
//			attachment.getBarrows().drainPrayer();
//		}
	}

	@Override
	public void stop() {
//		if (attachment == null) {
//			super.stop();
//			return;
//		}
//		attachment.getBarrows().getActive().ifPresent(brother -> {
//			brother.setActive(false);
//			NPC npc = brother.getNPC();
//			if (npc != null) {
//				NPCHandler.npcs[npc.getIndex()] = null;
//			}
//		});
//		super.stop();
	}

}
