package io.xeros.model.entity.npc.data;

import io.xeros.content.bosses.Skotizo;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.hydra.HydraStage;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.bosses.zulrah.Zulrah;
import io.xeros.content.minigames.inferno.InfernoWaveData;
import io.xeros.content.minigames.warriors_guild.AnimatedArmour;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.npc.NPCHandler;

public class DeathAnimation extends NPCHandler {

    public static int handleEmote(int npcId) {
        if (AnimatedArmour.isAnimatedArmourNpc(npcId)) {
            return 836;
        }
        return switch (npcId) {
            case Npcs.THE_MIMIC_8633 -> 8310;
            case Npcs.SARACHNIS, Npcs.SPAWN_OF_SARACHNIS, Npcs.SPAWN_OF_SARACHNIS_8715 -> 8318;
            case Npcs.REVENANT_CYCLOPS -> 4653;
            case 8194 -> 3262;
            case Npcs.BRYOPHYTA -> 4659; // unicorn
            case 2837, 2849 -> 6377;
            case Npcs.HESPORI -> Hespori.DEATH_ANIMATION;
            case 936 -> 287;
            case 8612 -> //Drake
                8277;
            case 8614 -> // Sulphur Lizard
                8284;
            case 8611 -> //wyrm
                8269;

            case 8360 -> 8094;

            //Wyverns
            //longtailed
            //taloned
            //spitting
            case 7792, 7793, 7794, 7795 ->//ancient
                7652;

            case 7860 -> 7843; //7841 attack mage //7840 melee
            case 7859 -> 7850;
            case 7858 -> 7854;
            case 852 -> 5726;//rat
            case 9026, 2510, 4501, 2511, 2512 -> 4935;//spider
            case 9027, 3018, 3017, 2477, 3023 -> 5329;
            case 9028 ->//bat
                4917;
            case 9029 ->//unicorn
                6377;//scorpion
            case 9030, 3024, 2479, 2480 -> 6256;//wolf
            case 9031, 107, 108 -> 6576;//bear
            case 9032, 2838, 3423 -> 4929;
            case 9033 ->//dragon
                92;
            case 9034 ->//dark beast
                2733;
            case 8062 ->//vorkath crab
                -1;
            case 9293 -> 8501;
            case 2241, 132, 3049, 3050, 3286, 3287 -> 167;
            //Inferno Npcs
            case TheUnbearable.NPC_ID -> 1759; //7841 attack mage //7840 melee
            case InfernoWaveData.JAL_NIB -> 7576;
            case InfernoWaveData.JAL_MEJRAH -> 7580;
            case InfernoWaveData.JAL_AK -> 7584;
            case InfernoWaveData.JAL_AKREK_KET -> 7584;
            case InfernoWaveData.JAL_AKREK_MEJ, InfernoWaveData.JAL_AKREK_XIL -> 7584;
            case InfernoWaveData.JAL_IMKOT -> 7599;
            case InfernoWaveData.JAL_XIL -> 7606;
            case InfernoWaveData.JAL_ZEK -> 7613;
            case InfernoWaveData.JALTOK_JAD -> 7594;
            case InfernoWaveData.TZKAL_ZUK, InfernoWaveData.JAL_MEJJAK -> 2865;
            case 5842 -> 5784;
            case 8622 -> HydraStage.ENRAGED.getDeathAnimation();
            case 8781, 6707 -> -1;
            case 7527, 7528, 7529 -> 7432;
            case 1605 -> 714;
            case 1606 -> 131;
            case 1607 -> 5321;
            case 1608 -> 843;
            case 1609 -> 68;
            case 8028 -> 7949;
            case 7566 -> 7415;
            case 7573 -> 7196;
            case 7563 -> 7426;
            case 7585 -> 6949;
            case Skotizo.SKOTIZO_ID -> 4624;
            case Skotizo.REANIMATED_DEMON -> 67; // Skeletal mystic
            // Skeletal mystic
            case 7604, 7605, 7606 -> // Skeletal mystic
                5491;
            case 2085 -> 4673;
            case 7544 -> // Tekton
                7495;
            case 5916 -> 4521;
            case 5890 -> 7100;
            case 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1642, 1643, 1654, 7302 -> 6615;
            case 6604 -> 307;
            case 6594 -> 7146;
            case 5816 -> 5784;
            case 5744 -> 5098;
            case 5762 -> 5397;

            /* Hydra */
            case 8609 -> 8264;

            //xeric minigame
            case 7547, 7548 -> 258;
            case 7559, 7560 -> 2304;
            case 7576, 7577, 7578, 7579 -> 1314;
            case 7586 -> 1580;
            case 7538 -> 7459;
            case 7531 -> 7459;
            case 7543 -> 7495;
            case 955, 957, 959 -> 6229; //range
            case 9021, 9022, 9023 -> //mage
                8422;
            case 9024 -> 8422;
            case 7144, 7145, 7146 -> 7229;
            case 458 -> 2778;
            case 3544 -> 278;
            case 1267 -> 2021;
            case 2064 -> 1013;
            case 2067 -> 6791;
            case 5129 -> 6502;
            case FragmentOfSeren.NPC_ID -> FragmentOfSeren.DEATH_ANIMATION;
            case FragmentOfSeren.CRYSTAL_WHIRLWIND -> FragmentOfSeren.CRYSTAL_DEATH_ANIMATION;
            case 2593 -> 6537;
            case 963 -> 6229;
            case 965 -> 6233; // Hunter birds
            case 5548, 5549, 5550, 5551, 5552 -> 6780;
            case 1505 -> // Hunter ferret
                5188; // Hunter chinchompas
            case 2910, 2911, 2912 -> 5182;
            case 5867, 5868, 5869 -> 4503;
            case 5862 -> 4495;
            case 465 -> // Skeletal wyverns
                2987; // Lizardman shaman
            case 6766, 6768, 6914, 6915, 6916, 6917, 6918, 6919 -> 7196; // Scorpia
            case 6615, 6616, 6617 -> 6256;
            case 2527 -> // Ghosts
                5542;
            case 2528, 2529, 2530 -> 5534; // Zombies
            case 2145, 6462, 6465, 26, 27, 28, 29, 31, 32, 33, 34, 35, 36, 30 -> 5575;
            case 320 -> // Dark core
                1688;
            case 6367 -> // Evil chicken
                2301;
            case 6369 -> // Agrith-na-na
                3503;
            case 6370 -> // Flambeed
                1752;
            case 6372 -> // Dessourt
                3509;
            case 2084 -> // Fire giant
                4668;
            case 6373, 6374, 6375, 6376, 6377, 6378 -> 1342; // Dagannoths
            case 970, 975, 983, 984, 985, 986, 987, 988 -> 1342;
            case 5944 -> // Rock lobster
                2862;
            case 5938 ->// Wallasalki
                2367;
            case 319 -> // Corporeal Beast
                1676; // Cave Crawler
            case 406, 7389 -> 228;
            case 481 -> 6081; // Smoke Devil
            case 498, 499, 7406 -> 3849;
            case 1610, 1611, 6618, 6619 -> 836;
            case 1612 -> 196;
            case 2805 -> 5851;
            case 2211, 2212 -> 2304;
            case 2233 -> 361;
            case 2234 -> 2938;
            case 2235 -> 4653;
            case 2237 -> 4321;
            case 2242, 2243, 2244 -> 4321;
            case 2245 -> 6182;
            case 3133 -> 6567;
            case 3134 -> 172;
            case 3135 -> 6537;
            case 3137 -> 2304;
            case 3138 -> 1553;
            case 3139, 3140 -> 1580;
            case 3141 -> 4302;
            case 3160, 3159, 3161 -> 2304;
            case 3162 -> 6979;
            case 3163, 3164, 3165, 3166, 3167, 3168, 3174, 3169 -> 6959;
            case 2042, 2043, 2044 -> -1;
            case Zulrah.SNAKELING -> 2408;
            case 6601, 6602 -> 65535;
            case 1739, 1740, 1741, 1742 -> 65535;
            case 1734 -> 3894;
            case 1724 -> 3922;
            case 1709 -> 3910;
            case 1704 -> 3917;
            case 1699 -> 3901;
            case 1689 -> 3888;
            case 70, 71, 72, 73, 74, 75, 76, 77, 78, 79 -> 5491; // Jelly
            case 437, 7277 -> -1;
            case 7400, 7399 -> 1587; // Cave Horror
            case 3209, 7401 -> 4233;
            case 411 -> // Kurask
                1513;
            case 1047 -> 4233;
            case 7405 -> 4233;
            case Npcs.SKELETON_HELLHOUND -> 6564;
            case 6611, 6612 -> 5491;
            case 6610 -> 5329;
            case 494, 492 -> 3987;
            case 871 ->// Ogre Shaman
                361;
            case 6609 -> // Castillo
                4929;
            case 5399 -> 5569; // Tree spirit
            case 438, 439, 440, 441, 442, 443 -> 97;// river troll
            case 391, 392, 393, 394, 395, 396 -> 287; // Rockslug
            case 421, 7392 -> 1568;
            // case 423:
            // begin new updates
            // moss
            case 420, 422, 891, 2090, 2091, 2092, 2093 -> 4659; // ghost
            case 85, 7258 -> 5542;
            case 2834 -> // bats
                4917;
            // end
            case 2206 -> 6377;
            case 2207 -> 7034;
            case 2216, 2217, 2218 -> 6156;
            case 3129, 3130, 3131, 3132 -> 67;
            case 6142 -> 1;
            case 6143 -> 1;
            case 6144 -> 1;
            case 6145 -> 1;
            case 5935 ->// sand crab
                1314;
            case 100 ->// rc
                1314;
            case 662 -> 6182;// Battle Mage
            case 414, 7272, 7391 -> 1524;
            case 7390 -> -1;// Ravager
            case 3742, 3743, 3744, 3745, 3746 -> 3916;// Brawler
            case 3772, 3773, 3774, 3775, 3776 -> 3894;
            case 5779 -> // giant mole
                3313;
            case 135, 7935 -> 6567;
            case 2205 -> 6968;
            case 2215 -> 7020;
            case 6267 -> 357;
            case 6268 -> 2938;
            case 6269 -> 4653;
            case 6270 -> 4653;
            case 6271 -> 4321;
            case 6272 -> 4321;
            case 6273 -> 4321;
            case 6274, 7937 -> 4321;
            case 2098, 2463, Npcs.OBOR -> 4653;
            case 1459 -> 1404;
            case 2559, 2560, 2561 -> 6956;
            case 3121 -> 2630;
            case 3118 -> 2627;
            case 2167 -> 2607;
            case 3116 -> 2620;
            case 3120 -> 2627;
            case 3123 -> 2638;
            case 2746 -> 2638;
            case 3125 -> 2646;
            case 3127 -> 2654;
            case 3777, 3778, 3779, 3780 -> -1;
            case 6342 -> 5898;
            case 2054 -> 3147;
            case 2035 -> // spider
                146;
            case 2033 -> // rat
                141;
            case 2031 -> // bloodvel
                2073;
            case 1769, 1770, 1771, 1772, 7931, 1773, 101 -> // goblin
                6182;
            case 1767, 397, 1766, 1768, 81 -> // cow
                5851;
            case 41 -> // chicken
                57; // dagannoth
            case 1338, 1340, 1342 -> 1342;
            case 2265, 2266, 2267 -> 2856;
            case 111 -> // ice giant
                131;
            case 2841 -> // ice warrior
                843;
            case 751 ->// Zombies!!
                302;
            case 1626, 1627, 1628, 1629, 1630, 1631, 1632 -> // turoth!
                1597; // basilisk
            case 417, 7395 -> 1548;
            case 1653 -> // hand
                1590;// demons
            case 2006, 7244, 2026, 2025, 1432, 7936, 1443 -> 67;// abby spec
            // Deviant spectre
            case 6, 7279, 7403 -> 1508;// baby drags
            case 51, 52, 1589, 3376 -> 28; // Gargoyle
            case 1543, 7407 -> 1518; // Bloodveld
            case 484, 1619, 7276, 7398, 7397 -> 1553; // Cockatrice
            case 419, 1621, 7393 -> 1563; // Dark Beast
            case 4005, 7938 -> 2732;
            case 7409 -> 2733; // Abby Demon
            case 415, 7410 -> 1538; // Dust devil
            case 423, 7404 -> 1558; // Nechryael
            case 11, 7411, 7278 -> 1530; // Pyrefiend
            case 435, 1634, 7932, 7394 -> 1580;
            case 7293 -> 1563; // Crawling hand
            case 448, 7388, 1649, 1650, 1651, 1652, 1655, 1656, 1657 -> 1590;
            case 102 -> 313;
            case 105, 106 -> 44;
            case 412 ->
                // case 2834:
                36;
            case 122, 123, 7933 -> 167;
            case 58, 59, 60, 61, 62, 63, 64, 3021 -> 5329;
            case 1153, 1154, 1155, 1156, 1157 -> 1190;
            case 104 -> 5534;
            case 118, 291 -> 102;// drags
            case 239, 7940 -> 92;
            case 247, 259, 268, 240, 241, 242, 243, 244, 245, 246, 264, 270, 2919, 1270, 273, 274, 6593, 7273, 7274, 7275, 8030, 8031, 8027 -> 92;
            case 1680 -> 4935;
            case 1679 -> 241;
            case 1678 -> 2072;
            case 1683, 1684 -> 6246;
            case 1685 -> 5491;
            default -> 2304;
        };
    }
}
