package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Change appearance
 **/
public class ChangeAppearance implements PacketType {

	private static final int[][] MALE_VALUES = { 
			{ 0, 8 }, // head
			{ 10, 17 }, // jaw
			{ 18, 25 }, // torso
			{ 26, 31 }, // arms
			{ 33, 34 }, // hands
			{ 36, 40 }, // legs
			{ 42, 43 }, // feet
	};

	private static final int[][] FEMALE_VALUES = { 
			{ 45, 54 }, // head
			{ 10, 17 }, // jaw
			{ 56, 60 }, // torso
			{ 61, 65 }, // arms
			{ 67, 68 }, // hands
			{ 70, 77 }, // legs
			{ 79, 80 }, // feet
	};

	private static final int[][] ALLOWED_COLORS = { 
			{ 0, 24 }, // hair color
			{ 0, 28 }, // torso color
			{ 0, 28 }, // legs color
			{ 0, 5 }, // feet color
			{ 0, 10 } // skin color
	};

	public static int getRandomValue(boolean male, int valueIdx) {
		int[][] values = male ? MALE_VALUES : FEMALE_VALUES;
		int[] category = values[valueIdx];
		int minimumVal = category[0];
		int maximumVal = category[1] - 1;
		return Misc.random(minimumVal, maximumVal);
	}

	public static int getRandomColor(int colorIdx) {
		int[][] values = ALLOWED_COLORS;
		int[] category = values[colorIdx];
		int minimumVal = category[0];
		int maximumVal = category[1] - 1;

		int randomColor = Misc.random(minimumVal, maximumVal);
		if (colorIdx == 4 && (randomColor == 8 || randomColor == 9 || randomColor == 10))
			return getRandomColor(colorIdx);

		return randomColor;
	}

	public static void generateRandomAppearance(Player player) {
		boolean isMale = true;
		player.playerAppearance[0] = 0; // gender
		player.playerAppearance[1] = getRandomValue(isMale, 0); // head
		player.playerAppearance[2] = getRandomValue(isMale, 2);// Torso
		player.playerAppearance[3] = getRandomValue(isMale, 3); // arms
		player.playerAppearance[4] = getRandomValue(isMale, 4); // hands
		player.playerAppearance[5] = getRandomValue(isMale, 5); // legs
		player.playerAppearance[6] = getRandomValue(isMale, 6); // feet
		player.playerAppearance[7] = getRandomValue(isMale, 1); // jaw

		player.playerAppearance[8] = getRandomColor(0); // hair colour
		player.playerAppearance[9] = getRandomColor(1); // torso colour
		player.playerAppearance[10] = getRandomColor(2); // legs colour
		player.playerAppearance[11] = getRandomColor(3); // feet colour
		player.playerAppearance[12] = getRandomColor(4); // skin colour
	}

	@Override
	public void processPacket(final Player c, final int packetType, final int packetSize) {
		if (c.getMovementState().isLocked())
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		final int gender = c.getInStream().readSignedByte();
		if (!c.canChangeAppearance)
			return;
		if (gender != 0 && gender != 1)
			return;

		final int[] apperances = new int[MALE_VALUES.length]; // apperance's
																// value
		// check
		for (int i = 0; i < apperances.length; i++) {
			int value = c.getInStream().readSignedByte();
			if (value < (gender == 0 ? MALE_VALUES[i][0] : FEMALE_VALUES[i][0]) || value > (gender == 0 ? MALE_VALUES[i][1] : FEMALE_VALUES[i][1]))
				value = (gender == 0 ? MALE_VALUES[i][0] : FEMALE_VALUES[i][0]);
			apperances[i] = value;
		}

		final int[] colors = new int[ALLOWED_COLORS.length]; // color value
																// check
		for (int i = 0; i < colors.length; i++) {
			int value = c.getInStream().readSignedByte();
			if (value < ALLOWED_COLORS[i][0] || value > ALLOWED_COLORS[i][1])
				value = ALLOWED_COLORS[i][0];
			colors[i] = value;
		}

		if (c.canChangeAppearance) {
			c.playerAppearance[0] = gender; // gender
			c.playerAppearance[6] = apperances[6]; // feet
			c.playerAppearance[7] = apperances[1]; // beard
			c.playerAppearance[8] = colors[0]; // hair colour
			c.playerAppearance[9] = colors[1]; // torso colour
			c.playerAppearance[10] = colors[2]; // legs colour
			c.playerAppearance[11] = colors[3]; // feet colour
			
			if(apperances[0] < 0) // head
				c.playerAppearance[1] = apperances[0] + 256;
			else
				c.playerAppearance[1] = apperances[0];
			if(apperances[2] < 0)
				c.playerAppearance[2] = apperances[2] + 256;
			else
				c.playerAppearance[2] = apperances[2];
			if(apperances[3] < 0)
				c.playerAppearance[3] = apperances[3] + 256;
			else
				c.playerAppearance[3] = apperances[3];
			if(apperances[4] < 0)
				c.playerAppearance[4] = apperances[4] + 256;
			else
				c.playerAppearance[4] = apperances[4];
			if(apperances[5] < 0)
				c.playerAppearance[5] = apperances[5] + 256;
			else
				c.playerAppearance[5] = apperances[5];
			
			if (colors[4] == 8 || colors[4] == 9 || colors[4] == 10) {
				//if (c.getHolidayStages().getStage("Halloween") < 6) {
				if (c.amDonated < 10) {
					//c.sendMessage("Only those whom entered at the dark times of halloween may use this skin.");
					c.sendMessage("You must be a donator to use these skin colors.");
					return;
				} else {
					c.playerAppearance[12] = colors[4]; // skin colour
				}
			} else {
				c.playerAppearance[12] = colors[4]; // skin colour
			}

			c.getPA().removeAllWindows();
			c.getPA().requestUpdates();
			c.canChangeAppearance = false;
		}
	}

}