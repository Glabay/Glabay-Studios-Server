package io.xeros.content.skills.crafting;

import java.util.Arrays;

import io.xeros.Server;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;

public class SpinMaterial {

	/**
	 * A single instance of this class
	 */
	private static final SpinMaterial INSTANCE = new SpinMaterial();

	public void spin(final Player player, final int material) {
		final Material mat = Arrays.stream(Material.values()).filter(m -> m.required == material).findFirst().orElse(null);

		if (mat == null) {
			return;
		}
		if (player.playerLevel[Player.playerCrafting] < mat.level) {
			player.sendMessage("You need a crafting level of " + mat.level + " to do this.");
			return;
		}
		if (!player.getItems().playerHasItem(material)) {
			player.sendMessage("You have no more material in your inventory.");
			return;
		}
		player.getPA().stopSkilling();
		Server.getEventHandler().submit(new SpinEvent("skilling", player, 2, mat));
	}

	/**
	 * The single instance of this class
	 * 
	 * @return the instance of this class
	 */
	public static final SpinMaterial getInstance() {
		return INSTANCE;
	}

	private final class SpinEvent extends Event<Player> {

		private final Material material;

		public SpinEvent(String signature, Player attachment, int ticks, final Material material) {
			super(signature, attachment, ticks);
			this.material = material;
		}

		@Override
		public void update() {
			if (!attachment.getItems().playerHasItem(material.required)) {
				attachment.sendMessage("You have run out of material to spin.");
				this.stop();
				return;
			}
		}

		@Override
		public void execute() {
			attachment.getItems().deleteItem2(material.required, 1);
			attachment.getItems().addItem(material.result, 1);
			attachment.getPA().addSkillXPMultiplied(material.experience, Player.playerCrafting, true);
			attachment.startAnimation(894);
		}

	}

	public enum Material {
		FLAX(1779, 1777, 1, 5), CROSSBOW(9436, 9438, 10, 15);

		/**
		 * The item required to craft
		 */
		private final int required;

		/**
		 * The result of spinning the required material
		 */
		private final int result;

		/**
		 * The level required to spin the material
		 */
		private final int level;

		/**
		 * The experience gained from spinning the material
		 */
		private final int experience;

		/**
		 * Creates a new {@link Material} that can be spun
		 * 
		 * @param required the item required to be spun
		 * @param result the result of spinning the item
		 * @param level the level required
		 * @param experience the experience gained
		 */
		Material(int required, int result, int level, int experience) {
			this.required = required;
			this.result = result;
			this.level = level;
			this.experience = experience;
		}

		/**
		 * The item required
		 * 
		 * @return the item
		 */
		public int getRequiredItem() {
			return required;
		}
	}

}
