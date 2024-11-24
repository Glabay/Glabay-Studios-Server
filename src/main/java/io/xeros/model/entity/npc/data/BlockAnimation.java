package io.xeros.model.entity.npc.data;

import io.xeros.content.bosses.Skotizo;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.minigames.inferno.InfernoWaveData;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;

public class BlockAnimation {

    public static int getAnimation(int npcId) {
        return switch (npcId) { // unicorn
            case 2837, 2849 -> 6375;
            case 936 -> 285;
            case Hespori.NPC_ID -> -1;
            //Inferno Npcs
            case InfernoWaveData.JAL_NIB -> 7575;
            case InfernoWaveData.JAL_MEJRAH -> 7579;
            case InfernoWaveData.JAL_AK -> 7585;
            case InfernoWaveData.JAL_AKREK_KET -> 7585;
            case InfernoWaveData.JAL_AKREK_MEJ -> 7585;
            case InfernoWaveData.JAL_AKREK_XIL -> 7585;
            case InfernoWaveData.JAL_IMKOT -> 7598;
            case InfernoWaveData.JAL_XIL -> 7607;
            case InfernoWaveData.JALTOK_JAD -> 7591;
            case InfernoWaveData.TZKAL_ZUK -> 7565;
            case Npcs.BRYOPHYTA -> 4657;
            case InfernoWaveData.JAL_MEJJAK -> 2869;
            case 8609 ->/* Hydra */
                -1;
            case 852 -> 5723;//rat
            case 9026, 2510, 2511, 2512, 4501 -> 4934;//spider
            case 9027, 3018, 3017, 2477, 3023 -> 5328;
            case 9028 ->//bat
                4916;
            case 9029 ->//unicorn
                6375;//scorpion
            case 9030, 3024, 2479, 2480 -> 6259;//wolf
            case 9031, 107, 108 -> 6578;//bear
            case 9032, 2838, 3423 -> 4927;
            case 9033 ->//dragon
                89;
            case 9034 ->//dark beast
                2732; //range
            case 9021, 9022, 9023 -> //mage
                8419;
            // case 7573:
            // return 7196;
            case 7563 -> 7421;
            case Skotizo.SKOTIZO_ID -> 4676;
            case 7585, Skotizo.REANIMATED_DEMON -> 65; // Skeletal mystic
            // Skeletal mystic
            case 7604, 7605, 7606 -> // Skeletal mystic
                5489;
            case 2085 -> 4671;
            case 7573 -> 7194;
            case 7544 -> // Tekton
                7489;
            case 5916 -> 4523;
            case 5890 -> 5755;
            case 955, 957, 959 -> 6232;
            case 7144, 7145, 7146 -> 7228;
            case 458 -> 2777;
            case 3544 -> 276;
            case 1267 -> 2020;
            case 2064 -> 1012;
            case 2067 -> 6792;
            case 2593 -> 6538;
            case 963 -> 6232;
            case 965 -> 6237;
            case 5862 -> 4489;
            case 465 -> // Skeletal wyverns
                2983; // Lizardman shaman
            case 6766, 6768, 6914, 6915, 6916, 6917, 6918, 6919 -> 7194; // Crazy Archaeologist
            case 6618, 6619 -> 424; // Scorpia
            case 6615, 6616, 6617 -> 6255;
            case 2527 -> // Ghosts
                5541;
            case 2528, 2529, 2530 -> 5533;
            //Xeric Minigame
            case 7547, 7548 -> 256;
            case 7559, 7560 -> 403;
            case 7576, 7577, 7578, 7579 -> 1313;
            case 7586 -> 1581;
            case 7538 -> 7455;
            case 7529 -> 7442;
            case 7531 -> 7455;
            case 7543 -> 7483;
            case 7566 -> 7412; // Zombies
            case 2145, 6462, 6465 -> 5574;
            case 6367 -> // Evil chicken
                2299;
            case 2241, 3049, 3050, 3286, 3287 -> 165;
            case 6369 -> // Agrith-na-na
                3500;
            case 6370 -> // Flambeed
                1751;
            case 6372 -> // Dessourt
                3505;
            case 6373, 6374, 6375, 6376, 6377, 6378 -> 1340; // Jelly
            // Kurask
            // Cave Horror
            case 437, 7277, 7400, 7399, 411, 7405, 3209, 7401 -> 4235;
            case 70, 71, 72, 73, 74, 75, 76, 77, 78, 79 -> 5489; // Cockatrice
            case 419, 7393, 8781 -> -1;
            case 101 -> 6183;
            case 3835 -> 6232;
            case 2037 -> 5489;
            case 5529 -> 5783;
            case 5219, 5218 -> 5096;
            case 5235 -> 5395;
            case 10127 -> 13170;
            case 10057 -> 10818;
            case 5904 -> 6330;
            case 5779 -> 3311;
            case 5903 -> 6346;
            case 9463, 9465, 9467 -> 12792;
            case 6624 -> 7413;
            case 6649 -> 7469;
            case 6646 -> 7462;
            case 3836 -> 6237; // Dark Beast
            case 4005, 7409 -> 2732;
            case 8133 -> 10058;
            case 10141 -> 13601;
            case 8349 -> 10923;
            case 9947 -> 13771;
            case 2215 -> 7019;
            case 1377 -> 1758;
            case 2216, 2217, 2218 -> 6155;
            case 3162 -> 6978;
            case 3163, 3164, 3165, 3169 -> 6952;
            case 6576 -> 6944;
            case 3130, 3131, 3132 -> 65;
            case 6575 -> 6966;
            case Items.THAMMARONS_SCEPTRE, 22552 -> 6966;
            case 6342 -> 5897;
            /*
             * case 2006: return 6375;
             */
            case 2007 -> 7017;
            case 2008 -> 4311;
            case 6229, 6230, 6231, 6232, 6233, 6234, 6235, 6236, 6237, 6238, 6239, 6240, 6241, 6242, 6243, 6244, 6245, 6246 -> 6952;
            case 6267 -> 360;
            case 6268 -> 2933;
            case 6269, 6270 -> 4651;
            case 6271, 6272, 6273, 6274 -> 4322;
            case 6275 -> 165;
            case 6276, 6277, 6278 -> 4322;
            case 6279, 6280 -> 6183;
            case 6281 -> 6136;
            case 6282 -> 6189;
            case 6283 -> 6183;
            case 6210 -> 6578;
            case 6211 -> 170;
            case 6212, 6213 -> 6538;
            case 6215 -> 1550;
            case 6216, 6217 -> 1581;
            case 6218 -> 4301;
            case 6258 -> 2561;
            case 10775 -> 13154;
            case 113 -> 129;
            case 114 -> 360;
            case 3058 -> 2937;
            case 3061 -> 2961;
            case 3063 -> 2937;
            case 3065 -> 2720;
            case 3066 -> 2926;
            case 5935 ->// sand crabs
                1313;
            case 100 ->// rc
                1313;
            case 662 ->// goblins
                6186;
            case 118 -> 100;
            case 2263 -> 2181;
            case 2006, 1432, 752, 3064, 2026 -> // lesser
                65;
            case 3347, 3346 -> 3325;
            case 1192 -> 1244;
            case 3062 -> 2953;
            case 3060 -> 2964; // Spinolyp
            // Spinolyp
            case 2892, 2894, 2896 -> // Spinolyp
                2869; // Dust Devil
            case 423, 7404 -> 1555;
            case 2054 -> 3148;// dagannoth
            case 1354, 1341, 2455, 2454, 2456, 983, 984, 985, 986, 987, 988 -> 1340;
            case 127 -> 186;
            case 291 -> 100; // supreme
            // prime
            case 2267, 2266, 2265 -> // rex
                2852;
            case 3452 ->// penance queen
                5413;
            case 2745 -> 2653;
            case 2743 -> 2645;// metal dragon
            case 1270, 273, 274, 6593, 8031, 8030, 8027 -> 89;// tzhaar-xil
            case 2598, 2599, 2600, 2610, 2601, 2602, 2603, 2606, 2591, 2604 ->// tzhar-hur
                2606;
            case 3121 -> 2629;
            case 66, 67, 168, 169, 162, 68 ->// gnomes
                193;
            case 160, 161 -> 194;
            case 163, 164 -> 193; // Tree spirit
            case 438, 439, 440, 441, 442, 443 -> 95;// river troll
            case 391, 392, 393, 394, 395, 396 -> 285;
            case 1153, 1154, 1155, 1156, 1157, 1158 -> // kalphite
                1186;
            case 1160 -> // kalphite
                1179;
            case 2734, 2627 ->// tzhaar
                2622;
            case 2630, 2629, 2736, 2738 -> 2626;
            case 2631, 2632 -> 2629;
            case 2741 -> 2635;
            case 908 -> 129;
            case 909 -> 147;
            case 911 -> 65;
            case 1459 ->// monkey guard
                1403; // pyrefiend
            case 435, 3406, 7394 -> 1581;// banshee
            case 414, 7272, 7391, 7390 -> 1525;
            case 448, 7388, 1649, 1650, 1651, 1652, 1653, 1654, 1655, 1656, 1657 ->// crawling hand
                1591;// bloodveld
            case 484, 7276, 1619, 7398, 7397 -> 1550;
            case 446, 7396, 1644, 1645, 1646, 1647 -> 430;// nechryael
            case 11, 7411, 7278 -> 1529;// gargoyle
            case 1543, 1611, 7407 -> 1519;// abyssal demon
            case 415, 7410 -> 1537;
            case 1770, 1771, 1772, 1773, 2678, 2679, 1774, 1775, 1776 ->// goblins
                312;
            case 132 -> // monkey
                221;
            case 1030, 1031, 1032, 1033, 1034, 1035 -> // wolfman
                6538;
            case 1456 ->// monkey archer
                1395;
            case 1477 -> 247;
            case 144 -> 6255;
            case 1125 ->// dad
                285;// troll
            case 1096, 1097, 1098, 1942, 1101, 1106 -> 285;
            case 1095 -> 285;
            case 123, 122 ->// hobgoblin
                165;// hellhound
            case 135, 142, 95 -> 6578; // wolf
            case 1593, 152, 45, 1558, 1954 -> 76;
            case 6707 -> -1;
            case 89 -> 6375;
            case 133 -> 290;
            case 105 ->// bear
                4921; // zombie
            case 5399, 751, 26, 27, 28, 29, 31, 32, 33, 34, 35, 36, 30 -> 5574; // spider
            case 60, 64, 59, 61, 63, 3021, 2035, 62, 1009 -> 5328;
            case 2534, 85, 7258, 749, 104, 655, 491 -> // ghost
                -1;
            case 1585, 2084 -> // giant
                4671;
            case 111 -> 4671;
            case 2098, 2090, 2091, 2092, 2093, 2463, 116, 891 -> 4651;
            case 239 -> // kbd
                89;// green dragon
            case 264, 268, 2919, 270, 742, 1589, 247, 52, 7273, 7274, 259, 7275 -> 89;
            case 2889 -> 2860; // cow
            case 81, 397 -> 5850;
            case 708 -> // imp
                170;
            case 86, 87 -> 139;
            case 47 ->// rat
                2706;
            case 2457 -> 2366; // snake
            case 128, 1479 -> 276;
            case 1017, 2693, 41 -> // chicken
                55;
            case 90, 91, 93 -> // skeleton
                261;
            case 1 -> 424;
            default -> -1;
        };
    }
}
