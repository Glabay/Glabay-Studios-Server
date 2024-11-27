package io.xeros.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/6/2024
 */
@Getter
@RequiredArgsConstructor
public enum FishingSpotTypes {

    SHRIMP(1, 1, 303, -1, 317, 10, 621, 321, 15, 30, 12000),
    SARDINE_HERRING(2, 5, 307, 313, 327, 20, 622, 345, 10, 30, 12000),
    MACKEREL(3, 16, 305, -1, 353, 20, 620, -1, -1, -1, 12000),
    TROUT(4, 20, 309, 314, 335, 50, 622, 331, 30, 70, 12000),
    BASS_COD(5, 23, 305, -1, 341, 45, 619, 363, 46, 100, 12000),
    PIKE(6, 25, 309, 314, 349, 60, 622, -1, -1, -1, 12000),
    TUNA_SWORDFISH_1(7, 35, 311, -1, 359, 80, 618, 371, 50, 100, 12000),
    TUNA_SWORDFISH_2(7, 35, 21028, -1, 359, 80, 7401, 371, 50, 100, 12000),
    TUNA_SWORDFISH_3(7, 35, 21031, -1, 359, 80, 7402, 371, 50, 100, 12000),
    LOBSTER(8, 40, 301, -1, 377, 90, 619, -1, -1, -1, 10000),
    MONKFISH(9, 62, 303, -1, 7944, 100, 620, -1, -1, -1, 8000),
    SHARK_1(10, 76, 311, -1, 383, 110, 618, -1, -1, -1, 6000),
    SHARK_2(10, 76, 21028, -1, 383, 110, 7401, -1, -1, -1, 6000),
    SHARK_3(10, 76, 21031, -1, 383, 110, 7402, -1, -1, -1, 6000),
    SEA_TURTLE(11, 79, 305, -1, 395, 100, 620, 389, 81, 130, 6000),
    MANTA_RAY(12, 81, 305, -1, 389, 130, 620, -1, -1, -1, 6000),
    DARK_CRAB(13, 85, 301, 11940, 11934, 132, 619, -1, -1, -1, 6000),
    KARAMBWANJI_AFK(14, 1, -1, -1, 3150, 1, 620, -1, -1, -1, 15000),
    KARAMABWAN(15, 65, 3159, 3150, 3142, 105, 620, -1, -1, -1, 8000),
    ANGLERFISH(16, 82, 307, 13431, 13439, 118, 622, -1, -1, -1, 6000);

    private final int fishingSpotId;
    private final int levelReq;
    private final int toolId;
    private final int baitId;
    private final int rawFishId;
    private final int experience;
    private final int animationID;
    private final int secondaryFishId;
    private final int SecondaryFishLevelReq;
    private final int secondaryFishExperience;
    private final int petChance;

}
