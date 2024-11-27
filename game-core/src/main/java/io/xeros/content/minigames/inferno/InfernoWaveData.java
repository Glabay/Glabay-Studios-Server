package io.xeros.content.minigames.inferno;

import io.xeros.util.Misc;

public class InfernoWaveData {

    public static final int
            JAL_NIB = 7691,
            JAL_MEJRAH = 7692,
            JAL_AK = 7693,
            JAL_AKREK_MEJ = 7694,
            JAL_AKREK_XIL = 7695,
            JAL_AKREK_KET = 7696,
            JAL_IMKOT = 7697,
            JAL_XIL = 7702,
            JAL_ZEK = 7699,
            JALTOK_JAD = 7700,
            YT_HURKOT = 7705,
            TZKAL_ZUK = 7706,
            ANCESTRAL_GLYPH = 7707,
            JAL_MEJJAK = 7708;

    public static final int[][] LEVEL = {
            {JAL_MEJRAH}, //1
            {JAL_MEJRAH, JAL_MEJRAH},
            {JAL_MEJRAH},
            {JAL_AK},
            {JAL_AK, JAL_MEJRAH}, //5
            {JAL_AK, JAL_MEJRAH, JAL_MEJRAH},
            {JAL_AK, JAL_AK},
            {JAL_MEJRAH},
            {JAL_IMKOT},
            {JAL_IMKOT, JAL_MEJRAH}, //10
            {JAL_IMKOT, JAL_MEJRAH, JAL_MEJRAH},
            {JAL_IMKOT, JAL_MEJRAH},
            {JAL_IMKOT, JAL_MEJRAH, JAL_AK},
            {JAL_IMKOT, JAL_MEJRAH, JAL_MEJRAH, JAL_AK},
            {JAL_AK, JAL_AK, JAL_IMKOT}, //15 -
            {JAL_IMKOT, JAL_IMKOT},
            {JAL_MEJRAH},
            {JAL_XIL},
            {JAL_XIL, JAL_MEJRAH},
            {JAL_XIL, JAL_MEJRAH, JAL_MEJRAH}, //20
            {JAL_XIL, JAL_AK},
            {JAL_XIL, JAL_AK, JAL_MEJRAH},
            {JAL_XIL, JAL_MEJRAH, JAL_MEJRAH, JAL_AK},
            {JAL_XIL, JAL_AK, JAL_AK},
            {JAL_XIL, JAL_IMKOT}, //25
            {JAL_XIL, JAL_IMKOT, JAL_MEJRAH},
            {JAL_XIL, JAL_MEJRAH, JAL_MEJRAH, JAL_IMKOT},
            {JAL_XIL, JAL_IMKOT, JAL_AK},
            {JAL_XIL, JAL_AK, JAL_IMKOT},
            {JAL_XIL, JAL_IMKOT, JAL_MEJRAH, JAL_MEJRAH, JAL_AK}, //30
            {JAL_XIL, JAL_AK, JAL_AK, JAL_IMKOT},
            {JAL_XIL, JAL_IMKOT, JAL_IMKOT},
            {JAL_XIL, JAL_XIL},
            {JAL_MEJRAH},
            {JAL_ZEK}, //35
            {JAL_ZEK, JAL_MEJRAH},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH},
            {JAL_ZEK, JAL_AK},
            {JAL_ZEK, JAL_AK, JAL_MEJRAH},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH, JAL_AK}, //40
            {JAL_ZEK, JAL_AK, JAL_AK},
            {JAL_ZEK, JAL_IMKOT},
            {JAL_ZEK, JAL_IMKOT, JAL_MEJRAH},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH, JAL_IMKOT},
            {JAL_ZEK, JAL_IMKOT, JAL_AK}, //45
            {JAL_ZEK, JAL_MEJRAH, JAL_AK, JAL_IMKOT},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH, JAL_AK, JAL_IMKOT},
            {JAL_ZEK, JAL_AK, JAL_AK, JAL_IMKOT},
            {JAL_ZEK, JAL_IMKOT, JAL_IMKOT},
            {JAL_ZEK, JAL_XIL}, //50
            {JAL_ZEK, JAL_MEJRAH, JAL_XIL},
            {JAL_ZEK, JAL_XIL, JAL_MEJRAH, JAL_MEJRAH},
            {JAL_ZEK, JAL_XIL, JAL_AK},
            {JAL_ZEK, JAL_MEJRAH, JAL_XIL, JAL_AK},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH, JAL_AK, JAL_XIL}, //55
            {JAL_ZEK, JAL_AK, JAL_AK, JAL_XIL},
            {JAL_ZEK, JAL_ZEK, JAL_IMKOT},
            {JAL_ZEK, JAL_MEJRAH, JAL_IMKOT, JAL_XIL},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH, JAL_IMKOT, JAL_XIL},
            {JAL_ZEK, JAL_AK, JAL_IMKOT, JAL_XIL}, //60
            {JAL_ZEK, JAL_MEJRAH, JAL_AK, JAL_IMKOT, JAL_XIL},
            {JAL_ZEK, JAL_MEJRAH, JAL_MEJRAH, JAL_AK, JAL_IMKOT, JAL_XIL},
            {JAL_ZEK, JAL_AK, JAL_AK, JAL_IMKOT, JAL_XIL},
            {JAL_ZEK, JAL_IMKOT, JAL_IMKOT, JAL_XIL},
            {JAL_ZEK, JAL_XIL, JAL_XIL}, //65
            {JAL_ZEK, JAL_ZEK, JAL_ZEK},
            {JALTOK_JAD},//placeholder
            {JALTOK_JAD},//placeholder
            {JALTOK_JAD}//placeholder

    };

    public static int getHp(int npc) {
        switch (npc) {
            case JAL_NIB:
            case JAL_AKREK_XIL:
            case JAL_AKREK_MEJ:
            case JAL_AKREK_KET:
                return 15;
            case JAL_MEJRAH:
                return 25;
            case JAL_AK:
                return 40;
            case YT_HURKOT:
                return 60;
            case JAL_IMKOT:
                return 75;
            case JAL_MEJJAK:
                return 80;
            case JAL_XIL:
                return 130;
            case JAL_ZEK:
                return 220;
            case JALTOK_JAD:
                return 350;
            case TZKAL_ZUK:
                return 1200;
        }
        return 50 + Misc.random(50);
    }

    public static int getMax(int npc) {
        switch (npc) {
            case JAL_NIB:
                return 2;
            case JAL_MEJJAK:
                return 10;
            case YT_HURKOT:
                return 14;
            case JAL_AKREK_XIL:
            case JAL_AKREK_MEJ:
            case JAL_AKREK_KET:
                return 18;
            case JAL_MEJRAH:
                return 19;
            case JAL_AK:
                return 29;
            case JAL_XIL:
                return 46;
            case JAL_IMKOT:
                return 49;
            case JAL_ZEK:
                return 70;
            case JALTOK_JAD:
                return 113;
            case TZKAL_ZUK:
                return 251;
        }
        return 5 + Misc.random(5);
    }

    public static int getAtk(int npc) {
        switch (npc) {
            case JAL_NIB:
            case JAL_MEJJAK:
            case JAL_AKREK_XIL:
            case JAL_AKREK_KET:
                return 1;
            case YT_HURKOT:
                return 140;
            case JAL_MEJRAH:
                return 0;
            case JAL_AK:
                return 160;
            case JAL_XIL:
                return 140;
            case JAL_IMKOT:
                return 49;
            case JAL_ZEK:
                return 370;
            case JALTOK_JAD:
                return 750;
            case TZKAL_ZUK:
                return 350;
        }
        return 50 + Misc.random(50);
    }

    public static int getDef(int npc) {
        switch (npc) {
            case JAL_NIB:
                return 15;
            case JAL_MEJJAK:
                return 100;
            case YT_HURKOT:
            case JAL_XIL:
                return 60;
            case JAL_MEJRAH:
                return 55;
            case JAL_AK:
            case JAL_AKREK_XIL:
            case JAL_AKREK_MEJ:
            case JAL_AKREK_KET:
                return 95;
            case JAL_IMKOT:
                return 49;
            case JAL_ZEK:
                return 260;
            case JALTOK_JAD:
                return 480;
            case TZKAL_ZUK:
                return 260;
        }
        return 50 + Misc.random(50);
    }

}