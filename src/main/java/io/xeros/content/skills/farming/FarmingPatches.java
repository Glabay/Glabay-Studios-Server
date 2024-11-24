package io.xeros.content.skills.farming;


import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Position;

public enum FarmingPatches {

	CATHERBY_ALLOTMENT_NORTH(new Position(2805, 3465), new Position(2815, 3469), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),
	CATHERBY_ALLOTMENT_SOUTH(new Position(2805, 3458), new Position(2815, 3461), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),
	CATHERBY_HERB(new Position(2813, 3462), new Position(2815, 3464), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.HERB),
	CATHERBY_FLOWER(new Position(2808, 3462), new Position(2811, 3465), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.FLOWER),

	FALADOR_HERB(new Position(3058, 3310), new Position(3060, 3313), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.HERB),
	FALADOR_FLOWER(new Position(3054, 3306), new Position(3056, 3307), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.FLOWER),
	FALADOR_ALLOTMENT_NORTH(new Position(3050, 3306), new Position(3055, 3312), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),
	FALADOR_ALLOTMENT_SOUTH(new Position(3055, 3302), new Position(3059, 3309), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),

	ARDOUGNE_HERB(new Position(2670, 3374), new Position(2671, 3375), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.HERB),
	ARDOUGNE_FLOWER(new Position(2666, 3374), new Position(2667, 3375), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.FLOWER),
	ARDOUGNE_ALLOTMENT_NORTH(new Position(2662, 3377), new Position(2671, 3379), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),
	ARDOUGNE_ALLOTMENT_SOUTH(new Position(2662, 3370), new Position(2671, 3372), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),

	PHAS_HERB(new Position(3605, 3529), new Position(3606, 3530), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.HERB),
	PHAS_FLOWER(new Position(3601, 3525), new Position(3602, 3526), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.FLOWER),
	PHAS_ALLOTMENT_WEST(new Position(3597, 3525), new Position(3601, 3530), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),
	PHAS_ALLOTMENT_EAST(new Position(3602, 3521), new Position(3606, 3526), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ALLOTMENT),

	LUMBRIDGE_TREE(new Position(3191, 3229, 0), new Position(3195, 3233, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.TREE),
	VARROCK_TREE(new Position(3227, 3457, 0), new Position(3231, 3461, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.TREE),
	FALADOR_TREE(new Position(3002, 3371, 0), new Position(3006, 3375, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.TREE),
	TAVERLY_TREE(new Position(2934, 3436, 0), new Position(2938, 3440, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.TREE),
	STRONGHOLD_TREE(new Position(2434, 3413, 0), new Position(2438, 3417, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.TREE),

	STRONGHOLD_FRUIT_TREE(new Position(2474, 3444, 0), new Position(2477, 3447, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.FRUIT_TREE),
	CATHERBY_FRUIT_TREE(new Position(2859, 3432, 0), new Position(2862, 3435, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.FRUIT_TREE),
	MAZE_FRUIT_TREE(new Position(2488, 3178, 0), new Position(2491, 3181, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.FRUIT_TREE),
	BRIMHAVEN_FRUIT_TREE(new Position(2763, 3211, 0), new Position(2766, 3214, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.FRUIT_TREE),
	LLETYA_FRUIT_TREE(new Position(2345, 3160, 0), new Position(2348, 3163, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.FRUIT_TREE),

	PORT_SARIM_SPIRIT_TREE(new Position(3058, 3256, 0), new Position(3062, 3260, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.SPIRIT_TREE),
	BRIMHAVEN_SPIRIT_TREE(new Position(2800, 3201, 0), new Position(2804, 3205, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.SPIRIT_TREE),
	ETCETRIA_SPIRIT_TREE(new Position(2611, 3856, 0), new Position(2615, 3860, 0), Farming.HARVEST_CHOPPING, -2, -2, SeedType.SPIRIT_TREE),

	/**
	 * StrongHold Fruit tree - 2473, 3443, 0
	 * Maze Fruit Tree - 2489, 3182, 0
	 * Catherby Fruit Tree - 2858, 3430, 0
	 * Brimhaven Fruit tree - 2767, 3213, 0
	 * Lleya Fruit Tree - 2344, 3165, 0
	 *
	 *
	 * Port Sarim Spirit Tree - 3063, 3259, 0
	 * Brimhaven Spirit Tree - 2802, 3206, 0
	 * Etcetria Spirit Tree - 2613, 3854, 0
	 *
	 *
	 * StrongHold Tree - 2436, 3412, 0
	 * Lumbridge Tree - 3196, 3231, 0
	 * Varrock Tree - 3227, 3456, 0
	 * Falador Tree - 3003, 3376, 0
	 * Taverly Tree - 2933, 3438,
	 */


	//EDGE_ANIMA(new Position(3121, 3481), new Position(3123, 3483), Farming.HARVEST_ANIMATION, Items.SEED_DIBBER, Items.SECATEURS, SeedType.ANIMA),
	;

	public final Position bottomLeft;
	public final Position topLeft;
	public final int harvestAnimation;
	public final int harvestItem;
	public final int planter;
	public final SeedType seedType;

	FarmingPatches(Position bottomLeft, Position topLeft, int harvestAnimation, int planter, int harvestItem, SeedType seedType) {
		this.bottomLeft = bottomLeft;
		this.topLeft = topLeft;
		this.harvestItem = harvestItem;
		this.harvestAnimation = harvestAnimation;
		this.planter = planter;
		this.seedType = seedType;
	}
}
