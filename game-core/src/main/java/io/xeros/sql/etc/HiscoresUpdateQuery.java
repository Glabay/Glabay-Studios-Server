package io.xeros.sql.etc;

import io.xeros.Server;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.*;
import java.util.Arrays;
import java.util.StringJoiner;

public class HiscoresUpdateQuery implements SqlQuery<Boolean> {

    /**
     * This is a temporary solution for hiscores, because we haven't migrated them to support display names.
     * Instead we will simply delete hiscores entries and re-insert them for the new display name when
     * a display name is changed.
     */
    public static void reinsertForNewDisplayName(Player player, String oldDisplayName) {
        Server.getDatabaseManager().exec(Server.getConfiguration().getHiscoresDatabase(), ((context, connection) -> {
            delete(connection, oldDisplayName.toLowerCase(), SKILL_HISCORES_DB_NAME);
            delete(connection, oldDisplayName.toLowerCase(), BOSS_HISCORES_DB_NAME);
            new HiscoresUpdateQuery(player).execute(context, connection);
            return null;
        }));
    }

    private static void delete(Connection connection, String username, String table) throws SQLException {
        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM " + table + " WHERE username=?");
        deleteStatement.setString(1, username);
        deleteStatement.execute();
    }

    private static final String SKILL_HISCORES_DB_NAME = "highscores";
    private static final String BOSS_HISCORES_DB_NAME = "boss_highscores";

    private static final String SKILL_EXP_COLUMN = "%s_exp";
    private static final String SKILL_LEVEL_COLUMN = "%s_level";
    private static final String SKILL_MAX_LEVEL_AT_COLUMN = "max_%s_exp_at";

    private static final KcEntry[] BOSS_HISCORES = {
            new KcEntry("barrelchest_kc", "barrelchest"),
            new KcEntry("dagsupreme_kc", "dagannoth supreme"),
            new KcEntry("dagprime_kc", "dagannoth supreme"),
            new KcEntry("dagrex_kc", "dagannoth rex"),
            new KcEntry("kbd_kc", "king black dragon"),
            new KcEntry("kq_kc", "kalphite queen"),
            new KcEntry("graardor_kc", "general graardor"),
            new KcEntry("tsutsaroth_kc", "k'ril tsutsaroth"),
            new KcEntry("kree_kc", "kree'arra"),
            new KcEntry("zilyana_kc", "commander zilyana"),
            new KcEntry("corp_kc", "corporeal beast"),
            new KcEntry("kraken_kc", "kraken"),
            new KcEntry("cerberus_kc", "cerberus"),
            new KcEntry("gorilla_kc", "demonic gorilla"),
            new KcEntry("lizardman_kc", "lizardman shaman"),
            new KcEntry("vorkath_kc", "vorkath"),
            new KcEntry("zulrah_kc", "zulrah"),
            new KcEntry("hydra_kc", "alchemical hydra"),
            new KcEntry("nightmare_kc", "the nightmare"),
            new KcEntry("mimic_kc", "mimic"),
            new KcEntry("sarachnis_kc", "sarachnis"),
            new KcEntry("cox_kc", "chamber of xeric"),
            new KcEntry("tob_kc", "theatre of blood"),
            new KcEntry("grotesque_kc", "grotesque guardians"),
    };

    private final Player player;

    public HiscoresUpdateQuery(Player player) {
        this.player = player;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        return updateSkillHiscores(connection) && updateBossHiscores(connection);
    }

