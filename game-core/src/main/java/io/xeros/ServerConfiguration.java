package io.xeros;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.xeros.sql.DatabaseCredentials;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains the configuration stored in YAML format.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
@Getter
@Setter
public class ServerConfiguration {

    public static final String CONFIGURATION_FILE = "config.yaml";

    public static ServerConfiguration getDefault() {
        var configuration = new ServerConfiguration();
            configuration.serverState = ServerState.DEBUG;
        return configuration;
    }

    @JsonProperty("server_state")
    private ServerState serverState;

    @JsonProperty("embedded_password")
    private String embeddedPassword;

    @JsonProperty("local_database")
    private DatabaseCredentials localDatabase;

    @JsonProperty("store_database")
    private DatabaseCredentials storeDatabase;

    @JsonProperty("vote_database")
    private DatabaseCredentials voteDatabase;

    @JsonProperty("hiscores_database")
    private DatabaseCredentials hiscoresDatabase;

    @JsonProperty("backup_ftp_credentials")
    private DatabaseCredentials backupFtpCredentials;

    /**
     * Path to the folder that contains mysqldump.exe (if applicable), include a leading slash.
     */
    @JsonProperty("mysqldump_path")
    private String mysqlDumpPath;

    public ServerConfiguration(ServerState serverState,
                               String embeddedPassword,
                               DatabaseCredentials storeDatabase,
                               DatabaseCredentials voteDatabase) {
        this.serverState = serverState;
        this.embeddedPassword = embeddedPassword;
        this.storeDatabase = storeDatabase;
        this.voteDatabase = voteDatabase;
    }

    public ServerConfiguration() {}

    @JsonIgnore
    public boolean isDisplayNamesDisabled() {
        return !isLocalDatabaseEnabled() || Configuration.DISABLE_DISPLAY_NAMES;
    }

    @JsonIgnore
    public boolean isLocalDatabaseEnabled() {
        return serverState.isSqlEnabled() && localDatabase != null;
    }
}
