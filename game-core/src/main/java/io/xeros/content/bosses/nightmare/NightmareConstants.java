package io.xeros.content.bosses.nightmare;

import io.xeros.model.Animation;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

public class NightmareConstants {

    public static final Position NIGHTMARE_PLAYER_EXIT_POSITION = new Position(3808, 9755, 1);
    public static final Position STATUS_NPC_SPAWN_POSITION = new Position(3806, 9757, 1);
    public static final Position NIGHTMARE_SPAWN_POSITION = new Position(3870, 9949, 3);
    public static final Position NIGHTMARE_PLAYER_SPAWN_POSITION = new Position(3872, 9942, 3);

    public static final Boundary BOUNDARY = new Boundary(3862, 9940, 3884, 9962);
    public static final Boundary LOBBY_BOUNDARY = new Boundary(3716, 9650, 3912, 9842);

    public static final int RESPAWN_TIMER = 50;
    public static final int AWAKE_TIMER = 11;
    public static final int NIGHTMARE_SLEEPING_ID = 9433;
    public static final int NIGHTMARE_ACTIVE_ID = 9425;
    public static final int SHIELD_SCALE_FACTOR = 600;
    public static final int MAX_SHIELD = 19600;
    public static final int MIN_SHIELD = 1000;// Lowered from osrs 2000
    public static final int HEALTH = 2400;
    public static final int TOTEMS_HIT = 800;
    public static final int TOTEM_DEFAULT_HEALTH = 75;// Lowered from osrs 100
    public static final int TOTEM_HEALTH_SCALE = 25;

    public static final Animation NIGHTMARE_AWAKE_ANIMATION = new Animation(8611);
    public static final Animation NIGHTMARE_DEATH_ANIMATION = new Animation(8612);
}
