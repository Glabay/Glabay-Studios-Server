package io.xeros.content.skills.farming;


import io.xeros.content.skills.Skill;
import io.xeros.model.Animation;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

public class GrassyPatch {
	public byte stage = 0;
	long time = System.currentTimeMillis();

	public void setTime() {
		time = System.currentTimeMillis();
	}

	public boolean isRaked() {
		return stage == 3;
	}

	public void process(Player player, int index) {
		if (stage == 0)
			return;
		long elapsed = (System.currentTimeMillis() - time) / 60_000;
		int grow = 1;

		if (elapsed >= grow) {
			for (int i = 0; i < elapsed / grow; i++) {
				if (stage == 0) {
					player.getFarming().doConfig();
					return;
				}

				stage = ((byte) (stage - 1));
				player.getFarming().doConfig();
			}
			setTime();
		}
	}

	public void click(Player player, int option, int index) {
		if (option == 1)
			rake(player, index);
	}

	boolean raking = false;
	public void rake(final Player p, final int index) {
		if(raking)
			return;
		if (isRaked()) {
			p.sendMessage("This plot is fully raked. Try planting a seed.");
			return;
		}
		if (!p.getItems().playerHasItem(5341)) {
			p.sendMessage("This patch needs to be raked before anything can grow in it.");
			p.sendMessage("You do not have a rake in your inventory.");
			return;
		}
		raking = true;
		p.startAnimation(new Animation(2273));
		p.setTickable((container, player1) -> {
			if (!p.getItems().playerHasItem(5341)) {
				p.sendMessage("This patch needs to be raked before anything can grow in it.");
				p.sendMessage("You do not have a rake in your inventory.");
				container.stop();
				return;
			}
			p.startAnimation(new Animation(2273));
			if(container.getTicks() != 0 && container.getTicks() % (3 + Misc.trueRand(3)) == 0) {
				setTime();
				GrassyPatch grassyPatch = GrassyPatch.this;
				grassyPatch.stage = ((byte) (grassyPatch.stage + 1));
				doConfig(p);
				p.getPA().addSkillXPMultiplied(Misc.trueRand(1) + 1, Skill.FARMING.getId(), true);
				p.getInventory().addAnywhere(new ImmutableItem(6055, 1));
				if (isRaked()) {
					p.sendMessage("Your patch is raked, no compost is required, just plant and water!");
					p.startAnimation(65_535);
					container.stop();
				}
			}

		});

		raking = false;
		p.startAnimation(new Animation(65535));
	}

	public static void doConfig(Player player) {
		player.getFarming().doConfig();
	}

	public int getStage() {
		return stage;
	}
}
