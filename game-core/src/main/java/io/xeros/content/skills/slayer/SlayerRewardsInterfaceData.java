package io.xeros.content.skills.slayer;

import io.xeros.util.Misc;

public class SlayerRewardsInterfaceData {

    static final String INFO_BOX_ACCEPT_CONSUMER_KEY = "slayer_info_accept";
    static final String INFO_BOX_DECLINE_CONSUMER_KEY = "slayer_info_decline";
    // Strings

    static final int CURRENT_SLAYER_POINTS_STRING = 49_999;

    // Task interface data
    static final int CURRENT_ASSIGNMENT_STRING = 50_202;
    static final int[] BLOCKED_TASK_STRINGS = {50_211, 50_212, 50_213, 50_214, 50_215, 50_216};
    static final int[] UNBLOCK_TASK_BUTTONS = {196_029, 196_030, 196_031, 196_032, 196_033, 196_034};
    static final int BLOCK_TASK_BUTTON = 196_028;
    static final int CANCEL_TASK_BUTTON = 196_027;

    // Info-box interface data
    static final int[] INFO_BOX_STRINGS = {50_304, 50_305, 50_306, 50_307, 50_308, 50_309, 50_310, 50_311, 50_312, 50_313};
    static final int INFO_CONFIRM_BUTTON = 196_127;
    static final int INFO_DECLINE_BUTTON = 196_126;

    public static final int UNLOCK_INTERFACE_ID = 50_000;
    public static final int EXTEND_INTERFACE_ID  = 50_100;
    public static final int TASK_INTERFACE_ID  = 50_200;
    static final int INFO_BOX_INTERFACE_ID = 50_300;

    public enum Extend {
        BLOODVELD(195_183, 75, TaskExtension.BLOODVELD),
        DUST_DEVIL(195_187, 100, TaskExtension.DUST_DEVIL),
        GARGOYLE(195_191, 100, TaskExtension.GARGOYLE),
        NECHS(195_195, 100, TaskExtension.NECHS),
        KRAKEN(195_199, 100, TaskExtension.KRAKEN),
        GREATER_DEMON(195_203, 100, TaskExtension.GREATER_DEMON),
        BLACK_DEMON(195_207, 100, TaskExtension.BLACK_DEMON)
        ;

        private final int button;
        private final int cost;
        private final TaskExtension unlock;

        Extend(int button, int cost, TaskExtension unlock) {
            this.button = button;
            this.cost = cost;
            this.unlock = unlock;
        }

        public int getConfig() {
            return 899 + ordinal();
        }

        public int getButton() {
            return button;
        }

        public int getCost() {
            return cost;
        }

        public TaskExtension getUnlock() {
            return unlock;
        }

        public String[] getInformation() {
            String name = Misc.formatPlayerName(unlock.name().replaceAll("_", " "));
            String[] info = new String[6];
            info[0] = String.format("Extend %s Tasks", name);
            info[1] = "";
            info[2] = String.format("Whenever you get a %s task, it will", name);
            info[3] = "be a bigger task.";
            info[4] = "";
            info[5] = String.format("@red@ This will cost %d points.", cost);
            return info;
        }
    }

    public enum Unlock {
        IMBUE_SLAYER_HELMET(195_083, 150, SlayerUnlock.IMBUE_HELMET,
                "Imbue Slayer Helmet", "This will imbue one Slayer helmet", "from your inventory."),
        MALEVOLENT_MASQUERADE(195_086, 400, SlayerUnlock.MALEVOLENT_MASQUERADE,
                "Malevolent masquerade", "Learn to combine the protective Slayer", "headgear and Slayer gem into one",
                            "universal helmet, with level 55 Crafting."),
        BIGGER_AND_BADDER(195_090, 150, SlayerUnlock.BIGGER_AND_BADDER,
                "Bigger and Badder","Increase the risk against certain slayer", "monsters with the chance of a superior",
                "version spawning whilst on a slayer task."),
        BROADER_FLETCHING(195_094, 300, SlayerUnlock.BROADER_FLETCHING,
                "Broader Fletching", "Learn to fletch Broad bolts", "and Amethyst broad bolts and Broad arrows.", "You can buy the (u) broad ammo in the", "Ranging shop to the north."),
        ;

        private final int button;
        private final int cost;
        private final SlayerUnlock unlock;
        private final String name;
        private final String[] information;

        Unlock(int button, int cost, SlayerUnlock unlock, String name, String...information) {
            this.button = button;
            this.cost = cost;
            this.unlock = unlock;
            this.name = name;
            this.information = information;
        }

        public int getConfig() {
            return 880 + ordinal();
        }

        public int getButton() {
            return button;
        }

        public int getCost() {
            return cost;
        }

        public SlayerUnlock getUnlock() {
            return unlock;
        }

        public String[] getInformation() {
            String[] info = new String[information.length + 4];
            System.arraycopy(information, 0, info, 2, information.length);
            info[0] = name;
            info[1] = "";
            info[info.length - 1] = String.format("@red@ This will cost %d points.", cost);
            info[info.length - 2] = "";
            return info;
        }
    }

    public enum Tab {
        UNLOCK(195_078, UNLOCK_INTERFACE_ID),
        EXTEND(195_077, EXTEND_INTERFACE_ID),
        BUY(195_076, -1),
        TASK(195_075, TASK_INTERFACE_ID);

        private final int button;
        private final int interfaceId;

        Tab(int button, int interfaceId) {
            this.button = button;
            this.interfaceId = interfaceId;
        }

        public int getButton() {
            return button;
        }

        public int getInterfaceId() {
            return interfaceId;
        }
    }
}
