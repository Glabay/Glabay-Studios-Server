package io.xeros.model.entity.npc;

public class NPCClipping {

	public static final int[][][] SIZES = {
		{ { 0, 0 } },
		{ { 0, 0 } }, // 1
		{ { 0, 1 }, { 1, 0 }, { 1, 1 } }, // 2
		{ { 2, 0 }, { 2, 1 }, { 2, 2 }, { 1, 2 }, { 0, 2 } }, // 3
		{ { 3, 0 }, { 3, 1 }, { 3, 2 }, { 3, 3 }, { 2, 3 }, { 1, 3 }, { 0, 3 } }, // 4
		{ { 4, 0 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 4, 4 }, { 3, 4 }, { 2, 4 }, { 1, 4 }, { 0, 4 } }, // 5
		{ { 5, 0 }, { 5, 1 }, { 5, 2 }, { 5, 3 }, { 5, 4 }, { 5, 5 }, { 4, 5 }, { 3, 5 }, { 2, 5 }, { 1, 5 }, { 0, 5 } }, // 6

		{ { 6, 0 }, { 6, 1 }, { 6, 2 }, { 6, 3 }, { 6, 4 }, { 6, 5 }, { 6, 6 }, { 5, 6 }, { 4, 6 }, { 3, 6 }, { 2, 6 }, { 1, 6 }, { 0, 6 } }, // 7
		{ { 7, 0 }, { 7, 1 }, { 7, 2 }, { 7, 3 }, { 7, 4 }, { 7, 5 }, { 7, 6 }, { 7, 7 }, { 6, 7 }, { 5, 7 }, { 4, 7 }, { 3, 7 }, { 2, 7 }, { 1, 7 }, { 0, 7 } }, // 8
		{ { 8, 0 }, { 8, 1 }, { 8, 2 }, { 8, 3 }, { 8, 4 }, { 8, 5 }, { 8, 6 }, { 8, 7 }, { 8, 8 }, { 7, 8 }, { 6, 8 }, { 5, 8 }, { 4, 8 }, { 3, 8 }, { 2, 8 }, { 1, 8 }, { 0, 8 } }, // 9
		{ { 9, 0 }, { 9, 1 }, { 9, 2 }, { 9, 3 }, { 9, 4 }, { 9, 5 }, { 9, 6 }, { 9, 7 }, { 9, 8 }, { 9, 9 }, { 8, 9 }, { 7, 9 }, { 6, 9 }, { 5, 9 }, { 4, 9 }, { 3, 9 }, { 2, 9 }, { 1, 9 }, { 0, 9 } }, // 10
	};

	public static final int[][] DIR = { { -1, 1 }, { 0, 1 }, { 1, 1 },
			{ -1, 0 }, { 1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 } };

	public static boolean withinBlock(int blockX, int blockY, int size, int x, int y) {
		return x - blockX < size && x - blockX > -1 && y - blockY < size && y - blockY > -1;
	}

	public static int getDirection(int x, int y) {
		for (int i = 0; i < 8; i++) {
			if (DIR[i][0] == x && DIR[i][1] == y)
				return i;
		}
		
		return -1;
	}

	public static int getDirection(int x, int y, int x2, int y2) {
		int xDiff = x2 - x;
		int yDiff = y2 - y;
		for (int i = 0; i < DIR.length; i++) {
			if (xDiff == DIR[i][0] && yDiff == DIR[i][1]) {
				return i;
			}
		}
		return -1;
	}
}

