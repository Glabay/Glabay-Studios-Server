package io.xeros.util;

public class Experience {

	public static final int[] LEVEL_XP;
	
	static {
		LEVEL_XP = new int[99];
		int i = 0;
		for (int j = 0; j < 99; j++) {
			int l = j + 1;
			int i1 = (int) (l + 300D * Math.pow(2D, l / 7D));
			i += i1;
			LEVEL_XP[j] = i / 4;
		}
	}
	
}