    private boolean updateSkillHiscores(Connection connection) throws SQLException {
        delete(connection, player.getDisplayName().toLowerCase(), SKILL_HISCORES_DB_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + SKILL_HISCORES_DB_NAME + " (");
        sb.append("username, ");
        sb.append("gamemode, ");
        sb.append("rights, ");
        sb.append("combat_level, ");
        sb.append("overall_exp, ");
        sb.append("overall_level, ");

        StringJoiner columns = new StringJoiner(", ");
        for (Skill skill : Skill.values()) {
            String name = skill.name().toLowerCase();
            columns.add(SKILL_LEVEL_COLUMN.replace("%s", name));
            columns.add(SKILL_EXP_COLUMN.replace("%s", name));
            columns.add(SKILL_MAX_LEVEL_AT_COLUMN.replace("%s", name));
        }

        sb.append(columns.toString());
        sb.append(") VALUES (");
        StringJoiner values = new StringJoiner(", ");

        for (int i = 0; i < 6; i++) {
            values.add("?");
        }

        for (Skill ignored : Skill.values()) {
            values.add("?").add("?").add("?");
        }

        sb.append(values.toString());
        sb.append(")");

        PreparedStatement statement = connection.prepareStatement(sb.toString());
        int index = 1;
        statement.setString(index++, player.getDisplayName().toLowerCase());
        statement.setString(index++, getModeString(player));
        statement.setInt(index++, player.getRights().getPrimary().getValue());
        statement.setInt(index++, player.calculateCombatLevel());
        statement.setLong(index++, player.getPA().getTotalXP());
        statement.setInt(index++, player.getPA().calculateTotalLevel());

        for (Skill skill : Skill.values()) {
            int experience = player.playerXP[skill.getId()];
            statement.setInt(index++, player.getLevelForXP(experience));
            statement.setInt(index++, experience);
            if (player.gained200mTime[skill.getId()] == 0) {
                statement.setTimestamp(index++, null);
            } else {
                statement.setTimestamp(index++, new Timestamp(player.gained200mTime[skill.getId()]));
            }
        }

        return statement.executeUpdate() > 0;
    }

    private boolean updateBossHiscores(Connection connection) throws SQLException {
        delete(connection, player.getDisplayName().toLowerCase(), BOSS_HISCORES_DB_NAME);

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(BOSS_HISCORES_DB_NAME);
        builder.append("(");
        builder.append("username, ");
        builder.append("gamemode, ");
        builder.append("rights, ");

        StringJoiner columns = new StringJoiner(", ");
        Arrays.stream(BOSS_HISCORES).forEach(it -> columns.add(it.row));
        builder.append(columns.toString());

        builder.append(") VALUES (");
        StringJoiner values = new StringJoiner(", ");

        for (int i = 0; i < 3; i++) {
            values.add("?");
        }

        Arrays.stream(BOSS_HISCORES).forEach(it -> values.add("?"));
        builder.append(values.toString());
        builder.append(")");

        PreparedStatement statement = connection.prepareStatement(builder.toString());
        int index = 1;
        statement.setString(index++, player.getDisplayName().toLowerCase());
        statement.setString(index++, getModeString(player));
        statement.setInt(index++, player.getRights().getPrimary().getValue());

        for (KcEntry entry : BOSS_HISCORES) {
            statement.setInt(index++, player.getNpcDeathTracker().getKc(entry.name));
        }

        return statement.executeUpdate() > 0;
    }

    private String getModeString(Player player) {
        String mode = "Normal";
        if (player.getMode().getType() == ModeType.STANDARD) {
            mode = "Normal";
        } else if (player.getMode().getType() == ModeType.IRON_MAN) {
            mode = "Iron Man";
        } else if (player.getMode().getType() == ModeType.ULTIMATE_IRON_MAN) {
            mode = "Ultimate Iron Man";
        } else if (player.getMode().getType() == ModeType.HC_IRON_MAN) {
            mode = "Hardcore Iron Man";
        } else if (player.getMode().getType() == ModeType.OSRS) {
            mode = "Extreme";
        } else if (player.getMode().getType() == ModeType.ROGUE) {
            mode = "Rogue";
        } else if (player.getMode().getType() == ModeType.ROGUE_IRONMAN) {
            mode = "Rogue Ironman";
        }  else if (player.getMode().getType() == ModeType.ROGUE_HARDCORE_IRONMAN) {
            mode = "Rogue HC Ironman";
        }
        return mode;
    }

    private static class KcEntry {
        private final String row;
        private final String name;

        public KcEntry(String row, String name) {
            this.row = row;
            this.name = name;
        }
    }
}
